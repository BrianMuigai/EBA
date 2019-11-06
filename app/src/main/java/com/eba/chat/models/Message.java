package com.eba.chat.models;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Message implements Serializable, Comparable {

    private String user1ID, user2ID;
    private String mLastChat;
    private String id;
    private long mTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser1ID() {
        return user1ID;
    }

    public void setUser1ID(String user1ID) {
        this.user1ID = user1ID;
    }

    public String getUser2ID() {
        return user2ID;
    }

    public void setUser2ID(String user2ID) {
        this.user2ID = user2ID;
    }

    public String getLastChat() {
        return mLastChat;
    }

    public void setLastChat(String lastChat) {
        mLastChat = lastChat;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String toString(){
        return "ID: "+getId()+" message: "+getLastChat();
    }

    @Override
    public int compareTo(@NonNull Object o) {
        long comparage = ((Message) o).getTime();
        return Integer.parseInt(String.valueOf(comparage - this.mTime));
    }
}