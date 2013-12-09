import java.awt.event.*;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;

//creates client's GUI
public class ChatFrame extends JFrame implements ActionListener{

	public static final long serialVersionUID = 3L;

	public JTextArea output;
	public JTextField input;

	protected JMenuItem newRoomItem;
	protected JMenuItem lookUpItem;
	protected JMenuItem leaveRoomItem;
	protected JMenuItem previousMsgItem;
	protected JMenuItem numberOfClientsItem;
	private JMenuItem exitItem;

	protected Thread listener;
	private ChatServer chatServer;
	protected ChatClient chatClient;
	private long clientId;


	public ChatFrame (String title, ChatServer chatServer, long clientId){

    	super (title);
    	this.chatServer = chatServer;
    	this.clientId = clientId;

    	setLayout (new BorderLayout ());
    	output = new JTextArea(5, 5);
    	JScrollPane pScroll = new JScrollPane(output);
    	add("Center", pScroll);

    	output.setEditable (false);
    	add ("South", input = new JTextField ());

    	this.setSize(500, 400);
    	this.setVisible(true);
    	this.setLocationRelativeTo(null);
    	this.addWindowListener(new FrameListener(this.clientId, this.chatServer));

		//create menubar for user functionality
		JMenuBar titleMenu = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu roomsMenu = new JMenu("Rooms");
        JMenu helpMenu = new JMenu("Help");

        exitItem = new JMenuItem("Exit Chat System", KeyEvent.VK_E);
		exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        newRoomItem = new JMenuItem("Create new Room", KeyEvent.VK_C);
        newRoomItem.addActionListener(this);
        newRoomItem.setEnabled(false);
        roomsMenu.add(newRoomItem);
        lookUpItem = new JMenuItem("Look up Rooms", KeyEvent.VK_L);
        lookUpItem.addActionListener(this);
        lookUpItem.setEnabled(false);
        roomsMenu.add(lookUpItem);
        roomsMenu.addSeparator();
        leaveRoomItem = new JMenuItem("Leave Current Room", KeyEvent.VK_C);
		leaveRoomItem.addActionListener(this);
		leaveRoomItem.setEnabled(false);
        roomsMenu.add(leaveRoomItem);
        roomsMenu.addSeparator();
		previousMsgItem = new JMenuItem("View Rooms Previous Messages", KeyEvent.VK_P);
		previousMsgItem.addActionListener(this);
		previousMsgItem.setEnabled(false);
        roomsMenu.add(previousMsgItem);
        numberOfClientsItem = new JMenuItem("View number of chat room participants", KeyEvent.VK_N);
		numberOfClientsItem.addActionListener(this);
		numberOfClientsItem.setEnabled(false);
        roomsMenu.add(numberOfClientsItem);

        titleMenu.add(fileMenu);
		titleMenu.add(roomsMenu);
        titleMenu.add(helpMenu);

        add("North", titleMenu);
        setVisible(true);
        titleMenu.setVisible(true);

		input.requestFocus();
	}

	public void actionPerformed(ActionEvent e){

		Object objectUsed = e.getSource();

	    if (objectUsed instanceof JMenuItem){

			if (e.getSource() == newRoomItem){

				NewRoom newRoom = new NewRoom(this, chatServer, clientId);
				newRoom.setLocationRelativeTo(this);


	        }else if (e.getSource() == lookUpItem){

				LookUp lookUp = new LookUp(this, chatServer, clientId);
				lookUp.setLocationRelativeTo(this);

        	}else if (e.getSource() == leaveRoomItem){

				chatServer.leaveRoom(clientId);
				setTitle("Chat Client");

        	}else if (e.getSource() == previousMsgItem){

				chatServer.previousMsg(clientId);

        	}else if (e.getSource() == numberOfClientsItem){

				output.append("Number of clients participating in this chat room is, " +
								chatServer.getNumberOfClientsInRoom(clientId) + "\n");

			}else if (e.getSource() == exitItem){

				chatServer.leaveRoom(clientId);
				chatServer.exitClient(clientId);
    			System.exit(0);
			}
		}
	}
}

//overridden WindoeAdaptor, causing clients reference to be removed from server before shutting down client
class FrameListener extends WindowAdapter{

	private long clientId;
	private ChatServer chatServer;

	public FrameListener(long clientId, ChatServer chatServer){

		this.clientId = clientId;
		this.chatServer = chatServer;

	}

	public void windowClosing(WindowEvent arg0) {

		chatServer.leaveRoom(clientId);
		chatServer.exitClient(clientId);
    	System.exit(0);

	}
}