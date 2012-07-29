import java.awt.EventQueue;
import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) {
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				
				try { UIManager.setLookAndFeel(   UIManager.getSystemLookAndFeelClassName()   ); } catch (Exception e) {}
				
				try {
					FriendsListWindow messengerWindow = new FriendsListWindow();
					messengerWindow.setVisible(true);					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				
			}
		});
	}



}
