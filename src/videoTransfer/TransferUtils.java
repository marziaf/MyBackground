package videoTransfer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * This class is a collection of utilities to read, write and manage the
 * connections this project needs.
 */
public final class TransferUtils {

	/**
	 * Convert a byte array to an int
	 * @param ba - byte array
	 * @return
	 */
	public static int convertByteArrayToInt(byte[] ba) {
		return ba[3] & 0xFF | (ba[2] & 0xFF) << 8 | (ba[1] & 0xFF) << 16 | (ba[0] & 0xFF) << 24;
	}

	/**
	 * Read "size" bytes from the "buf" stream, buffering until completion
	 * @param buf - buffered input stream to read from
	 * @param data - bytes array where to save data
	 * @param size - size of data expected
	 * @throws IOException if the stream fails
	 */
	public static void readFromSocket(BufferedInputStream buf, byte[] data, int size) throws IOException {
		int bytesAlreadyRead = 0;
		while (bytesAlreadyRead < size) {
			int bytesRead = buf.read(data, bytesAlreadyRead, size - bytesAlreadyRead);
			bytesAlreadyRead += bytesRead;
		}
	}

	/**
	 * Convert an int to an array of bytes
	 * @param val - int to convert
	 * @return the converted value as byte array
	 */
	public static byte[] convertIntToByteArray(int val) {
		byte[] ret = new byte[4];
		ret[0] = (byte) ((0xFF000000 & val) >> 24);
		ret[1] = (byte) ((0x00FF0000 & val) >> 16);
		ret[2] = (byte) ((0x0000FF00 & val) >> 8);
		ret[3] = (byte) (0x000000FF & val);
		return ret;
	}

	/**
	 * Receive data from the stream "bufRead". This method is expected to be use with
	 * "send" as it automatically firstly expects an integer representing the size of 
	 * the data array, then receives it using a buffered reader
	 * @param bufRead the input stream
	 * @return data received
	 * @throws IOException if the stream fails
	 */
	public static byte[] receive(BufferedInputStream bufRead) throws IOException {
		byte[] sizeByte = new byte[4]; 				// Where to get the size of the message
		readFromSocket(bufRead, sizeByte, 4);		// Read size
		int sizeInt = convertByteArrayToInt(sizeByte); // Convert size
		byte[] content = new byte[sizeInt];			// get the file
		readFromSocket(bufRead, content, sizeInt);
		return content;
	}

	/**
	 * High-level method to receive bytes as input from the specified socket. It calls 
	 * the receive method which automatically expects the right size->bytes format from the sender
	 * @param socket the socket on which listen for input
	 * @return the final byte array expected
	 * @throws IOException if the stream fails
	 */
	public static byte[] getDataBytes(Socket socket) throws IOException {
		// Prepare input stream
		BufferedInputStream readFromSocket = new BufferedInputStream(socket.getInputStream());
		// receive data
		return receive(readFromSocket);
	}

	/**
	 * Utility that converts a file into a byte array.
	 * @param file the file to convert
	 * @return a byte array representation of this file
	 * @throws IOException on any reading operation fail
	 */
	public static byte[] fileToBytesArray(File file) throws IOException {
		byte[] buf = new byte[(int) file.length()]; // where to put files
		FileInputStream fileInStream = new FileInputStream(file);
		// load up to buf.length bytes into the array
		fileInStream.read(buf);
		fileInStream.close();
		return buf;
	}

	/**
	 * High-level method to send files as output from the specified socket. It calls 
	 * the send method which automatically organizes the "send size"->"send data" format
	 * the "receive" method expects
	 * 
	 * @param clientSocket the socket on which to send the output stream of data
	 * @param file the file to send through the byte pipe
	 * @throws IOException if the stream fails
	 */
	public static void send(Socket clientSocket, File file) throws IOException {
		// Create output stream
		PrintStream outToClient = new PrintStream(clientSocket.getOutputStream());
		byte[] buf = fileToBytesArray(file);
		// Send file
		send(outToClient, buf);
	}

	/**
	 * Method which sends a byte array on the ouput stream "outToSocket". The method firstly
	 * sends the size of the data, then the actual byte array. To use in junction with "receive"
	 * 
	 * @param outToSocket the ouput PrintStream
	 * @param buf bytes to send
	 */
	public static void send(PrintStream outToSocket, byte[] buf) {
		// send file
		byte[] fileSize = convertIntToByteArray(buf.length);
		// write to socket bytes from 0 to length
		outToSocket.write(fileSize, 0, fileSize.length);
		outToSocket.write(buf, 0, buf.length);
	}

	/**
	 * This method writes byte array data to a particular file
	 * 
	 * @param data the data bytes to be written
	 * @param filename the pathname of the file
	 * @throws FileNotFoundException if file does not exist
	 * @throws IOException if not allowed
	 */
	public static void writeDataToFile(byte[] data, String filename) throws FileNotFoundException, IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(filename);
		fileOutputStream.write(data);
		fileOutputStream.close();
	}

	/**
	 * Method for rapid file-filesystem compatibility, can be used to check wheter a
	 * particular path is a path that the operative system can accept
	 * @param path the path to verify
	 * @return true if the OS would accept it as valid, false otherwise
	 */
	public static boolean isValidFileInput(String path) {
		try {
			path = (new File(path)).getCanonicalPath();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
