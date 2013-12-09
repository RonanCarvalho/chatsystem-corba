import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.DataInputStream;
import java.io.IOException;

import java.util.Vector;
import java.util.Iterator;

import org.omg.PortableServer.POA;

//ChatServer implementation/ Servant, provides interface to methods available for remote client invocation
public class ChatServerImpl extends ChatServerPOA {

	private String password = "123456";
	private int nextClientId = 0;
    private ChatClient listOfClients[];
    private int numberOfClients;
    private RoomInter listOfRooms[];
    private int numberOfRooms;
    private FileWriter fstream[];
	private BufferedWriter out[];

    POA theRootpoa;

    public ChatServerImpl(POA rootpoa) {

		listOfRooms = new RoomInter[100];
		numberOfRooms = 0;
		listOfClients = new ChatClient[100];
		numberOfClients = 0;
		theRootpoa = rootpoa;
		fstream = new FileWriter[100];
		out = new BufferedWriter[100];

	}

    public int nextClientId(){

		return nextClientId;
	}

	//registers new ChatClient in server's list of clients
    public ChatClient registerClient(ChatClient newClient) throws ChatServerPackage.TooManyClientsException{

		if (numberOfClients > 99){

			throw new ChatServerPackage.TooManyClientsException();
		}else{

			listOfClients[numberOfClients] = newClient;

			numberOfClients++;
			nextClientId++;
		}

		return newClient;
    }

    public boolean authenticate(String passwordAttempt){

		boolean authenticateStatus;

		if (passwordAttempt.compareTo(password) == 0){

			authenticateStatus = true;

		}else{

			authenticateStatus = false;
		}

		return authenticateStatus;
	}

    public RoomInter newRoom(Room room) throws ChatServerPackage.TooManyRoomsException{

		RoomInter roomInter = null;

		if (numberOfRooms > 99){

			throw new ChatServerPackage.TooManyRoomsException();
		}else{

			try{

			 	//Create log file for chat room
			    fstream[numberOfRooms] = new FileWriter(room.roomName + ".txt", true);
			    out[numberOfRooms] = new BufferedWriter(fstream[numberOfRooms]);
			    room.fileIndex = numberOfRooms;

			}catch (Exception e){
			      System.err.println("Error: " + e.getMessage());
    		}

			//create reference to new Room instance
			RoomInterImpl roomInterRef = new RoomInterImpl(room);

			try {
				org.omg.CORBA.Object ref = theRootpoa.servant_to_reference(roomInterRef);
				roomInter = RoomInterHelper.narrow(ref);
			}catch (Exception e) {
				System.err.println("ERROR: " + e);
				e.printStackTrace(System.out);
        	}

			listOfRooms[numberOfRooms] = roomInter;
			numberOfRooms++;

		}
		return roomInter;
	}

	public RoomInter[] getRoomList(){

	    return listOfRooms;
    }

    public long getNumberOfRooms(){

		return numberOfRooms;
	}

	public void setCurrentRoom(long clientId, String currentRoom){

		for (int i = 0; i < numberOfClients; i++){

			if (listOfClients[i].clientId == clientId){

				listOfClients[i].currentRoom = currentRoom;
			}
		}
	}

	public void joinRoom(String roomName, long clientId){

		for(int i = 0; i < numberOfRooms; i++){

			Room room = listOfRooms[i].getAllState();

			if (room.roomName.compareTo(roomName) == 0){

				room.clientsInRoomList[room.clientsJoined] = clientId;
				room.clientsJoined++;

				RoomInter roomInter = null;
				RoomInterImpl roomInterRef = new RoomInterImpl(room);

				try {
					org.omg.CORBA.Object ref = theRootpoa.servant_to_reference(roomInterRef);
					roomInter = RoomInterHelper.narrow(ref);
				}catch (Exception e) {
					System.err.println("ERROR: " + e);
					e.printStackTrace(System.out);
				}

				listOfRooms[i] = roomInter;

			}
		}

		for (int k = 0; k < numberOfClients; k++){

			if (listOfClients[k].clientId == clientId){

				listOfClients[k].currentRoom = roomName;
			}
		}
	}

