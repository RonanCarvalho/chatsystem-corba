import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JMenuItem;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;


//Pop up allowing user to create a new room
public class NewRoom extends JDialog implements ActionListener{

	private JButton saveButton, cancelButton;
	private JTextField roomNameTextField;
	private ChatFrame chatFrame;
	private ChatServer chatServer;
	private long clientId;

	public NewRoom(ChatFrame frame, ChatServer chatServer, long clientId){

		this.setTitle("New Room");
		this.setSize(200, 125);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(chatFrame);
		this.chatFrame = frame;
		this.chatServer = chatServer;
		this.clientId = clientId;

		JPanel dialogPanel = new JPanel(new BorderLayout());
		JPanel name1Panel = new JPanel(new GridLayout(1, 1));

		//create fields for user input
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Room name: ");
		roomNameTextField = new JTextField(10);
		namePanel.add(nameLabel);
		namePanel.add(roomNameTextField);

		name1Panel.add(namePanel);

		//create buttons
		saveButton = new JButton("Save");
		cancelButton = new JButton("Cancel");
		saveButton.addActionListener(this);
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

		dialogPanel.add(name1Panel, BorderLayout.CENTER);

		this.add(dialogPanel);
	}

	public void actionPerformed(ActionEvent e){

		Object objectUsed = e.getSource();

		if (objectUsed instanceof JButton){

			if (e.getSource() == saveButton){

				//prevent blank user names (Validation)
				if (roomNameTextField.getText().compareTo("") == 0){

					roomNameTextField.setBackground(Color.YELLOW);

				}else{

					this.dispose();

					//create new room object through the ChatServer reference
					try{

						long [] roomClientList = new long[100];

						//make all client positions an invalid clientId
						for (int k = 0; k < 100; k++){

							roomClientList[k] = -1;
						}

						roomClientList[0] = clientId;

						//remote method invocation on ChatServer, supplying new Room object as parameter
						chatServer.newRoom(new Room(roomNameTextField.getText(), roomClientList, 1, 0));
						chatFrame.setTitle(roomNameTextField.getText());

						chatServer.setCurrentRoom(clientId, roomNameTextField.getText());

						chatFrame.leaveRoomItem.setEnabled(true);
						chatFrame.previousMsgItem.setEnabled(true);
						chatFrame.numberOfClientsItem.setEnabled(true);
						chatFrame.output.setText("You've joined, " + roomNameTextField.getText() + "\n");

					}catch(ChatServerPackage.TooManyRoomsException fe){

						chatFrame.output.append("Error, Too many rooms.\n");
					}
				}
			}
		}

		if (e.getSource() == cancelButton){

			this.dispose();
		}
	}
}