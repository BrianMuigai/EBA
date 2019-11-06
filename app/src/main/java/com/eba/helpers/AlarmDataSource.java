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
import com.eba.models.Alarm;

public class AlarmDataSource {
    private static final String TAG = "TaskList";

    private static final AlarmDataSource M_ALARM_DATA_SOURCE = new AlarmDataSource();
    private static Context mContext = null;
    private static ArrayList<Alarm> mList = null;
    private static long mNextId;

    private static final String DATA_FILE_NAME = "alarmme.txt";
    private static final long MAGIC_NUMBER = 0x54617261646f7641L;

    protected AlarmDataSource() {
    }

    public static synchronized AlarmDataSource getInstance(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
            load();
        }
        return M_ALARM_DATA_SOURCE;
    }

    public static List<Alarm> getList(int type){
        List<Alarm> res = new ArrayList<>();
        for (Alarm alarm : mList){
            Log.e(TAG, "alarm: "+alarm.toString());
            if (alarm.getType() == type){
                res.add(alarm);
            }
        }
        return res;
    }

    private static void load() {
        Log.i(TAG, "AlarmDataSource.load()");

        mList = new ArrayList<Alarm>();
        mNextId = 1;

        try {
            DataInputStream dis = new DataInputStream(mContext.openFileInput(DATA_FILE_NAME));
            long magic = dis.readLong();
            int size;

            if (MAGIC_NUMBER == magic) {
                mNextId = dis.readLong();
                size = dis.readInt();

                for (int i = 0; i < size; i++) {
                    Alarm alarm = new Alarm(mContext);
                    alarm.deserialize(dis);
                    mList.add(alarm);
                }
            }

            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        Log.i(TAG, "AlarmDataSource.save()");

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

    public static int countHealthTasks(){
        int counter = 0;
        for (Alarm alarm : mList){
            if (alarm.getType() == TaskList.HEALTH){
                counter ++;
            }
        }
        return counter;
    }

    public static int countOtherTasks(){
        int counter = 0;
         for (Alarm alarm : mList){
             if (alarm.getType() == TaskList.OTHER);{
                 counter ++;
             }
         }
         return counter;
    }

    public static int size() {
        return mList.size();
    }

    public static Alarm get(int position) {
        return mList.get(position);
    }

    public static void add(Alarm alarm) {
        alarm.setId(mNextId++);
        mList.add(alarm);
        Collections.sort(mList);
        save();
    }

    public static void remove(int index) {
        mList.remove(index);
        save();
    }

    public static void remove(Alarm alarm){
        mList.remove(alarm);
        save();
    }

    public static void update(Alarm alarm) {
        alarm.update();
        Collections.sort(mList);
        save();
    }

    public static long getNextId() {
        return mNextId;
    }
}

