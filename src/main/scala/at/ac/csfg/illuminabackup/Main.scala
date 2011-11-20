package at.ac.csfg.illuminabackup

import java.io.File
import com.beust.jcommander.{JCommander, Parameter}
import collection.JavaConversions._
import org.slf4j.LoggerFactory
import com.beust.jcommander.JCommanderFactory
 
object Main {
  private val log = LoggerFactory.getLogger(getClass)
  
  object Args {
    
    @Parameter(names = Array("-r", "--runFolder"), description = "runfolder path", required=true)
    var runFolderPath: String = ""
    
    @Parameter(names = Array("-o", "--outCopyFolder"), description = "copy folder path", required=true)
    var outCopyFolder: String = ""
    
    @Parameter(names = Array("-t", "--outTarFolder"), description = "tar folder path", required=true)
    var outTarFolder: String = ""

  }
 
  def main(args: Array[String]): Unit = {
    val jc = JCommanderFactory.createWithArgs(Args)
    try{
      jc.parse(args.toArray:_*)
      val missing = checkFolders()
      if(missing.size > 0){
        val err = "missing folders: "+missing.mkString("\t")
        log.error(err)
        System.err.println(err)
        sys.exit(1)
      }
      val run = new Run(Args.runFolderPath, Args.outCopyFolder, Args.outTarFolder)
      val success = run.save()
      val (successString, exitCode) = if(success) ("success", 0) else ("failed", 1)
      if(!success){
         System.err.println("failed backup "+Args.runFolderPath)
      }
      log.info(Args.runFolderPath+" done backup "+successString)   
      sys.exit(exitCode)
    }catch{
      case e: Exception => {
         jc.usage()
      }
    }
    
  }
  
  def checkFolders(): Seq[String] = {
      var missingFiles = List[String]()
      if(!new File(Args.runFolderPath).exists){
         missingFiles ::= "runFolder:"+Args.runFolderPath
      }
      if(!new File(Args.outCopyFolder).exists){
        missingFiles ::= "outCopyFolder: "+Args.outCopyFolder
      }
      if(!new File(Args.outTarFolder).exists){
        missingFiles ::= "outTarFolder: "+Args.outTarFolder
      }
      missingFiles
  }
  
}