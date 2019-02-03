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
	
	// Names of files and directories on server
	private static String videoInDirName = "Video_in";
	private static String videoOutDirName = "Video_out";
	private static String backInDirName = "Background_in";
	private static String scriptsDirName = "matlab_scripts";
	private static String ServerDirName = "ServerBuffer";
	// Directories where files are stored
	public static File mainServerDirectory;
	public static File videoInDir;
	public static File videoOutDir;
	public static File backInDir;
	// Directory of matlab scripts
	public static File scriptsDir;
	// Port to connect to
	public static final int Port = 40000;
	
	/**
	 * The executable method for the class.
	 * It starts a Server, which starts listening for connections
	 * and manages to create a ServerConnectionInstance for each
	 * @param args -not used
	 */
	public static void main(String[] args) {
		
		try {
			// -------- PREPARE FOR CONNECTION -------
			Scanner console = new Scanner(System.in);
			ServerSocket welcomeSocket = new ServerSocket(Port);
			int instanceCounter = 1;	// Counts instances to track clients id
			createDirectories();		// Verify existence of needed directories
			
			// -------------- CONNECT --------------
			System.out.print("Press 'q' to quit, 'l' to listen for new connections: ");
			while (console.next().equals("l")) {
				Socket newClientSocket = welcomeSocket.accept();	// Connect
				System.out.println("Connected with " 
						+ newClientSocket.getInetAddress().getHostAddress());
				serveNewClient(newClientSocket, instanceCounter++);	// Create connection 
																	// instance to serve client
				System.out.print("Press 'q' to quit, 'l' to listen for new connections: ");
			}
			console.close();
		} catch (IOException e) {
			System.out.println("IO Error, port "+Port+" already in use?");
		}
	}
	
	
	/**
	 * Private method to serve new connections, which lives on clientSocket
	 * @param clientSocket - the new connection socket with the new client
	 */
    private static void serveNewClient(Socket clientSocket, int instanceNum) {
        ServerConnectionInstance newServerInstance = new ServerConnectionInstance(clientSocket, instanceNum);
        Thread newServerInsanceThread = new Thread(newServerInstance);
        newServerInsanceThread.start();
    }
    
    /**
     * If directories don't exist, create them
     */
    private static void createDirectories() {
    	// check if launched from bin or not
    	File currentAbsPath = new File(".");
    	String currentRelativePath = null;
    	try {
        	currentRelativePath = currentAbsPath.getCanonicalFile().getName().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (currentRelativePath.equals("bin")) { // If ran via script
			mainServerDirectory = new File(".."+File.separator+ServerDirName);
			scriptsDir = new File(".."+File.separator+scriptsDirName);
		} else {								// If ran via eclipse/others
			mainServerDirectory = new File(ServerDirName); 
			scriptsDir = new File(scriptsDirName);
		}
		// Set subDirs
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