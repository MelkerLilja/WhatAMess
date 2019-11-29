package com.jesper.shutapp.model;

import java.util.ArrayList;

public class Chat { //A chat class that includes all of the info we want, need to add like time etc later.
    private String message;
    private String receiver;
    private String sender;
    private boolean isseen;

    public Chat() {

    }

    public Chat(String message, String receiver, String sender, boolean isseen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.isseen = isseen;
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

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
