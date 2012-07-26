package bcmServer;

import java.io.*;
import java.net.*;

public class ChatClient extends Thread {
	static final int DEF_PORT = 3232;
	static final int DEF_PORT2 = 3233;
	static final String HANDSHAKE = "PaulPogi";
	static final char MESSAGE_CODE = '0';
	static final char CLOSED_CODE = '4';

	private String commandTyped;
	private char prependCode;
	private Socket connection;
	private BufferedReader incoming;
	private PrintWriter outgoing;
	private String messageIn;
	private String ipAdd;
	private int curPort;
	
	public ChatClient(String ipAdd, int curPort){
		this.ipAdd = ipAdd;
		this.curPort = curPort;
		this.start();
		
	}
	
	public void run(){
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
				System.out.print("> ");
				commandTyped = stdIn.readLine();

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