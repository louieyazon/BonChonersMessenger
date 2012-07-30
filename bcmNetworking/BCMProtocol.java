package bcmNetworking;

public class BCMProtocol {
	//Default port values
	static final int MANAGER_PORT = 3232;
	static final int MAX_PORT = 55600;
	static final int MIN_PORT = 55501;
	
	//Secret Handshake
	public static final String HANDSHAKE = "PaulPogi";
	
	//Protocol Messages
	public static final String CONNECTION_DISALLOWED = "disallowed";
	public static final String CONNECTION_APPROVED = "approved";
	public static final String PORT_AVAILABLE = "available";
	public static final String PORT_UNAVAILABLE = "unavailable";
	public static final String CONNECTION_SUCCESS = "completed";
	
	
	
	//list of prefixes that the ChatServer will
	public static final char MESSAGE_CODE = '0';
	public static final char BUZZ_CODE = '1';
	public static final char ISTYPING_CODE = '2';
	public static final char CLOSED_CODE = '3';
	

	

}
