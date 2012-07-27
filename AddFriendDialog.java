import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Dialog.ModalityType;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class AddFriendDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField tfUserName = new JTextField();
	private JTextField tfNickname = new JTextField();
	private JTextField tfIPAddress = new JTextField();
	private Friend currentFriend;
	
	boolean emptyFriend;
	
	
	//COMPONENTS
	JPanel pnlFriendFields = new JPanel();
	
	JPanel pnlUserName = new JPanel();
	JLabel lblUsername = new JLabel("Username");
	JPanel pnlNickName = new JPanel();
	JLabel lblNickname = new JLabel("Nickname");
	JPanel pnlIPadd = new JPanel();
	JLabel lbliP = new JLabel("IP Address");

	public AddFriendDialog(Friend cF) {
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
			currentFriend = new Friend("","","");
			this.setTitle("New Friend");
			
		}
		this.setVisible(true);
	}

	
	
	

	private void setComponentProperties() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 321, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		
			
			pnlFriendFields.setSize(200,400);
			pnlFriendFields.setMaximumSize(new Dimension(200, 400));
			pnlFriendFields.setLayout(new BoxLayout(pnlFriendFields, BoxLayout.Y_AXIS));
			
			contentPanel.add(pnlFriendFields);
				pnlFriendFields.add(pnlUserName);
					pnlUserName.add(lblUsername);
					tfUserName.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent k) {
							if (k.getKeyCode() == KeyEvent.VK_ENTER) { 
								saveChanges();
							}
							
							if (k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
								cancelChanges();
							}
						}
					});
					pnlUserName.add(tfUserName);
					tfUserName.setColumns(15);
			
				pnlFriendFields.add(pnlNickName);
					pnlNickName.add(lblNickname);
					tfNickname.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent k) {
							if (k.getKeyCode() == KeyEvent.VK_ENTER) { 
								saveChanges();
							}
							
							if (k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
								cancelChanges();
							}
						}
					});
					pnlNickName.add(tfNickname);
					tfNickname.setColumns(15);
				pnlFriendFields.add(pnlIPadd);
					pnlIPadd.add(lbliP);
					tfIPAddress.addKeyListener(new KeyAdapter() {
						@Override
						public void keyPressed(KeyEvent k) {
							if (k.getKeyCode() == KeyEvent.VK_ENTER) { 
								saveChanges();
							}
							
							if (k.getKeyCode() == KeyEvent.VK_ESCAPE) { 
								cancelChanges();
							}
						}
					});
					pnlIPadd.add(tfIPAddress);
					tfIPAddress.setColumns(15);
				
		
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);

				JButton okButton = new JButton("OK");

				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						saveChanges();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);

				JButton cancelButton = new JButton("Cancel");
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
		currentFriend.setUsername(tfUserName.getText().trim());
		currentFriend.setNickname(tfNickname.getText().trim());
		currentFriend.setIP(tfIPAddress.getText().trim());
		this.dispose();
	}
	
	private void cancelChanges() {
		this.dispose();
	}

}
