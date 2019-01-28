package videoTransfer;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
private static Socket connectionSocket;
	
	public static void main(String[] args) throws Exception {
		//-----------------CONNECT-----------------
		int port = 40000;
		ServerSocket welcomeSocket = new ServerSocket(port);
		//we could allow multiple connections at once -> multithreading with many connection
		//instances running in parallel?
		connectionSocket = welcomeSocket.accept();
		
		//----------DIRECTORIES REFERENCES---------
		//Root directory for the project
		//these could be made nonstatic fields of this class?
		File absRootDirectory = new File("");
		String videoInDirName = "video_in/";
		String videoOutDirName = "video_out/";
		String backgroundDirName = "backgrounds/";
		File vidInDir = new File(absRootDirectory,videoInDirName);
		File vidOutDir = new File(absRootDirectory, videoOutDirName);
		File backDir = new File(absRootDirectory,backgroundDirName);
		
		
		//TODO: get/send files through this socket or create many instances
		//if we want to go multi-user
		
		//TODO: call matlab interface MatlabBinderInstance and evaluate
		
		//TODO: return to user relevant things (again, this should be done on
		//separate instances in case of multi-user)
		
		connectionSocket.close();
		welcomeSocket.close();
	}
}