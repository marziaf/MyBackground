package videoTransfer;

import java.net.*;
import java.io.*;
import videoTransfer.ReceiveFileUtils;

/**
 * A server-side class that keeps the connection alive and interacts
 * with a single client. Created by Server.
 */
public class ServerConnectionInstance implements Runnable{
	
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
		
	}

}
