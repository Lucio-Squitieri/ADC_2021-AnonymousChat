package beans;

import net.tomp2p.peers.PeerAddress;

import java.io.Serializable;
import java.util.HashSet;

public class Room implements Serializable {
    private final String roomName;
    private final HashSet<PeerAddress> users;


    public Room(String roomName, HashSet<PeerAddress> users) {
        this.roomName = roomName;
        this.users = users;
    }

    public String getRoomName() {
        return roomName;
    }

    public HashSet<PeerAddress> getUsers() {
        return users;
    }

    public void removeUser(PeerAddress user){
        if(this.users!=null)
            this.users.remove(user);

    }

    public void addUser(PeerAddress user){
        if(this.users!=null)
            this.users.add(user);
    }
}
