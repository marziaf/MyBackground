package videoTransfer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import videoTransfer.TransferUtils;

public class ReceiveFileUtils {

	public static byte[] receive(BufferedInputStream bufRead) throws IOException {
		// get the size of the message
		byte[] sizeByte = new byte[4];
		TransferUtils.readFromSocket(bufRead, sizeByte, 4);
		int sizeInt = TransferUtils.convertByteArrayToInt(sizeByte);
		// get the file
		byte[] fileContent = new byte[sizeInt];
		TransferUtils.readFromSocket(bufRead, fileContent, sizeInt);
		// System.out.println("file received"); //DEBUG
		return fileContent;
	}

	public static byte[] getDataBytes(Socket socket) throws IOException{
		// Prepare input stream
		BufferedInputStream readFromSocket = new BufferedInputStream(socket.getInputStream());
		BufferedReader bufRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		// receive data
		return ReceiveFileUtils.receive(readFromSocket);
	}

}
