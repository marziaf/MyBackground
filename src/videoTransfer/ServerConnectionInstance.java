package videoTransfer;

import java.net.*;
import java.io.*;

/**
 * A server-side class that keeps the connection alive and interacts
 * with a single client. Created by Server.
 */
public class ServerConnectionInstance implements Runnable{

	/**
	 * Little class to keep all the possible different server requests neatly 
	 * organized. This also makes it easier to serve them since there is only need 
	 * to send an integer 
	 */
	public final class ServerRequest {
		
		public static final int LIST_VIDEOS = 1;
		
		public static final int CHOOSE_VIDEO = 2;
		
		public static final int SEND_VIDEO = 3;
		
		public static final int LIST_BACKGROUNDS = 4;
		
		public static final int CHOOSE_BACKGROUND = 5;
		
		public static final int SEND_BACKGROUND = 6;
	}
	
	private PrintStream toClientStream;
    
    private BufferedReader fromClientStream;       

    private Socket socket;

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
        }
        catch (IOException e) {System.out.print("Failed to create Server connection instance, socket IO problem\n");}
    }
	
    
	@Override
	public void run() {
		//TODO: implement busy-waiting serve
		while(true) {
			//wait for request....
			int requestType = 0;
			serveRequest(requestType);
		}
	}

	private void serveRequest(int requestType) {
		switch(requestType) {
			case(ServerRequest.CHOOSE_BACKGROUND):
				break;
			//...
			default:
				break;
		}
	}
}
