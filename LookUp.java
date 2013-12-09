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

import java.util.Vector;

//Pop up allowing user to view available rooms to join
public class LookUp extends JDialog implements ActionListener{

	private Vector <String> roomStrings = new Vector<String>();
	private JButton joinButton, cancelButton;
	private JComboBox eventList;
	private ChatFrame chatFrame;
	private ChatServer chatServer;
	private long clientId;
	private long numberOfRooms;
	private RoomInter [] listOfRooms;

	public LookUp(ChatFrame frame, ChatServer chatServer, long clientId){

		this.setTitle("Look up Rooms");
		this.setSize(250, 115);
		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.setLocationRelativeTo(chatFrame);
		this.chatFrame = frame;
		this.chatServer = chatServer;
		this.clientId = clientId;

		JPanel dialogPanel = new JPanel(new BorderLayout());
		JPanel name1Panel = new JPanel(new GridLayout(1, 2));
		JPanel namePanel = new JPanel();
		JLabel nameLabel = new JLabel("Available Rooms: ");
		namePanel.add(nameLabel);

		roomStrings.clear();
		roomStrings.trimToSize();

		numberOfRooms = chatServer.getNumberOfRooms();
		listOfRooms = chatServer.getRoomList();

		//determine if rooms are available
		if (numberOfRooms > 0){

			for(int i = 0; i < numberOfRooms; i++){

				//obtain a room object using the RoomInter servant
				Room room = listOfRooms[i].getAllState();
				System.out.println(room.roomName);
				if (room.roomName.compareTo("") != 0){
					roomStrings.add(room.roomName);
				}
			}

			eventList = new JComboBox(roomStrings);
			eventList.setSelectedIndex(0);
			eventList.addActionListener(this);
			namePanel.add(eventList);

			name1Panel.add(namePanel);

			//create buttons
			joinButton = new JButton("Join Room");
			cancelButton = new JButton("Cancel");
			joinButton.addActionListener(this);
			cancelButton.addActionListener(this);
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(joinButton);
			buttonPanel.add(cancelButton);
			dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

			dialogPanel.add(name1Panel, BorderLayout.CENTER);

			this.add(dialogPanel);

		}else{

			this.dispose();
			chatFrame.output.append("No rooms have been created yet.\n");
		}
	}

	public void actionPerformed(ActionEvent e){

		Object objectUsed = e.getSource();

		if (objectUsed instanceof JButton){

			if (e.getSource() == joinButton){

				int selectedEvent = eventList.getSelectedIndex();

				System.out.println(roomStrings.get(selectedEvent));

				for(int i = 0; i < numberOfRooms; i++){

					Room room = listOfRooms[i].getAllState();

					if (room.roomName.compareTo(roomStrings.get(selectedEvent)) == 0){

						//Join an existing room using the ChatServer's remote method from servant
						chatServer.joinRoom(roomStrings.get(selectedEvent), clientId);
						chatFrame.setTitle(roomStrings.get(selectedEvent));

						chatFrame.lookUpItem.setEnabled(true);
						chatFrame.leaveRoomItem.setEnabled(true);
						chatFrame.previousMsgItem.setEnabled(true);
						chatFrame.numberOfClientsItem.setEnabled(true);

						chatFrame.output.setText("You've joined, " + roomStrings.get(selectedEvent) + "\n");

						this.dispose();
					}
				}

			}else if (e.getSource() == cancelButton){

				this.dispose();
			}
		}
	}
}