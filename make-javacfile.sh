#!/bin/bash

SCRIPTNAME=$(basename "$0")
# project's directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
# source and bin dir
SRC="./src/videoTransfer/"
BIN="bin/"

# get file to be compiled
if [ -n $1 ]
then
	FILE=$1
else
	echo "Missing argument.\nUsage: $SCRIPTNAME file.java"
	exit 1
fi

FILEPATH="$SRC$FILE"
pwd #DEBUG
echo $FILEPATH #DEBUG

# check existence of file
# TODO file validity (format) non checked
if [ ! -f $FILEPATH ]
then
	echo "Non existing file"
	exit 1
fi
if [[ ! -d ./bin ]]
then
	mkdir ./bin
fi

javac -d $BIN $FILEPATH


