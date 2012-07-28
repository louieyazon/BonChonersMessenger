package bcmNetworking;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingWorker;


public class FakeFriend extends SwingWorker<Void, String> {

	private LinkedList<String> mBuff;
	
	private LinkedList<String> outgoingmBuff = new LinkedList<String>();
	private String randCode;
	
	
	public FakeFriend(LinkedList<String> fromOutside) {
		super();
		mBuff = fromOutside;
		System.out.println("SwingWorker created");	
	}
	
	synchronized public void sendMsg(String msg) {
		outgoingmBuff.addLast(msg);
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		int i = 0;
		int interval;

		System.out.println("doInBackground called");
		 while (!isCancelled()) {  // so the thread loops but can be stopped by the outside thread
			 	interval = (int)(Math.random() * 1000) + 500;
			 	randCode = String.valueOf(  (int)(Math.random() * 4)  );
			 	//randCode = BCMProtocol.MESSAGE_CODE + "";		
			 	
			 	if(!outgoingmBuff.isEmpty()) {
			 		randCode = BCMProtocol.MESSAGE_CODE + "";
			 		publish("you said: " + outgoingmBuff.removeFirst());
			 	}
			 	
			 	
			 	Thread.sleep(interval);
				publish(i + " : " + interval + "ms");
			 	i++;
		    }
		return null;
	}


	
	  @Override
	  protected void process(List<String> chunks){
	    for(String message : chunks){
	      System.out.println(randCode + message + "   process called");
	      mBuff.addLast(randCode + "<FakeFriend> " + message + "");
	    }
	  }
	
	
}
