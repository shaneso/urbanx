package com.example.immigr8.model;

public class ChatList {
    private String sender;
    private String receiver;

    public ChatList(String sender, String receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }

    public ChatList() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
