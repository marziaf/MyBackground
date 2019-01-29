package videoTransfer;

import videoTransfer.SendFileUtils;
import java.net.Socket;
import java.util.Scanner;
import java.io.File;

public class Client { // TODO try{}finally{}

	public static void main(String[] args) throws Exception {
		// Connect
		Socket clientSocket = connect();
		System.out.println("Connection estabilished successfully"); // DEBUG
		// Get files to send
		File backgroundImage = getFile("background");
		File video = getFile("video");
		// Send background image
		SendFileUtils.send(clientSocket, backgroundImage);
		SendFileUtils.send(clientSocket, video);

	}

	/**
	 * Try connection to socket until successful
	 * 
	 * @param clientSocket
	 */
	private static Socket connect() {
		boolean isValidInput = false;
		while (!isValidInput) {
			try {
				// ask for connection parameters
				String serverAddress = getServerAddress();
				// try connection
				Socket clientSocket = new Socket(serverAddress, Server.Port);
				// TODO this doesn't throw any exception if trying to connect to
				// non-connected hosts
				isValidInput = true;
				return clientSocket;
			} catch (Exception e) {
				System.err.println("Invalid input. Connection error");
			}
		}
		return null;
	}

	/**
	 * Ask user for server to connect to
	 * 
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
	 */ //CHECK FOR INCOMPATIBLE TIPES! ONLY PNG AND AVI FORMATS
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
				return file;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
		return null;
	}

}
