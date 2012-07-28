import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.util.LinkedList;
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
	
	// MESSAGE LOG UPDATER
		LinkedList<String> messageBuffer = new LinkedList<String>();
		Timer updatetimer;
		int updatedelay = 500;
		
	//BUZZ FUNCTION VARIABLES
		short maxbuzzframes = 9;
		Timer buzztime;
		short buzzframes;
		int buzztimerdelay = 34;  // 33.333ms per frame is 30fps
		private int[] bdaX;
		private int[] bdaY;
		private double[] baMagnitude = new double[maxbuzzframes];
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
		
		updatetimer.start();
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
		chatToolBar.setFloatable(false);
		
		// MESSAGE LOG AREA
		scrlpnMsgLogArea.setAutoscrolls(true);
		textArea.setEditable(false);
		textArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
		textArea.setRows(10);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		
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
		
		buzztime = new Timer(buzztimerdelay, evlJiggle);
		buzztime.setRepeats(true);
		genBuzzMagnitudeArray();
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
		if (buzzframes == 0) {
			newBuzzArray();
			boundsholder = this.getBounds();
			sendMessage("BUZZ!!");
			buzztime.start();
		}	
	}

	private void newBuzzArray() {
		bdaX = new int[maxbuzzframes];
		bdaY = new int[maxbuzzframes];
		for(int r = 0; r < maxbuzzframes; r++) {
			double direction = Math.random() * 360;
			bdaX[r] = (int)(Math.cos(direction) * baMagnitude[r]);
			bdaY[r] = (int)(Math.sin(direction) * baMagnitude[r]);
		}
	}
	
	private void genBuzzMagnitudeArray() {
		for(int r = 0; r < maxbuzzframes; r++) {
			baMagnitude[r] = Math.pow((double)maxbuzzframes + 1,  1.2) / (int)(Math.pow((double)r + 1, 1.2));
		}
	}
	
	// EVL JIGGLE IS REPEATEDLY TRIGGERED
	private ActionListener evlJiggle = new ActionListener() {
		public void actionPerformed(ActionEvent ae){
			oscillateWindow();
		}
	};
	
	// CONSEQUENTLY, OSCILLATEWINDOW IS CALLED SEVERAL TIMES
	private void oscillateWindow(){
		if (buzzframes < maxbuzzframes) {
			this.setBounds(boundsholder.x + bdaX[buzzframes], boundsholder.y + bdaY[buzzframes], boundsholder.width, boundsholder.height);
			buzzframes++;
		} else {
			buzztime.stop();
			buzzframes = 0;
			this.setBounds(boundsholder);
			return;
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
	
	
	
	// UPDATER
	private ActionListener evlUpdater = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			updateMessageLog();
		}
	};
	
	private void updateMessageLog() {
		while(!messageBuffer.isEmpty())
		{   textArea.append(messageBuffer.getFirst());
			messageBuffer.removeFirst();
		}
	}
	
	// WINDOW CLOSER
	private WindowAdapter evlCloseWindow = new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
	        timeToClose();
	    }
	};
	
	private void timeToClose() {
		ff.cancel(false);
    	this.dispose();
	}
	
	
	
	//FAKEFRIEND
	private void connectToFakeFriend() {
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
	}
	
	
	
	
}
