package videoTransfer;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client { // TODO try{}finally{}

	public static void main(String[] args) throws Exception {
		//-----------------CONNECT---------------------
		Scanner sc = new Scanner(System.in);
		Socket clientSocket = null;
		//get connection info
		boolean isValidInput = false;
		while(!isValidInput) {
			try {
				//ask for connection parameters
				System.out.println("Server address?");
				String serverAddress = sc.next();
				System.out.println("Port?");
				int port = sc.nextInt();
				//try connection
				clientSocket = new Socket(serverAddress, port);
				//TODO this doesn't throw any exception if trying to connect to
				//non-connected hosts
				isValidInput = true;
			} catch (Exception e) {
				System.err.println("Invalid input. Connection error");
			}
		}
		System.out.println("Connection estabilished successfully");
		
		//---------------OPTION MENU------------------
		String optionMenu = 
				"Digit one of the following commands to continue\n"
				+ "'lv'	:	List Videos			- list the available default input videos\n"
				+ "'cv'	:	Choose Video		- use a default video\n"
				+ "'sv'	:	Send Video			- send a custom video\n"
				+ "'lb'	:	List Backgrounds	- list the available default backgrounds\n"
				+ "'cb'	:	Choose Background	- use a default background\n"
				+ "'sv'	:	Send Background		- send a custom background\n";
		PrintStream outToSocket = new PrintStream(clientSocket.getOutputStream());
		
		
	}

}
