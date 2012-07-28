package bcmNetworking;

import java.io.*;
import java.net.*;

public class RequestSocket extends Thread {
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
			connection = new Socket(ipAdd, BCMProtocol.MANAGER_PORT);
			incoming = new BufferedReader( new InputStreamReader(connection.getInputStream()) );
			outgoing = new PrintWriter(connection.getOutputStream(), true);
			
			//Send out handshake
			outgoing.println(BCMProtocol.HANDSHAKE);
			
			//Verify handshake
			messageIn = incoming.readLine();
			if(! BCMProtocol.HANDSHAKE.equals(messageIn)){
				throw new Exception("Connected program is not a BCMsgr");
			}
			System.out.println("Handshake Successful. Now Connected.");
			
			//Propose & Build the first connection -> Manager:Receiver::Requester:Sender
			proposedPort = proposePort();
			
			while(true){
				System.out.println("Sending " + proposedPort + " to Manager.");
				outgoing.println(proposedPort);
				
				messageIn = incoming.readLine();
				System.out.println("Received from Manager: \"" + messageIn + "\"");
				
				if(messageIn.equalsIgnoreCase("available") == true){ //If proposed port is available
					System.out.println("Manager says port is available");
					SendSocket ss = new SendSocket(ipAdd,proposedPort); //Build a Sender
					System.out.println("SendSocket built on Requester Side");
					outgoing.println("completed");//Inform the Manager that connection building is complete
					break;
				} else {
					System.out.println("Manager says:" + messageIn);
					//get the next available port
					proposedPort++;
					while(!portAvailable(proposedPort))
						proposedPort++;
					if(proposedPort > BCMProtocol.MAX_PORT)
						connection.setReuseAddress(true);
						connection.close();
						throw new IllegalArgumentException("No Ports Available.");
				}
			}
			
			this.sleep(1000);
			System.out.println("Attempting to build 2nd connection");
			//Propose & Build the 2nd connection -> Manager:Sender::Requester:Receiver
			proposedPort = proposePort();
			
			while(true){
				System.out.println("Sending " + proposedPort + " to Manager.");
				outgoing.println(proposedPort);
				
				messageIn = incoming.readLine();
				System.out.println("Received from Manager: \"" + messageIn + "\"");
				
				if(messageIn.equalsIgnoreCase("available") == true){ //If proposed port is available
					System.out.println("Manager says port is available");
					ReceiveSocket rs = new ReceiveSocket(proposedPort);
					System.out.println("ReceiveSocket built on Requester Side");
					sleep(500);
					outgoing.println(connection.getLocalAddress());//Inform the Manager that connection building is complete
					messageIn = incoming.readLine(); //receive confirmation from Manager
					
					connection.setReuseAddress(true);
					connection.close();
					break;
				} else {
					System.out.println("Manager says:" + messageIn);
					//get the next available port
					proposedPort++;
					while(!portAvailable(proposedPort))
						proposedPort++;
					if(proposedPort > BCMProtocol.MAX_PORT)
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
		
		if(port > BCMProtocol.MAX_PORT || port < BCMProtocol.MIN_PORT)
			throw new IllegalArgumentException("Invalid start port: " + port);
		
		 ServerSocket ss = null;
		 Socket s = null;
		 try {
		        System.out.println("Checking port " + port);
			 	ss = new ServerSocket(port);
		        ss.setReuseAddress(true);
		        s = new Socket("localhost",port);
		        s.setReuseAddress(true);
		        return true;
		 } catch (IOException e) {
			 return false;
		 } finally {
		        if (s != null) {
		            try{
		            	s.close();
		            } catch (IOException e) {
		            	/*should not be thrown*/
		            }
		        	
		        }

		        if (ss != null) {
		            try {
		                ss.close();
		            } catch (IOException e) {
		                /* should not be thrown */
		            }
		        }
		 }

	}
	
	public int proposePort(){
		System.out.print("proposePort called...");
		int port = BCMProtocol.MIN_PORT;
		while(true){
			if(portAvailable(port) == true){
				System.out.print("portAvailable says " + port + " is available");
				break;
			}
			if(port == BCMProtocol.MAX_PORT+1){
				System.out.println("Ended, no ports available");
				return 0;}
			port++;
		}
		System.out.println("Ended, proposing " + port);
		return port;
	}
	
}