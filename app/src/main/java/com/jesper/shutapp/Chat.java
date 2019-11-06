package com.jesper.shutapp;

public class Chat { //A chat class that includes all of the info we want, need to add like time etc later.
    private String message;
    private String receiver;
    private String sender;

    public Chat() {

    }

    public Chat(String message, String receiver, String sender) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void Setsender(String sender) {
        this.sender = sender;
    }
}
