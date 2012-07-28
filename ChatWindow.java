import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.*;


public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 958392446358889555L;
	
	//THREAD TESTER // DEBUG
	FakeFriend ff;
	
	//VALUES
	private Friend chatmate;
	private String username;

	//CONNECTIONS
		//private ManagerSocket mgtSocket = new ManagerSocket();
		
	//BUZZ FUNCTION VARIABLES
		short maxbuzzframes = 6;
		Timer buzztime;
		short buzzframes;
		int buzztimerdelay = 30;
		Rectangle boundsholder;
		
	//COMPONENTS
	private JPanel contentPane = new JPanel();	
		private JPanel pnlComposing = new JPanel();
			private JTextField composeMessageField = new JTextField();
			private JButton btnSendButton = new JButton("Send");
			private final JTextArea textArea = new JTextArea();
			private final JScrollPane scrlpnMsgLogArea = new JScrollPane(textArea);
			private final JToolBar chatToolBar = new JToolBar();
				private final JButton btnBuzz = new JButton("BUZZ");
				private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrlpnMsgLogArea, pnlComposing);
	
	//CONSTRUCTOR
	public ChatWindow(Friend cm, String username) {
		this.username = username;
		
		//prepare window
		this.chatmate = cm;
		this.setIconImage(BCMTheme.chatIcon);
		this.setTitle(chatmate.getNickname());
		
		//prepare components
		setComponentProperties();
		setComponentHierarchy();
		this.setVisible(true);	
		
		//SET THIS TEXT IF CONNECTION WAS SUCCESSFUL
		connectToFakeFriend();  //REMOVE ME PLEASE
		textArea.append("Now chatting with " + chatmate.getNickname() + "\n");
	}

	
	
	private void setComponentProperties() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 450, 300);
		this.setContentPane(contentPane);
		
		splitPane.resetToPreferredSizes();
		splitPane.setResizeWeight(1.0);
		splitPane.setDividerSize(4);
		
		pnlComposing.setLayout(new BorderLayout(0, 0));
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		
		composeMessageField.setPreferredSize(new Dimension(10, 30));
		composeMessageField.setColumns(10);
		chatToolBar.setFloatable(false);
		
		// MESSAGE LOG AREA
		scrlpnMsgLogArea.setAutoscrolls(true);
		textArea.setEditable(false);
		textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
		textArea.setRows(10);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		
		
		//ASSIGN LISTENERS
		composeMessageField.addKeyListener(evlmsgField);
		btnSendButton.addActionListener(evlSendButton);
		
		//BUZZ FEATURE
		btnBuzz.setMnemonic('B');
		btnBuzz.setToolTipText("Send a buzz to your friend");
		btnBuzz.addActionListener(evlBuzzer);
		
		buzztime = new Timer(buzztimerdelay, evlJiggle);
		buzztime.setRepeats(true);
		
		composeMessageField.grabFocus();
	}
	
	
	private void sendMessageBoxContents() {
		if(!composeMessageField.getText().equals("")) {
			sendMessage(composeMessageField.getText());
			composeMessageField.setText("");
			composeMessageField.grabFocus();
		}
	}
	
	
	// replace contents with something that hooks onto the client thread 
	private void sendMessage(String msg) {
		//ctc.sendMessage(composeMessageField.getText());
		textArea.append("\n<" + username + "> " + msg + "");   // dummy send
	}
	
	
	private void setComponentHierarchy() {
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		pnlComposing.add(composeMessageField, BorderLayout.CENTER);
		pnlComposing.add(btnSendButton, BorderLayout.EAST);
		pnlComposing.add(chatToolBar, BorderLayout.NORTH);
		chatToolBar.add(btnBuzz);
	}
	
	
	
	
	// BUZZ FUNCTION
	
	private void buzzWindow(){
		//System.out.println("BUZZ");
		if (buzzframes == 0) {
			boundsholder = this.getBounds();
			sendMessage("BUZZ!!");
			buzztime.start();
		}	
	}
		
	// EVL JIGGLE IS REPEATEDLY TRIGGERED
	private ActionListener evlJiggle = new ActionListener() {
		public void actionPerformed(ActionEvent ae){
			randJig();
		}
	};
			
	private void randJig(){
		int rx = (int)(  (Math.random()-Math.random()) * (  30-(buzzframes*4)  )   ) ;
		int ry = (int)(  (Math.random()-Math.random()) * (  30-(buzzframes*4)  )   ) ;
		
		this.setBounds(boundsholder.x + rx, boundsholder.y + ry, boundsholder.width, boundsholder.height);
		
		buzzframes++;
		if (buzzframes > maxbuzzframes) {
			buzztime.stop();
			buzzframes = 0;
			this.setBounds(boundsholder);
		}
	}
	
	
	
	
	

	
	
	
	
	
	
	//LISTENERS
	private KeyAdapter evlmsgField = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent k) {
			if (k.getKeyCode() == KeyEvent.VK_ENTER) { 
				sendMessageBoxContents();
			}
			
			if (k.getKeyCode() == KeyEvent.VK_B && k.getModifiers() == KeyEvent.VK_CONTROL) {
				buzzWindow();
			}
			
			
		}
	};
	
	private ActionListener evlSendButton = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			sendMessageBoxContents();
		}
	};
	
	
	private ActionListener evlBuzzer = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			buzzWindow();
		}
	};
	
	
	
	
	//FAKEFRIEND
	private void connectToFakeFriend() {
		
		if (ff == null) {
			
				//TO START A NEW SWINGWORKER THREAD
		        SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		            	ff = new FakeFriend(textArea);
		            	ff.execute();
		            }
		        });
		       //TO START A NEW THREAD
		}
	}
	
	
	
	
	

	
	
	
	
}
