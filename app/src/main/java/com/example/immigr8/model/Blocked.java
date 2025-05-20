package com.example.immigr8.model;

public class Blocked {
    private String sender;
    private String receiver;

    public Blocked(){}

    public Blocked(String receiver, String sender) {
        this.sender = sender;
        this.receiver = receiver;
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
