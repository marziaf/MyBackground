package videoTransfer;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Client { // TODO try{}finally{}

	public static void main(String[] args) throws Exception {
		// Connect
		Socket clientSocket = null;
		connect(clientSocket);
		System.out.println("Connection estabilished successfully"); //DEBUG
		// Get files to send
		File backgroundImage = getFile("background");
		File video = getFile("video");
		// Send background image
		send(clientSocket, backgroundImage);

	}

	/**
	 * Try connection to socket until successful
	 * @param clientSocket
	 */
	private static void connect(Socket clientSocket) {
		boolean isValidInput = false;
		while (!isValidInput) {
			try {
				// ask for connection parameters
				String serverAddress = getServerAddress();
				int port = 40000;
				// try connection
				clientSocket = new Socket(serverAddress, port);
				// TODO this doesn't throw any exception if trying to connect to
				// non-connected hosts
				isValidInput = true;
			} catch (Exception e) {
				System.err.println("Invalid input. Connection error");
			}
		}
	}
	
	/**
	 * Ask user for server to connect to
	 * @return ip
	 */
	private static String getServerAddress() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Server address?");
		return sc.next();
	}
	
	/**
	 * Get background or video name and return file
	 * 
	 * @param s - "background" or "video"
	 * @return file
	 */
	private static File getFile(String s) {
		Scanner scanner = new Scanner(System.in);
		boolean gotValidName = false;
		// Ask file name until a valid file is given
		while (!gotValidName) {
			try {
				// Ask user for file name
				if (s.equals("background"))
					System.out.println("Which image do you want to set as new background?");
				else
					System.out.println("Which video do you want to transform?");
				String fileName = scanner.next();
				// Get file and check existence
				File file = new File(fileName);
				gotValidName = file.isFile();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		return null;
	}

	/**
	 * Main sender method
	 * 
	 * @param clientSocket
	 * @param file
	 * @throws IOException
	 */
	private static void send(Socket clientSocket, File file) throws IOException {
		// Create output stream
		PrintStream outToClient = new PrintStream(clientSocket.getOutputStream());
		byte[] buf = new byte[0]; // initialization of data buffer
		convertFile(buf, file);
		// Send file
		sendFile(outToClient, buf);

	}

	/**
	 * 
	 * @param outToSocket - Prinstream to socket
	 * @param buf         - buffer of bytes to send
	 */
	private static void sendFile(PrintStream outToSocket, byte[] buf) {
		// send file
		byte[] fileSize = convertIntToByteArray(buf.length);
		// write to socket bytes from 0 to length
		outToSocket.write(fileSize, 0, fileSize.length);
		// System.out.println("File size sent: " + buf.length); // DEBUG
		outToSocket.write(buf, 0, buf.length);
		System.out.println("File sent"); // DEBUG
		outToSocket.close();
	}

	/**
	 * Convert file into byte buffer
	 * 
	 * @param buf
	 * @param file
	 * @throws IOException
	 */
	private static void convertFile(byte[] buf, File file) throws IOException {
		buf = new byte[(int) file.length()]; // where to put files
		FileInputStream fileInStream = new FileInputStream(file);
		// System.out.println("Ready to send file"); //DEBUG
		// loid up to buf.length bytes into the array
		fileInStream.read(buf); // carica il buffer con i byte del file
		fileInStream.close();
	}

	// TODO ricordare come viene usato
	/**
	 * Convert integer to byte array for data transmission
	 * 
	 * @param val - integer value to convert
	 * @return - byte array
	 */
	private static byte[] convertIntToByteArray(int val) {
		byte[] ret = new byte[4];
		ret[0] = (byte) ((0xFF000000 & val) >> 24);
		ret[1] = (byte) ((0x00FF0000 & val) >> 16);
		ret[2] = (byte) ((0x0000FF00 & val) >> 8);
		ret[3] = (byte) (0x000000FF & val);
		return ret;
	}

}