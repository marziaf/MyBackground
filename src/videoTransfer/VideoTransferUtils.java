package videoTransfer;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * This class is a collection of utilities to read, write and manage 
 * the connections this project needs.
 */
public final class VideoTransferUtils {

	   public static int convertByteArrayToInt(byte[] ba) {
	        return ba[3] & 0xFF | (ba[2] & 0xFF) << 8 | (ba[1] & 0xFF) << 16 |(ba[0] & 0xFF) << 24;
	    }

	   public static void readFromSocket(BufferedInputStream buf, byte[] data, int size) throws IOException {
	        int bytesAlreadyRead = 0;
	        while(bytesAlreadyRead < size) {
	            int bytesRead = buf.read(data, bytesAlreadyRead, size-bytesAlreadyRead);
	            bytesAlreadyRead += bytesRead;
	        }
	    }
	   
       public static byte[] convertIntToByteArray(int val) {
           byte[] ret = new byte[4];
           ret[0]=(byte)((0xFF000000&val)>>24);
           ret[1]=(byte)((0x00FF0000&val)>>16);
           ret[2]=(byte)((0x0000FF00&val)>>8);
           ret[3]=(byte)(0x000000FF&val);
           return ret;
        }
}
