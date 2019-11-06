package com.eba.chat.models;

import java.io.Serializable;

public class Conversation implements Serializable {
    private String text, senderID;
    private long time;
    private boolean seen;

    public Conversation(){}

    public void setSenderID(String userID) {
        this.senderID = userID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
