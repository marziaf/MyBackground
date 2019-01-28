package videoTransfer;

public class TestClass {

	public static void main(String[] args) throws InterruptedException {
		MatlabBinderInstance mbi = new MatlabBinderInstance();
		Thread mbiThread = new Thread(mbi);
		mbiThread.start();
		System.out.println("mbi thread started");
		mbi.start();
		System.out.println("engine started");
		while (!mbi.isReady()) {} //can take forever... error?
		System.out.println("engine ready"); //waits for the engine to be ready
		mbi.computeCommandAsynchronously("imwrite([0,0;0,0;0,0],'uhe.png')");
		while (mbi.isComputing()) {Thread.sleep(10000);System.out.println("not completed");}
		
		MatlabBinderInstance mbi1 = new MatlabBinderInstance();
		Thread mbiThread1 = new Thread(mbi);
		mbiThread1.start();
		System.out.println("mbi1 thread started");
		mbi1.start();
		System.out.println("engine1 started");
		while (!mbi1.isReady()) {} 
		System.out.println("engine ready"); 
		mbi1.computeCommandAsynchronously("imwrite([0,0;0,0;0,0],'uhe1.png')");
		while (mbi1.isComputing()) {Thread.sleep(10000);System.out.println("not completed1");}
		
		mbi.close();
		mbi1.close();
	}

}
