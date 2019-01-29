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
	public static void convertFile(byte[] buf, File file) throws IOException {
		buf = new byte[(int) file.length()]; // where to put files
		FileInputStream fileInStream = new FileInputStream(file);
		// System.out.println("Ready to send file"); //DEBUG
		// loid up to buf.length bytes into the array
		fileInStream.read(buf); // carica il buffer con i byte del file
		fileInStream.close();
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
		byte[] buf = new byte[0]; // initialization of data buffer
		convertFile(buf, file); //TODO check
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
		// System.out.println("File size sent: " + buf.length); // DEBUG
		outToSocket.write(buf, 0, buf.length);
		System.out.println("File sent"); // DEBUG
		outToSocket.close();
	}
}
