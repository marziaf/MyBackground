package videoTransfer;

import videoTransfer.TransferUtils;
import java.net.Socket;
import java.util.Scanner;
import java.util.PrimitiveIterator.OfDouble;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class Client {

	private static File video; // video to send
	private static File backgroundImage; // background to send
	private static File outputVideo;
	private static String outputDir = "video_out";

	public static void main(String[] args) throws Exception {

		Scanner console = new Scanner(System.in);

		// Connect
		Socket clientSocket = connect();
		if (clientSocket.isConnected())
			System.out.println("Connection estabilished successfully"); // DEBUG
		else {
			System.out.println("Problem during connection, aborting...");
			return;
		}

		// USER PARAMETERS
		// get the background
		backgroundImage = getFile("background", "Which image do you want to set as new background?");
		// get the input video
		video = getFile("video", "Which video do you want to transform?");
		// get the algorithm
		System.out.println("Choose the algorithm (1->median-based (default) 2->motion-based): ");
		int algorithmToUse = (console.nextLine().equals("2")) ? 2 : 1;

		// Send background
		TransferUtils.send(clientSocket, backgroundImage);
		// Send video
		TransferUtils.send(clientSocket, video);
		// send algorithm to use
		sendAlgorithmChoice(clientSocket, algorithmToUse);

		// server-side computation...
		System.out.println("Waiting for eleboration and download of the file...");

		// get the output name
		outputVideo = new File(outputDir + File.separator + video.getName() + "_" + backgroundImage.getName());
		// get elaborated video
		byte[] newVideo = TransferUtils.receive(new BufferedInputStream(clientSocket.getInputStream()));
		// write received video to file
		outputVideo.getParentFile().mkdirs();
		TransferUtils.writeDataToFile(newVideo, outputVideo.getAbsolutePath());

		clientSocket.close();
	}

	/**
	 * Try connection to socket until successful
	 * 
	 * @param clientSocket
	 */
	private static Socket connect() {
		boolean isValidInput = false;
		Socket clientSocket = null;
		while (!isValidInput) {
			try {
				// ask for connection parameters
				String serverAddress = getServerAddress();
				// try connection
				clientSocket = new Socket(serverAddress, Server.Port);
				// TODO this doesn't throw any exception if trying to connect to
				// non-connected hosts
				isValidInput = true;

			} catch (Exception e) {
				System.err.println("Invalid input. Connection error");
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
		System.out.println("Server address?");
		String input = sc.next();
		if (input.equals("l"))
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
		// Ask file name until a valid file is given
		while (true) {
			try {
				System.out.println(question);

				String fileName = scanner.next(); // should use nextLine, paths can have spaces
				// Get file and check existence
				File file = new File(fileName);
				if (file.exists()) {
					// scanner.close();
					return file;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void sendAlgorithmChoice(Socket clientSocket, int algorithmToUse) throws IOException {
		byte[] alg = TransferUtils.convertIntToByteArray(algorithmToUse);
		PrintStream toServerStream = new PrintStream(clientSocket.getOutputStream());
		TransferUtils.send(toServerStream, alg);
	}

}