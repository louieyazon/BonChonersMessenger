package bcmGUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import bcmBackend.Friend;
import bcmBackend.bcmPlaySound;


public class BCMTheme {

	// COLORS
	public static Color colGrayedText = new Color(200, 200, 200);
	public static Color colWhite = new Color(255, 255, 255);
	public static Color colLightBlue = new Color(235,235,255);
	public static Color colError = new Color(250, 150, 150);
	public static Color colBG = new Color(255, 255, 255);
	
	// FRIEND LIST THEME
	public static int strutHeight = 5;
	public static Dimension dimButtonSize = new Dimension(200, 25);
	public static int loginFieldWidth = 15;
	public static int icongap = 6;
	
	public static Cursor FRIEND_CURSOR = new Cursor(Cursor.HAND_CURSOR);
	public static ImageIcon CONTACT_ICON = new ImageIcon("bcmchaticon.png");
	public static ImageIcon MESSENGER_LOGO = new ImageIcon("BCMLogo.png");
	public static ImageIcon MESSENGER_LOGO_BIG = new ImageIcon("BCMLogoAbout.png");
	public static Image CHAT_ICON = Toolkit.getDefaultToolkit().getImage("bcmicon.png");
	public static Image CLIENT_ICON = Toolkit.getDefaultToolkit().getImage("bcmicon.png");
	public static ImageIcon TROLOLOL = new ImageIcon("trollol.gif");
	
	public static int STATUS_SIGNEDIN = 1;
	
	// BUZZ CONSTANTS
	public static short MAX_BUZZ_FRAMES    = 9;
	public static int   BUZZ_TIMER_DELAY   = 34;  // 33.333ms per frame is 30fps
	public static int   MAX_BUZZ_MAGNITUDE = 30;
	
	
	// MESSAGES
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

	
	public static String attemptingConnectMessage(Friend fr){
		return ("Attempting to connect to " + fr.getNickname() + "...");
	}
	
	public static String isTypingMessage(Friend fr) {
		return (fr.getNickname() + " is typing");
	}
	
	public static String chatMessage(String sender, String msg){
		return ("\n<" + sender + "> " + msg);
	}
	
	public static String chatMessage(Friend fr, String msg) {
		return chatMessage(fr.getNickname(), msg);
	}
	
	
	//XXX SOUNDS
	public static void playBuzz() {
		playSound("buzz.wav");
	}
	
	public static void playMessage() {
		playSound("message.wav");
	}
	
	public static void playNewChat() {
		playSound("newchat.wav");
	}
	
	public static  void playLogin() {
		playSound("login.wav");
	}
	
	public static void playSound(String filename) {
		new bcmPlaySound(filename).start();
	}
	
}
