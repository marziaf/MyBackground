package videoTransfer;

import com.mathworks.engine.*;
import java.util.concurrent.Future;

/**
 * This class binds the client-server functionality of this java program with
 * MATLAB, whose engine is used for elaboration. The engine is started when this
 * class is constructed, and remains alive for command execution until the appropriate
 * function is called, which stops the MATLAB engine too.
 *
 */
public class MatlabBinderInstance implements Runnable {

	private MatlabEngine engine;
	
	public Object engineOnOffLock = new Object();
	
	private boolean to_start = false;
	
	private boolean to_stop = false;
	
	private Future<Void> computing = null;
	
	//if the final UI is Graphical, should implement a Queue to manage the
	//commands sent to the matlab engine for evaluation
	
	public MatlabBinderInstance() {}
	
	/**
	 * Calls the MATLAB function "evalAsync" to evaluate "command" withoud need to create a separate 
	 * flow of execution here. To wait for the completion of it, one should wait until "computing"
	 * is set to false. -> wait for notify or busy-waiting? Busy-Waiting, because the Future object
	 * returned is made for it.
	 * @param command the string to evaluate as a command
	 */
	public void computeCommandAsynchronously(String command) {
		while(computing != null && isComputing()) {} //if another command was executed and it is still running, wait
		computing = engine.evalAsync(command);
	}
	
	//There is no interaction from the server to the matlab engine! All variables are exchanged
	//via the server non-volatile memory
	//	public void getVariableAsynchronously(String variable) {
	//		while(computing != null && isComputing()) {} //if another command was executed and it is still running, wait
	//		computing = engine.getVariableAsync(variable);
	//		
	//	}
	
	/**
	 * The run() method of this runnable class. This method is used to keep the engine
	 * alive, to avoid loosing the data on every call
	 */
	@Override
	public void run() {
		//loops to keep the engine alive, should use
		synchronized(engineOnOffLock) {
			startOrStayAction();
		}
		
		//close() was called, cause the while loop exited
		try {engine.close();}
		catch (EngineException e) {e.printStackTrace(); System.out.println("engine failed closing");}
		to_stop = false;
		//will now join with the main thread, because close() was called
	}
	
	/**
	 * Method that manages to keep the engine alive and respond to notifies
	 * on whether or not to start the engine (just here to keep run() clean)
	 */
	private void startOrStayAction() {
		while (!to_stop) { //to_stop and computing are only read, no need for synchronization
			
			if (!to_start || isComputing()) {
				try {
					//if it is already started or it is evaluating something, wait
					engineOnOffLock.wait();//waits for the call to start(), which sends a notify()
				} 
				catch (InterruptedException e) {System.out.println("Interruption waiting for engine start"); e.printStackTrace();} 
			}
			else {
				try {
					engine = MatlabEngine.startMatlab();
					to_start = false;
					System.out.println("MATLAB Engine started");
				} 
				catch (EngineException | IllegalArgumentException | IllegalStateException | InterruptedException e) {
					System.out.println("Error starting MATLAB");
					e.printStackTrace();
				}
				
				
			}	
		}
	}
	
	/**
	 * This method is used to stop this thread, by signaling with a flag
	 * that the Main Thread wants to stop this instance. From the MT, call 
	 * join() to wait for this to happen.
	 */
	public void close() {
		synchronized(engineOnOffLock) {to_stop = true; engineOnOffLock.notify();}
	}
	
	/**
	 * Sets the flag to start the matlab engine on the parallel task run(),
	 * should not be called twice.
	 * @throws InterruptedException 
	 */
	public void start() {
		synchronized(engineOnOffLock) { //waits for startOrStayAction for the monitor
			to_start = true;
			engineOnOffLock.notify();
		}
	}
	
	/**
	 * Returns the information on whether or not MATLAB is currently evaluating
	 * a command it was sent. Used in a busy-waiting scheme when executing a
	 * certain task from the main thread
	 * @return true if the matlab engine is currently evaluating a command sent by the server, 
	 * false otherwise
	 */
	public boolean isComputing() {return computing != null && computing.isDone();}
	
}
