# source and bin dir
PACKAGE="videoTransfer"
FILESRC="./src/$PACKAGE/"
FILEBIN="bin/"
# Matlab setup
MAT_ROOT=$(which matlab | grep -oe "^.*/R20....")
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:${MAT_ROOT}bin/glnxa64:${MAT_ROOT}sys/os/glnxa64
export LD_LIBRARY_PATH
