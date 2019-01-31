package videoTransfer;

import java.net.*;
import java.io.*;

/**
 * A server-side class that keeps the connection alive and interacts with a
 * single client. Created by Server.
 */
public class ServerConnectionInstance implements Runnable {

	private PrintStream toClientStream;

	private BufferedReader fromClientStream;

	private Socket socket;

	private MatlabBinderInstance matlabInterface;

	private File receivedVideo;

	private File receivedBackground;

	/**
	 * Specifies the algorithm to use for estimating the background 1 -> median on
	 * the frames (default) 2 -> motion blocks and fills
	 */
	private int algorithmToUse = 1;

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
			fromClientStream = new BufferedReader(new InputStreamReader(newClientSocket.getInputStream()));
			// matlabInterface = new MatlabBinderInstance();
			// Thread mIThread = new Thread(matlabInterface);
			// mIThread.start();
			// matlabInterface.start();
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
		getAlgorithm();
		//TODO verificare che lo script funzioni con matlab
		//TODO in caso contrario è necessario fare cd bin anziche java -cp bin
		// fourth thing: elaborate
		// elaborate();
		// wait for elaboration...
		// while (matlabInterface.isComputing()) {

		// fifth thing: send back the video
		// sendBackVideo();
		// }
		socket.close();

	}

	/**
	 * Common interface: the two algorithms choose the video in the fold
	 */
	private void elaborate() {
		while (!matlabInterface.isReady()) {
		}

		// tell the matlab workspace about the files we want to work on
		matlabInterface.computeCommandAsynchronously("video = '.." + Server.VideoInDir + File.separator + "vid"
				+ instanceNumber + ".avi';" + "newBackground = '.." + Server.BackgroundDir + File.separator + "new_bg"
				+ instanceNumber + ".png';" + "video_out = '.." + Server.VideoOutDir + File.separator + "new_vid"
				+ instanceNumber + ".avi';");
		while (matlabInterface.isComputing()) {
		}

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
		writeData(videoData, Server.VideoInDir + File.separator + "vid" + instanceNumber);
	}

	private void getBackground() throws IOException {
		byte[] backgroundData = TransferUtils.getDataBytes(socket);
		// write to file
		writeData(backgroundData, Server.BackgroundDir + File.separator + "new_bg" + instanceNumber);
	}

	private void getAlgorithm() {

	}

	private void sendBackVideo() {

	}

	private static void writeData(byte[] data, String filename) throws FileNotFoundException, IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(filename);
		fileOutputStream.write(data);
		fileOutputStream.close();
	}

}
