package bcmServer;
import java.net.*;
import java.io.*;

public class ChatServer extends Thread{
	static final int DEF_PORT = 3232;
	static final int DEF_PORT2 = 3233;
	static final String HANDSHAKE = "PaulPogi";
	static final char MESSAGE = '0';
	static final char CLOSED = '4';
	static final int INIT_TYPE = 1;
	static final int PART_TYPE = 2;	
	
	private int port;
	private ServerSocket listener;
	private Socket connection;
	private BufferedReader incoming;
	private PrintWriter outgoing;
	private String messageOut;
	private String messageIn;
	private String ipAdd;
	private int curPort;
	
	public ChatServer(String ipAdd, int curPort){
		this.ipAdd = ipAdd;
		this.curPort = curPort;
		this.start();
		
	}
	
	public void run(){
		port = curPort;
		
		try{
			listener = new ServerSocket(port);
			System.out.println("Listening on port" + listener.getLocalPort());
			connection = listener.accept();
			listener.close();
			incoming  = new BufferedReader(new InputStreamReader(connection.getInputStream()) );
			outgoing = new PrintWriter(connection.getOutputStream());
			
			outgoing.println(HANDSHAKE);
			outgoing.flush();
			
			messageIn = incoming.readLine();
			
			if(! HANDSHAKE.equals(messageIn)){
				throw new Exception("Connected program is not a BCMsgr");
			}
			System.out.println("Connected.");
			
		} catch (Exception e) {
			System.out.println("An error occured while opening the connection.");
			System.out.println(e.toString());
			return;
		}
		
		try{
			while(true) {
				//System.out.println("Starting Loop");
				messageIn = incoming.readLine();
				
				if(messageIn.length() > 0) {
					if(messageIn.charAt(0) == CLOSED) {
						System.out.println("Quit Command Recieved");
						connection.close();
						break;
					}
				}
				System.out.print("You have a message: ");
				messageIn = messageIn.substring(1);
				System.out.println(messageIn);
			}
			
			
		} catch (Exception e) {
			System.out.println("Sorry, an error has occured. Connection lost.");
			System.exit(1);
		}
	}
}
