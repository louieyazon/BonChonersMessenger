package bcmNetworking;
import java.net.*;
import java.io.*;

public class ManagerSocket extends Thread{
	//Information passed to the Socket to facilitate creation
	//private String ipAdd;
	private int curPort;
	
	public ManagerSocket(){
		this.start();
		
	}
	
	public void run(){
		
		int port = BCMProtocol.MANAGER_PORT;
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
			outgoing = new PrintWriter(connection.getOutputStream(),true);
			
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
			System.out.println("Handshake succesful. Now connected.");
			
			/*
			 *Receive the proposed port from the RequestSocket 
			 *Check if it is available, and if so, create the
			 *Send and Receive threads on this side of the connection
			 */
			messageIn = incoming.readLine();
			while(messageIn.equalsIgnoreCase("completed")){
				
				System.out.println("Connection requested at port " + messageIn);				
				int proposedPort = Integer.parseInt(messageIn);
				if(portAvailable(proposedPort)){
					System.out.print("Building ReceiveSocket and waiting of client...");
					ReceiveSocket rs = new ReceiveSocket(proposedPort); //Build the ReceiveSocket
					outgoing.println("available"); //Inform Request that socket is available
					
					while(!rs.getConnectStatus()){} //Wait for the Requester to connect to the Receive Socket
					messageIn = incoming.readLine(); //Receive the ready message from the Requester
					System.out.println("Success.");
					
					String locAdd = connection.getLocalAddress().toString();//retrieve the IP address of the Requester
					String ipAdd[] = locAdd.split("\\");		
					SendSocket ss = new SendSocket(ipAdd[1], proposedPort);//Connect to the Requester's ReceiveSocket
					messageIn = incoming.readLine(); //receive the completed message from the Requester
				
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
		
		if(port > BCMProtocol.MAX_PORT || port < BCMProtocol.MIN_PORT)
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
	
}
