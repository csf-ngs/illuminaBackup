#!/bin/sh
name=$(basename $1)
now=$(date +"%Y%m%d")
qsub -N bak.$name.$now  -cwd -wd /groups/vbcf-ngs/tmp/log /groups/vbcf-ngs/bin/funcGen/illuminaBackup.sh -o  /groups/vbcf-ngs/data/SavedRuns  -t no -r $1
