package bcmNetworking;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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
		
	// ADT
	private Friend selectedFriend; 
	private FriendList friendListObj = new FriendList();
	private LinkedList<JLabel> friendLabel = new LinkedList<JLabel>();
	private JLabel selectedLabel;

	// VALUES
	private Dimension dimMinWindowSize = new Dimension(200, 250);
	private boolean imOnline = false;
	private String username;

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
			signin();
		}
	};
	
	
	private ItemListener evlSignOut = new ItemListener() {
		public void itemStateChanged(ItemEvent ie) {
			if (ie.getStateChange() == ItemEvent.SELECTED && ie.getItem().toString().equalsIgnoreCase("Offline")) {
				modeLogin();
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
					try { new ChatWindow(selectedFriend, username); }
					catch (Exception e) { e.printStackTrace(); }
				}
				
			}
		}
		
	};
	
	
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
	


	// CONSTRUCTOR
	public FriendsListWindow() {
		setWindowProperties();
		setComponentProperties(); // contains all component modification
		setLoginComponentProperties();
		setFriendsListComponentHierarchy(); // contains all the panel.add calls for friends list
		setLoginScreenComponentHierarchy(); // contains all panel.add calls for login screen
		buildFriendListButtons(); // initiates the friends list array
		//modeFriendsList();
		modeLogin();
	}

	
	
	private void setWindowProperties() {
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
		cmbStatus.addItemListener(evlSignOut);


		// STATUS COMBO BOX
		cmbStatus.setToolTipText("Set status");
			cmbStatus.addItem("Available");
			cmbStatus.addItem("Offline");
		// TODO What's wrong with these items? Also, fix combo box functionality
		
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
		friendListPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				hideContactRightClickMenu();
				deselectLabel();
			}
		});

		friendListPanel.setBackground(BCMTheme.colBG);
		friendListPanel.setSize(this.getSize());
		friendListPanel.setLayout(new BoxLayout(friendListPanel, BoxLayout.Y_AXIS));
		
		// LISTENERS
		mntmrAddContact.addMouseListener(evlAddContact);
		mntmrEditContact.addMouseListener(evlEditContact);	
		mntmrDeleteContact.addMouseListener(evlDeleteContact);	
		 
		
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
	
	private void addFriend() {
		selectedFriend = new Friend("","","");
		friendListObj.getList().add(selectedFriend);
		editFriend();
		
		if (selectedFriend.isEmpty() == true) {
			friendListObj.getList().remove(selectedFriend);
			selectedFriend = null;
			System.out.println("friend add canceled");
		} else {
			wipeFriendListButtons();
			buildFriendListButtons();
			System.out.println("friend added");
			scrollPane.revalidate();
		}
	}
	
	
	private void editFriend() {
		new AddFriendDialog(selectedFriend);
		refreshFriendList();
	}
	
	private void deleteFriend() {
		int trulyDelete = JOptionPane.showConfirmDialog(this, "Delete this contact?", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
		
		if (trulyDelete == JOptionPane.OK_OPTION)
		{	friendListObj.getList().remove(selectedFriend);
			selectedFriend = null;
			wipeFriendListButtons();
			buildFriendListButtons();
			scrollPane.revalidate();
		} 
	}
	
	private void wipeFriendListButtons(){
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

	
	
	//WINDOW MODES
	private void modeFriendsList() {
		this.setContentPane(pnlMainFriends);
		this.repaint();
		this.revalidate();
	}
	
	private void signin() {
		imOnline = true;
		username = fldUserName.getText();
		lblMessengerStatus.setText(BCMTheme.statusText(BCMTheme.STATUS_SIGNEDIN, username)); 
		modeFriendsList();
	}

	private void modeLogin() {
		imOnline = false;
		this.setContentPane(pnlLogin);
		this.repaint();
		this.revalidate();
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
	
	
	

	
	
}
