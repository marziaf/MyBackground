package videoTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import videoTransfer.TransferUtils;

public final class SendFileUtils {
	/**
	 * Convert file into byte buffer
	 * 
	 * @param buf
	 * @param file
	 * @throws IOException
	 */
	public static byte[] convertFile(File file) throws IOException {
		byte[] buf = new byte[(int) file.length()]; // where to put files
		FileInputStream fileInStream = new FileInputStream(file);
		// load up to buf.length bytes into the array
		fileInStream.read(buf); // carica il buffer con i byte del file
		fileInStream.close();
		return buf;
	}

	/**
	 * Main file sender method
	 * 
	 * @param clientSocket
	 * @param file
	 * @throws IOException
	 */
	public static void send(Socket clientSocket, File file) throws IOException {
		// Create output stream
		PrintStream outToClient = new PrintStream(clientSocket.getOutputStream());
		byte[] buf = convertFile(file); //TODO check
		// Send file
		sendFile(outToClient, buf);
	}

	/**
	 * 
	 * @param outToSocket - PrintStream to socket
	 * @param buf         - buffer of bytes to send
	 */
	public static void sendFile(PrintStream outToSocket, byte[] buf) {
		// send file
		byte[] fileSize = TransferUtils.convertIntToByteArray(buf.length);
		// write to socket bytes from 0 to length
		outToSocket.write(fileSize, 0, fileSize.length);
		outToSocket.write(buf, 0, buf.length);
		outToSocket.close();
	}
}
