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
		connectionSocket = welcomeSocket.accept();
		
		//----------DIRECTORIES REFERENCES---------
		//Root directory for the project
		File absRootDirectory = new File("");
		String videoInDirName = "video_in/";
		String videoOutDirName = "video_out/";
		String backgroundDirName = "backgrounds/";
		File vidInDir = new File(absRootDirectory,videoInDirName);
		File vidOutDir = new File(absRootDirectory, videoOutDirName);
		File backDir = new File(absRootDirectory,backgroundDirName);
		
		
		connectionSocket.close();
		welcomeSocket.close();
	}
}
