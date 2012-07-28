import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingWorker;


public class FakeFriend extends SwingWorker<Void, String> {

	private LinkedList<String> mBuff;
	
	public FakeFriend(LinkedList<String> fromOutside) {
		super();
		mBuff = fromOutside;
		System.out.println("SwingWorker created");	
	}
	
	
	
	@Override
	protected Void doInBackground() throws Exception {
		int i = 0;
		int interval;
		System.out.println("doInBackground called");
		 while (!isCancelled()) {  // so the thread loops but can be stopped
			 	interval = (int)(Math.random() * 1000) + 500;
			 	Thread.sleep(interval);
				publish(i + " : " + interval);
			 	i++;
		    }
		return null;
	}


	
	  @Override
	  protected void process(List<String> chunks){
	    for(String message : chunks){
	      System.out.println(message + "process called");
	      mBuff.addLast("\n<FakeFriend> " + message + "");
	    }
	  }
	
	
}
