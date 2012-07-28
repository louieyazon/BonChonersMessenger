package bcmNetworking;

public class BCMProtocol {
	//Default port vales
	public static final int MANAGER_PORT = 3232;
	public static final int MAX_PORT = 3400;
	public static final int MIN_PORT = 3301;
	
	//Secret Handshake
	public static final String HANDSHAKE = "PaulPogi";
	
	//list of prefixes that the ChatServer will
	public static final char MESSAGE_CODE = '0';
	public static final char BUZZ_CODE = '1';
	public static final char ISTYPING_CODE = '2';
	public static final char CLOSED_CODE = '3';
	

	

}
