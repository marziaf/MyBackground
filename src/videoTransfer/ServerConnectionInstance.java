package videoTransfer;

import java.net.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.io.*;

/**
 * A server-side class that keeps the connection alive and interacts with a
 * single client. Created by Server.
 */
public class ServerConnectionInstance implements Runnable {

	// Streams towards client
	private PrintStream toClientStream;
	private BufferedInputStream fromClientStream;
	// Socket
	private Socket socket;
	// Interface for matlab computations
	private MatlabBinderInstance matlabInterface;
	// Keeps track of client id
	private int instanceNumber;
	// Base names of files, to concatenate to client id
	private static String baseVideoInName = "video";
	private static String baseBackgroundInName = "image";
	private static String baseVideoOutName = "video_out";
	// Files to write
	private File finalPathVideoIn;
	private File finalPathBackground;
	private File finalPathVideoOut;

	/**
	 * Constructs a new ServerConnectionInstance, using the given socket to create
	 * the appropriate input and output streams. Starts matlab interface.
	 * 
	 * @param newClientSocket the socket on which the connection lives
	 * @param instanceNum     the number associated with this instance, which marks
	 *                        the files that belong to this particular instance
	 */
	public ServerConnectionInstance(Socket newClientSocket, int instanceNum) {
		
		try {
			instanceNumber = instanceNum;					// Sets client id
			socket = newClientSocket;						// Get socket
															// IO streams
			toClientStream = new PrintStream(newClientSocket.getOutputStream());
			fromClientStream = new BufferedInputStream(newClientSocket.getInputStream());
															// Start matlab interface
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
		setFilePaths();				// Prepare files where to write
		try {
			serveRequest();			// Get files and start matlab computation
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets data from client, elaborates video and
	 * sends it back.
	 * @throws IOException
	 */
	private void serveRequest() throws IOException {
		// ----------- GET DATA --------------
		getBackground();			// Receive and save background
		getVideo();					// Receive and save video
		BufferedReader readAlg = new BufferedReader(new 
				InputStreamReader(socket.getInputStream())); 
		int algorithmToUse = readAlg.read()%48;		// Get algorithm
		
		// ---------- ELABORATE --------------
		elaborate(algorithmToUse);	// Start elaboration

		while (matlabInterface.isComputing()) {	// Give feedback to
			try {								// Server while computing
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("Still computing...");
		}
		// ------- SEND RESULT TO CLIENT ------
		System.out.println("Sending back video to client " + instanceNumber);
		sendBackVideo();						
		socket.close();
		System.out.println("Closed connection with client "+instanceNumber);
	}

	/**
	 * Common interface: the two algorithms choose the video in the fold
	 * @param algorithmToUse - 1 for median, 2 for motion
	 */
	private void elaborate(int algorithmToUse) {
		
		// ---------- WAIT UNTIL ENGINE READY --------
		Instant t1 = Instant.now();
		Instant t2 = Instant.now();		// Start anyway after 10 seconds (to avoid bugs)
		while (!matlabInterface.isReady() || Duration.between(t1, t2).toMillis() > 10000) {
			t2 = Instant.now();
		}
		// --------- GET MATLAB CODE PATH ------------
		String finalPathScript = Server.scriptsDir + File.separator
				+ ((algorithmToUse == 2) ? "blocks_fills_bg_substitute.m" : "median_bg_substitute.m");
		if (Paths.get("").toAbsolutePath().getParent().toString().equals("bin"))
			finalPathScript = ".." + File.separator + finalPathScript;
		// --------- SET MATLAB WORKSPACE ------------
		matlabInterface.computeCommandAsynchronously("video = '" + finalPathVideoIn + "';" + "newBackground = '"
				+ finalPathBackground + "';" + "video_out = '" + finalPathVideoOut + "';");
		while (matlabInterface.isComputing()) {}
		// ------------- START SCRIPTS --------------
		matlabInterface.computeCommandAsynchronously("run('" + finalPathScript + "');");
	}

	/**
	 * Receives video from client and saves it
	 * @throws IOException
	 */
	private void getVideo() throws IOException {
		byte[] videoData = TransferUtils.getDataBytes(socket);
		// write to file
		TransferUtils.writeDataToFile(videoData, finalPathVideoIn.getAbsolutePath());
	}

	/**
	 * Receives background from client and saves it
	 * @throws IOException
	 */
	private void getBackground() throws IOException {
		byte[] backgroundData = TransferUtils.getDataBytes(socket);
		// write to file
		TransferUtils.writeDataToFile(backgroundData, finalPathBackground.getAbsolutePath());
	}

	@Deprecated
	private int getAlgorithm() throws IOException {
		byte[] algData = new byte[4];
		TransferUtils.readFromSocket(fromClientStream, algData, 4);
		return TransferUtils.convertByteArrayToInt(algData);
	}

	/**
	 * Sends elaborated video back to client
	 * @throws IOException
	 */
	private void sendBackVideo() throws IOException {
		TransferUtils.send(socket, finalPathVideoOut); 
		// on windows, matlab automatically puts an avi-extension to the file, if it is missing
	}
	
	/**
	 * Prepare the various paths that are needed for the scripts, based on the ones provided 
	 */
	private void setFilePaths() {
		finalPathBackground = new File(
				Server.backInDir.getAbsolutePath() + File.separator + baseBackgroundInName + instanceNumber);
		finalPathVideoIn = new File(
				Server.videoInDir.getAbsolutePath() + File.separator + baseVideoInName + instanceNumber);
		finalPathVideoOut = new File(
				Server.videoOutDir.getAbsolutePath() + File.separator + baseVideoOutName + instanceNumber + ".avi");
	}

}
