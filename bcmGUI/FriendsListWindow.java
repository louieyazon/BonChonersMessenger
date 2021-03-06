package bcmGUI;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import bcmBackend.*;
import bcmNetworking.*;

public class FriendsListWindow extends JFrame {
	
	//XXX CONSTRUCTOR
	public FriendsListWindow() throws IOException {
		setWindowProperties();
		setComponentProperties(); 				// contains all component modification
		setLoginComponentProperties();
		setFriendsListComponentHierarchy(); 	// contains all the panel.add calls for friends list
		setLoginScreenComponentHierarchy(); 	// contains all panel.add calls for login screen
		buildFriendListButtons(); 				// initiates the friends list array
		//modeFriendsList();
		modeLogin();	
	}
		
	//XXX WINDOW MODES
	private void signin() {
		if (!fldUserName.getText().trim().equals("")) {
			username = fldUserName.getText();
			lblMessengerStatus.setText(BCMTheme.statusText(BCMTheme.STATUS_SIGNEDIN, username));
			connectedIPs = new IPList();
			managerSocket = new ManagerSocket(friendListObj, username, connectedIPs);
			modeFriendsList();
			imOnline = true;
			BCMTheme.playLogin();
		}
	}
	
	private void modeFriendsList() {
		this.setContentPane(pnlMainFriends);
		this.repaint();
		this.revalidate();
	}
	
	private void signout() {
		if (managerSocket != null) {
			managerSocket.stopNow();
			managerSocket = null;
		}
		modeLogin();
	}

	private void modeLogin() {
		imOnline = false;
		this.setContentPane(pnlLogin);
		this.repaint();
		this.revalidate();
	}
	
	private void timeToClose() {
    	friendListObj.saveChanges();
    	System.out.println("Friend list saved.");
		System.exit(0);
	}
	
	//XXX ChatWindow Initiation
	private void startChat() {
		if (  connectedIPs.find(selectedFriend.getIP()) == null  ) {		// reject double chat window
			System.out.println("trying to connect");
			connectedIPs.add(selectedFriend.getIP());
			System.out.println(connectedIPs);
			try { new RequestSocket(selectedFriend, username, connectedIPs); }
			catch (Exception e) {
				e.printStackTrace();
				System.out.println(connectedIPs);
			}
		}
	}
	
	private void startChat(Friend f) {
		selectedFriend = f;
		startChat();
		selectedFriend = null;
	}
	
	
	
	//XXX Friendlist manipulators
	private void addFriend() {
		selectedFriend = new Friend("","","");
		friendListObj.getList().add(selectedFriend);
		editFriend();
		
		if (selectedFriend.isEmpty()) {					//add friend canceled
			friendListObj.getList().remove(selectedFriend);
			selectedFriend = null;
		} else {										//add friend success
			wipeFriendListButtons();
			buildFriendListButtons();
			scrollPane.revalidate();
			friendListObj.saveChanges();
		}
	}
		
	private void editFriend() {
		new AddFriendDialog(selectedFriend);
		refreshFriendList();
		friendListObj.saveChanges();
	}
	
