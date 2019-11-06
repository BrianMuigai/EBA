package com.eba.chat.models;

import java.io.Serializable;

public class Topic implements Serializable {
    
    private String itemID;
    
    public Topic(){}

    public Topic(String itemID){
        this.itemID = itemID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }
}
