#!/bin/bash
#$ -S /bin/sh
#$ -l vf=5g
#$ -cwd
#$ -q solexa.q
#$ -q nonofficehours.q
#$ -P pipeline

#script calling illuminaBackup.jar for backing up illumina runs
#demux files are now not backed up
java -Xmx2000m -Xms2000m -jar /projects/solexadst/bin/funcGen/illuminaBackup.jar "$@"

