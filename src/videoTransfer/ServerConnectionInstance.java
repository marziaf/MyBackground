package videoTransfer;

import java.net.*;

import java.io.*;
import videoTransfer.ReceiveFileUtils;

/**
 * A server-side class that keeps the connection alive and interacts with a
 * single client. Created by Server.
 */
public class ServerConnectionInstance implements Runnable {

	private PrintStream toClientStream;

	private BufferedReader fromClientStream;

	private Socket socket;

	private static String bufferFolderName = "DataFromClient";
	private static File bufferFolder;

	/**
	 * Constructs a new ServerConnectionInstance, using the given socket to create
	 * the appropriate input and output streams
	 * 
	 * @param newClientSocket the socket on which the connection lives
	 */
	public ServerConnectionInstance(Socket newClientSocket) {
		try {
			socket = newClientSocket;
			toClientStream = new PrintStream(newClientSocket.getOutputStream());
			fromClientStream = new BufferedReader(new InputStreamReader(newClientSocket.getInputStream()));
		} catch (IOException e) {
			System.out.print("Failed to create Server connection instance, socket IO problem\n");
		}
	}

	@Override
	public void run() {
		try {
			prepareDir();
			// get data
			byte[] backgroundData = ReceiveFileUtils.getDataBytes(socket);
			System.out.println("Got background"); //DEBUG
			// write to file
			writeData(backgroundData, "background");
			byte[] videoData = ReceiveFileUtils.getDataBytes(socket);
			System.out.println("Got video"); //DEBUG
			writeData(videoData, "video");
		} catch (IOException e) {
			System.err.println("IO Error");
		}
	}

	private static void writeData(byte[] data, String s) throws FileNotFoundException, IOException{
		FileOutputStream fileOutputStream = new 
				FileOutputStream(bufferFolderName + File.separator + s);
		fileOutputStream.write(data);
		fileOutputStream.close();
	}

	// Prepare buffer folder, where to put temporary files
	private static void prepareDir() {
		bufferFolder = new File(bufferFolderName);
		bufferFolder.mkdirs();
	}

}
