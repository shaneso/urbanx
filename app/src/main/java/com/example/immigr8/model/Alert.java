package com.example.immigr8.model;

public class Alert {
    private String sender;
    private String receiver;
    private String date;

    public Alert() {
    }

    public Alert(String date, String receiver, String sender) {
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
