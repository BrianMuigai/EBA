package com.eba.chat.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eba.R;
import com.eba.chat.models.Conversation;
import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUnits;
import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.firebase.database.annotations.NotNull;

import java.util.Date;
import java.util.List;


public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Conversation> conversations;
    private Context mContext;
    private String myId;

    private final int TOPIC = 0, YOU = 1, ME = 2;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ConversationAdapter(Context context, List<Conversation> conversationMap, String myId) {
        this.mContext = context;
        this.conversations = conversationMap;
        this.myId = myId;
    }

    // Return the size of your data-set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.conversations.size();
    }

    @Override
    public int getItemViewType(int position) {
        //More to come
        if (conversations.get(position).getSenderID() == null)
            return TOPIC;
        else if (conversations.get(position).getSenderID().equals(myId))
            return ME;
        else if (!conversations.get(position).getSenderID().equals(myId))
            return YOU;

        return -1;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        if (viewType == YOU) {
            View v2 = inflater.inflate(R.layout.layout_holder_you, viewGroup, false);
            viewHolder = new HolderYou(v2);
        } else {
            View v = inflater.inflate(R.layout.layout_holder_me, viewGroup, false);
            viewHolder = new HolderMe(v);
        }
        return viewHolder;
    }

    public void addMessages(List<Conversation> convs) {
        conversations.addAll(convs);
        notifyDataSetChanged();
    }

    public void addMessage(Conversation message){
        conversations.add(message);
        notifyDataSetChanged();
    }

    public void removeMessage(Conversation message){
        conversations.remove(message);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case YOU:
                HolderYou vh2 = (HolderYou) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                HolderMe vh = (HolderMe) viewHolder;
                configureViewHolder3(vh, position);
                break;
        }
    }


    private void configureViewHolder3(HolderMe vh1, int position) {
        Date date = DateTimeUtils.formatDate(conversations.get(position).getTime(),
                DateTimeUnits.MILLISECONDS);
        vh1.getTime().setText(DateTimeUtils.getTimeAgo(mContext, date, DateTimeStyle.SHORT));
        vh1.getChatText().setText(conversations.get(position).getText());
    }

    private void configureViewHolder2(HolderYou vh1, int position) {
        Date date = DateTimeUtils.formatDate(conversations.get(position).getTime(),
                DateTimeUnits.MILLISECONDS);
        vh1.getTime().setText(DateTimeUtils.getTimeAgo(mContext, date, DateTimeStyle.SHORT));
        vh1.getChatText().setText(conversations.get(position).getText());
    }

}
