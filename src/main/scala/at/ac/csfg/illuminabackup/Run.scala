package at.ac.csfg.illuminabackup
import java.io.File
import scala.sys.process._
import scala.collection.mutable.ArrayBuffer
import java.text.SimpleDateFormat
import java.util.Date
import org.slf4j.LoggerFactory





/**
 * 
 * what to do on checksum error?
 * 
 **/
class Run(folderPath: String, outCopyBase: String, outTarBase: String, ignoreDemux: Boolean) {
  private val log = LoggerFactory.getLogger(getClass)
  
  val outBaseFolder = new File(outCopyBase).getAbsoluteFile()+"/"+new File(folderPath).getAbsoluteFile().getName
  
  logInfo("backing up to: "+outBaseFolder+" tar: "+outTarBase)
   
  val osxMD5 = """MD5.*=\s*(\w*)""".r
  val linuxMD5 = """(\w*)\s*\w*""".r
  val rs = "rsync.files.txt"
  
  def date = new Date()
  
  val runFolderName = new File(folderPath).getName
  
  val tarFileBase = new SimpleDateFormat("yyMMdd").format(date)+"_"+runFolderName
  
  val rsyncFile = new File(folderPath).getAbsolutePath+"/"+rs
  val logFile = new File(folderPath).getAbsolutePath+"/"+rs+".log"
  val backuppedFile = new File(folderPath).getAbsolutePath+"/backupped"
  
  def outTarLane(laneNr: Int) = new File(outTarBase).getAbsolutePath+"/"+tarFileBase+"_"+laneNr+".tar"
  
  lazy val folders = collectFolders()
  
  def filter(folders: Seq[Folder]) = folders.filter(_.doCopy)
  
  /**
   * this is the method to call
   */
  def save(): Boolean = createRsyncFile && fillRsyncFile && rsync && tar && setBackuppedTag
   
  def createRsyncFile(): Boolean = {
      logInfo(folderPath+" creating rsync file: "+rsyncFile)
      val rsync = new File(rsyncFile)
      if(rsync.exists){
        rsync.delete()         
      }
      rsync.createNewFile()
  }
  
  def fillRsyncFile(): Boolean = {
      logInfo(folderPath+" collecting files ")
      folders.map(_.fillRsyncFile(rsyncFile, folderPath)).forall(_ == true)
  }
  
  def rsync(): Boolean = {
      logInfo("rsyncing ")
      import scala.sys.process._  
      val cmd = "rsync -a --chmod=Dugo=rx,Fugo=r --files-from="+rsyncFile+" --from0 --log-file="+logFile+" "+folderPath+" "+outBaseFolder
    
      val success = cmd !
     
      logInfo("done rsyncing exit status "+success)
      success == 0
  }  
 
  def tar(): Boolean = {
      if(outTarBase.toLowerCase == "no"){
        logInfo("not taring")
        true
      }else{
        logInfo("taring ")
        (1 to 8).map(laneNr => tarResults(laneNr)).forall(_ != false)
      } 
 }
  
  def tarResults(laneNr: Int): Boolean = {
      logInfo("taring lane "+laneNr)
      val out = outTarLane(laneNr)
      val created = "tar --create --file="+out+""" --files-from=/dev/null""" ! //is this really necessary? I forgot maybe it inits a tar file
      
      folders.map(_.toTarFile(out, laneNr, folderPath)).forall(_ == true)
  }
  
  def setBackuppedTag(): Boolean = {
      logInfo("setting backupped tag")
      val f = new File(backuppedFile)
      f.createNewFile()
  }
  
  /**
   * returns all subfolders filtered on doCopy
   */
  def collectFolders(): Seq[Folder] = {//there is a recursive way to do this but not now
     val runFolder = RunFolder(new File(folderPath))
     val data = filter(runFolder.subFolders)
     val intensities = filter(data.map(_.subFolders).flatten)
     val basecalls = filter(intensities.map(_.subFolders).flatten)
     val results = filter(basecalls.map(_.subFolders).flatten)
     val demux = filter(results.map(_.subFolders).flatten)
     val demuxYes = if(ignoreDemux) Nil else demux
     Seq(runFolder) ++ data ++ intensities ++ basecalls ++ results ++ demuxYes
  }

  def logInfo(infoMessage: String) = log.info(folderPath+"\t"+infoMessage)
  
}


