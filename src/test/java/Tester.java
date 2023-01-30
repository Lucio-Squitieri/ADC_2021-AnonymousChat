import com.sun.net.httpserver.Authenticator;
import beans.Message;
import beans.Room;
import implementation.AnonymousChatI;
import interfaces.MessageListener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tester {

    private static AnonymousChatI peer0, peer1, peer2, peer3;

    static class MessageListenerI implements MessageListener {
        int peerId;

        public MessageListenerI(int peerId) {
            this.peerId = peerId;
        }

        @Override
        public void parseObject(Object obj) {
            Message messageReceived = (Message) obj;

            System.out.println(messageReceived.getMessageFormatted());
        }
    }

    //initialization of the four peer necessary for the test

    @BeforeAll
    static void initialization() throws Exception {
        peer0 = new AnonymousChatI("127.0.0.1", 0, new MessageListenerI(0));
        peer1 = new AnonymousChatI("127.0.0.1", 1, new MessageListenerI(1));
        peer2 = new AnonymousChatI("127.0.0.1", 2, new MessageListenerI(2));
        peer3 = new AnonymousChatI("127.0.0.1", 3, new MessageListenerI(3));
    }

    /*
    test room creation
    Expected sequence for a correct execution:
    peer0 create a room --> "Success"
    */
    @Test

    @DisplayName("Option 1 Create room")
    void testCaseRoomCreation() {
        String res = peer0.tryCreateRoom(new Room("room", new HashSet<>()));
        assertEquals(res, "Success");
    }

    /*
    Test case for the exception "room already created".
    Expected sequence for a correct execution:
    peer0 create a room -->"Success"
    peer1 try to create a room with the same name --> "Failure"
    */
    @Test
    @DisplayName("Option 1 create room exception: 'room already created'")
    void tesCaseRoomAlreadyCreated() {
        //peer0 creates the room
        String res1 = peer0.tryCreateRoom(new Room("test option 1", new HashSet<>()));
        assertEquals(res1, "Success");
        //peer0 try to create a room with the same name
        String res2 = peer0.tryCreateRoom(new Room("test option 1", new HashSet<>()));
        assertEquals(res2, "This room already exists and you joined it");
    }

    /*
     Test case for the option 2  "join a room".
     Expected sequence for a correct execution:
     peer0 create a room --> "Success"
     peer1 try to join the room just created --> "Joined successfully"
     */
    @Test
    @DisplayName("Option 2 join room")
    void testCaseJoinRoom() {
        //peer0 create a room
        String res1 = peer0.tryCreateRoom(new Room("test option 2", new HashSet<>()));
        assertEquals(res1, "Success");
        //peer1 try to join the room created by peer0
        String res2 = peer1.tryjoinRoom("test option 2");
        assertEquals(res2, "Joined successfully");
    }

     /*
    Test case for the option 2 "join a room" exception "room already joined".
    Expected sequence for a correct execution:
    peer0 create a room --> "Success"
    peer1 joins the room --> "joined successfully"
    peer1 try to join the same room --> "you already joined this room"
    */

    @Test
    @DisplayName("Option 2 join room exception: 'room already joined'")
    void testCaseRoomAlreadyJoined() {
        //peer0 creates the room
        String res1 = peer0.tryCreateRoom(new Room("test option 2 exception 1", new HashSet<>()));
        assertEquals(res1, "Success");
        //peer1 try to join the room
        String res2 = peer1.tryjoinRoom("test option 2 exception 1");
        assertEquals(res2, "Joined successfully");
        //peer1 try to join the room again
        String res3 = peer1.tryjoinRoom("test option 2 exception 1");
        assertEquals(res3, "you alredy joined this room");
    }

    /*
      Test case for the option 2 "join a room" exception "room doesn't exist".
      Expected sequence for a correct execution:

      peer0 try to join a room never created and thus doesn't exit--> "error probably the room does not exists"
      */
    @Test
    @DisplayName("Option 2 join room exception 'room doesn't exists'")
    void testCaseRoomDoesntExist() {
        //peer0 try to join a not existing e room
        String res3 = peer0.tryjoinRoom("test option 2 exception 2");
        assertEquals(res3, "error probably the room does not exists");
    }
    /*
    test case for the option 3 "leave room"
    Expected sequence for a correct execution:
    peer0 create a room --> "Success"
    peer1, peer2 and peer3 joins the room --> "Joined successfully"
    peer1, peer2 and peer3 joins the room --> true
     */
    @Test
    @DisplayName("Option 3 leave room")
    void testCaseLeaveRoom() {
        String res;
        boolean res1;
        //peer0 create a room
        res = peer0.tryCreateRoom(new Room("test option 3", new HashSet<>()));
        assertEquals(res, "Success");

        //peer1, peer2 and peer3 join the room
        res = peer1.tryjoinRoom("test option 3");
        assertEquals(res, "Joined successfully");
        res = peer2.tryjoinRoom("test option 3");
        assertEquals(res, "Joined successfully");
        res = peer3.tryjoinRoom("test option 3");
        assertEquals(res, "Joined successfully");


        //peer1, peer2 and peer3 leave the room
        res1 = peer1.leaveRoom("test option 3");
        assert res1;
        res1 = peer2.leaveRoom("test option 3");
        assert res1;
        res1 = peer3.leaveRoom("test option 3");
        assert res1;
    }

    /*
    test case for the option 3 "leave room" exception "room not joined"
    Expected sequence for a correct execution:
    peer0 leave a room he hasn't joined-->false
     */


    @Test
    @DisplayName("Option 3 leave room exception: 'room not joined'")
    void testCaseRoomNotJoined() {
        //peer0 try to leave the room
        boolean res = peer0.leaveRoom("test option 3 exception 1");
        assert !res;
    }

    /*
      test case for the option 4 "Send message"
      Expected sequence for a correct execution:
      peer0 create a room
      peer1, peer2, peer3 join the room
      peer1 sends a test message-->true
       */
    @Test
    @DisplayName("Option 4 Send message")
    void testCaseSendMessage(){
        String res;
        boolean res1;
        //peer0 create a room
        res = peer0.tryCreateRoom(new Room("test option 4", new HashSet<>()));
        assertEquals(res, "Success");

        //peer1, peer2 and peer3 join the room
        res = peer1.tryjoinRoom("test option 4");
        assertEquals(res, "Joined successfully");
        res = peer2.tryjoinRoom("test option 4");
        assertEquals(res, "Joined successfully");
        res = peer3.tryjoinRoom("test option 4");
        assertEquals(res, "Joined successfully");

        //peer1 sends a message
        res1= peer1.sendMessage("test option 4","test message");
        assert res1;
    }
    /*
       test case for the option 4 "Send message" exception "error"
       Expected sequence for a correct execution:

       peer0 sends a test message-->false
        */
    @Test
    @DisplayName("Option 4 Send message exception: 'error")
    void testCaseSendMessageError(){
        boolean res1;
        //peer1 sends a message
        res1= peer0.sendMessage("test option 4 error","test message");
        assert !res1;
    }

    /*
       test case for the option 5 "Leave all room"
       Expected sequence for a correct execution:
       peer0 create a room
       peer1, peer2, peer3 join the room
       peer1 create a room
       peer0, peer2, peer3 join the room

       peer0 get the number of users in each room--> 4
       peer3 leave all rooms-->true
       peer0 get the number of users in each room--> 3
      */
    @Test
    @DisplayName("Option 5 Leave all rooms")
    void testCaseLeaveAllRooms(){
        String res;
        //peer0 create a room
        res = peer0.tryCreateRoom(new Room("test option 5 room 1", new HashSet<>()));
        assertEquals(res, "Success");

        //peer1, peer2 and peer3 join the room
        res = peer1.tryjoinRoom("test option 5 room 1");
        assertEquals(res, "Joined successfully");
        res = peer2.tryjoinRoom("test option 5 room 1");
        assertEquals(res, "Joined successfully");
        res = peer3.tryjoinRoom("test option 5 room 1");
        assertEquals(res, "Joined successfully");

        //peer1 create a room
        res = peer1.tryCreateRoom(new Room("test option 5 room 2", new HashSet<>()));
        assertEquals(res, "Success");

        //peer0, peer2 and peer3 join the room
        res = peer0.tryjoinRoom("test option 5 room 2");
        assertEquals(res, "Joined successfully");
        res = peer2.tryjoinRoom("test option 5 room 2");
        assertEquals(res, "Joined successfully");
        res = peer3.tryjoinRoom("test option 5 room 2");
        assertEquals(res, "Joined successfully");

        //number of users in the first room. Expected result 4
        int r;
        r=peer0.getNumberUsers("test option 5 room 1");
        assertEquals(r,4);

        r=peer0.getNumberUsers("test option 5 room 2");
        assertEquals(r,4);

        boolean res1=peer3.leaveAllRooms();
        assert res1;

        //number of users in the first room. Expected result 3
        r=peer0.getNumberUsers("test option 5 room 1");
        assertEquals(r,3);

        r=peer0.getNumberUsers("test option 5 room 2");
        assertEquals(r,3);

    }

    /*
           test case for the option 6 "Get number of users"
           Expected sequence for a correct execution:
           peer0 create a room
           peer1, peer2, peer3 join the room
           peer0 gets the number of users --> 4

           peer1 leaves the room
           peer0 gets the number of users --> 3

           peer2 leaves the room
           peer0 gets the number of users --> 2

           peer3 leaves the room
           peer0 gets the number of users --> 1

           peer1 joins the room
           peer0 gets the number of users --> 2

           peer2 joins the room
           peer0 gets the number of users --> 3

           peer3 joins the room
           peer0 gets the number of users --> 4

          */
    @Test
    @DisplayName("Option 6 Get number of users in a room")
    void testCaseNumberUsers(){
        String res;
        int r;
        boolean res1;
        //peer0 create a room
        res = peer0.tryCreateRoom(new Room("test option 6", new HashSet<>()));
        assertEquals(res, "Success");

        //peer1, peer2 and peer3 join the room
        res = peer1.tryjoinRoom("test option 6");
        assertEquals(res, "Joined successfully");
        res = peer2.tryjoinRoom("test option 6");
        assertEquals(res, "Joined successfully");
        res = peer3.tryjoinRoom("test option 6");
        assertEquals(res, "Joined successfully");

        //peer0 gets the number of users in the first room. Expected result 4
        r= peer0.getNumberUsers("test option 6");
        assertEquals(r,4);

        //a peer leaves the room
        res1=peer1.leaveRoom("test option 6");
        assert res1;

        //peer0 gets the number of users in the first room. Expected result 3
        r= peer0.getNumberUsers("test option 6");
        assertEquals(r,3);

        //a peer leaves the room
        res1=peer2.leaveRoom("test option 6");
        assert res1;

        //peer0 gets the number of users in the first room. Expected result 2
        r= peer0.getNumberUsers("test option 6");
        assertEquals(r,2);

        //a peer leaves the room
        res1=peer3.leaveRoom("test option 6");
        assert res1;

        //peer0 gets the number of users in the first room. Expected result 1
        r= peer0.getNumberUsers("test option 6");
        assertEquals(r,1);

        res = peer1.tryjoinRoom("test option 6");
        assertEquals(res, "Joined successfully");
        //peer0 gets the number of users in the first room. Expected result 2
        r= peer0.getNumberUsers("test option 6");
        assertEquals(r,2);

        res = peer2.tryjoinRoom("test option 6");
        assertEquals(res, "Joined successfully");
        //peer0 gets the number of users in the first room. Expected result 3
        r= peer0.getNumberUsers("test option 6");
        assertEquals(r,3);

        res = peer3.tryjoinRoom("test option 6");
        assertEquals(res, "Joined successfully");
        //peer0 gets the number of users in the first room. Expected result 4
        r= peer0.getNumberUsers("test option 6");
        assertEquals(r,4);


    }

    /*
          test case for the option 5 "Leave all room"
          Expected sequence for a correct execution:
          peer0 creates two rooms
           peer1 joins both
           peer1 gets the rooms it joined and we get the number --> 2
           peer1 leave all the rooms--> true
           peer1 joins a room
            peer1 gets the rooms it joined and we get the number --> 1
     */
    @Test
    @DisplayName("Option 8 Show the rooms you joined")
    void testCaseShowRooms(){
        String res;
        int r;
        boolean res1;

        //be sure that peer1 hasn't joined no other room
        res1=peer1.leaveAllRooms();
        assert res1;
        //peer0 create a room
        res = peer0.tryCreateRoom(new Room("test option 7 room 1", new HashSet<>()));
        assertEquals(res, "Success");

        //peer0 create a second room
        res = peer0.tryCreateRoom(new Room("test option 7 room 2", new HashSet<>()));
        assertEquals(res, "Success");

        res = peer1.tryjoinRoom("test option 7 room 1");
        assertEquals(res, "Joined successfully");

        r=peer1.getMyRoomList().size();
        assertEquals(r,1);

        res = peer1.tryjoinRoom("test option 7 room 2");
        assertEquals(res, "Joined successfully");

        r=peer1.getMyRoomList().size();
        assertEquals(r,2);

        peer1.leaveRoom("test option 7 room 1");
        r=peer1.getMyRoomList().size();
        assertEquals(r,1);

        peer1.leaveRoom("test option 7 room 2");
        r=peer1.getMyRoomList().size();
        assertEquals(r,0);




    }
}
