import java.awt.event.*;
import java.io.*;
import javax.swing.JOptionPane;

//KeyAdapter implementation, overriding its default behaviour on the JTextField message input area
public class EnterListener extends KeyAdapter {

	private ChatServer chatServer;
	private ChatFrame gui;
	private ListenerImpl listener;
	private ChatClient chatClient;
	private long attempts;

	public EnterListener (ChatFrame gui, ChatServer chatServer, ListenerImpl listener, ChatClient chatClient) {

    	this.chatServer = chatServer;
    	this.gui = gui;
    	this.listener = listener;
    	this.chatClient = chatClient;
    	this.attempts = 3;
    	this.gui.chatClient = chatClient;
   	}

   	public void keyPressed(KeyEvent e){

    	if (e.getKeyCode()==KeyEvent.VK_ENTER){

			//check if user is authenticated, false
    	   	if (listener.getAuthenticated() == false){

				boolean status = chatServer.authenticate(gui.input.getText());

				if (status == true){

					listener.setAuthenticated(true);

					gui.output.setText("You've been authenticated. \n" +
										"Please enter a username. \n");

				}else{

					attempts--;

					if (attempts == 0){

						JOptionPane.showMessageDialog(gui, "You have failed authentication, Goodbye");
						chatServer.exitClient(chatClient.clientId);
						System.exit(0);
					}else{

						gui.output.append("Password is incorrect. " + (attempts) + " attempts left. \n");
					}
				}

			//true
			}else{

				if (listener.getUserName().compareTo("") == 0){

					//prevent blank username (Validation)
					if (gui.input.getText().compareTo("") == 0){

						gui.output.append("Username must not be blank.\n");
					}else{

						listener.setUserName(gui.input.getText());

						gui.output.setText("Welcome " + gui.input.getText() + ". \n" +
											"Please join or create a room. \n");

						gui.newRoomItem.setEnabled(true);
						gui.lookUpItem.setEnabled(true);
					}

				}

				chatServer.recieveMsg(listener.getUserName(), chatClient.clientId, gui.input.getText());

			}

			gui.input.setText("");


      	}
   }
}
