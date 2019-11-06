package com.eba.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eba.R;
import com.eba.helpers.BooksDataSource;
import com.eba.models.Book;

import java.text.MessageFormat;
import java.util.Date;

public class BookListAdapter extends BaseAdapter {
    private final String TAG = "TaskList";

    private Context mContext;
    private BooksDataSource booksDataSource;
    private LayoutInflater mInflater;

    public BookListAdapter(Context context) {
        mContext = context;
        booksDataSource = BooksDataSource.getInstance(context);

        Log.i(TAG, "BookListAdapter.create()");

        mInflater = LayoutInflater.from(context);

        dataSetChanged();
    }

    public void save() {
        booksDataSource.save();
    }

    public void update(Book book) {
        booksDataSource.update(book);
        dataSetChanged();
    }

    public void updateBooks() {
        Log.i(TAG, "BookListAdapter.updateBooks()");
        for (int i = 0; i < booksDataSource.size(); i++)
            booksDataSource.update(booksDataSource.get(i));
        dataSetChanged();
    }

    public void add(Book book) {
        booksDataSource.add(book);
        dataSetChanged();
    }

    public void delete(int index) {
        booksDataSource.remove(booksDataSource.get(index));
        dataSetChanged();
    }

    public int getCount() {
        return booksDataSource.size();
    }

    public Book getItem(int position) {
        return booksDataSource.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Book book = booksDataSource.get(position);
        Log.e(TAG, "Book: "+book.toString());

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.book, null);

            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.description = convertView.findViewById(R.id.description);
            holder.details = (TextView) convertView.findViewById(R.id.item_details);
            holder.currentPage = convertView.findViewById(R.id.current_page);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(book.getTitle());
        holder.description.setVisibility(View.GONE);
        holder.details.setText(new Date(book.getTimeStamp()).toString());
        holder.currentPage.setText(MessageFormat.format("Current Page: {0}", book.getCurrentPage()));

        return convertView;
    }

    private void dataSetChanged() {
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView title;
        TextView currentPage;
        TextView description;
        TextView details;
    }
}
