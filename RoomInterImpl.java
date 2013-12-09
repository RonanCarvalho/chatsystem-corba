import org.omg.CosNaming.*;
import org.omg.CORBA.*;

//class responsible for Room interface implementation
class RoomInterImpl extends RoomInterPOA {

    Room room;

    public RoomInterImpl(Room room){
    	this.room = room;
    }

	//returns the room object associated with the reference
	public Room getAllState() {
    	return this.room;
	}

	public void clientJoinRoom(Room room){

		this.room = room;
	}
}