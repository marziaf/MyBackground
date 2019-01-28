package videoTransfer;

public class TestClass {

	public static void main(String[] args) throws InterruptedException {
		MatlabBinderInstance mbi = new MatlabBinderInstance();
		Thread mbiThread = new Thread(mbi);

		mbiThread.start();
		System.out.println("mbi thread started");
		Thread.sleep(2000);
		mbi.start();
		System.out.println("engine started");
		Thread.sleep(10000);
		synchronized(mbi.engineOnOffLock) {System.out.println("engine ready");} //waits for the engine to be ready
		mbi.computeCommandAsynchronously("imwrite([0,0;0,0;0,0],'ehe.png')");
		while (mbi.isComputing()) {Thread.sleep(10000);System.out.println("not completed");}
		//^seems to go on forever, even if the command actually does work
		mbi.close();
	}

}
