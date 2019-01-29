package videoTransfer;

import java.net.*;

import java.io.*;

/**
 * A server-side class that keeps the connection alive and interacts with a
 * single client. Created by Server.
 */
public class ServerConnectionInstance implements Runnable {

	private Socket socket;
	private PrintStream toClientStream;
	private BufferedReader fromClientStream;
	MatlabBinderInstance matlabInterface;

	private static String bufferFolderName = "DataFromClient";
	private static File bufferFolder;
	/**
	 * Specifies the algorithm to use for estimating the background 1 -> median on
	 * the frames (default) 2 -> motion blocks and fills
	 */
	private int algorithmToUse = 1;

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
			matlabInterface = new MatlabBinderInstance();
			Thread mIThread = new Thread(matlabInterface);
			mIThread.start();
			matlabInterface.start();
		} catch (IOException e) {
			System.out.print("Failed to create Server connection instance, socket IO problem\n");
		}
	}

	@Override
	public void run() {
		try {
			prepareDir();
			serveRequest();
		} catch (Exception e) {
			System.err.println("Error");
		}

	}

	private static void writeData(byte[] data, String s) throws FileNotFoundException, IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(bufferFolderName + File.separator + s);
		fileOutputStream.write(data);
		fileOutputStream.close();
	}

	// Prepare buffer folder, where to put temporary files
	private static void prepareDir() {
		bufferFolder = new File(bufferFolderName);
		bufferFolder.mkdirs();
	}

	private void serveRequest() throws IOException{
		// first thing: accept incoming background
		getBackground();
		// second thing: accept incoming video
		getVideo();

		// third thing: accept selected algorithm
		getAlgorithm();

		// fourth thing: elaborate
		elaborate();
		// wait for elaboration...
		while (matlabInterface.isComputing()) {
		}

		// fifth thing: send back the video
		sendBackVideo();
	}

	/**
	 * Common interface: the two algorithms choose the video in the fold
	 */
	private void elaborate() {
		while (!matlabInterface.isReady()) {
		}
		// let's estimate the background
		switch (algorithmToUse) {
		case 2:
			matlabInterface.computeCommandAsynchronously(
					"run('" + Server.ScriptsDir + File.separator + "blocks_fills_bg_estimate.m')");
			break;
		case 1:
		default:
			matlabInterface.computeCommandAsynchronously(
					"run('" + Server.ScriptsDir + File.separator + "median_bg_estimate.m')");
		}
	}

	private void getVideo() throws IOException {
		byte[] videoData = ReceiveFileUtils.getDataBytes(socket);
		System.out.println("Got video"); // DEBUG
		writeData(videoData, "video");
	}

	private void getBackground() throws IOException {
		byte[] backgroundData = ReceiveFileUtils.getDataBytes(socket);
		System.out.println("Got background"); // DEBUG
		// write to file
		writeData(backgroundData, "background");
	}

	private void getAlgorithm() {

	}

	private void sendBackVideo() {

	}

}
