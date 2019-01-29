package videoTransfer;

import java.net.*;
import java.io.*;

/**
 * A server-side class that keeps the connection alive and interacts
 * with a single client. Created by Server.
 */
public class ServerConnectionInstance implements Runnable{
	
	private PrintStream toClientStream;
    
    private BufferedReader fromClientStream;       

    private Socket socket;
    
    private MatlabBinderInstance matlabInterface;
    
    private File receivedVideo;
    
    private File receivedBackground;
    
    /**
     * Specifies the algorithm to use for estimating the background
     * 1 -> median on the frames (default)
     * 2 -> motion blocks and fills
     */
    private int algorithmToUse = 1;

    /**
     * Constructs a new ServerConnectionInstance, using the given socket
     * to create the appropriate input and output streams
     * @param newClientSocket the socket on which the connection lives
     */
    public ServerConnectionInstance(Socket newClientSocket){
        try {
	        socket = newClientSocket;
	        toClientStream = new PrintStream(newClientSocket.getOutputStream());
	        fromClientStream = new BufferedReader( new InputStreamReader(newClientSocket.getInputStream()));
	        matlabInterface = new MatlabBinderInstance();
	        Thread mIThread = new Thread(matlabInterface);
	        mIThread.start();
	        matlabInterface.start();
        }
        catch (IOException e) {System.out.print("Failed to create Server connection instance, socket IO problem\n");}
    }
	
    
	@Override
	public void run() {
		//TODO: implement busy-waiting serve
		while(true) {
			//wait for request....
			//some way to wait for the connection
			serveRequest();
		}
	}

	private void serveRequest() {
		//first thing: accept incoming video
		getVideo();
		//save in local
		
		//second thing: accept incoming background
		getBackground();
		//save in local
		
		//third thing: accept selected algorithm
		getAlgorithm();
		
		//fourth thing: elaborate
		elaborate();
		//wait for elaboration...
		while(matlabInterface.isComputing()) {}
		
		//fifth thing: send back the video
		sendBackVideo();
	}
	
	/**
	 * Common interface: the two algorithms choose the video in the fold
	 */
	private void elaborate() {
		while(!matlabInterface.isReady()) {}
		//let's estimate the background
		switch(algorithmToUse) {
		case 2:
			matlabInterface.computeCommandAsynchronously(
					"run('"+Server.ScriptsDir+File.separator+"blocks_fills_bg_estimate.m')");
			break;
		case 1:
		default:
			matlabInterface.computeCommandAsynchronously(
					"run('"+Server.ScriptsDir+File.separator+"median_bg_estimate.m')");
		}	
	}
	
	private void getVideo() {
		
	}
	
	private void getBackground() {
		
	}
	
	private void getAlgorithm() {
		
	}
	
	private void sendBackVideo() {
		
	}
	
}
