package com.jesper.shutapp;

import java.util.ArrayList;

public class GroupChatClass {
    private String sender;
    private String message;
    private ArrayList<String> receiver;

    public GroupChatClass(String sender, String message, ArrayList<String> receiver) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
    }

    public GroupChatClass() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<String> getReceiver() {
        return receiver;
    }

    public void setReceiver(ArrayList<String> receiver) {
        this.receiver = receiver;
    }
}

