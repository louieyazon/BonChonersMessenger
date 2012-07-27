import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.ImageIcon;


public class BCMTheme {

	public static Color colGrayedText = new Color(200, 200, 200);
	public static Color colWhite = new Color(255, 255, 255);
	public static Color colLightBlue = new Color(235,235,255);
	public static Color colBG = new Color(255, 255, 255);
	
	public static int strutHeight = 5;
	public static Dimension dimButtonSize = new Dimension(200, 25);
	public static int loginFieldWidth = 15;
	public static int icongap = 10;
	
	public static Cursor friendCursor = new Cursor(Cursor.HAND_CURSOR);
	public static ImageIcon contactIcon = new ImageIcon("fb.ico");
	
	public static int STATUS_SIGNEDIN = 1;
	
	public static String statusText (int a, String username) {
		if (a == STATUS_SIGNEDIN) {
			return ("Signed in as " + username);
		}
		
		return "";
	}
	
	public static String genTp(String un, String ip){
		return (un + " (" + ip + ")");
	}
	
	
	public static String genTp(Friend fr){
		return (fr.getUsername() + " (" + fr.getIP() + ")");
	}
	
	
}
