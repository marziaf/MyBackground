package videoTransfer;

import videoTransfer.TransferUtils;
import java.net.Socket;
import java.util.Scanner;
import java.util.PrimitiveIterator.OfDouble;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
/**
 * Client is the class which sends a video and an image (chosen by user) to the server,
 * which elaborates the informations and sends back the result
 */
public class Client {

	// This parameters are used to define files chosen from user
	private static File video; 						// Video to send
	private static File backgroundImage;			// Background to send
	private static File outputVideo; 				// Where the final video is stored
	private static String outputDir = "video_out";	// The name of the final video

	/**
	 * Main class connects to server, interacts with user and sends files
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		Scanner console = new Scanner(System.in);	// Scanner used to interact with user
		// ----------- CONNECT ----------
		Socket clientSocket = connect();			// Establish connection with server
		if (clientSocket.isConnected()) {
			System.out.println("Connection established successfully");
		} else {
			System.out.println("Problem during connection, aborting...");
			return;
		}

		// --------- GET USER PARAMETERS -------
		backgroundImage = getFile("background", "Which image"
				+ " do you want to set as new background?");	// Get the background
		video = getFile("video", "Which video"
				+ " do you want to transform?");				// Get the input video
		System.out.println("Choose the algorithm (1->median-based"
				+ " (default) 2->motion-based): ");				// Get algorithm to use
		int algorithmToUse = (console.nextLine().equals("2")) ? 2 : 1;

		// ------------- SEND FILES --------------
		TransferUtils.send(clientSocket, backgroundImage);		// Send background
		TransferUtils.send(clientSocket, video);				// Send video
		PrintStream printToServer = new PrintStream(			
				clientSocket.getOutputStream());				// Send algorithm
		printToServer.print(algorithmToUse);

		// ------- SERVER-SIDE COMPUTATION -------
		System.out.println("Waiting for eleboration and download of the file...");

		// ------------ RECEIVE VIDEO ------------
		outputVideo = new File(outputDir + File.separator + 
				backgroundImage.getName() + "_" + video.getName());	// Get the output name
		
		byte[] newVideo = TransferUtils.receive(new 
				BufferedInputStream(clientSocket.getInputStream())); // Receive video data
		
		outputVideo.getParentFile().mkdirs();						 // Create output dir
		TransferUtils.writeDataToFile(newVideo, outputVideo.getAbsolutePath()); // Save file
		
		System.out.println("Finished, you'll find the file at: "
				+outputVideo.getAbsolutePath());
		
		clientSocket.close();
	}

	/**
	 * Try to connect to socket until successful
	 * 
	 * @param clientSocket
	 */
	private static Socket connect() {
		
		boolean isValidInput = false;
		Socket clientSocket = null;
		while (!isValidInput) {
			try {
				String serverAddress = getServerAddress();			// Ask for connection parameters
				clientSocket = new Socket(serverAddress, Server.Port); // Try connection
				isValidInput = true;
			} catch (Exception e) {
				System.err.println("Invalid input. Connection error. Retry");
			}
		}
		return clientSocket;
	}

	/**
	 * Ask user for server to connect to
	 * 
	 * @return ip
	 */
	private static String getServerAddress() {
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Server address?");	// Ask server to connect to
		String input = sc.nextLine();
		if (input.equals("l"))					// Shortcut for localhost
			return "127.0.0.1";
		return input;
	}

	/**
	 * Get background or video name and return file
	 * 
	 * @param s - "background" or "video"
	 * @return file
	 */
	private static File getFile(String s, String question) {
		
		Scanner scanner = new Scanner(System.in);
		while (true) {
			try {									// Ask file name until a valid file is given
				System.out.println(question);		
				String fileName = scanner.nextLine();
				File file = new File(fileName);		// Get file and check existence
				if (file.exists()) {
					return file;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Deprecated
	private static void sendAlgorithmChoice(Socket clientSocket, int algorithmToUse) throws IOException {
		byte[] alg = TransferUtils.convertIntToByteArray(algorithmToUse);
		PrintStream toServerStream = new PrintStream(clientSocket.getOutputStream());
		TransferUtils.send(toServerStream, alg);
	}

}