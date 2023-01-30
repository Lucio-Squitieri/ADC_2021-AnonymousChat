package interfaces;

import java.util.HashSet;

public interface AnonymousChat {
    /**
     * Creates new room.
     * @param _room_name a String the name identify the public chat room.
     * @return true if the room is correctly created, false otherwise.
     */
    public boolean createRoom(String _room_name);
    /**
     * Joins in a public room.
     * @param _room_name the name identify the public chat room.
     * @return true if join success, false otherwise.
     */
    public boolean joinRoom(String _room_name);
    /**
     * Leaves in a public room.
     * @param _room_name the name identify the public chat room.
     * @return true if leave success, false otherwise.
     */
    public boolean leaveRoom(String _room_name);
    /**
     * Sends a string message to all members of a public room.
     * @param _room_name the name identify the public chat room.
     * @param _text_message a message String value.
     * @return true if send success, false otherwise.
     */
    public boolean sendMessage(String _room_name, String _text_message);

    //METODI AGGIUNTI PER ARRICCHIRE IL PROGETTO

    //Mostra il numero di utenti registrati ad una stanza
    int getNumberUsers(String _room_name);

    //esci da tutte le stanza
    public boolean leaveAllRooms();
    //ottieni tutte le stanze joinate
    public HashSet<String> getMyRoomList();

}
