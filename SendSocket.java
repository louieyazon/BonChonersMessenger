package bcmNetworking;

import java.io.*;
import java.net.*;

public class SendSocket extends Thread {
	static final int DEF_PORT = 3232;
	static final int DEF_PORT2 = 3233;
	
	//Secret Handshake
	static final String HANDSHAKE = "PaulPogi";
	
	//list of prefixes that the ChatServer will
	static final char MESSAGE_CODE = '0';
	static final char CLOSED_CODE = '4';
	
	//Information passed to the Socket to facilitate creation
	private String ipAdd;
	private int curPort;

	
	
	public SendSocket(String ipAdd, int curPort){
		this.ipAdd = ipAdd;
		this.curPort = curPort;
		this.start();
		
	}
	
	public void run(){
		String commandTyped;	
		Socket connection;
		BufferedReader incoming;
		PrintWriter outgoing;
		String messageIn;
		
		try{
			connection = new Socket(ipAdd, curPort);
			incoming = new BufferedReader( new InputStreamReader(connection.getInputStream()) );
			outgoing = new PrintWriter(connection.getOutputStream(), true);
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
			
			//Send out handshake
			outgoing.println(HANDSHAKE);
			
			//take in handshake
			messageIn = incoming.readLine();
			if(! HANDSHAKE.equals(messageIn)){
				throw new Exception("Connected program is not a BCMsgr");
			}
			System.out.println("Connected.");
			
			
			while(true) {
				//System.out.print("> ");
				commandTyped = stdIn.readLine();
				char prependCode;
				if(commandTyped.equalsIgnoreCase("quit")) {
					prependCode = CLOSED_CODE;
				}
				else {
					prependCode = MESSAGE_CODE;
				}
				outgoing.println(prependCode + commandTyped);
			}

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
	}
	
}