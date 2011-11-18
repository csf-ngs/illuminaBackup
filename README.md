illuminabackup
=============

sbt
assembly

java -jar target/illuminaBackup.jar -r /path/to/run -t /path/to/tar/folder -o /path/to/savedRuns

copies selected files + folders into destination directory with rsync
creates a per lane tarfile