	private void deleteFriend() {
		int trulyDelete = JOptionPane.showConfirmDialog(this, "Delete this contact?", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
		
		if (trulyDelete == JOptionPane.OK_OPTION)
		{	friendListObj.getList().remove(selectedFriend);
			refreshFriendListLabels();
			friendListObj.saveChanges();
		} 
	}

	private void refreshFriendListLabels() {
		selectedFriend = null;
		wipeFriendListButtons();
		buildFriendListButtons();
		scrollPane.revalidate();
	}
	
	private void importFriendsList() {
		int chosenOption = fchooser.showOpenDialog(this);
        if (chosenOption == JFileChooser.APPROVE_OPTION) {
            File importedFile = fchooser.getSelectedFile();
            friendListObj.importFile(importedFile);
            
            refreshFriendListLabels();
        } 
	}	
	
	private void showContactRightClickMenu() {
		System.out.println("right clicked");
		mnuRightClickContact.setLocation(getMousePosition().x + this.getLocation().x, getMousePosition().y + this.getLocation().y);
		mnuRightClickContact.setVisible(true);
	}
	
	
	private void hideContactRightClickMenu() {
		mnuRightClickContact.setVisible(false);
	}
	

	private void refreshFriendList() {
		JLabel currentLabel;
		Friend currentFriend;
		for (int i = 0; i < friendLabel.size(); i++) {
			currentLabel = friendLabel.get(i);
			currentFriend = friendListObj.getList().get(i);
			currentLabel.setText(currentFriend.getNickname());
			currentLabel.setToolTipText(BCMTheme.genTp(currentFriend));
		}
	}

	private void wipeFriendListButtons(){
		friendLabel = new LinkedList<JLabel>();
		friendListPanel.removeAll();
	}
	
	private void buildFriendListButtons() {
		JLabel currentLabel;
		Friend currentFriend;
		// traverse through all friends in friendListObj
		// generate JLabels for each
		for (int i = 0; i < friendListObj.getList().size(); i++) {
			currentFriend = friendListObj.getList().get(i);
			
			currentLabel = new JLabel(currentFriend.getNickname());
			currentLabel.setOpaque(true);
			currentLabel.setBackground(BCMTheme.colBG);
			currentLabel.setIcon(BCMTheme.CONTACT_ICON);
			currentLabel.setIconTextGap(BCMTheme.icongap);
			currentLabel.setFocusable(true);
			currentLabel.setToolTipText(BCMTheme.genTp(currentFriend));
			currentLabel.addMouseListener(evlContactClick);
			currentLabel.setName("f" + i);
			currentLabel.setCursor(BCMTheme.FRIEND_CURSOR);
			
			friendLabel.add(currentLabel);
			friendListPanel.add(Box.createVerticalStrut(BCMTheme.strutHeight));
			friendListPanel.add(currentLabel);
		}
	}
	
	
	private void deselectLabel() {
		if (selectedLabel != null ) {
			deselectLabel(selectedLabel);
		}
	}
	
	private void deselectLabel(JLabel l) {
		l.setBackground(BCMTheme.colBG);
		l.repaint();
	}
		
	private void selectLabel(JLabel l) {
		selectedLabel = l;
		selectedLabel.setBackground(BCMTheme.colLightBlue);
		selectedLabel.repaint();
	}
	
	
	
	
	private void setWindowProperties() {
		this.setTitle("BonChonMessenger");
		this.setBackground(BCMTheme.colBG);
		this.setMinimumSize(dimMinWindowSize);
		this.setIconImage(BCMTheme.CLIENT_ICON);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(evlCloseWindow);
		this.setBounds(100, 100, 259, 357);	
	}
	
	
	private void setLoginComponentProperties() throws UnknownHostException{
		pnlLogin.setLayout(new BoxLayout(pnlLogin, BoxLayout.Y_AXIS));
		pnlLoginFields.setMaximumSize(new Dimension(150, 300));
		pnlLoginFields.setLayout(new BoxLayout(pnlLoginFields, BoxLayout.Y_AXIS));
		pnlLoginFields.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		pnlLoginFields.setBackground(BCMTheme.colWhite);

		// FIELDS AND BUTTON
		lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
		fldUserName.setText("");
		fldUserName.setColumns(BCMTheme.loginFieldWidth);
		
		lbliClientImage.setPreferredSize(new Dimension(150, 150));
		lbliClientImage.setAlignmentX(JComponent.CENTER_ALIGNMENT);
		lbliClientImage.setIcon(BCMTheme.MESSENGER_LOGO);
		
		btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnLogin.addMouseListener(evlSignIn);
		btnLogin.setMaximumSize(BCMTheme.dimButtonSize);
		btnLogin.setMinimumSize(BCMTheme.dimButtonSize);
		btnLogin.setPreferredSize(BCMTheme.dimButtonSize);
		
		lblIp.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblIp.setText("IP: "+ InetAddress.getLocalHost().getHostAddress());
	}
	
	private void setComponentProperties() {
		// MAIN PANE
		pnlMainFriends.setBackground(BCMTheme.colBG);
		pnlMainFriends.setBorder(new EmptyBorder(0, 0, 0, 0));
		pnlMainFriends.setLayout(new BorderLayout(0, 0));
		pnlProg.setLayout(new BorderLayout(0, 0));

		// LOGIN PANE
		pnlLogin.setBackground(BCMTheme.colBG);
		pnlLogin.setBorder(new EmptyBorder(0, 0, 0, 0));
		pnlLogin.setLayout(new BorderLayout(0, 0));

		// TOP PANEL
		topPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		txtSearch.addKeyListener(evlSearchBoxType);
		
		// SEARCH BOX
		txtSearch.setForeground(BCMTheme.colGrayedText);
		txtSearch.setText(deftxtSearch);
		txtSearch.setColumns(10);
		txtSearch.addFocusListener(evlsearchGray);

		// FRIENDS LIST
		scrollPane.setToolTipText("Friendlist");
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBackground(BCMTheme.colWhite);
		
		friendListPanel.setBorder(new EmptyBorder(3, 7, 0, 0));
		friendListPanel.setBackground(BCMTheme.colBG);
		friendListPanel.setSize(this.getSize());
		friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));
		
