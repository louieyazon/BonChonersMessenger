package bcmGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.util.Arrays;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 6452901402568420111L;

	public AboutDialog() {
		setAboutWindowProperties();
		setAboutProperties();
	}

	private void setAboutWindowProperties() {
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		this.setTitle("About BonChonersMessenger");
		this.setIconImage(BCMTheme.CLIENT_ICON);
		this.setResizable(false);
		this.setBounds(100, 100, 420, 270);
		contentPanel.setBackground(BCMTheme.colBG);
		this.setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
	}

	private void setAboutProperties() {
		{
			JLabel lblTrollol = new JLabel("");
			lblTrollol.setHorizontalAlignment(SwingConstants.CENTER);
			lblTrollol.setIcon(BCMTheme.TROLOLOL);
			contentPanel.add(lblTrollol, BorderLayout.EAST);
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setBackground(BCMTheme.colBG);
			panel.setLayout(null);
			{
				JLabel lblNewLabel = new JLabel("BonChoners Messenger");
				lblNewLabel.setBounds(78, 93, 121, 14);
				panel.add(lblNewLabel);
			}
			
			JLabel lblBonchonerslogo = new JLabel("");
			lblBonchonerslogo.setBounds(10, 4, 269, 76);
			lblBonchonerslogo.setIcon(BCMTheme.MESSENGER_LOGO_BIG);
			panel.add(lblBonchonerslogo);
			
			JTextArea textArea = new JTextArea();
			textArea.setLineWrap(true);
			textArea.setFont(UIManager.getFont("Label.font"));
			textArea.setEditable(false);
			Arrays.sort(devs);
			for (String dev: devs) {
				textArea.append(dev + "\n");
			}
			
			
			
			textArea.setBounds(78, 114, 225, 107);
			panel.add(textArea);
			
			JLabel lblBuild = new JLabel("Build 1105");
			lblBuild.setBounds(214, 93, 81, 14);
			panel.add(lblBuild);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(evlClose);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}
	
	
	private final JPanel contentPanel = new JPanel();
	private String[] devs = {"Hye-Sung Paul Chun","John Dy","Louie Yazon", "Joef Salazar", "Jose Monzon"};
	private ActionListener evlClose = new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
			timeToClose();
		}
	};
	
	private void timeToClose() {
		this.dispose();
	}
	
}
