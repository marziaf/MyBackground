package videoTransfer;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.omg.PortableServer.ServantActivator;

/**
 * This Class builds and manages a Server for the application. The
 * Server listens for connections on the desired port and, acting as 
 * a manager, creates a ServerConnectionInstance for every incoming
 * connection, which lives separately from all the others.
 */
public class Server {
	
	//----------DIRECTORIES PREFERENCES---------
	
	private static String videoInDirName = "Video_in";
	private static String videoOutDirName = "Video_out";
	private static String backInDirName = "Background_in";
	private static String scriptsDirName = "matlab_scripts";
	private static String ServerDirName = "ServerBuffer";
	
	public static File mainServerDirectory;
	public static File videoInDir;
	public static File videoOutDir;
	public static File backInDir;

	public static File scriptsDir;
	
	
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
			createDirectories();
			

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
    
    private static void createDirectories() {
    	// check if launched from bin or not
    	File currentAbsPath = new File(".");
    	String currentRelativePath = null;
    	try {
        	currentRelativePath = currentAbsPath.getCanonicalFile().getName().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (currentRelativePath.equals("bin")) {
			// if ran via script
			mainServerDirectory = new File(".."+File.separator+ServerDirName);
			scriptsDir = new File(".."+File.separator+scriptsDirName);
		} else {
			// if ran via eclipse/others
			mainServerDirectory = new File(ServerDirName); 
			scriptsDir = new File(scriptsDirName);
		}
		// then set subDirs
		backInDir = new File(mainServerDirectory.getAbsolutePath()+ File.separator
				+backInDirName);
		videoInDir = new File(mainServerDirectory.getAbsolutePath()+File.separator
				+videoInDirName);
		videoOutDir = new File(mainServerDirectory.getAbsolutePath()+ File.separator
				+videoOutDirName);
    	
    	if(!videoInDir.exists()) videoInDir.mkdirs();
		if(!backInDir.exists()) backInDir.mkdir();
		if(!videoOutDir.exists()) videoOutDir.mkdir();
    	
    }
}