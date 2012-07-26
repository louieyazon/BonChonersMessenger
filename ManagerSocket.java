package bcmNetworking;
import java.net.*;
import java.io.*;

public class ManagerSocket extends Thread{
	//Default port vales
	static final int MANAGER_PORT = 3232;
	static final int MAX_PORT = 55600;
	static final int MIN_PORT = 55501;
	
	//Secret Handshake
	static final String HANDSHAKE = "PaulPogi";
	
	//list of prefixes that the ChatServer will
	static final char MESSAGE_CODE = '0';
	static final char CLOSED_CODE = '4';
	
	public ManagerSocket(){
		this.start();
		
	}
	
	public void run(){
		
		int port = MANAGER_PORT;
		ServerSocket listener;
		Socket connection;
		BufferedReader incoming;
		PrintWriter outgoing;
		String messageIn;
		int proposedPort;
		
		port = MANAGER_PORT;
		
		try{
			//Initialize Server
			listener = new ServerSocket(port);
			System.out.println("Listening on port" + listener.getLocalPort());
			
			//Upon successful connection, close listener
			connection = listener.accept();
			listener.close();
			
			//Initialize Stream Writers and Readers
			incoming  = new BufferedReader(new InputStreamReader(connection.getInputStream()) );
			outgoing = new PrintWriter(connection.getOutputStream(),true);
			
			//Send handshake
			outgoing.println(HANDSHAKE);
			//receive handshake and verify 
			/*TODO: Create a loop that will wait for the right connection for an 
			* appropriate amount of time, then timeout. 
			*/
			messageIn = incoming.readLine();
			if(! HANDSHAKE.equals(messageIn)){
				throw new Exception("Connected program is not a BCMsgr");
			}
			System.out.println("Handshake succesful. Now connected.");
			
			/*
			 *Receive the proposed port from the RequestSocket 
			 *Check if it is available, and if so, create the
			 *Send and Receive threads on this side of the connection
			 */
			messageIn = incoming.readLine();
			
			while(true){
				
				System.out.println("Connection requested at port " + messageIn);				
				proposedPort = Integer.parseInt(messageIn);
				if(portAvailable(proposedPort)){
					System.out.print("Proposed port good. Building ReceiveSocket and waiting of Requester...");
					ReceiveSocket rs = new ReceiveSocket(proposedPort); //Build the ReceiveSocket
					outgoing.println("available"); //Inform Requester that socket is available
					messageIn = incoming.readLine(); //receive the completed message from the Requester
					System.out.println("Requester says " + messageIn);
					break;
				} else {
					outgoing.println("not available");
				}	
			}
			sleep(1000);
			
			System.out.println("Attempting to build 2nd Half");
			while(true){
				messageIn = incoming.readLine();
				System.out.println("Connection requested at port " + messageIn);				
				
				proposedPort = Integer.parseInt(messageIn);
				if(portAvailable(proposedPort)){
					outgoing.println("available"); //Inform Requester that socket is available
					System.out.print("Proposed port good. Waiting for requester to build ReceiveSocket...");
					messageIn = incoming.readLine(); //Receive ready message from Requester
					System.out.println("Requester says: " + messageIn);
					
					String locAdd = messageIn;//retrieve the IP address of the Requester
					locAdd = locAdd.substring(1);
					System.out.println(locAdd);
					SendSocket ss = new SendSocket(locAdd, proposedPort);//Connect to the Requester's ReceiveSocket
					outgoing.println("SenderSocket on Manager side built");
					System.out.println("Breaking");
					break;
				} else {
					outgoing.println("not available");
				}
			}
		connection.setReuseAddress(true);
		connection.close();
			
		} catch (Exception e) {
			System.out.println("An error occured while opening the connection.");
			System.out.println(e.toString());
			return;
		}
		
	}
	
	/*
	 *Checks if the port provided is available
	 *@param port to be checked
	 *@return true if available, false if not
	 */
	public boolean portAvailable(int port){
		
		if(port > MAX_PORT || port < MIN_PORT)
			throw new IllegalArgumentException("Invalid start port: " + port);
		
		ServerSocket ss = null;
		try{
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			System.out.println("portAvailable says true");
			return true;
		}catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if(ss != null){
				try{
					ss.close();
				} catch (IOException e) {
					//Should not happen.
				}
				
			}
		} 
		System.out.println("portAvailable says false");
		return false;
	}
	
}
