package bcmGUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import bcmBackend.*;
import bcmNetworking.*;

public class FriendsListWindow extends JFrame {
	
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
	private void modeFriendsList() {
		this.setContentPane(pnlMainFriends);
		this.repaint();
		this.revalidate();
	}
	
	private void signin() {
		if (!fldUserName.getText().trim().equals("")) {
			imOnline = true;
			username = fldUserName.getText();
			lblMessengerStatus.setText(BCMTheme.statusText(BCMTheme.STATUS_SIGNEDIN, username)); 
			modeFriendsList();
			connectedIPs = new IPList();
			managerSocket = new ManagerSocket(friendListObj, username, connectedIPs);
		}
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
			selectedFriend = null;
			wipeFriendListButtons();
			buildFriendListButtons();
			scrollPane.revalidate();
			friendListObj.saveChanges();
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
		lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
		fldUserName.setText("userad");
		fldUserName.setColumns(BCMTheme.loginFieldWidth);
		fldPassword.setColumns(BCMTheme.loginFieldWidth);
		
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
		cmbStatus.addItemListener(evlSignOut);

		// STATUS COMBO BOX
		cmbStatus.setToolTipText("Set status");
			cmbStatus.addItem("Available");
			cmbStatus.addItem("Offline");
		// FIXME What's wrong with these items? Also, fix combo box functionality
		
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
		
		// LISTENERS
		mntmrAddContact.addMouseListener(evlAddContact);
		mntmrEditContact.addMouseListener(evlEditContact);	
		mntmrDeleteContact.addMouseListener(evlDeleteContact);
		fldUserName.addKeyListener(evlLoginFromTextField);
		fldPassword.addKeyListener(evlLoginFromTextField);
		friendListPanel.addMouseListener(evlPanelClick);
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
		topPanel.add(cmbStatus);
		topPanel.add(txtSearch);
		pnlProg.add(scrollPane, BorderLayout.CENTER);
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
			pnlLoginFields.add(lblPassword);
			pnlLoginFields.add(fldPassword);			
				pnlLoginFields.add(Box.createVerticalStrut(BCMTheme.strutHeight));
			pnlLoginFields.add(btnLogin);
			pnlLoginFields.add(lblIp);
	}
	
	
	
	
	
	
	//XXX VARIABLES
	private static final long serialVersionUID = 1L;
	private JPanel pnlLogin = new JPanel();
	private JPanel pnlMainFriends = new JPanel();
	private final JPanel pnlProg = new JPanel();

	// LOGIN PANEL
	private JPanel pnlLoginFields = new JPanel();
	private JLabel lbliClientImage = new JLabel("   ");
	private final JLabel lblUsername = new JLabel("Username");
	private JTextField fldUserName = new JTextField();
	private final JLabel lblPassword = new JLabel("Password");
	private JPasswordField fldPassword = new JPasswordField();
	private JButton btnLogin = new JButton("Sign In");
	private JLabel lblIp = new JLabel ();

	// TOP PANEL
	private JPanel topPanel = new JPanel();
	private JComboBox cmbStatus = new JComboBox();
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
		
		
		
	//XXX ADT
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

	private MouseAdapter evlSignIn = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			signin();
		}
	};
	
	
	//FIXME Use this
	private ItemListener evlSignOut = new ItemListener() {
		public void itemStateChanged(ItemEvent ie) {
			if (ie.getStateChange() == ItemEvent.SELECTED && ie.getItem().toString().equalsIgnoreCase("Offline")) {
				modeLogin();
			}
		}
	};
	
	
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
					System.out.println("double click detected");
					
					if (  connectedIPs.find(selectedFriend.getIP()) == null  ) {
						System.out.println("trying to connect");
						connectedIPs.add(selectedFriend.getIP());
						try { new RequestSocket(selectedFriend, username); }
						catch (Exception e) { e.printStackTrace(); }
					}
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
	
	
	
	//XXX IPList
	public class IPList extends LinkedList<String> {
		private static final long serialVersionUID = -2482412766406762970L;

		public String find(String ipToSearch) {
			for (String k: this) {
				if (ipToSearch.equals(k)) return k;
			}
			return null;
		}
		
		
	}
	
	

	
}
