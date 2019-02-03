package videoTransfer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * This class is a collection of utilities to read, write and manage 
 * the connections this project needs.
 */
public final class TransferUtils {

	   public static int convertByteArrayToInt(byte[] ba) {
	        return ba[3] & 0xFF | (ba[2] & 0xFF) << 8 | (ba[1] & 0xFF) << 16 |(ba[0] & 0xFF) << 24;
	    }
	   
	   
	   public static void readFromSocket(BufferedInputStream buf, byte[] data, int size) throws IOException {
	        int bytesAlreadyRead = 0;
	        while(bytesAlreadyRead < size) {
	            int bytesRead = buf.read(data, bytesAlreadyRead, size-bytesAlreadyRead);
	            bytesAlreadyRead += bytesRead;
	        }
	    }
	   
       public static byte[] convertIntToByteArray(int val) {
           byte[] ret = new byte[4];
           ret[0]=(byte)((0xFF000000&val)>>24);
           ret[1]=(byte)((0x00FF0000&val)>>16);
           ret[2]=(byte)((0x0000FF00&val)>>8);
           ret[3]=(byte)(0x000000FF&val);
           return ret;
        }
       
   	public static byte[] receive(BufferedInputStream bufRead) throws IOException {
		// get the size of the message
		byte[] sizeByte = new byte[4];
		readFromSocket(bufRead, sizeByte, 4);
		int sizeInt = convertByteArrayToInt(sizeByte);
		// get the file
		byte[] content = new byte[sizeInt];
		readFromSocket(bufRead, content, sizeInt);
		//bufRead.close();
		return content;
	}

	public static byte[] getDataBytes(Socket socket) throws IOException{
		// Prepare input stream
		BufferedInputStream readFromSocket = new BufferedInputStream(socket.getInputStream());
		// receive data
		return receive(readFromSocket);
	}

   /**
   	 * Convert file into byte buffer
   	 * 
   	 * @param buf
   	 * @param file
   	 * @throws IOException
   	 */
   	public static byte[] fileToBytesArray(File file) throws IOException {
   		byte[] buf = new byte[(int) file.length()]; // where to put files
   		FileInputStream fileInStream = new FileInputStream(file);
   		// load up to buf.length bytes into the array
   		fileInStream.read(buf); // carica il buffer con i byte del file
   		fileInStream.close();
   		return buf;
   	}

   	
   	/**
   	 * Main file sender method
   	 * 
   	 * @param clientSocket
   	 * @param file
   	 * @throws IOException
   	 */
   	public static void send(Socket clientSocket, File file) throws IOException {
   		// Create output stream
   		PrintStream outToClient = new PrintStream(clientSocket.getOutputStream());
   		byte[] buf = fileToBytesArray(file); //TODO check
   		// Send file
   		send(outToClient, buf);
   	}

   	/**
   	 * This method sends any byte array to the outToSocket stream
   	 * @param outToSocket - PrintStream to socket
   	 * @param buf         - buffer of bytes to send
   	 */
   	public static void send(PrintStream outToSocket, byte[] buf) {
   		// send file
   		byte[] fileSize = convertIntToByteArray(buf.length);
   		// write to socket bytes from 0 to length
   		outToSocket.write(fileSize, 0, fileSize.length);
   		outToSocket.write(buf, 0, buf.length);
   }
   	
   	/**
   	 * This method writes byte array data to a particular file
   	 * @param data the data to be written
   	 * @param filename the pathname of the file
   	 * @throws FileNotFoundException if file does not exist
   	 * @throws IOException if not allowed
   	 */
	public static void writeDataToFile(byte[] data, String filename) throws FileNotFoundException, IOException {
		FileOutputStream fileOutputStream = new FileOutputStream(filename);
		fileOutputStream.write(data);
		fileOutputStream.close();
	}
	
	public static boolean isValidFileInput(String path) {
		try {
			path = (new File(path)).getCanonicalPath();
		}
		catch (Exception e) {return false;}
		return true;
	}

}
