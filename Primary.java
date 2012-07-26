package bcmServer;

import javax.swing.JOptionPane;

public class Primary {
	static final int DEF_PORT = 3232;
	static final int DEF_PORT2 = 3233;
	public static void main (String [] args){
		ChatServer cs = new ChatServer("10.50.46.47", DEF_PORT);
		JOptionPane.showConfirmDialog(null, "Game?");
		System.out.println("Starting Client");
		ChatClient cc = new ChatClient("10.50.46.47", DEF_PORT2);
	}
}
