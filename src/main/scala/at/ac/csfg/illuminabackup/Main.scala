package at.ac.csfg.illuminabackup

import java.io.File
import com.beust.jcommander.{JCommander, Parameter}
import collection.JavaConversions._
import org.slf4j.LoggerFactory
 
object Main {
  private val log = LoggerFactory.getLogger(getClass)
  
  object Args {
    
    @Parameter(names = Array("-r", "--runFolder"), description = "runfolder path")
    var runFolderPath: String = ""
    
    @Parameter(names = Array("-o", "--outCopyFolder"), description = "copy folder path")
    var outCopyFolder: String = ""
    
    @Parameter(names = Array("-t", "--outTarFolder"), description = "tar folder path")
    var outTarFolder: String = ""

  }
 
  def main(args: Array[String]): Unit = {
    new JCommander(Args, args.toArray: _*)
    val missing = checkFolders()
    if(missing.size > 0){
       val err = "missing folders: "+missing.mkString("\t")
       log.error(err)
       sys.error(err)
    }
    val run = new Run(Args.runFolderPath, Args.outCopyFolder, Args.outTarFolder)
    val success = run.save()
    log.info("done backup "+success)
    
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