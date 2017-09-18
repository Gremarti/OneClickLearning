#!/bin/bash

pwd=`pwd`

cd "./ressources/"
rm -v *.att *.tmp *.*dat

cd "../log"
rm -v *.csv *.txt *.log

cd ".."
rmdir log

cd $pwd
