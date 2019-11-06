package com.eba.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.eba.helpers.AlarmDataSource;
import com.eba.helpers.DateTime;
import com.eba.R;
import com.eba.models.Alarm;
import com.eba.services.AlarmReceiver;

import java.util.List;

public class AlarmListAdapter extends BaseAdapter {
    private final String TAG = "TaskList";

    private Context mContext;
    private AlarmDataSource mAlarmDataSource;
    private LayoutInflater mInflater;
    private DateTime mDateTime;
    private int mColorOutdated;
    private int mColorActive;
    private AlarmManager mAlarmManager;
    private List<Alarm> alarmList;
    private int mType;

    public AlarmListAdapter(Context context, int type) {
        mContext = context;
        mType = type;
        mAlarmDataSource = AlarmDataSource.getInstance(context);
        alarmList = mAlarmDataSource.getList(type);

        Log.i(TAG, "AlarmListAdapter.create()");

        mInflater = LayoutInflater.from(context);
        mDateTime = new DateTime(context);

        mColorOutdated = mContext.getResources().getColor(R.color.alarm_title_outdated);
        mColorActive = mContext.getResources().getColor(R.color.alarm_title_active);

        mAlarmManager = (AlarmManager) context.getSystemService(mContext.ALARM_SERVICE);

        dataSetChanged();
    }

    public void save() {
        mAlarmDataSource.save();
    }

    public void update(Alarm alarm) {
        mAlarmDataSource.update(alarm);
        dataSetChanged();
    }

    public void updateAlarms() {
        Log.i(TAG, "AlarmListAdapter.updateAlarms()");
        for (int i = 0; i < mAlarmDataSource.size(); i++)
            mAlarmDataSource.update(mAlarmDataSource.get(i));
        dataSetChanged();
    }

    public void add(Alarm alarm) {
        mAlarmDataSource.add(alarm);
        dataSetChanged();
    }

    public void delete(int index) {
        cancelAlarm(alarmList.get(index));
        mAlarmDataSource.remove(alarmList.get(index));
        dataSetChanged();
    }

    public void onSettingsUpdated() {
        mDateTime.update();
        dataSetChanged();
    }

    public int getCount() {
        return alarmList.size();
    }

    public Alarm getItem(int position) {
        return alarmList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Alarm alarm = alarmList.get(position);
        Log.e(TAG, "Alarm: "+alarm.toString());

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.alarm, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.description = convertView.findViewById(R.id.description);
            holder.details = (TextView) convertView.findViewById(R.id.item_details);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(alarm.getTitle());
        holder.description.setText(alarm.getDescription());
        holder.details.setText(mDateTime.formatDetails(alarm) + (alarm.getEnabled() ? "" : " [disabled]"));

        if (alarm.getOutdated())
            holder.title.setTextColor(mColorOutdated);
        else
            holder.title.setTextColor(mColorActive);

        return convertView;
    }

    private void dataSetChanged() {
        for (int i = 0; i < mAlarmDataSource.size(); i++)
            setAlarm(mAlarmDataSource.get(i));

        alarmList = mAlarmDataSource.getList(mType);
        notifyDataSetChanged();
    }

    private void setAlarm(Alarm alarm) {
        PendingIntent sender;
        Intent intent;

        if (alarm.getEnabled() && !alarm.getOutdated()) {
            intent = new Intent(mContext, AlarmReceiver.class);
            alarm.toIntent(intent);
            sender = PendingIntent.getBroadcast(mContext, (int) alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarm.getDate(), sender);
            Log.i(TAG, "AlarmListAdapter.setAlarm(" + alarm.getId() + ", '" + alarm.getTitle() + "', " + alarm.getDate() + ")");
        }
    }

    private void cancelAlarm(Alarm alarm) {
        PendingIntent sender;
        Intent intent;

        intent = new Intent(mContext, AlarmReceiver.class);
        sender = PendingIntent.getBroadcast(mContext, (int) alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(sender);
    }

    static class ViewHolder {
        TextView title;
        TextView description;
        TextView details;
    }
}

