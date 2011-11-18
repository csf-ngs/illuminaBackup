#illuminaBackup

copies selected files and folders into destination directory using rsync.
tars selected files and folders into lane specific tar files.

A file called excludeBackup in any of the folders excludes the folder + its subfolders from being backed up.

Its a simple filter for rsync with many integration tests ensuring that only the intended
files get copied + checksummed.


* ####usage:
```bash
>git clone git@github.com:csf-ngs/illuminaBackup.git 
>cd illuminaBackup
>sbt
>assembly
>java -jar target/illuminaBackup.jar -r /path/to/run -t /path/to/tar/folder -o /path/to/savedRuns
```
 
* ####known bugs:
the tar file unfortunately begins one level to low. When untaring the user must untar
in a newly created folder.

* ####todo:
learn sbt release plugin and do a proper release

* ####technology:
scala, sbt, rsync, tar, jcommander, slf4j