		//XXX LISTENER ASSIGNMENTS
		mntmrAddContact.addMouseListener(evlAddContact);
		mntmrEditContact.addMouseListener(evlEditContact);	
		mntmrDeleteContact.addMouseListener(evlDeleteContact);
		
		fldUserName.addKeyListener(evlLoginFromTextField);
		friendListPanel.addMouseListener(evlPanelClick);
		mntmSignOut.addActionListener(evlSignOut);
		mntmExit.addActionListener(evlExitMenu);
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		mntmImportFriends.addActionListener(evlImportFriends);
		mntmImportFriends.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		
	}

	private void setFriendsListComponentHierarchy() {
		mnuRightClickContact.setFocusTraversalKeysEnabled(true);
		mnuRightClickContact.setInheritsPopupMenu(true);
		
		
		pnlProg.add(mnuRightClickContact);
			mnuRightClickContact.add(mntmrAddContact);
			mnuRightClickContact.add(mntmrEditContact);
			mnuRightClickContact.add(mntmrDeleteContact);
			
		pnlMainFriends.add(pnlProg, BorderLayout.CENTER);
		pnlProg.add(topPanel, BorderLayout.NORTH);
		topPanel.add(txtSearch);
		pnlProg.add(scrollPane, BorderLayout.CENTER);
		
		
		//MENU BAR
		pnlMainFriends.add(menuBar, BorderLayout.NORTH);
		menuBar.setAlignmentX(Component.RIGHT_ALIGNMENT);
		menuBar.setBorderPainted(false);
		menuBar.add(mnMessenger);
			mnMessenger.add(mntmSignOut);
			mnMessenger.add(mntmImportFriends);
			mnMessenger.add(mntmExit);
			
			menuBar.add(mnHelp);
			mntmAbout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					new AboutDialog();
				}
			});
			
			mnHelp.add(mntmAbout);
		
		//STATUS BAR
		tbStatusBar.setFloatable(false);
		pnlMainFriends.add(tbStatusBar, BorderLayout.SOUTH);
		tbStatusBar.add(lblMessengerStatus);
	}
	
	private void setLoginScreenComponentHierarchy(){
		pnlLogin.add(Box.createVerticalGlue());
		pnlLogin.add(pnlLoginFields);
		pnlLogin.add(Box.createVerticalGlue());
		
		pnlLoginFields.add(lbliClientImage);
			pnlLoginFields.add(lblUsername);
			pnlLoginFields.add(fldUserName);		
				pnlLoginFields.add(Box.createVerticalStrut(BCMTheme.strutHeight));
			pnlLoginFields.add(btnLogin);
			pnlLoginFields.add(lblIp);
	}
	
	
	
	
	
	
	//XXX VARIABLES
	private static final long serialVersionUID = 1L;
	
	// ADT
	private Friend selectedFriend; 
	private FriendList friendListObj = new FriendList();
	private LinkedList<JLabel> friendLabel = new LinkedList<JLabel>();
	private IPList connectedIPs = new IPList();
	private JLabel selectedLabel;
	
	// VALUES
	private Dimension dimMinWindowSize = new Dimension(200, 250);
	private boolean imOnline = false;
	private String username;

	//NETWORKING
	private ManagerSocket managerSocket;
	
	// SWING
	private JPanel pnlLogin = new JPanel();
	private JPanel pnlMainFriends = new JPanel();
	private final JPanel pnlProg = new JPanel();
	final JFileChooser fchooser = new JFileChooser();
	
	// LOGIN PANEL
	private JPanel pnlLoginFields = new JPanel();
	private JLabel lbliClientImage = new JLabel("   ");
	private final JLabel lblUsername = new JLabel("Username");
	private JTextField fldUserName = new JTextField();
	private JButton btnLogin = new JButton("Sign In");
	private JLabel lblIp = new JLabel ();

	// TOP PANEL
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnMessenger = new JMenu("Messenger");
	private final JMenuItem mntmExit = new JMenuItem("Exit");
	private final JMenuItem mntmSignOut = new JMenuItem("Sign out");
	private final JMenuItem mntmImportFriends = new JMenuItem("Import Friends List...");
	private JPanel topPanel = new JPanel();
	private JTextField txtSearch = new JTextField();
	private String deftxtSearch = "Search...";

	// MAIN FRIENDS LIST
	private JPanel friendListPanel = new JPanel();
	private JScrollPane scrollPane = new JScrollPane(friendListPanel);
	private final JToolBar tbStatusBar = new JToolBar();
	private final JLabel lblMessengerStatus = new JLabel("Signed In");
	
	// RIGHT CLICK MENU
	private final JPopupMenu mnuRightClickContact = new JPopupMenu();
		private final JMenuItem mntmrAddContact = new JMenuItem("Add Contact...");
		private final JMenuItem mntmrEditContact = new JMenuItem("Edit Contact...");
		private final JMenuItem mntmrDeleteContact = new JMenuItem("Delete Contact...");
		




	

	
	
	
	

	//XXX LISTENERS
	private FocusAdapter evlsearchGray = new FocusAdapter() {
		@Override
		public void focusGained(FocusEvent fe) {
			txtSearch.setForeground(SystemColor.windowText);
			if (txtSearch.getText().equals("Search..."))
				txtSearch.setText("");
		}

		@Override
		public void focusLost(FocusEvent fe) {
			
			if (txtSearch.getText().equals("")) {
				txtSearch.setForeground(BCMTheme.colGrayedText);
				txtSearch.setText(deftxtSearch);
			}
		}
	};
	
	private ActionListener evlExitMenu = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			timeToClose();
		}
	};
	
	private ActionListener evlImportFriends = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			importFriendsList();
		}
	};
	
	private ActionListener evlSignOut = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			signout();
		}
	};

	private MouseAdapter evlSignIn = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			signin();
		}
	};
	
	
	private KeyAdapter evlSearchBoxType = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent ke) {
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) { 
				attemptSearchBoxChat();
			}
		}
	};
	
	
	private void attemptSearchBoxChat() {
		Friend friendToChat = friendListObj.searchFriendWithNickname(txtSearch.getText().trim());
		if (friendToChat != null) {
			startChat( friendToChat );
			txtSearch.setText("");
		}
		
	}
	
	
	private KeyAdapter evlLoginFromTextField = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent k) {
			if(k.getKeyCode()== KeyEvent.VK_ENTER){
				signin();
			}
		}
	};
	
	
	private MouseAdapter evlAddContact = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			hideContactRightClickMenu();
			addFriend();
		}
	};
	
	private MouseAdapter evlEditContact = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			hideContactRightClickMenu();
			editFriend();
		}
	};
	
	private MouseAdapter evlDeleteContact = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			hideContactRightClickMenu();
			deleteFriend();
		}
	};
	
	private WindowAdapter evlCloseWindow = new WindowAdapter() {
	    public void windowClosing(WindowEvent e) {
	        timeToClose();
	    }
	};
	
	// Listener for clicks on contacts
	private MouseAdapter evlContactClick = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			
			// VISUAL FEEDBACK ON CLICKED LABEL
			if (selectedLabel != null) { deselectLabel(selectedLabel); }
			selectLabel((JLabel) me.getComponent());

			// REFLECT FRIEND SELECTION
			int f = Integer.parseInt(selectedLabel.getName().substring(1));
			selectedFriend = friendListObj.getList().get(f);
			
			// IF RIGHT CLICK
			if (me.getButton() == MouseEvent.BUTTON3) {
				showContactRightClickMenu();
			}
			
			// IF LEFT CLICK
			if ( me.getButton() == MouseEvent.BUTTON1) {
				hideContactRightClickMenu();
				
				// DOUBLE CLICK OPENS CHAT WIDOW
				if (me.getClickCount() == 2) {					
					startChat();
				}
				
			}
		}
		
	};
	
	private MouseAdapter evlPanelClick = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			hideContactRightClickMenu();
			deselectLabel();
		}
	};	
	private final JMenu mnHelp = new JMenu("Help");
	private final JMenuItem mntmAbout = new JMenuItem("About...");
	
	

	
}
