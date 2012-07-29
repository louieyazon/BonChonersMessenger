package bcmNetworking;

public class BCMProtocol {
	//Default port values
	static final int MANAGER_PORT = 3232;
	static final int MAX_PORT = 55600;
	static final int MIN_PORT = 55501;
	
	//Secret Handshake
	public static final String HANDSHAKE = "PaulPogi";
	
	//list of prefixes that the ChatServer will
	public static final char MESSAGE_CODE = '0';
	public static final char BUZZ_CODE = '1';
	public static final char ISTYPING_CODE = '2';
	public static final char CLOSED_CODE = '3';
	

	

}