	//returns list of messages previously sent in current room
	public void previousMsg(long clientId){

		ChatClient currentClient = null;

		for (int k = 0; k < numberOfClients; k++){

			if (listOfClients[k].clientId == clientId){

				currentClient = listOfClients[k];
			}
		}


		for(int i = 0; i < numberOfRooms; i++){

			Room room = listOfRooms[i].getAllState();

			if (room.roomName.compareTo(currentClient.currentRoom) == 0){

				try{

					FileInputStream fInStream = new FileInputStream(currentClient.currentRoom + ".txt");
					DataInputStream in = new DataInputStream(fInStream);
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;

					//Read File Line By Line
					while ((strLine = br.readLine()) != null){

						// Print the content through the associated listeners remote method
						currentClient.listener.recievePreviousMsg(strLine + "\n");
					}

					in.close();
				}catch (Exception e){//Catch exception if any
				      System.err.println("Error: " + e.getMessage());
    			}
			}
		}
	}

	public long getNumberOfClientsInRoom(long clientId){

		String currentRoom = "";
		long numberOfClientsInRoom = 0;

		for (int k = 0; k < numberOfClients; k++){

			if (listOfClients[k].clientId == clientId){

				currentRoom = listOfClients[k].currentRoom;
			}
		}

		for(int i = 0; i < numberOfRooms; i++){

			Room room = listOfRooms[i].getAllState();

			if (room.roomName.compareTo(currentRoom) == 0){

				numberOfClientsInRoom = room.clientsJoined;

			}
		}

		return numberOfClientsInRoom;

	}

	public void leaveRoom(long clientId){

		String currentRoom = "";

		for (int k = 0; k < numberOfClients; k++){

			if (listOfClients[k].clientId == clientId){

				currentRoom = listOfClients[k].currentRoom;
				listOfClients[k].currentRoom = "";
			}
		}


		for(int i = 0; i < numberOfRooms; i++){

			Room room = listOfRooms[i].getAllState();

			if (room.roomName.compareTo(currentRoom) == 0){

				for (int j = 0; j < 100; j++){

					if (room.clientsInRoomList[j] == clientId){

						room.clientsInRoomList[j] = -1;
						room.clientsJoined--;

					}
				}

				RoomInter roomInter = null;
				RoomInterImpl roomInterRef = new RoomInterImpl(room);

				try {
					org.omg.CORBA.Object ref = theRootpoa.servant_to_reference(roomInterRef);
					roomInter = RoomInterHelper.narrow(ref);
				}catch (Exception e) {
					System.err.println("ERROR: " + e);
					e.printStackTrace(System.out);
				}

				listOfRooms[i] = roomInter;

			}
		}
	}


    public void recieveMsg(String userName, long clientId, String msg){

		message(userName, clientId, msg);
	}

	public void exitClient(long _clientId){

		for (int i = 0; i < numberOfClients; i++){

			if (listOfClients[i].clientId != -1){

				if (listOfClients[i].clientId == _clientId){

					listOfClients[i].clientId = -1;
				}
			}
		}
	}

    public void message(String userName, long clientId, String msg) {

		String currentRoom = "";

		for (int i = 0; i < numberOfClients; i++){

			if (listOfClients[i].clientId == clientId){

				currentRoom = listOfClients[i].currentRoom;
			}
		}

		for(int j = 0; j < numberOfRooms; j++){

			Room room = listOfRooms[j].getAllState();

		    if (room.roomName.compareTo(currentRoom) == 0){

				out[room.fileIndex] = new BufferedWriter(fstream[room.fileIndex]);

				try{
					out[room.fileIndex].write(userName + ": " + msg + "\n");
					out[room.fileIndex].flush();
				}catch(IOException ioe){
					System.out.println("Error writing to file");
				}

				for (int k = 0; k < numberOfClients; k++){

					if (listOfClients[k].currentRoom.compareTo(currentRoom) == 0){

						listOfClients[k].listener.message(userName, msg);

					}
				}
			}
		}
    }
}