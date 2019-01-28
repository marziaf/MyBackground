package videoTransfer;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * This Class builds and manages a Server for the application. The
 * Server listens for connections on the desired port and, acting as 
 * a manager, creates a ServerConnectionInstance for every incoming
 * connection, which lives separately from all the others.
 */
public class Server {
	
	//----------DIRECTORIES PREFERENCES---------
	
	public static final String VideoInDir = "video_in/";
	
	public static final String VideoOutDir = "video_out/";
	
	public static final String BackgroundDir = "backgrounds/";
	
	
	//-----------CONNECTION PREFERENCES---------
	
	public static final int Port = 40000;
	
	/**
	 * The executable method for the class.
	 * It starts a Server, which starts listening for connections
	 * and manages to create a ServerConnectionInstance for each
	 * @param args -not used
	 */
	public static void main(String[] args) {
		try {
		
		Scanner console = new Scanner(System.in);
		
        System.out.print("Server initalization... ");
        System.out.print("Creating server-side socket to listen for connections \n");
		ServerSocket welcomeSocket = new ServerSocket(Port);

		while (true) {
			//Ask if want to listen to new connections
			System.out.print("Press 'q' to quit, 'l' to listen for new connections: ");
			if (console.next().equals("q")) break;
            System.out.print("Listening for new connections @ "+ Port);
            //Create socket
            Socket newClientSocket = welcomeSocket.accept();
            System.out.println("Connected with "+newClientSocket.getInetAddress().getHostAddress());
            serveNewClient(newClientSocket);
        }		
		
		
		//TODO: get/send files through this socket or create many instances
		//if we want to go multi-user
		
		//TODO: call matlab interface MatlabBinderInstance and evaluate
		
		//TODO: return to user relevant things (again, this should be done on
		//separate instances in case of multi-user)
		
		welcomeSocket.close();
		}
		catch (IOException e) {System.out.println("IO Error, invalid port?");}
	}
	
	/**
	 * Private method to serve new connections, which live on clientSocket
	 * @param clientSocket the new connection socket with the new client
	 */
    private static void serveNewClient(Socket clientSocket) {
        ServerConnectionInstance newServerInstance = new ServerConnectionInstance(clientSocket);
        Thread newServerInsanceThread = new Thread(newServerInstance);
        newServerInsanceThread.start();
    }
    
    /**
     * Convert byte array (received from socket) to int
     * @param ba - byte array
     * @return - int
     */
	private static int convertByteArray4ToInt(byte[] ba) {
		return (ba[0] & 0xFF) <<24 | (ba[1] & 0xFF) <<16 | 
				(ba[2] & 0xFF) <<8 | (ba[3] & 0xFF);
	}
}