package beans;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
//roomName identifies the name of the roome, message is the body of the message
String roomName, message;
//date is the date of when the message was sent
Date date;
// isMine is a variable that indicates the ownership of the message
// (it's an anonymous chat so the only message marked true are the one you sent)
boolean isMine;

public Message(String roomName,String message,Date date){
    this.roomName= roomName;
    this.message=message;
    this.date=date;
}

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    //format the date nd the message to be more readable
    public String getDateFormatted(){
    return this.getDate().getHours()+":"+this.getDate().getMinutes()+" "+this.getDate().getDate()+"/"+getDate().getMonth()+1;
    }
    public String getMessageFormatted(){
    return "Room: '"+ this.getRoomName()+"' "+this.getMessage()+" was sent @"+this.getDateFormatted();
    }
    public String getFormattedMessage() {
        return this.getMessage() + " " + this.getDateFormatted();
    }

}
