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
	
	private static String maindir = "ServerBuffer"+File.separator;
	
	public static  File MainDir = new File("ServerBuffer");
	
	public static File VideoInDir = new File(maindir+"video_in");
	
	public static File VideoOutDir = new File(maindir+"video_out");
	
	public static File BackgroundDir = new File(maindir+"backgrounds");
	
	public static File ScriptsDir = new File("matlab_scripts");
	
	
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

			ServerSocket welcomeSocket = new ServerSocket(Port);
			// instanceCounter used to identify which files belong to whom (otherwise the various instances may go blep
			// when saving their files)
			int instanceCounter = 1;
			
			// Verify existence of buffer directories where to save files
			if(!VideoInDir.exists()) VideoInDir.mkdirs();
			if(!BackgroundDir.exists()) BackgroundDir.mkdir();
			if(!VideoOutDir.exists()) VideoOutDir.mkdir();
			System.out.println(VideoInDir.getAbsolutePath());

			// Ask if to keep listening for connections or close (stop listening to connection requests)
			System.out.print("Press 'q' to quit, 'l' to listen for new connections: ");
			while (console.next().equals("l")) {
				Socket newClientSocket = welcomeSocket.accept();
				System.out.println("Connected with " + newClientSocket.getInetAddress().getHostAddress());
				// Serve client requests
				serveNewClient(newClientSocket, instanceCounter++);
				System.out.print("Press 'q' to quit, 'l' to listen for new connections: ");
			}
			console.close();

		} catch (IOException e) {
			System.out.println("IO Error, port already in use?");
		}
	}
	
	
	/**
	 * Private method to serve new connections, which live on clientSocket
	 * @param clientSocket the new connection socket with the new client
	 */
    private static void serveNewClient(Socket clientSocket, int instanceNum) {
        ServerConnectionInstance newServerInstance = new ServerConnectionInstance(clientSocket, instanceNum);
        Thread newServerInsanceThread = new Thread(newServerInstance);
        newServerInsanceThread.start();
    }
}