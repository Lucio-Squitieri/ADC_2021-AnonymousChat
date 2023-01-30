import beans.*;

import implementation.AnonymousChatI;
import interfaces.MessageListener;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.Option;

import java.util.*;

public class Main {
    @Option(name = "-m", aliases = "--masterip",usage= "specify the master peer address", required = true)
    private static String master;

    @Option(name = "-id", aliases = "--identifierPeer",usage= "specify the identifier for this peer", required = true)
    private static int id;

    HashMap<String, List<Message>> joinedRooms;
    AnonymousChatI peer;
    TextIO textIO;
   TextTerminal terminal;

    public Main(String masterPeer, int peerID) throws Exception {
        joinedRooms =new HashMap<>();
        textIO = TextIoFactory.getTextIO();

    }

    public static void main(String[] args) throws Exception {
        String masterPeer =args[1];
        int peerID = Integer.parseInt(args[3]);
        Main main = new Main(masterPeer,peerID);
        main.start(masterPeer,peerID);
        
    }

    private void start(String masterPeer, int peerID) {
        try {
            this.peer= new AnonymousChatI(masterPeer,peerID, new MessageListenerI(peerID));

        terminal = textIO.getTextTerminal();
        terminal.printf("Master: %s, Peer ID=%d\n", masterPeer, peerID);

        while (true) {
            menu(terminal);
            int option = textIO.newIntInputReader()
                    .withMaxVal(8)
                    .withMinVal(1)
                    .read("Option");
            if(Integer.valueOf(option) <1 &&Integer.valueOf(option)>8){}
            else{

            switch (option) {
                case 1:
                    createNewRoom(terminal);
                    break;
                case 2:
                    joinRoom(terminal);
                    break;
                case 3:
                    leaveRoom(terminal);
                    break;
                case 4:
                    sendMessageOption(terminal);
                    break;
                case 5:
                    leaveAll(terminal);
                    break;
                case 6:
                    getNumUsers(terminal);
                    break;
                case 7:
                    showMessages(terminal);
                    break;
                case 8:
                    showRooms(terminal);
                    break;

                default:
                    break;

            }
            }
        }} catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private void createNewRoom(TextTerminal terminal) {
        terminal.printf("\n*** ENTER ROOM NAME ***\n");
        terminal.printf("If a name is not inserted it will be created with a name assigned by default (default-room)\n");
        String roomName = textIO.newStringInputReader().withDefaultValue("default-room").read("Name:");

        if(roomName !=null && !roomName.isEmpty()){
            Room room= new Room(roomName, new HashSet<>());
            String result =peer.tryCreateRoom(room);

            if(result.equals("Success")){
                joinedRooms.put(roomName,new ArrayList<>());
                terminal.printf("Room created succesfully\n");
            } else terminal.printf(result+"\n");
            return;
        }terminal.printf("error\n");
    }

    private void joinRoom(TextTerminal terminal) {
        terminal.printf("\n*** ENTER ROOM NAME ***\n");
        terminal.printf("If a name is not inserted by default it will 'default-room'\n");
        String roomName = textIO.newStringInputReader().withDefaultValue("default-room").read("Name:");

        if(roomName !=null && !roomName.isEmpty()){
            String res= peer.tryjoinRoom(roomName);
            if(res.equals("Joined successfully")){
                terminal.println("Joined successfully");
                joinedRooms.put(roomName,new ArrayList<>());
            }
            else terminal.println(res);

        }
    }

    private void leaveRoom(TextTerminal terminal) {
        terminal.printf("\n*** ENTER ROOM NAME ***\n");
        terminal.printf("If a name is not inserted it will by default 'default-room'\n");
        String roomName = textIO.newStringInputReader().withDefaultValue("default-room").read("Name:");

        if(roomName != null && !roomName.isEmpty()){
            if(peer.leaveRoom(roomName)) {
                terminal.printf("Room left successfully\n");
                joinedRooms.remove(roomName);
            }
            else terminal.printf("Error, it's possible you never joined this room\n");
        }

    }

    private void sendMessageOption(TextTerminal terminal) {
        terminal.printf("\n*** ENTER ROOM NAME ***\n");
        terminal.printf("If a name is not inserted it will by default 'default-room'\n");
        String roomName = textIO.newStringInputReader().withDefaultValue("default-room").read("Name:");

        if(roomName != null && !roomName.isEmpty()){
            if(!peer.getMyRoomList().contains(roomName)) terminal.printf("Please insert the name of a room you joined\n");
            else { terminal.printf("ROOM ENTERED\n");
                boolean sending= true;
                while(sending){
                String text = textIO.newStringInputReader()
                        .withMinLength(1)
                        .read("Enter message (write 'ext' to stop sending messages):");
                if(!text.equals("ext")){
                Message msg= new Message(roomName, text,Calendar.getInstance().getTime());
                msg.setMine(true);
                if(!peer.sendMessage(roomName,text)) {
                    terminal.printf("Error while sending the message");
                }else {
                    joinedRooms.computeIfAbsent(roomName, k -> new ArrayList<>());
                    joinedRooms.get(roomName).add(msg);

                }
                }else {
                    sending=false;
                    break;
                }
                }
            }
    }

    }

    private void leaveAll(TextTerminal terminal) {
        peer.leaveAllRooms();
    }
    private void getNumUsers(TextTerminal terminal) {
        terminal.printf("\n*** ENTER ROOM NAME ***\n");
        terminal.printf("If a name is not inserted it will by default 'default-room'\n");

        String roomName = textIO.newStringInputReader().withDefaultValue("default-room").read("Name:");

        int size= peer.getNumberUsers(roomName);
        if(size>=1) terminal.println("There are "+size+" users in this room");
        else terminal.println("The room does not exists");
    }

    private void showMessages(TextTerminal terminal) {
        terminal.printf("\n*** ENTER ROOM NAME ***\n");
        terminal.printf("If a name is not inserted it will by default 'default-room'\n");

        String roomName = textIO.newStringInputReader().withDefaultValue("default-room").read("Name:");
        List<Message> msgs = joinedRooms.get(roomName);
        if(joinedRooms.containsKey(roomName)){
            if(msgs.size() != 0){
                for(Message msg : msgs){
                    if(msg.isMine()) terminal.printf("YOU:  "+msg.getFormattedMessage()+"\n");
                    else terminal.printf(msg.getFormattedMessage()+"\n");
                }
            }
        }else terminal.printf("you are not a member of this room");

    }

    private void showRooms(TextTerminal terminal) {
    HashSet<String> allRoom= peer.getMyRoomList();
    if(allRoom.isEmpty()) terminal.printf("you have yet to join a room");
    else {
    for(String room: allRoom) terminal.printf("*"+room+"\n");
        }
    }




    private void menu(TextTerminal terminal){
        terminal.printf("\n\n\n\n");
        terminal.printf("*** MENU ***\n");
        terminal.printf("*** Select an option ***\n");
        terminal.printf("1 - Create room\n");
        terminal.printf("2 - Join room\n");
        terminal.printf("3 - Leave room\n");
        terminal.printf("4 - Send messages\n");
        terminal.printf("5 - Leave all rooms\n");
        terminal.printf("6 - Show the number of users that joined a room\n");
        terminal.printf("7 - Show the messages that were sent in a room\n");
        terminal.printf("8 - Show the rooms you joined\n");
}
    class MessageListenerI implements MessageListener {
        int peerId;

        public MessageListenerI(int peerId) {
            this.peerId = peerId;
        }

        @Override
        public void parseObject(Object obj) {
            Message messageReceived = (Message) obj;

            // If ReceivedMessage is OK && ReceivedMessage's Room exists it prints the message
            if (messageReceived != null && joinedRooms.get(messageReceived.getRoomName()) != null) {
                joinedRooms.get(messageReceived.getRoomName()).add(messageReceived);
                terminal.println(messageReceived.getMessageFormatted());
            }
        }


    }


}
