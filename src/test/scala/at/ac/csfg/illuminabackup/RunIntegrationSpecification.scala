package at.ac.csfg.illuminabackup

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import java.io.File
import scala.sys.process.ProcessCreation
import java.util.GregorianCalendar
import java.util.Calendar


@RunWith(classOf[JUnitRunner])
class RunIntegrationSpecification extends Specification { 
      sequential
      
      val dir = "111014_ILLUMINA-96BC32_00042_FC62LYNAAXX"
      val inDir = "testData/"+dir
      val testInput = "testData/testInput" 
      val testInputDir = "testData/testInput"+"/"+dir
      val outPath = new File("testData/testSavedRun").getAbsolutePath()
      val actual = "testData/testOutput.actual.txt"
      val tarFile = "testData/tarfiles"
      val jul = new GregorianCalendar(1789,Calendar.JULY,14).getTime
      var checkSumFailures = Seq[String]()
      val run = new Run(testInputDir, outPath, tarFile){
           override def date = jul
      } 
      
	  step {
        rmPaths()
        import scala.sys.process._
        new File(testInput).mkdirs()
        new File("testData/testSavedRun").mkdirs()
        new File(tarFile).mkdirs()
        stringToProcess("cp -r "+inDir+" "+testInput) ! //because of gzipping

	    success
	  }
  
      "A Run" should {
          "create the empty rsync file" in {
             val rs = new File(run.rsyncFile)
             rs.exists() === false             
             val created = run.createRsyncFile()
             created === true
             rs.exists() === true
          }
          "filled the rsync file" in {
            val filled = run.fillRsyncFile()            
            filled === true
          }
          "have copied everything requested" in {
             val rsynced = run.rsync() 
             rsynced === true
             
             import scala.sys.process._
             stringToProcess("find testData/testSavedRun/"+dir) #> new File(actual) ! 
             
             file2String("testData/testOutput.expected.txt") === file2String(actual)
          }
          "copied files set to read only" in {
             val permissions = getPermission(outPath+"/"+dir)
             for(p <- permissions){
                p must beOneOf( "dr-xr-xr-x" , "-r--r--r--")
             }
             permissions.size === 8
           }
           "create a tar file for each lane" in {
              val outNames = (1 to 8).map(run.outTarLane(_))
              val tarred = run.tar()
              
              checkTars(0, 1, 3)
              checkTars(1, 13, 3)
              checkTars(2, 1, 3)
              checkTars(3, 1, 3)
              checkTars(4, 1, 3)
              checkTars(5, 1, 3)
              checkTars(6, 1, 3)
              checkTars(7, 19, 3)
                            
              def checkTars(laneNr: Int, expectedBam: Int, expectedSequence: Int){
                 val content = listTars(outNames(laneNr))
                 content.filter(_.endsWith("sequence.txt.gz")).size === expectedSequence
                 content.filter(_.endsWith("bam")).size === expectedBam
              }
              tarred === true
           } 
      }
        
     
	  step { 
	    rmPaths()
	    success
	  }    
      
      def getPermission(path: String): Seq[String] = {
          val PermissionPattern = """(\S*)\s*.*""".r
          var permissions = List[String]()
          import scala.sys.process._
          stringToProcess("ls -l "+path) ! ProcessLogger({ str =>
              if(!str.contains("total")){
	              str match {
	                case PermissionPattern(p) => permissions ::= p
	                case _ => ""
	              }
              }
          })
          permissions
      }
	  
      def listTars(path: String): Seq[String] = {
        var list = List[String]()    
        import scala.sys.process._
         stringToProcess("tar --list -f "+path) ! ProcessLogger({ str =>
             list ::= str
         })
         list
      }
     
      def rmPath(path: String) {
         import scala.sys.process._
         stringToProcess("rm -rf "+path) !
      }
      
      def rmPaths(){
          rmPath(outPath)
	      rmPath(testInputDir)
	      rmPath(tarFile)
      }
      
      def file2String(fileName: String) = scala.io.Source.fromFile(fileName).getLines.filter(! _.endsWith(".DS_Store")).toSeq //shitty OSX
      

      
      
      
}
