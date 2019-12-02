package com.jesper.shutapp.model;

import java.util.ArrayList;

public class GroupChat {

    private String groupName;
    private String message;
    private ArrayList<String> receiver;
    private String sender;

    public GroupChat(String groupName, String message, ArrayList<String> receiver, String sender) {
        this.groupName = groupName;
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
    }

    public GroupChat (){

    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
