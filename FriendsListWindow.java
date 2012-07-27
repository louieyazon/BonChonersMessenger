import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class FriendsListWindow extends JFrame {

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
	

	// MENU BAR
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnNewMenu = new JMenu("Messaging");
		private final JMenuItem mntmMessaging = new JMenuItem("New Chat Window");
		private final JMenuItem mntmExit = new JMenuItem("Exit");
	private final JMenu mnContacts = new JMenu("Contacts");
		private final JMenuItem mntmAddContact = new JMenuItem("Add Contact...");
		private final JMenuItem mntmEditContact = new JMenuItem("Edit Contact...");
		private final JMenuItem mntmDeleteContact = new JMenuItem("Delete Contact...");
	private final JMenu mnHelp = new JMenu("Help");
		private final JMenuItem mntmAbout = new JMenuItem("About...");

	// TOP PANEL
	private JPanel topPanel = new JPanel();
	private JComboBox cmbStatus = new JComboBox();
	private JTextField txtSearch = new JTextField();
	private String deftxtSearch = "Search...";

	// MAIN FRIENDS LIST
	private JPanel friendListPanel = new JPanel();
	private JScrollPane scrollPane = new JScrollPane(friendListPanel);
	
	// RIGHT CLICK MENU
	private final JPopupMenu mnuRightClickContact = new JPopupMenu();
		private final JMenuItem mntmrAddContact = new JMenuItem("Add Contact...");
		private final JMenuItem mntmrEditContact = new JMenuItem("Edit Contact...");
		private final JMenuItem mntmrDeleteContact = new JMenuItem("Delete Contact...");
		
	// ADT
	private Friend selectedFriend; 
	private FriendList friendListObj = new FriendList();
	private LinkedList<JLabel> friendLabel = new LinkedList<JLabel>();
	private ImageIcon serviceo = new ImageIcon("fb.ico");

	// VALUES
	private Dimension dimMinWindowSize = new Dimension(200, 250);
	private boolean imonline = false;

	// LISTENERS
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
			modeFriendsList();
		}
	};
	
	
	private ItemListener evlSignOut = new ItemListener() {
		public void itemStateChanged(ItemEvent ie) {
			if (ie.getStateChange() == ItemEvent.SELECTED && ie.getItem().toString().equalsIgnoreCase("Offline")) {
				
				modeLogin();
			}
			
		}
	};
	
	
	// Listener for contacts
	private MouseAdapter evlContactClick = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent me) {
			
			
			int f = Integer.parseInt(me.getComponent().getName().substring(1));
			selectedFriend = friendListObj.getList().get(f);
			
			//RIGHT CLICK
			if (me.getButton() == MouseEvent.BUTTON3){
				showContactRightClickMenu();
			}
			
			
			//LEFT CLICK
			if ( me.getButton() == MouseEvent.BUTTON1) {
				hideContactRightClickMenu();
				
				if (me.getClickCount() == 2) {
					try {
						new ChatWindow(selectedFriend);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	
	

	// Exit Menu Item
	private ActionListener evlExit = new ActionListener() {
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	};
	

	
	// CONSTRUCTOR
	public FriendsListWindow() {
		setWindowProperties();
		setComponentProperties(); // contains all component modification
		setLoginComponentProperties();
		
		setFriendsListComponentHierarchy(); // contains all the panel.add calls for friends list
		setLoginScreenComponentHierarchy(); // contains all panel.add calls for login screen
		
		

		buildFriendListButtons(); // initiates the friends list array
		modeFriendsList();
		
		//modeLogin();
	}

	private void setWindowProperties() {
		// WHOLE WINDOW
		this.setTitle("BonChonMessenger");
		this.setBackground(BCMTheme.colBG);
		this.setMinimumSize(dimMinWindowSize);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 259, 357);
		
	}
	
	
	
	
	
	private void setLoginComponentProperties(){
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
		mnNewMenu.setMnemonic('M');
		cmbStatus.addItemListener(evlSignOut);


		// STATUS COMBO BOX
		cmbStatus.setToolTipText("Set status");
			cmbStatus.addItem("Available");
			cmbStatus.addItem("Offline");
		mntmExit.addActionListener(evlExit);
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));

		
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

		friendListPanel.setBackground(BCMTheme.colBG);
		friendListPanel.setSize(this.getSize());
		friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));
		
		
		// RIGHT CLICK MENU PROPERTIES

		mnContacts.setMnemonic('C');
		mnHelp.setMnemonic('H');
		 
		
	}

	
	
	
	
	
	private void setFriendsListComponentHierarchy() {
		pnlMainFriends.add(menuBar, BorderLayout.NORTH);
		
		menuBar.add(mnNewMenu);
			mntmMessaging.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
			mnNewMenu.add(mntmMessaging);
			mnNewMenu.add(new JSeparator());
			mnNewMenu.add(mntmExit);

		menuBar.add(mnContacts);
			mnContacts.add(mntmAddContact);
			mnContacts.add(mntmEditContact);
			mnContacts.add(mntmDeleteContact);
			
		menuBar.add(mnHelp);
			mnHelp.add(mntmAbout);
		mnuRightClickContact.setFocusTraversalKeysEnabled(true);
		mnuRightClickContact.setInheritsPopupMenu(true);
		
		
		
		pnlProg.add(mnuRightClickContact);
			mntmrAddContact.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					hideContactRightClickMenu();
					addFriend();
				}
			});
			
			
			mnuRightClickContact.add(mntmrAddContact);
			
			
			
			mntmrEditContact.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent me) {
					hideContactRightClickMenu();
					editFriend();
				}
			});
			
			
			
			mnuRightClickContact.add(mntmrEditContact);
			mntmrDeleteContact.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					hideContactRightClickMenu();
					deleteFriend();
				}
			});
			mnuRightClickContact.add(mntmrDeleteContact);
				
				
		
		pnlMainFriends.add(pnlProg, BorderLayout.CENTER);
		pnlProg.add(topPanel, BorderLayout.NORTH);
		topPanel.add(cmbStatus);
		topPanel.add(txtSearch);
		pnlProg.add(scrollPane, BorderLayout.CENTER);
	}
	
	private void addFriend() {
		selectedFriend = new Friend("","","");
		friendListObj.getList().add(selectedFriend);
		editFriend();
		if (selectedFriend.isEmpty()) {
			friendListObj.getList().remove(selectedFriend);
		}
		selectedFriend = null;
	}
	
	
	private void editFriend() {
		AddFriendDialog ad = new AddFriendDialog(selectedFriend);
		refreshFriendList();
	}
	
	private void deleteFriend() {
		friendListObj.getList().remove(selectedFriend);
		selectedFriend = null;
		
		wipeFriendListButtons();
		System.out.println("friendlist cleared");
		buildFriendListButtons();
		System.out.println("buttons rebuilt");
		this.setVisible(false);
		this.setVisible(true);

	}
	
	
	private void wipeFriendListButtons(){
		//for(JLabel fl: friendLabel){ friendLabel.remove(fl); }
		
		friendLabel = new LinkedList<JLabel>();
		friendListPanel.removeAll();
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
	}


	private void modeFriendsList() {
		imonline = true;
		this.setContentPane(pnlMainFriends);
		this.repaint();
		this.setVisible(true);
	}

	private void modeLogin() {
		imonline = false;
		this.setContentPane(pnlLogin);
		this.repaint();
		this.setVisible(true);
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
			currentLabel.setToolTipText(genTp(currentFriend.getUsername(), currentFriend.getIP()));
		}
	}

	
	private void buildFriendListButtons() {
		JLabel currentLabel;
		Friend currentFriend;
		
		// traverse through all friends in friendListObj
		// generate JLabels for each
		for (int i = 0; i < friendListObj.getList().size(); i++) {
			currentFriend = friendListObj.getList().get(i);
			
			currentLabel = new JLabel(currentFriend.getNickname());
			currentLabel.setIcon(serviceo);
			currentLabel.setIconTextGap(10);
			currentLabel.setFocusable(true);
			currentLabel.setToolTipText(genTp(currentFriend.getUsername(), currentFriend.getIP()));
			currentLabel.addMouseListener(evlContactClick);
			currentLabel.setName("f" + i);
			currentLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
			
			friendLabel.add(currentLabel);
			friendListPanel.add(Box.createVerticalStrut(5));
			friendListPanel.add(currentLabel);
		}

	}
	
	
	
	private String genTp(String un, String ip){
		return (un + " (" + ip + ")");
	}
	
	
}
