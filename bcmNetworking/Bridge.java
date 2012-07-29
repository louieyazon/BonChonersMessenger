package bcmNetworking;

public class Bridge {
	String messageTyped; 
	boolean valueSet = false; 
	public synchronized String getMessage() { 
		if(!valueSet) 
			try { 
				wait(); 
			} catch(InterruptedException e) { 
				//System.out.println("InterruptedException caught"); 
			} 
		//System.out.println("Got: " + messageTyped); 
		valueSet = false; 
		notify(); 
		return messageTyped; 
	} 
	public synchronized void putMessage(String messageTyped) { 
		if(valueSet) 
			try { 
				wait(); 
			} catch(InterruptedException e) { 
				//System.out.println("InterruptedException caught"); 
			} 
		this.messageTyped = messageTyped; 
		valueSet = true; 
		//System.out.println("Send: " + messageTyped); 
		notify(); 
	} 
}
