package com.eba.chat.helper;

import android.content.Context;
import android.util.Log;

import com.eba.helpers.AlarmDataSource;
import com.eba.models.Alarm;
import com.google.gson.JsonElement;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TaskHelper {

    private static final SimpleDateFormat DATE_FORMATER = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIME_FORMATER = new SimpleDateFormat("hh-mm-dd");

    private static boolean isValidDate(String dateTime, SimpleDateFormat format){
        if (dateTime == null){
            return false;
        }
        format.setLenient(false);
        try{
            Date date = format.parse(dateTime);
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void addAlarm(Context context, HashMap<String, JsonElement> params){
        if (params != null && !params.isEmpty()){
            String dateTime = params.get("date-time").getAsString().toLowerCase();
            String title = params.get("name").getAsString();
            Alarm alarm = new Alarm(context);
            alarm.setTitle(title);
            alarm.setDate(getDate(dateTime).getTime());
            alarm.setDescription(title);

            AlarmDataSource.getInstance(context).add(alarm);
            Log.e("Bot helper", "added alarm time: "+getDate(dateTime).toString());
            Log.e("Bot helper", "added alarm: "+alarm.toString());
        }
    }

    private static Date getDate(final String dateTime){
        Date d = new Date();
        DateFormat dateFormat;
        try {
            dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.US);
            d = dateFormat.parse(dateTime);
        } catch (ParseException e) {
            try{
                dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                d = dateFormat.parse(dateTime);
            }catch (ParseException e1){
                if (dateTime.contains("t")){
                    try {
                        String tmp = dateTime.replace("t", " ");
                        d = new SimpleDateFormat("yyyy-M-dd HH:mm:ss", Locale.US).parse(tmp);
                    }catch (ParseException e2){

                    }
                }
            }
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        d = calendar.getTime();
        return d;
    }
}
