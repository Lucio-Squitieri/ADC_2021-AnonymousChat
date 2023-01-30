package implementation;


import interfaces.*;
import beans.*;
import interfaces.MessageListener;
import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDirect;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class AnonymousChatI implements AnonymousChat {

    private final static int DEFAULT_MASTER_PORT= 4000;
    private final Peer peer;
    private final PeerDHT dht;
    private final HashSet<String> myRoomList= new HashSet<>();

    //constructor
    public AnonymousChatI ( String master_peer, int id, MessageListener listener) throws Exception {
        peer = new PeerBuilder(Number160.createHash(id)).ports(DEFAULT_MASTER_PORT+ id).start();
        dht = new PeerBuilderDHT(peer).start();

        FutureBootstrap fb = peer.bootstrap().inetAddress(InetAddress.getByName(master_peer)).ports(DEFAULT_MASTER_PORT).start();
        fb.awaitUninterruptibly();

        if(fb.isSuccess())
            peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
        else
            throw new Exception("Error in master peer bootstrap");

        peer.objectDataReply(new ObjectDataReply() {
            @Override
            public Object reply(PeerAddress peerAddress, Object request) throws Exception {
                listener.parseObject(request);
                return "Success";
            }
        });

    }

    //find if the nameRoom inserted exist
    public Room findRoom(String roomName) {
        try {
            if (roomName != null) {
                FutureGet futureGet = dht.get(Number160.createHash(roomName)).start();
                futureGet.awaitUninterruptibly();

                if (futureGet.isSuccess())
                    if (futureGet.isEmpty())
                        return null;

                return (Room) futureGet.dataMap().values().iterator().next().object();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //create the room
    @Override
    public boolean createRoom(String _room_name) {
        try {
            Room room = new Room(_room_name, new HashSet<>());
            room.getUsers().add(dht.peer().peerAddress());
            dht.put(Number160.createHash(_room_name)).data(new Data(room)).start().awaitUninterruptibly();

            myRoomList.add(_room_name);

            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
            return false;
    }

    //chack if the user already joined the room or if it exists before its creation
    public String tryCreateRoom(Room room){
        try{
            if(!myRoomList.isEmpty() && myRoomList.contains(room.getRoomName()))
                return "This room already exists and you joined it";

            boolean exists= findRoom(room.getRoomName()) !=null;

            if(!exists){
                boolean result= createRoom(room.getRoomName());
                if(result) return "Success";
                else
                   return "Failure";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "Failure";
    }
public String tryjoinRoom(String _room_name){
    if(myRoomList != null && myRoomList.contains(_room_name)) {
        return "you alredy joined this room";
    }else if(joinRoom(_room_name)) return "Joined successfully";
    else return "error probably the room does not exists";
}
    @Override
    public boolean joinRoom(String _room_name) {

            Room room =findRoom(_room_name);
            if(room!= null) {
                room.addUser(dht.peer().peerAddress());
                try {
                    dht.put(Number160.createHash(_room_name)).data(new Data(room)).start().awaitUninterruptibly();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                myRoomList.add(_room_name);
                return true;
            }
        return false;
    }


    @Override
    public boolean leaveRoom(String _room_name) {
if(myRoomList.isEmpty() || !myRoomList.contains(_room_name))
    return false; //you never joined this room

            Room room= findRoom(_room_name);
            if(room!=null){
                room.removeUser(dht.peer().peerAddress());
                try {
                    dht.put(Number160.createHash(_room_name)).data(new Data(room)).start().awaitUninterruptibly();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                myRoomList.remove(_room_name);
                return true;
            }

            return false;
    }

    @Override
    public boolean sendMessage(String _room_name, String _text_message) {
       if(myRoomList.contains(_room_name)){
           Room room=findRoom(_room_name);
           if(room!=null) {
               Date date = new Date();
               Message msg = new Message(_room_name, _text_message, date);
               for (PeerAddress peerAddress : room.getUsers()) {
                   if (!peerAddress.equals(dht.peer().peerAddress())) {
                       FutureDirect futureDirect = dht.peer().sendDirect(peerAddress).object(msg).start();
                       if(futureDirect.isFailed()) System.out.println("errore nell'invio del messaggio");
                   }
               }
               return true;
           } else return false;
        }
        else return false;
    }


    @Override
    public int getNumberUsers(String _room_name) {
        Room room = findRoom(_room_name);
        if(room != null){
            return room.getUsers().size();
        }
        else return -1;
    }


    public boolean leaveAllRooms() {
        for (String room : new ArrayList<>(myRoomList)) {
            leaveRoom(room);
        }
        return true;
    }


    @Override
    public HashSet<String> getMyRoomList() {
        return myRoomList;
    }






}
