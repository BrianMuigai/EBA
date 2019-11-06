package com.eba.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.eba.R;
import com.eba.adapters.BookListAdapter;
import com.eba.models.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

public class BooksList extends BaseActivity {

    private static final String TAG = "BooksList";
    private static final int NEW_BOOK_ACTIVITY = 111;
    private static final int EDIT_BOOK_ACTIVITY = 222;
    private final int CONTEXT_MENU_EDIT = 0;
    private final int CONTEXT_MENU_DELETE = 1;

    private ListView booksList;
    private BookListAdapter bookListAdapter;

    public static void start(MainActivity mainActivity) {
        Intent intent = new Intent(mainActivity, BooksList.class);
        mainActivity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        me = FirebaseAuth.getInstance().getCurrentUser();

        if (isLoggedIn){
            TextView nameView = findViewById(R.id.name);
            nameView.setText(me.getDisplayName());
        }
        TextView date = findViewById(R.id.date);
        date.setText(new Date().toString().substring(0, 10));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddBookClick(view);
            }
        });

        booksList = (ListView) findViewById(R.id.object_list);

        bookListAdapter = new BookListAdapter(this);
        booksList.setAdapter(bookListAdapter);
        booksList.setOnItemClickListener(mListOnItemClickListener);
        registerForContextMenu(booksList);

        TextView category = findViewById(R.id.task_category);
        category.setText("Books");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "BooksList.onDestroy()");
//    mAlarmListAdapter.save();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "BooksList.onResume()");
        bookListAdapter.updateBooks();
    }

    public void onAddBookClick(View view) {
        Intent intent = new Intent(getBaseContext(), EditBook.class);
        new Book(this).toIntent(intent);

        BooksList.this.startActivityForResult(intent, NEW_BOOK_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Book book = new Book(BooksList.this);
        if (requestCode == NEW_BOOK_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                book.fromIntent(data);
                bookListAdapter.add(book);
            }
        } else if (requestCode == EDIT_BOOK_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                book.fromIntent(data);
                bookListAdapter.update(book);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.object_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            menu.setHeaderTitle(bookListAdapter.getItem(info.position).getTitle());
            menu.add(Menu.NONE, CONTEXT_MENU_EDIT, Menu.NONE, "Edit");
            menu.add(Menu.NONE, CONTEXT_MENU_DELETE, Menu.NONE, "Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = item.getItemId();

        if (index == CONTEXT_MENU_EDIT) {
            Intent intent = new Intent(getBaseContext(), EditBook.class);

            Book book = bookListAdapter.getItem(info.position);
            book.toIntent(intent);
            startActivityForResult(intent, EDIT_BOOK_ACTIVITY);
        } else if (index == CONTEXT_MENU_DELETE) {
            bookListAdapter.delete(info.position);
        }

        return true;
    }

    private AdapterView.OnItemClickListener mListOnItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getBaseContext(), EditBook.class);

            Book book = bookListAdapter.getItem(position);
            book.toIntent(intent);
            BooksList.this.startActivityForResult(intent, EDIT_BOOK_ACTIVITY);
        }
    };

}
