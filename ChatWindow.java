import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;


public class ChatWindow extends JFrame {
	private static final long serialVersionUID = 958392446358889555L;
	
	//VALUES
		private Friend chatmate;
		private ChatClient ctc;
		
	//BUZZ FUNCTION VARIABLES
		short maxbuzzframes = 6;
		Timer buzztime;
		short buzzframes;
		int buzztimerdelay = 30;
		Rectangle boundsholder;
		
	//COMPONENTS
	private JPanel contentPane = new JPanel();
		private JTextPane txtpneMessageLog = new JTextPane();
		private JScrollPane pneMessageScrollPane = new JScrollPane(txtpneMessageLog);	
		private JPanel pnlComposing = new JPanel();
			private JTextField composeMessageField = new JTextField();
			private JButton btnSendButton = new JButton("Send");
			private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pneMessageScrollPane, pnlComposing);
			private final JButton btnBuzz = new JButton("BUZZ");
			
		

	//LISTENERS
	private KeyAdapter evlmsgField = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent k) {
			if (k.getKeyCode() == KeyEvent.VK_ENTER) { 
				sendMessageBoxContents();
			}
		}
	};
	
	private ActionListener evlBuzzer = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			buzzWindow();
		}
	};
	
	
	//CONSTRUCTOR
	public ChatWindow(Friend cm) {
		//prepare window
		this.chatmate = cm;
		this.setTitle(chatmate.getNickname());
		
		ctc = new ChatClient();
		
		//prepare components
		setComponentProperties();
		setComponentHierarchy();
		this.setVisible(true);	
	}

	
	
	private void setComponentProperties() {
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setBounds(100, 100, 450, 300);
		this.setContentPane(contentPane);
		
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(new BorderLayout(0, 0));
		composeMessageField.setPreferredSize(new Dimension(10, 30));
		composeMessageField.setColumns(10);
		pnlComposing.setLayout(new BorderLayout(0, 0));
		
		//ASSIGN LISTENERS
		composeMessageField.addKeyListener(evlmsgField);
		btnBuzz.addActionListener(evlBuzzer);
		pneMessageScrollPane.setColumnHeaderView(btnBuzz);
		
		buzztime = new Timer(buzztimerdelay, evlJiggle);
		buzztime.setRepeats(true);
		
	}

	private void sendMessageBoxContents() {
		ctc.sendMessage(composeMessageField.getText());
		composeMessageField.setText("");
	}
	
	
	private void setComponentHierarchy() {
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		splitPane.resetToPreferredSizes();
		splitPane.setResizeWeight(1.0);
		splitPane.setDividerSize(4);
		
		pnlComposing.add(composeMessageField, BorderLayout.CENTER);
		pnlComposing.add(btnSendButton, BorderLayout.EAST);
	}
	
	
	
	
	
	
	
	// BUZZ FUNCTION
	
	private void buzzWindow(){
		System.out.println("BUZZ");
		if (buzzframes == 0) {
			boundsholder = this.getBounds();
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
		//System.out.println( this.getClass() );
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
	
	
	
}
