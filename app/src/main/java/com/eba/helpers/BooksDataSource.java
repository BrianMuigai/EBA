package com.eba.helpers;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import android.content.Context;

import com.eba.activities.TaskList;
import com.eba.models.Book;

public class BooksDataSource {
    private static final String TAG = "BookList";

    private static final BooksDataSource M_BOOK_DATA_SOURCE = new BooksDataSource();
    private static Context mContext = null;
    private static ArrayList<Book> mList = null;
    private static long mNextId;

    private static final String DATA_FILE_NAME = "books.txt";
    private static final long MAGIC_NUMBER = 0x54617261646f7641L;

    protected BooksDataSource() {
    }

    public static synchronized BooksDataSource getInstance(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
            load();
        }
        return M_BOOK_DATA_SOURCE;
    }

    private static void load() {
        Log.i(TAG, "BooksDataSource.load()");

        mList = new ArrayList<Book>();
        mNextId = 1;

        try {
            DataInputStream dis = new DataInputStream(mContext.openFileInput(DATA_FILE_NAME));
            long magic = dis.readLong();
            int size;

            if (MAGIC_NUMBER == magic) {
                mNextId = dis.readLong();
                size = dis.readInt();

                for (int i = 0; i < size; i++) {
                    Book book = new Book(mContext);
                    book.deserialize(dis);
                    mList.add(book);
                }
            }

            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        Log.i(TAG, "BooksDataSource.save()");

        try {
            DataOutputStream dos = new DataOutputStream(mContext.openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE));

            dos.writeLong(MAGIC_NUMBER);
            dos.writeLong(mNextId);
            dos.writeInt(mList.size());

            for (int i = 0; i < mList.size(); i++)
                mList.get(i).serialize(dos);

            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int size() {
        return mList.size();
    }

    public static Book get(int position) {
        return mList.get(position);
    }

    public static void add(Book book) {
        book.setId(mNextId++);
        mList.add(book);
        Collections.sort(mList);
        save();
    }

    public static void remove(int index) {
        mList.remove(index);
        save();
    }

    public static void remove(Book book){
        mList.remove(book);
        save();
    }

    public static void update(Book book) {
        Collections.sort(mList);
        save();
    }

    public static long getNextId() {
        return mNextId;
    }
}

