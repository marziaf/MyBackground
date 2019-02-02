#!/bin/bash

SCRIPTNAME=$(basename "$0")
#------------------------------------------------------------------------
# project's directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
source var.sh
#------------------------------------------------------------------------

# get files to be compiled
i=1
if [ ! -n $1 ]
then
	echo "Missing argument.\nUsage: $SCRIPTNAME file1 file2 ..."
fi
# list of paths to files to be compiled
while [ -n "$1" ]; do
	THISFILEPATH=$FILESRC$1".java"
	if [ ! -f $THISFILEPATH ]; then
		echo "$THISFILEPATH doesn't exist"
		exit 1;
	else
		FILEPATH=$FILEPATH" "$THISFILEPATH
	fi
	shift
done

# if bin directory doesn't exist, then create
if [[ ! -d $FILEBIN ]]
then
	mkdir $FILEBIN
fi

javac -d $FILEBIN -classpath $MAT_ROOT/extern/engines/java/jar/engine.jar $FILEPATH

