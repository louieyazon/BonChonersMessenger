package bcmNetworking;

import java.io.*;
import java.net.*;

public class RequestSocket extends Thread {
	//Default port vales
	static final int MANAGER_PORT = 3232;
	static final int MAX_PORT = 3400;
	static final int MIN_PORT = 3301;
	
	//Secret Handshake
	static final String HANDSHAKE = "PaulPogi";
	
	//list of prefixes that the ChatServer will
	static final char MESSAGE_CODE = '0';
	static final char CLOSED_CODE = '4';
	
	//Information passed to the Socket to facilitate creation
	private String ipAdd;
	
	public RequestSocket(String ipAdd){
		this.ipAdd = ipAdd;
		this.start();
		
	}
	
	public void run(){
		String commandTyped;	
		Socket connection;
		BufferedReader incoming;
		PrintWriter outgoing;
		String messageIn;
		int proposedPort;
		
		try{
			connection = new Socket(ipAdd, MANAGER_PORT);
			incoming = new BufferedReader( new InputStreamReader(connection.getInputStream()) );
			outgoing = new PrintWriter(connection.getOutputStream(), true);
			
			//Send out handshake
			outgoing.println(HANDSHAKE);
			
			//Verify handshake
			messageIn = incoming.readLine();
			if(! HANDSHAKE.equals(messageIn)){
				throw new Exception("Connected program is not a BCMsgr");
			}
			System.out.println("Connected.");
			proposedPort = proposePort();
			
			while(!messageIn.equalsIgnoreCase("available")){
				
				outgoing.println(proposedPort);
				
				messageIn = incoming.readLine();
				if(messageIn.equalsIgnoreCase("available") == true){ //If proposed port is availabe
					
					ReceiveSocket rs = new ReceiveSocket(proposedPort); //Build a Receiver
					SendSocket ss = new SendSocket(ipAdd,proposedPort); //Build a Sender
					outgoing.println("ready"); //Inform Manager that Receiver on this end is ready
					while(!rs.getConnectStatus()){} //Wait for the Manager to connect to our Receiver
					outgoing.println("completed");//Inform the Manager that connection building is complete
					
					connection.setReuseAddress(true);
					connection.close();
				} else {
					//get the next available port
					proposedPort++;
					while(!portAvailable(proposedPort))
						proposedPort++;
					if(proposedPort > MAX_PORT)
						connection.setReuseAddress(true);
						connection.close();
						throw new IllegalArgumentException("No Ports Available.");
				}
			}
			//Determine First Free Port and Propose
			
				

		} catch (Exception e) {
			System.out.println("Error: " + e);
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
		} return false;
	}
	
	public int proposePort(){
		int port;
		for(port = MIN_PORT; !portAvailable(port); port++){
			if(port == MAX_PORT+1)
				return 0;
		}
		return port;
	}
	
}