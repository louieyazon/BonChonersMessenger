package bcmNetworking;

import java.io.*;
import java.net.*;
import javax.swing.SwingWorker;

public class SendSocket extends SwingWorker<Void, String> {
	//Information passed to the Socket to facilitate creation
	private String ipAdd;
	private int curPort;
	
	public SendSocket(String ipAdd, int curPort){
		this.ipAdd = ipAdd;
		this.curPort = curPort;
	}
	

	@Override
	protected Void doInBackground() throws Exception {
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
			outgoing.println(BCMProtocol.HANDSHAKE);

			//take in handshake
			messageIn = incoming.readLine();
			if(! BCMProtocol.HANDSHAKE.equals(messageIn)){
				throw new Exception("Connected program is not a BCMsgr");
			}
			System.out.println("Connected.");





			while(!isCancelled()) {
				//System.out.print("> ");
				commandTyped = stdIn.readLine();

				char prependCode;
				if(commandTyped.equalsIgnoreCase("quit")) {
					prependCode = BCMProtocol.CLOSED_CODE;
				}
				else {
					prependCode = BCMProtocol.MESSAGE_CODE;
				}
				outgoing.println(prependCode + commandTyped);

			}

		} catch (Exception e) {
			System.out.println("Error: " + e);
		}
		return null;
	}

}