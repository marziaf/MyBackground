package videoTransfer;

import java.net.*;

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
			instanceNumber = instanceNum;
			socket = newClientSocket;
			toClientStream = new PrintStream(newClientSocket.getOutputStream());
			fromClientStream = new BufferedInputStream(newClientSocket.getInputStream());
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
			System.err.println("Error");
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
		while (!matlabInterface.isReady()) {
		}

		// tell the matlab workspace about the files we want to work on
		matlabInterface.computeCommandAsynchronously("video = '.." + File.separator + Server.VideoInDir + File.separator
				+ "vid" + instanceNumber + ".avi';" + "newBackground = '.." + File.separator + Server.BackgroundDir
				+ File.separator + "new_bg" + instanceNumber + ".png';" + "video_out = '.." + File.separator
				+ Server.VideoOutDir + File.separator + "new_vid" + instanceNumber + ".avi';");
		while (matlabInterface.isComputing()) {
		} // shouldn't take long

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
		}
	}

	private void getVideo() throws IOException {
		byte[] videoData = TransferUtils.getDataBytes(socket);
		TransferUtils.writeDataToFile(videoData, Server.VideoInDir + File.separator + "vid" + instanceNumber);
	}

	private void getBackground() throws IOException {
		byte[] backgroundData = TransferUtils.getDataBytes(socket);
		// write to file
		TransferUtils.writeDataToFile(backgroundData,
				Server.BackgroundDir + File.separator + "new_bg" + instanceNumber);
	}

	private int getAlgorithm() throws IOException {
		byte[] algData = new byte[4];
		TransferUtils.readFromSocket(fromClientStream, algData, 4);
		return TransferUtils.convertByteArrayToInt(algData);
	}

	private void sendBackVideo() throws IOException {
		TransferUtils.send(socket, new File(Server.VideoOutDir + File.separator + "new_vid" + instanceNumber + ".avi"));
	}

}
