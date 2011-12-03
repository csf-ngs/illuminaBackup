package at.ac.csfg.illuminabackup
import java.io.File
import scala.sys.process._
import scala.collection.mutable.ArrayBuffer



trait Folder {
    
  /**
   * path to folder
   */
   def path: File
     
   /**
   * return the subfolders 
   */
   def subFolders: Seq[Folder] = Nil
   
  /**
   * path names of subfolders to copy completely
   */
  def copySubDirs(): Seq[String] = Nil
  
  /**
   * name of files to gzip before copying
   * 
   */
  def gzipNames: Seq[String] = Nil 
  
  /**
   * condition for copying the files  excludeBackup is token to exclude folder from backup
   * subdirs will also not be copied
   * 
   * default: true
   * 
   */
  def doCopy = true
  
  /**
   * globs to include in search
   * could start/end with *xx?x* 
   * will be orred
   */
  def includeNames: Seq[String] = Nil
  
  /**
   * regexes to include in search
   * could start/end with .*xxx.* 
   * will be orred
   */
  def includePatterns: Seq[String] = Nil
  
  /**
   * regex exclude for find => cpio
   * could start/end with .*xxx.* 
   */
  def excludePatterns: Seq[String] = Nil
  
  /**
   * glob exclude for find => cpio
   * could start/end with *xxx*
   */
  def excludeNames: Seq[String] = Nil
  
  /**
   * important files regex patterns will be ored 
   * mostly for result folders 
   */
  def importantNames: Seq[String] = Nil
  
  /**
  * doing the copy
  */
  def commandsCp(rsyncFile: String): Seq[String] = if(doCopy) Seq(gzipCmd, findFiles+" >> "+rsyncFile) ++ findSubdirsCommand.map(_+" >> "+rsyncFile) else Nil

  def commandsTar(tarFile: String, laneNr: Int): Seq[String] = if(doCopy) (Seq(findTarFiles(laneNr)) ++ findSubdirsCommand).map(_+" | tar --append --null --files-from=- --file="+tarFile) else Nil
  
  def fillRsyncFile(rsyncFile: String, basePath: String): Boolean = {
      val successes = for(command <- commandsCp(rsyncFile) if command != "") yield {
    	  Process((new java.lang.ProcessBuilder("sh", "-c", command)) directory new File(basePath).getAbsoluteFile()) !
      }
      successes.forall(_ == 0)
  }
   
  def toTarFile(tarFile: String, laneNr: Int, basePath: String): Boolean = {
     val successes = for(command <- commandsTar(tarFile, laneNr) if command != "") yield {
    	  Process((new java.lang.ProcessBuilder("sh", "-c", command)) directory new File(basePath).getAbsoluteFile()) !
      }
      successes.forall(_ == 0)
  }   
  
  /**
   * add files to tar
   * default all in folder
   * resultsfolder and below then split by lane
   * 
   **/
  def findTarFiles(laneNr: Int): String = findFiles
  
  def quote(string: String) = "\""+string+"\""
   
  def findFiles(): String = toFindCmd(includeNamesToFind+includePatternsToFind+excludePatternsFind+excludePatternsName)+" -print0 "
  
  def gzipCmd(): String = if(gzipNames.length > 0) toFindCmd(gzipNamesToFind)+" | xargs -I{} gzip {}" else ""
  
  def toFindCmd(searchPatterns: String): String = if(searchPatterns != "")  "find "+findPath+""" -type f -maxdepth 1  \( """+searchPatterns+""" \)""" else ""
    
  def toFind(patterns: Seq[String], separator: String, optSep: String = ""): String = {
      if(patterns.length == 0) "" else patterns.toSet.map(quote).mkString(separator, optSep+separator, "")
  }
  
  def gzipNamesToFind(): String = toFind(gzipNames, " -name ", " -o ")
    
  def includeNamesToFind(): String = toFind(includeNames, " -name ", " -o ")
  
  def includePatternsToFind(): String = toFind(includePatterns, " -regex ", " -o ")
  
  def excludePatternsFind(): String = toFind(excludePatterns, " ! -regex ")
  
  def excludePatternsName(): String = toFind(excludeNames, " ! -name  ")
  
  val excludeBackupSet: Boolean = new File(path.getAbsolutePath()+"/"+"excludeBackup").exists
  
  def subDirsDepth = 1
  
  def depth = 0
  
  def findPath = path.getAbsolutePath.split("/").reverse.take(depth).reverse.mkString("./","/","")
  
  def findSubdirsCommand(): Seq[String] = {
      for{ 
          d <- copySubDirs()
          f <- checkFile(path, d)
      } yield {
         "find "+findPath+"/"+d+ " -type f -maxdepth "+subDirsDepth+" -print0 "
      }
  }
  
