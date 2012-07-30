package bcmGUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import bcmBackend.Friend;
import bcmBackend.FriendList;
/**
 * 
 * This window accepts a Friend object that has been pre-added to the
 * Friend List Array in the main chatwindow.
 * 
 *
 */

public class AddFriendDialog extends JDialog {
	private static final long serialVersionUID = 5860039927122697711L;
	
	private final JPanel contentPanel = new JPanel();
	private FriendDataField tfUserName = new FriendDataField();
	private FriendDataField tfNickname = new FriendDataField();
	private FriendDataField tfIPAddress = new FriendDataField();
	private Friend currentFriend;
	
	
	//COMPONENTS
	JPanel pnlFriendFields = new JPanel();
	
	JPanel pnlUserName = new JPanel();
	JLabel lblUsername = new JLabel("Username");
	JPanel pnlNickName = new JPanel();
	JLabel lblNickname = new JLabel("Nickname");
	JPanel pnlIPadd = new JPanel();
	JLabel lbliP = new JLabel("IP Address");
	private final JLabel lblErrorLabel = new JLabel("");
	
	
	private KeyAdapter evlTypingInField = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent k) {
			
			
			FriendDataField currentField = (FriendDataField)k.getSource();
			
			if (currentField.getName() == "IPField") {
				currentField.reflectIPError();
			} else {
				currentField.reflectError();
			}
			
			
			
			if (k.getKeyCode() == KeyEvent.VK_ENTER) { 
				saveChanges();
			}
			
			if (k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
				cancelChanges();
			}
			
			
		}
	};
	

	public AddFriendDialog(Friend cF) {
		lbliP.setDisplayedMnemonic('I');
		lbliP.setLabelFor(tfIPAddress);
		lblNickname.setDisplayedMnemonic('N');
		lblNickname.setLabelFor(tfNickname);
		lblUsername.setDisplayedMnemonic('U');
		lblUsername.setLabelFor(tfUserName);
		tfIPAddress.setName("IPField");
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent k) {
				if (k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
					cancelChanges();
				}
			}
		});
		
		
		
		setComponentProperties();
		
		currentFriend = cF;

		
		if (!currentFriend.isEmpty()) {
			
			this.setTitle("Editing " + currentFriend.getNickname());
			tfUserName.setText(currentFriend.getUsername());
			tfNickname.setText(currentFriend.getNickname());
			tfIPAddress.setText(currentFriend.getIP());

			
		} else {
			this.setTitle("New Friend");
			
		}
		this.setVisible(true);
	}

	
	
	

	private void setComponentProperties() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setBounds(100, 100, 321, 200);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		
			
			pnlFriendFields.setSize(200,400);
			pnlFriendFields.setMaximumSize(new Dimension(200, 400));
			pnlFriendFields.setLayout(new BoxLayout(pnlFriendFields, BoxLayout.Y_AXIS));
			
			contentPanel.add(pnlFriendFields);
				pnlFriendFields.add(pnlUserName);
					pnlUserName.add(lblUsername);
					tfUserName.addKeyListener(evlTypingInField);
					pnlUserName.add(tfUserName);
					tfUserName.setColumns(15);
			
				pnlFriendFields.add(pnlNickName);
					pnlNickName.add(lblNickname);
					tfNickname.addKeyListener(evlTypingInField);
					pnlNickName.add(tfNickname);
					tfNickname.setColumns(15);
				pnlFriendFields.add(pnlIPadd);
					pnlIPadd.add(lbliP);
					tfIPAddress.addKeyListener(evlTypingInField);
					pnlIPadd.add(tfIPAddress);
					tfIPAddress.setColumns(15);
				
		
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

				JButton okButton = new JButton("OK");
				okButton.setMnemonic('O');

				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						saveChanges();
					}
				});
				
				buttonPane.add(lblErrorLabel);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);

				JButton cancelButton = new JButton("Cancel");
				cancelButton.setMnemonic('C');
				cancelButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						cancelChanges();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);

	}
	
	
	private void saveChanges() {
		boolean goodData = checkData();
		if (goodData) {
			currentFriend.setUsername(tfUserName.getText().trim());
			currentFriend.setNickname(tfNickname.getText().trim());
			currentFriend.setIP(tfIPAddress.getText().trim());
			this.dispose();
		}
		
	}
	
	
	private boolean checkData() {
		if (tfUserName.containsDelimiter() ||
			tfNickname.containsDelimiter() ||
			!tfIPAddress.goodIP()) {
			return false;
		}
		return true;
	}
	
	private void cancelChanges() {
		this.dispose();
	}
	
	
	
	
	
	
	public class FriendDataField extends JTextField {
		private static final long serialVersionUID = 1L;

		public boolean containsDelimiter() {				
				return (this.getText().contains(FriendList.fileDelimiter));
			}
		
		public boolean goodIP() {
			return Pattern.matches("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)", this.getText().trim());			
		}

		
		
		public void showError () {
				this.setBackground(BCMTheme.colError);
				this.repaint();
			}
		
		public void showNoError() {
				this.setBackground(BCMTheme.colBG);
				this.repaint();
		}
		

		
		public void reflectError() {
			if (this.containsDelimiter()) {	showError(); }
			else { showNoError();}
		}

		public void reflectIPError() {
			if (!this.goodIP()) {showError();}
			else {showNoError();}
		}
	}

}
