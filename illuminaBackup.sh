#!/bin/bash
#$ -S /bin/sh
#$ -l vf=5g
#$ -cwd
#$ -q solexa.q
#$ -q nonofficehours.q
#$ -P pipeline

#script calling illuminaBackup.jar for backing up illumina runs

java -Xmx5000m -Xms5000m -jar /projects/solexadst/bin/funcGen/illuminaBackup.jar "$@"
