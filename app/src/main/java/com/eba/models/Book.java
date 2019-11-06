package com.eba.models;

import android.content.Context;
import android.content.Intent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class Book implements Comparable<Book>{
    String title, description;
    int currentPage;
    long timeStamp, id;

    public Book(Context context) {
        timeStamp = Calendar.getInstance().getTimeInMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void toIntent(Intent intent){
        intent.putExtra("com.eba.book.title", title);
        intent.putExtra("com.eba.book.currentPage", currentPage);
        intent.putExtra("com.eba.book.timestamp", timeStamp);
        intent.putExtra("com.eba.book.description", description);
    }

    public void fromIntent(Intent intent){
        title = intent.getStringExtra("com.eba.book.title");
        currentPage = intent.getIntExtra("com.eba.book.currentPage", 1);
        timeStamp = intent.getLongExtra("com.eba.book.timestamp", Calendar.getInstance().getTimeInMillis());
        description = intent.getStringExtra("com.eba.book.description");
    }

    public void serialize(DataOutputStream dos) throws IOException {
        dos.writeChars(title);
        dos.writeInt(currentPage);
        dos.writeLong(timeStamp);
        dos.writeChars(description);
    }

    public void deserialize(DataInputStream dis) throws  IOException{
        title = String.valueOf(dis.readChar());
        currentPage = dis.readInt();
        timeStamp = dis.readLong();
        description = String.valueOf(dis.readChar());
    }

    public int compareTo(Book aThat)
    {
        final long thisNext = getTimeStamp();
        final long thatNext = aThat.getTimeStamp();
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this == aThat)
            return EQUAL;

        if (thisNext > thatNext)
            return AFTER;
        else if (thisNext < thatNext)
            return BEFORE;
        else
            return EQUAL;
    }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", currentPage=" + currentPage +
                ", timeStamp=" + timeStamp +
                ", id=" + id +
                '}';
    }
}
