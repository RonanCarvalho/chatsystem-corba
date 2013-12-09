//Listener Implementation, declaring methods available for remote invocation by server
public class ListenerImpl extends ListenerPOA {

	private ChatFrame gui = null;
	public boolean authenticated;
	public long clientId;
	public String userName;

	//create new listener servant instance
	public ListenerImpl(ChatFrame gui, long clientId){

		this.gui = gui;
		this.clientId = clientId;
		this.authenticated = false;
		this.userName = "";
	}

	//methods available for ChatServer invocation
	public boolean getAuthenticated(){

		return authenticated;
	}

	public void setAuthenticated(boolean status){

		authenticated = status;
	}

	public String getUserName(){

		return userName;
	}

	public void setUserName(String userName){

		this.userName = userName;
	}

	public long getClientId(){

		return clientId;
	}

    public void message(String userName, String msg) {

		gui.output.append(userName + ": " + msg + "\n");

    }

    public void recievePreviousMsg(String msg){

    	gui.output.append(msg);
	}
}