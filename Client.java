import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;

public class Client {

	public static ChatFrame gui = null;

    public static void main(String[] args) {

		try {

			//initialize orb
			Properties props = System.getProperties();
			props.put("org.omg.CORBA.ORBInitialPort", "1050");
			//Replace MyHost with the name of the host on which you are running the server
			props.put("org.omg.CORBA.ORBInitialHost", "localhost");
			ORB orb = ORB.init(args, props);
			System.out.println("Initialized ORB");

			//Resolve ChatServer
			ChatServer chatServer = ChatServerHelper.narrow(
				orb.string_to_object("corbaname:iiop:1.2@localhost:1050#ChatServer"));

			//obtain next available client Id from server through ChatServer reference
			long clientId = chatServer.nextClientId();

			//create simple GUI
			gui = new ChatFrame("Chat Client", chatServer, clientId);


			//Instantiate Servant and create reference
			POA rootPOA = POAHelper.narrow(
				orb.resolve_initial_references("RootPOA"));
			ListenerImpl listener  = new ListenerImpl(gui, clientId);
			rootPOA.activate_object(listener);
			Listener ref = ListenerHelper.narrow(
				rootPOA.servant_to_reference(listener));


			ChatClient chatClient = null;

			try{

				chatClient = new ChatClient(clientId, "", ref);

				//Register chatClient reference (callback object) with ChatServer
				chatServer.registerClient(chatClient);

			}catch(ChatServerPackage.TooManyClientsException fe){

				System.out.println("Too many clients connected.\n");
				System.out.println("Please try again later.\n");
				System.exit(0);
			}

			//create overridden keyadaptor for user entry
			gui.input.addKeyListener (new EnterListener(gui, chatServer, listener, chatClient));

			//Activate rootpoa
			rootPOA.the_POAManager().activate();

			//Wait for messages
			gui.output.append("Please enter the password... \n");

			orb.run();

		} catch (Exception e) {

			e.printStackTrace();
		}
    }
}