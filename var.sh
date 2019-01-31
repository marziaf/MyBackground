# source and bin dir
PACKAGE="videoTransfer"
SRC="./src/$PACKAGE/"
BIN="bin/"
# Matlab setup
MATLAB_ROOT=$(which matlab | grep -oe "^.*/R20....")
LD_LIBRARY_PATH=${MATLAB_ROOT}bin/glnxa64:${MATLAB_ROOT}sys/os/glnxa64:$LD_LIBRARY_PATH
#export LD_LIBRARY_PATH