  def checkFile(file: File, subFolder: String): Option[File] = {
      val newDir = new File(file.getAbsolutePath()+"/"+subFolder)
      if(! excludeBackupSet && newDir.exists && newDir.isDirectory()) Some(newDir) else None
  }
  
}


case class RunFolder(path: File) extends Folder { //RunFolder

  override def gzipNames = Seq("RTA_*csv", "RunLog*xml", "Recipe*xml")
  
  override def includeNames = Seq("RunInfo.xml", "*params") ++ gzipNames.map(_+".gz")
    
  override def copySubDirs = Seq("Config")
  
  override def subFolders: Seq[Data] = checkFile(path, "Data").map(Data(_)).toList
    
}


case class Data(path: File) extends Folder {

  
  override def includeNames = Seq("Status.htm")   
  
  override def copySubDirs = Seq("Status_Files", "RunBrowser", "reports")
  
  override def subFolders: Seq[Intensities] = checkFile(path, "Intensities").map(Intensities(_)).toList
  
  override def depth = 1
  
}

case class Intensities(path: File) extends Folder { //Data/Intensities
  
   override def includeNames = Seq("config.xml", "RTAConfiguration.xml")   
   
   override def subFolders: Seq[Basecalls] = {
       val paths = path.listFiles.filter( f => f.getName.startsWith("BaseCalls") || f.getName.startsWith("Bustard") )
       paths.map(p => new Basecalls(p))
   }
   
   override def depth = 2
     
}

class Basecalls(val path: File) extends Folder { //Data/Intensities/Basecalls|Bustard
  
   override def doCopy = !excludeBackupSet
 
   override def includeNames = Seq("BustardSummary.xml", "BustardSummary.xsl", "config.xml", "*htm", "*Signal_Means.txt" )
  
   override def copySubDirs = Seq("Plots")
   
   override def subFolders: Seq[Folder] = phasing ++ matrix ++ resultFolders
      
   def resultFolders: Seq[Folder] = {
      val geralds = path.listFiles.filter { f => f.getName.startsWith("GERALD") }.map(f => new Gerald(f))      
      val illumina2bams = path.listFiles.filter { f => f.getName.startsWith("illumina2bam") }.map(f => new Illumina2Bam(f))
      geralds ++ illumina2bams
   }
   
   def phasing = checkFile(path, "Phasing").map(new BasecallsPhasing(_)).toList
   
   def matrix = checkFile(path, "Matrix").map(new BasecallsMatrix(_)).toList
 
   override def depth = 3
}
 
class BasecallsPhasing(val path: File) extends Folder {//Data/Intensities/Basecalls|Bustard/Phasing
   
   override def includeNames = Seq("s_?_?_phasing.txt","s_?_phasing.txt")
  
   override def depth = 4
}


class BasecallsMatrix(val path: File) extends Folder {//Data/Intensities/Basecalls|Bustard/Matrix
  
   override def includeNames = Seq("s_?_?_matrix.txt")
 
   override def depth = 4
}

trait ResultFolder extends Folder {
  
   override def subFolders: Seq[Demux] = {
     val demuxDirs = path.listFiles.filter(f => f.isDirectory() && f.getName.startsWith("demux_adapter"))
     demuxDirs.map(f => new Demux(f))
   }
   
   override def includeNames = Seq("*")
   
   override def doCopy = !excludeBackupSet
   
   override def excludePatterns: Seq[String] = Seq(".*jnomicss.*", ".*makeQuality.*")
   
   override def depth = 4
   
}

class Gerald(val path: File) extends ResultFolder {//Data/Intensities/Basecalls|Bustard/GERALD
   
    override def excludeNames = Seq("makeQuality.py.*")

    override def findTarFiles(laneNr: Int): String = "find "+findPath+" -maxdepth 1 "+"-name 's_"+laneNr+"_*sequence.*' -print0 " 

    override def copySubDirs = Seq("fastQC")
    
    override def subDirsDepth = 2 //for fastQC
        
}

class Illumina2Bam(val path: File) extends ResultFolder {//Data/Intensities/Basecalls|Bustard/illumina2bam
      
    override def findTarFiles(laneNr: Int): String = "find "+findPath+" -maxdepth 1 "+"-name '*_"+laneNr+"_*.bam*'  -print0 "  
  
    override def subDirsDepth = 2 //for QC    
   
}

class Demux(val path: File) extends ResultFolder {//Data/Intensities/Basecalls|Bustard/illumina2bam demux_reads_1
    
    override def subFolders = Nil
        
    override def findTarFiles(laneNr: Int) = if(path.getName.endsWith(laneNr.toString)) "find "+findPath+" -maxdepth 1 -print0" else "find . -maxdepth 1 -name dontfindanything -print0 "
 
    override def subDirsDepth = 2 //for QC  
 
    override def depth = 5
}









