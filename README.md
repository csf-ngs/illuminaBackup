illuminabackup
==============

copies selected files and folders into destination directory using rsync.
tars selected files and folders into lane specific tar files.

A file called excludeBackup in any of the folders excludes the folder + its subfolders from being backed up.

Its a simple filter for rsync with many tests ensuring that only the intended
files get copied + checksummed.


* usage:
   
    >git clone 
    >cd illuminabackup
    >sbt
    >assembly
    >java -jar target/illuminaBackup.jar -r /path/to/run -t /path/to/tar/folder -o /path/to/savedRuns

* known bugs:
the tar file unfortunately begins one level to low. When untaring the user must untar
in a newly created folder.

* todo:
??


* technology:
scala, sbt, rsync, tar, jcommander, slf4j




