#!/bin/sh
name=$(basename $1)
now=$(date +"%Y%m%d")
qsub -N bak.$name.$now  -cwd -wd /groups/csf-ngs/tmp/log /projects/solexadst/bin/funcGen/illuminaBackup.sh -o  /groups/csf-ngs/data/SavedRuns  -t no -r $1
