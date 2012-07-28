package bcmNetworking;
import java.net.*;
import java.util.LinkedList;
import java.io.*;

import javax.swing.SwingWorker;

public class ReceiveSocket extends SwingWorker<Void, String> {
	// Information passed to the Socket to facilitate creation
	// private String ipAdd;
	private int currPort;

	// Regarding the status of its connection
	private boolean connectStatus = false;
	
	// Connection Data
	private ServerSocket listener;
	private Socket connection;
	private BufferedReader incoming;
	private PrintWriter outgoing;
	private String messageIn;
	private LinkedList<String> stringToWindow;
	

	public ReceiveSocket(int port, LinkedList<String> fromOutside){
		this.currPort = port;
		stringToWindow = fromOutside;
		//this.ipAdd = ipAdd;
	}

	public boolean getConnectStatus(){
		return this.connectStatus;
	}

	@Override
	protected Void doInBackground() throws Exception {
		try{
			initServerConnection();		
			initIOStreams();
			verifyReceiver();
		} catch (Exception e) {
			System.out.println("An error occured while opening the connection.");
			System.out.println(e.toString());
			this.cancel(false);
		}
		
		//Continuously read lines from the input stream
		while(!isCancelled()) {
			messageIn = incoming.readLine();
			if(messageIn.length() > 0) {
					acceptMessage();
			}
		}

		return null;
	}

	
	
	
	
	
	private void acceptMessage() {
		stringToWindow.addLast(messageIn);
		System.out.println(messageIn);
	}

	
	
	private void verifyReceiver() throws IOException, Exception {
		//Send handshake
		outgoing.println(BCMProtocol.HANDSHAKE);
		outgoing.flush();
		
		
		
		//receive handshake and verify 
		/*TODO: Create a loop that will wait for the right connection for an 
		 * appropriate amount of time, then timeout. 
		 */
		messageIn = incoming.readLine();
		if(! BCMProtocol.HANDSHAKE.equals(messageIn)){
			throw new Exception("Connected program is not a BCMsgr");
		}
		connectStatus = true;
		System.out.println("Connected.");
	}

	
	
	private void initServerConnection() throws IOException {
		//Initialize Server
		listener = new ServerSocket(currPort);
		System.out.println("Listening on port" + listener.getLocalPort());

		//Upon successful connection, close listener
		connection = listener.accept();
		listener.close();
	}

	private void initIOStreams() throws IOException {
		//Initialize Stream Writers and Readers
		incoming  = new BufferedReader(new InputStreamReader(connection.getInputStream()) );
		outgoing = new PrintWriter(connection.getOutputStream());
	}
	
	
	
	
	
	
	
	
	
	
}
