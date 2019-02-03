package videoTransfer;

import java.net.*;
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
	
	private String baseVideoInName = "video";
	private String baseBackgroundInName = "image";
	private String baseVideoOutName = "video_out";

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
		//TODO send client the progress bar
		System.out.println("Got what I needed, now I should be doing matlab stuff"); //DEBUG
		while (matlabInterface.isComputing()) { try {Thread.sleep(10000);} catch
		(InterruptedException e) {} System.out.println("Still computing..."); }

		// fifth thing: send back the video
		sendBackVideo();

		// to be left open if we want it to be left alive for another computation
		socket.close();
	}


	/**
	 * Common interface: the two algorithms choose the video in the fold
	 */
	private void elaborate(int algorithmToUse) {
		System.out.println("Inside algorithm to use"); //DEBUG
		//TODO wtf?
		Instant t1 = Instant.now();
		Instant t2 = Instant.now();
		while(!matlabInterface.isReady() || Duration.between(t1, t2).toMillis() > 10000) {
			t2 = Instant.now();
		}
		/*try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println("Ready to set workspace"); //DEBUG

		// set matlab workspace with the files we want to work on
		matlabInterface.computeCommandAsynchronously("video = '" + Server.VideoInDir.getAbsolutePath() + File.separator
				+ baseVideoInName + instanceNumber + "';" 
				+ "newBackground = '" + Server.BackgroundDir.getAbsolutePath()
				+ File.separator + baseBackgroundInName + instanceNumber + "';" 
				+ "video_out = '" + Server.VideoOutDir.getAbsolutePath() + File.separator + baseVideoOutName + instanceNumber + "';");
		
		while (matlabInterface.isComputing()) {
		} // shouldn't take long

		System.out.println("Ready to calculate background image"); //DEBUG

		// let's estimate the background
		switch (algorithmToUse) {
		case 2:
			matlabInterface.computeCommandAsynchronously(
					"run('" + Server.ScriptsDir + File.separator + "blocks_fills_bg_substitute.m')");
			break;
		case 1:
		default:
			matlabInterface.computeCommandAsynchronously(
					"run('" + Server.ScriptsDir + File.separator + "median_bg_substitute.m')"); 
			//TODO set definitive parameters for median algorithm
		}
	}

	private void getVideo() throws IOException {
		byte[] videoData = TransferUtils.getDataBytes(socket);
		// write to file
		TransferUtils.writeDataToFile(videoData, 
				Server.VideoInDir + File.separator + baseVideoInName + instanceNumber);
	}

	private void getBackground() throws IOException {
		byte[] backgroundData = TransferUtils.getDataBytes(socket);
		// write to file
		TransferUtils.writeDataToFile(backgroundData,
				Server.BackgroundDir + File.separator + baseBackgroundInName + instanceNumber);
	}

	private int getAlgorithm() throws IOException {
		byte[] algData = new byte[4];
		TransferUtils.readFromSocket(fromClientStream, algData, 4);
		return TransferUtils.convertByteArrayToInt(algData);
	}

	private void sendBackVideo() throws IOException {
		TransferUtils.send(socket, new File(Server.VideoOutDir + File.separator + baseVideoOutName + instanceNumber + ".avi"));
	}

}
