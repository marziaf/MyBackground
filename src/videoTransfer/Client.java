package videoTransfer;

import videoTransfer.TransferUtils;
import java.net.Socket;
import java.util.Scanner;
import java.util.PrimitiveIterator.OfDouble;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.PrintStream;

public class Client { // TODO try{}finally{}

	public static void main(String[] args) throws Exception {
		
		Scanner console = new Scanner(System.in);
		
		// Connect
		Socket clientSocket = connect();
		if (clientSocket.isConnected())
			System.out.println("Connection estabilished successfully"); // DEBUG
		else 
			{System.out.println("Problem during connection, aborting..."); return;}
		
		//get the background
		File backgroundImage = getFile("background", "Which image do you want to set as new background?");		
		
		//get the input video
		File video = getFile("video","Which video do you want to transform?");
		
		//get the algorithm
		System.out.println("Choose the algorithm (1->median-based, default, 2->motion-based): ");
		int algorithmToUse = (console.next().equals("2")) ? 2 : 1;
		
		// Send background
		TransferUtils.send(clientSocket, backgroundImage);
		// Send video
		TransferUtils.send(clientSocket, video);
		// send algorithm to use
		byte[] alg = TransferUtils.convertIntToByteArray(algorithmToUse);
		PrintStream toServerStream = new PrintStream(clientSocket.getOutputStream());
		TransferUtils.send(toServerStream, alg);
		
		//server-side computation...
		
		//receive the new video
		byte[] newVideo = TransferUtils.receive(new BufferedInputStream(clientSocket.getInputStream()));
		//write it to file
		System.out.println("Where do you want to save the new video?");
		String newVideoPath = console.nextLine(); //check for invalid input
		File newVideoFile = new File(newVideoPath);
		newVideoFile.mkdirs();
		TransferUtils.writeDataToFile(newVideo, newVideoPath);
		
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
		if (input.equals("l")) return "127.0.0.1";
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

				String fileName = scanner.next();
				// Get file and check existence
				File file = new File(fileName);
				System.out.println(file.getAbsolutePath());
				if (file.exists()) {
					//scanner.close();
					return file;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			} 
		}
	}

}