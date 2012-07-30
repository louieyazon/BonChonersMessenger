package bcmGUI;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import bcmBackend.*;
import bcmNetworking.*;

public class ChatWindow extends JFrame {

	public ChatWindow(Friend cm, String uname, Bridge br, IPList cip) {
		this.username = uname;
		this.chatmate = cm;
		this.connectedIPs = cip;
		this.setIconImage(BCMTheme.CHAT_ICON);		//prepare window
		this.setTitle(chatmate.getNickname());
		messageLogTextArea.append(BCMTheme.attemptingConnectMessage(chatmate) + "\n");
		
		setComponentProperties();
		setComponentHierarchy();
		loadingIndicator.setVisible(true);
		this.setVisible(true);
		
		msgUpdater = newMessageUpdater();			// initiate networking 
		this.bridge = br;
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public ChatWindow(Friend cm, String un, ReceiveSocketSW rs, SendSocketSW ss, Bridge br, IPList cip) {
		this(cm, un, br, cip);
		this.sendSocket = ss;
		this.receiveSocket = rs;
		this.receiveSocket.setInformable(msgUpdater);
		enableChat();
	}
	
	public void timeToClose() {
		bridge.putMessage(BCMProtocol.CLOSED_CODE+ "");
		if (receiveSocket != null) receiveSocket.end();
		if (sendSocket != null)    sendSocket.end();
		//FIXME what if chatmate is null? Is chatmate ever null?
		System.out.println(chatmate.getIP());
		connectedIPs.removeIP(chatmate.getIP());		//removeIP has internal checking if it exists 
    	this.dispose();
	}
	
	public void enableChat (){
		composeMessageField.setEnabled(true);
		messageLogTextArea.setEnabled(true);
		btnBuzz.setEnabled(true);
		btnSendButton.setEnabled(true);
		composeMessageField.grabFocus();
		this.setTitle(chatmate.getNickname());
		loadingIndicator.setVisible(false);
		lblActivityLabel.setText("");
		messageLogTextArea.append("Now chatting with " + chatmate.getNickname() + "\n");
		updatetimer.start();
		
	}
	

	
	private String getMessageBoxContents() {
		if(!composeMessageField.getText().equals("")) {
			String toSend = BCMProtocol.MESSAGE_CODE + composeMessageField.getText();
			messageLogTextArea.append("\n" + username + ": " + toSend.substring(1));
			composeMessageField.setText("");
			composeMessageField.grabFocus();
			return toSend;
		}
		return "";
	}
	
	private void sendIsTyping() {
		bridge.putMessage(BCMProtocol.ISTYPING_CODE + "");		
	}
	
	private void sendBuzz() {
		bridge.putMessage(BCMProtocol.BUZZ_CODE + "");
	}
	

	
	
	
	//XXX BUZZ FUNCTION
	private void buzzWindow(){
		if (currentBuzzFrame == 0) {
			
			newBuzzArray();
			boundsholder = this.getBounds();
			buzztime.start();
			messageLogTextArea.append("\n BUZZ!!");
			new bcmPlaySound("Greetings.wav").start();
		}	
	}

	private void newBuzzArray() {
		bdaX = new int[BCMTheme.MAX_BUZZ_FRAMES];
		bdaY = new int[BCMTheme.MAX_BUZZ_FRAMES];
		for(int r = 0; r < BCMTheme.MAX_BUZZ_FRAMES; r++) {
			double direction = (Math.random() * 180) * ((currentBuzzFrame % 2) + 1);
			bdaX[r] = (int)(Math.cos(direction) * baMagnitude[r]);
			bdaY[r] = (int)(Math.sin(direction) * baMagnitude[r]);
		}
	}
	
	private void genBuzzMagnitudeArray() {
		for(int r = 0; r < BCMTheme.MAX_BUZZ_FRAMES; r++) {
			baMagnitude[r] = BCMTheme.MAX_BUZZ_MAGNITUDE / (int)(Math.pow((double)r + 1, 1.2));
			//System.out.printf("%1.1f  ", baMagnitude[r]);		//DEBUG display Magnitude array
		}
	}
	
	private ActionListener evlJiggle = new ActionListener() {
		public void actionPerformed(ActionEvent ae){
			oscillateWindow();
		}
	};
	
	private void oscillateWindow(){
		if (currentBuzzFrame < BCMTheme.MAX_BUZZ_FRAMES) {
			this.setBounds(boundsholder.x + bdaX[currentBuzzFrame], boundsholder.y + bdaY[currentBuzzFrame], boundsholder.width, boundsholder.height);
			currentBuzzFrame++;
		} else {
			buzztime.stop();
			buzzBuffer--;
			currentBuzzFrame = 0;
			this.setBounds(boundsholder);
			return;
		}
	}
	
	
	
	//LISTENERS
	private KeyAdapter evlmsgField = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent k) {
			
			sendIsTyping();
			
			if (k.getKeyCode() == KeyEvent.VK_ENTER) { 
				bridge.putMessage(getMessageBoxContents());
			}
			
			if (k.getKeyCode() == KeyEvent.VK_B && k.getModifiers() == KeyEvent.VK_CONTROL) {
				buzzWindow();
			}
		}
	};
	
	private ActionListener evlSendButton = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			bridge.putMessage(getMessageBoxContents());
		}
	};
	
	private ActionListener evlBuzzer = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			if (currentBuzzFrame == 0) { 
				buzzBuffer++;
				sendBuzz();
				buzzWindow();
			}
			
		}
	};
	
	private ActionListener evlUpdater = new ActionListener(){
		public void actionPerformed(ActionEvent arg0){
			showNotTyping();
		}
	};
	
	//XXX INCOMING MESSAGE PARSING
	private void parseIncomingMessage(String currMessage) {
		char code;
			code = currMessage.charAt(0);
			if(code == BCMProtocol.MESSAGE_CODE) {
				messageLogTextArea.append(  BCMTheme.chatMessage(chatmate, currMessage.substring(1))  );
				showNotTyping();
			}
			else if (code == BCMProtocol.CLOSED_CODE) {
				messageLogTextArea.append("\n" + chatmate.getNickname() + " has gone offline.");
				JOptionPane.showMessageDialog(null, "Chatmate has left the chat. Closing Window.");
				timeToClose();
			}
			else if (code == BCMProtocol.ISTYPING_CODE) {
				showIsTyping();
			}
			else if (code == BCMProtocol.BUZZ_CODE) {
				buzzBuffer++;
				buzzWindow();
				showNotTyping();
			}
	}
	
	private void showIsTyping() {
		this.setTitle(BCMTheme.isTypingMessage(chatmate));
		lblActivityLabel.setText(BCMTheme.isTypingMessage(chatmate));
	}
	
	private void showNotTyping() {
		this.setTitle(chatmate.getNickname());
		lblActivityLabel.setText("");
	}
	
	private WindowAdapter evlCloseWindow = new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
	        timeToClose();
	    }
	};
	
	
	//XXX Networking
	public void setReceiveSocket (ReceiveSocketSW receiveSocket){
		this.receiveSocket = receiveSocket;
		this.receiveSocket.setInformable(msgUpdater);
	}
	
	public void setSendSocket (SendSocketSW sendSocket){
		this.sendSocket = sendSocket;
	}
	
	//FAKEFRIEND
	/*private void connectToFakeFriend() {
		if (ff == null) {
				//TO START A NEW SWINGWORKER THREAD
		        SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		            	ff = new FakeFriend(messageBuffer);
		            	ff.execute();
		            }
		        });
		       //TO START A NEW THREAD
		}
	}*/
	
	
	//XXX SET COMPONENT PROPERTIES
	private void setComponentHierarchy() {
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		pnlComposing.add(composeMessageField, BorderLayout.CENTER);
		pnlComposing.add(btnSendButton, BorderLayout.EAST);
		pnlComposing.add(chatToolBar, BorderLayout.NORTH);
		chatToolBar.add(btnBuzz);
		chatToolBar.add(toolbarSeparator);
		chatToolBar.add(lblActivityLabel);
		chatToolBar.add(Box.createHorizontalStrut(5));
		chatToolBar.add(loadingIndicator);
	}
	
	private void setComponentProperties() {
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(evlCloseWindow);
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
		composeMessageField.grabFocus();
		chatToolBar.setFloatable(false);
		
		loadingIndicator.setMaximumSize(new Dimension(30, 14));
		loadingIndicator.setIndeterminate(true);
		loadingIndicator.setFocusTraversalKeysEnabled(false);
		loadingIndicator.setFocusable(false);
		loadingIndicator.setVisible(false);
		
		// MESSAGE LOG AREA
		scrlpnMsgLogArea.setAutoscrolls(true);
		messageLogTextArea.setEditable(false);
		messageLogTextArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
		messageLogTextArea.setRows(10);
		messageLogTextArea.setAutoscrolls(true);
		messageLogTextArea.setWrapStyleWord(true);
		messageLogTextArea.setLineWrap(true);
		
		//MESSAGE LOG UPDATER TIMER
		updatetimer = new Timer(updatedelay, evlUpdater);
		updatetimer.setRepeats(true);
		
		//ASSIGN LISTENERS
		composeMessageField.addKeyListener(evlmsgField);
		btnSendButton.addActionListener(evlSendButton);

		composeMessageField.grabFocus();
		
		//BUZZ FEATURE
		btnBuzz.setMnemonic('B');
		btnBuzz.setToolTipText("Send a buzz to your friend");
		btnBuzz.addActionListener(evlBuzzer);
		
		buzztime = new Timer(BCMTheme.BUZZ_TIMER_DELAY, evlJiggle);
		buzztime.setRepeats(true);
		genBuzzMagnitudeArray();
		
		//DISABLE BY DEFAULT
		composeMessageField.setEnabled(false);
		messageLogTextArea.setEnabled(false);
		btnBuzz.setEnabled(false);
		btnSendButton.setEnabled(false);
		this.setTitle(BCMTheme.attemptingConnectMessage(chatmate));
		lblActivityLabel.setText(BCMTheme.attemptingConnectMessage(chatmate));
	}
	
	
	public void setConnectedIPs(IPList ipl) {
		connectedIPs = ipl;
	}
	
	
	//XXX VARIABLES
		private static final long serialVersionUID = 958392446358889555L;
		private Friend chatmate;
		private String username;
		private IPList connectedIPs;

		//CONNECTIONS
		//private ManagerSocket mgtSocket = new ManagerSocket();
		
		// MESSAGE LOG UPDATER
		private Informable msgUpdater;
		private Informable newMessageUpdater() {
			return (  new Informable() {
				@Override
				public void messageReceived(String message){
					parseIncomingMessage(message);
				}
			} );
		}
		
		// ISTYPING REFRESHER
		private Timer updatetimer;
		private int updatedelay = 3000;
			
		// BUZZ FUNCTION VARIABLES
			private short buzzBuffer = 0;
			private Timer buzztime;
			private short currentBuzzFrame;
			private int[] bdaX;
			private int[] bdaY;
			private double[] baMagnitude = new double[BCMTheme.MAX_BUZZ_FRAMES];
			private Rectangle boundsholder;
			
		// COMPONENTS
		private JPanel contentPane = new JPanel();	
			private JPanel pnlComposing = new JPanel();
				private JTextField composeMessageField = new JTextField();
				private JButton btnSendButton = new JButton("Send");
				private final JTextArea messageLogTextArea = new JTextArea();
				private final JScrollPane scrlpnMsgLogArea = new JScrollPane(messageLogTextArea);
				private final JToolBar chatToolBar = new JToolBar();
					private final JButton btnBuzz = new JButton("BUZZ");
					private final JLabel lblActivityLabel = new JLabel("");
					private final JProgressBar loadingIndicator = new JProgressBar();
					private final Component toolbarSeparator = Box.createHorizontalStrut(20);
					
		private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrlpnMsgLogArea, pnlComposing);
		
		
		//NETWORKING
		private ReceiveSocketSW receiveSocket;
		private SendSocketSW sendSocket;
		private Bridge bridge;
		
}
