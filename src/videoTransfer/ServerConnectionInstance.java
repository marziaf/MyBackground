package videoTransfer;

import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.io.*;

/**
 * A server-side class that keeps the connection alive and interacts with a
 * single client. Created by Server.
 */
public class ServerConnectionInstance implements Runnable {

	private PrintStream toClientStream;

	private BufferedInputStream fromClientStream;

	private Socket socket;

	private MatlabBinderInstance matlabInterface;

	private int instanceNumber;

	private static String baseVideoInName = "video";
	private static String baseBackgroundInName = "image";
	private static String baseVideoOutName = "video_out";
	
	private  File finalPathVideoIn;
	private  File finalPathBackground;
	private  File finalPathVideoOut;

	
	/**
	 * Constructs a new ServerConnectionInstance, using the given socket to create
	 * the appropriate input and output streams
	 * 
	 * @param newClientSocket the socket on which the connection lives
	 * @param instanceNum     the number associated with this instance, which marks
	 *                        the files that belong to this particular instance
	 */
	public ServerConnectionInstance(Socket newClientSocket, int instanceNum) {
		try {
			// Set connection-related stuff
			instanceNumber = instanceNum;
			socket = newClientSocket;
			toClientStream = new PrintStream(newClientSocket.getOutputStream());
			fromClientStream = new BufferedInputStream(newClientSocket.getInputStream());
			// Matlab engine //TODO check
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
		// TODO: implement busy-waiting serve
		setFilePaths();
		try {
			serveRequest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void serveRequest() throws IOException {
		// first thing: accept incoming background
		getBackground();
		// second thing: accept incoming video
		getVideo();
		// third thing: accept selected algorithm
		int algorithmToUse = getAlgorithm();
		// fourth thing: elaborate
		elaborate(algorithmToUse);
		// wait for elaboration...
		// TODO send client the progress bar
		//System.out.println("Got what I needed, now I should be doing matlab stuff"); //DEBUG
		// TODO DEBUG
		while (matlabInterface.isComputing()) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
			}
			System.out.println("Still computing..."); //TODO qui si blocca -> check isComputing
			//TODO possible solution: wait until file exists (and some more seconds until it's finished),
			//but files have to be removed every time...
		}

		// fifth thing: send back the video
		System.out.println("sending back video client "+instanceNumber);
		sendBackVideo();

		// to be left open if we want it to be left alive for another computation
		socket.close();
	}

	/**
	 * Common interface: the two algorithms choose the video in the fold
	 */
	private void elaborate(int algorithmToUse) {
		//System.out.println("Inside algorithm to use"); // DEBUG
		Instant t1 = Instant.now();
		Instant t2 = Instant.now();
		while (!matlabInterface.isReady() || Duration.between(t1, t2).toMillis() > 10000) {
			t2 = Instant.now();
		}
		//System.out.println("Ready to set workspace"); // DEBUG

		String finalPathScript = Server.scriptsDir + File.separator +
			((algorithmToUse==2) ? "blocks_fills_bg_substitute.m" : "median_bg_substitute.m");
		if (Paths.get("").toAbsolutePath().getParent().toString().equals("bin"))
			finalPathScript = ".." + File.separator + finalPathScript;
		// set matlab workspace with the files we want to work on
		matlabInterface.computeCommandAsynchronously(
				"video = '" + finalPathVideoIn + "';" + 
				"newBackground = '" + finalPathBackground+ "';" + 
				"video_out = '"	+ finalPathVideoOut + "';");

		while (matlabInterface.isComputing()) {} // shouldn't take long

		//System.out.println("Ready to calculate background image"); // DEBUG
		
		// let's do the actual calculations
		matlabInterface.computeCommandAsynchronously(
				"run('" + finalPathScript +"');");
	}

	private void getVideo() throws IOException {
		byte[] videoData = TransferUtils.getDataBytes(socket);
		// write to file
		TransferUtils.writeDataToFile(videoData, finalPathVideoIn.getAbsolutePath());
	}

	private void getBackground() throws IOException {
		byte[] backgroundData = TransferUtils.getDataBytes(socket);
		// write to file
		TransferUtils.writeDataToFile(backgroundData, finalPathBackground.getAbsolutePath());
	}

	private int getAlgorithm() throws IOException {
		byte[] algData = new byte[4];
		TransferUtils.readFromSocket(fromClientStream, algData, 4);
		return TransferUtils.convertByteArrayToInt(algData);
	}

	private void sendBackVideo() throws IOException {
		TransferUtils.send(socket,
				new File(finalPathVideoOut +".avi")); 
		//MATLAB seems to automatically put an avi-extension to the file name 
	}
	
	private void setFilePaths() {
		finalPathBackground = new File(Server.backInDir.getAbsolutePath()+File.separator+baseBackgroundInName+instanceNumber);
		finalPathVideoIn = new File(Server.videoInDir.getAbsolutePath()+File.separator+baseVideoInName+instanceNumber);
		finalPathVideoOut = new File(Server.videoOutDir.getAbsolutePath()+File.separator+baseVideoOutName+instanceNumber);
	}

}
