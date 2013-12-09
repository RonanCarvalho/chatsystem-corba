import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import java.util.Properties;

public class Server {

    public static void main(String[] args) {
	try {
            //create and initialize the ORB
            Properties props = System.getProperties();
            props.put("org.omg.CORBA.ORBInitialPort", "1050");
            //Replace MyHost with the name of the host on which you are running the server
            props.put("org.omg.CORBA.ORBInitialHost", "localhost");
            ORB orb = ORB.init(args, props);
	    	System.out.println("Initialized ORB");

            //Instantiate Servant and create reference
	    	POA rootPOA = POAHelper.narrow(
			orb.resolve_initial_references("RootPOA"));
	    	ChatServerImpl csImpl = new ChatServerImpl(rootPOA);
	    	rootPOA.activate_object(csImpl);
	    	ChatServer csRef = ChatServerHelper.narrow(
			rootPOA.servant_to_reference(csImpl));

            //Bind reference with NameService
	    	NamingContext namingContext = NamingContextHelper.narrow(
			orb.resolve_initial_references("NameService"));
            System.out.println("Resolved NameService");
            NameComponent[] nc = { new NameComponent("ChatServer", "") };
	    	namingContext.rebind(nc, csRef);

	    	//Activate rootpoa
            rootPOA.the_POAManager().activate();

            //Start readthread and wait for incoming requests
            System.out.println("Server ready and running ....");

            orb.run();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}