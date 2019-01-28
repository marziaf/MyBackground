package videoTransfer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 * This class is a collection of utilities to read, write and manage the
 * connections this project needs.
 */
public final class VideoTransferUtils {

	public static int convertByteArrayToInt(byte[] ba) {
		return ba[3] & 0xFF | (ba[2] & 0xFF) << 8 | (ba[1] & 0xFF) << 16 | (ba[0] & 0xFF) << 24;
	}

	public static void readFromSocket(BufferedInputStream buf, byte[] data, int size) throws IOException {
		int bytesAlreadyRead = 0;
		while (bytesAlreadyRead < size) {
			int bytesRead = buf.read(data, bytesAlreadyRead, size - bytesAlreadyRead);
			bytesAlreadyRead += bytesRead;
		}
	}

	public static byte[] convertIntToByteArray(int val) {
		byte[] ret = new byte[4];
		ret[0] = (byte) ((0xFF000000 & val) >> 24);
		ret[1] = (byte) ((0x00FF0000 & val) >> 16);
		ret[2] = (byte) ((0x0000FF00 & val) >> 8);
		ret[3] = (byte) (0x000000FF & val);
		return ret;
	}

	/**
	 * Convert file into byte buffer
	 * 
	 * @param buf
	 * @param file
	 * @throws IOException
	 */
	public static void convertFile(byte[] buf, File file) throws IOException {
		buf = new byte[(int) file.length()]; // where to put files
		FileInputStream fileInStream = new FileInputStream(file);
		// System.out.println("Ready to send file"); //DEBUG
		// loid up to buf.length bytes into the array
		fileInStream.read(buf); // carica il buffer con i byte del file
		fileInStream.close();
	}


	/**
	 * Main sender method
	 * 
	 * @param clientSocket
	 * @param file
	 * @throws IOException
	 */
	public static void send(Socket clientSocket, File file) throws IOException {
		// Create output stream
		PrintStream outToClient = new PrintStream(clientSocket.getOutputStream());
		byte[] buf = new byte[0]; // initialization of data buffer
		VideoTransferUtils.convertFile(buf, file);
		// Send file
		sendFile(outToClient, buf);

	}

	/**
	 * 
	 * @param outToSocket - Printstream to socket
	 * @param buf         - buffer of bytes to send
	 */
	public static void sendFile(PrintStream outToSocket, byte[] buf) {
		// send file
		byte[] fileSize = VideoTransferUtils.convertIntToByteArray(buf.length);
		// write to socket bytes from 0 to length
		outToSocket.write(fileSize, 0, fileSize.length);
		// System.out.println("File size sent: " + buf.length); // DEBUG
		outToSocket.write(buf, 0, buf.length);
		System.out.println("File sent"); // DEBUG
		outToSocket.close();
	}
}
