package bcmNetworking;
import java.net.*;
import java.io.*;

public class ReceiveSocket extends Thread{
	//Information passed to the Socket to facilitate creation
	//private String ipAdd;
	private int curPort;
	
	//Regarding the status of its connection
	private boolean connectStatus = false;
	
	public ReceiveSocket(int curPort){
		//this.ipAdd = ipAdd;
		this.curPort = curPort;
		this.start();
		
	}
	
	public void run(){
		
		int port;
		ServerSocket listener;
		Socket connection;
		BufferedReader incoming;
		PrintWriter outgoing;
		String messageIn;
		
		port = curPort;
		
		try{
			//Initialize Server
			listener = new ServerSocket(port);
			System.out.println("Listening on port" + listener.getLocalPort());
			//Upon successful connection, close listener
			connection = listener.accept();
			listener.close();
			
			//Initialize Stream Writers and Readers
			incoming  = new BufferedReader(new InputStreamReader(connection.getInputStream()) );
			outgoing = new PrintWriter(connection.getOutputStream());
			
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
			
		} catch (Exception e) {
			System.out.println("An error occured while opening the connection.");
			System.out.println(e.toString());
			return;
		}
		
			//Continuously read lines from the input stream
		try{
			while(true) {
				messageIn = incoming.readLine();
				
				if(messageIn.length() > 0) {
					
					if(messageIn.charAt(0) == BCMProtocol.CLOSED_CODE) {
						System.out.println("Quit Command Recieved");
						connection.close();
						break;
					} else if (messageIn.charAt(0) == BCMProtocol.MESSAGE_CODE) {
						messageIn = messageIn.substring(1);
						System.out.println(messageIn);
					}
				}
			}
			
			
		} catch (Exception e) {
			System.out.println("Sorry, an error has occured. Connection lost.");
			System.exit(1);
		}
	}
	
	public boolean getConnectStatus(){
		return this.connectStatus;
	}
	
}
