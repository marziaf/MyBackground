#!/bin/bash

SCRIPTNAME=$(basename "$0")
#------------------------------------------------------------------------
# project's directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
# source and bin dir
SRC="./src/videoTransfer/"
BIN="bin/"
# Matlab setup
MATLAB_ROOT=$(which matlab | grep -oe "^.*/R20....")
LD_LIBRARY_PATH=$MATLAB_ROOT/bin/glnxa64:$MATLAB_ROOT/sys/os/glnxa64:$LD_LIBRARY_PATH
export LD_LIBRARY_PATH
#------------------------------------------------------------------------

# get files to be compiled
i=1
if [ ! -n $1 ]
then
	echo "Missing argument.\nUsage: $SCRIPTNAME file1 file2 ..."
fi
# list of paths to files to be compiled
while [ -n "$1" ]; do
	THISFILEPATH=$SRC$1".java"
	if [ ! -f $THISFILEPATH ]; then
		echo "$THISFILEPATH doesn't exist"
		exit 1;
	else
		FILEPATH=$FILEPATH" "$THISFILEPATH
	fi
	shift
done

# if bin directory doesn't exist, then create
if [[ ! -d $BIN ]]
then
	mkdir $BIN
fi

javac -d $BIN -classpath $MATLAB_ROOT/extern/engines/java/jar/engine.jar $FILEPATH

#NOTE: per poi eseguire si dovrà:
#java -classpath .:$MATLAB_ROOT/extern/engines/java/jar/engine.jar $FILE
#ricordare di usare il nome senza estensione, qui

