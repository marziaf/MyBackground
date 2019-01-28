package videoTransfer;

import java.io.BufferedInputStream;
import java.io.IOException;
import videoTransfer.VideoTransferUtils;

public class ReceiveFileUtils {
	
	public static byte[] receive(BufferedInputStream bufRead) throws IOException{
		//get the size of the message
		byte[] sizeByte = new byte[4];
		VideoTransferUtils.readFromSocket(bufRead, sizeByte, 4);
		int sizeInt = VideoTransferUtils.convertByteArrayToInt(sizeByte);
		System.out.println("File size known: " + sizeInt); //DEBUG
		//get the file
		byte[] fileContent = new byte[sizeInt];
		VideoTransferUtils.readFromSocket(bufRead, fileContent, sizeInt);
		//System.out.println("file received"); //DEBUG
		return fileContent;
	}
	
}
