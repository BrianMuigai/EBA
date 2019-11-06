package com.eba.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.eba.R;
import com.eba.chat.ConversationActivity;
import com.eba.helpers.AlarmDataSource;
import com.eba.helpers.BooksDataSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Date;

public class MainActivity extends BaseActivity {

    private TextView healthCounter;
    private TextView bookCounter;
    private TextView otherCounter;
    private TextView musicCounter;
    private AlarmDataSource alarmDataSource;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        me = FirebaseAuth.getInstance().getCurrentUser();
        if (!isLoggedIn){
            AuthActivity.start(MainActivity.this);
            finish();
        }else{
            TextView nameView = findViewById(R.id.name);
            nameView.setText(me.getDisplayName());
        }
        TextView date = findViewById(R.id.date);
        date.setText(new Date().toString().substring(0, 10));
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);//todo

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLoggedIn){
                    ConversationActivity.start(MainActivity.this);
                }else{
                    AuthActivity.start(MainActivity.this);
                    MainActivity.this.finish();
                }
            }
        });
        healthCounter = findViewById(R.id.health_counter);
        bookCounter = findViewById(R.id.book_counter);
        otherCounter = findViewById(R.id.other_counter);
        musicCounter = findViewById(R.id.music_counter);
        alarmDataSource = AlarmDataSource.getInstance(this);
        initCounter();

        findViewById(R.id.health_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskList.start(MainActivity.this, TaskList.HEALTH);
            }
        });
        findViewById(R.id.books_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BooksList.start(MainActivity.this);
            }
        });
        findViewById(R.id.maps_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DirectionsActivity.start(MainActivity.this);
            }
        });
        findViewById(R.id.other_tasks_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskList.start(MainActivity.this, TaskList.OTHER);
            }
        });
        findViewById(R.id.music_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN,
                        Intent.CATEGORY_APP_MUSIC);
                startActivity(intent);
            }
        });
    }

    private void initCounter() {
        alarmDataSource = AlarmDataSource.getInstance(this);
        healthCounter.setText(String.valueOf(alarmDataSource.countHealthTasks()));
        bookCounter.setText(String.valueOf(BooksDataSource.getInstance(this).size()));
        otherCounter.setText(String.valueOf(alarmDataSource.countOtherTasks()));
        musicCounter.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCounter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_settings) {
            Intent intent = new Intent(getBaseContext(), Preferences.class);
            startActivityForResult(intent, PREFERENCES_ACTIVITY);
        }

        return super.onOptionsItemSelected(item);
    }
}
