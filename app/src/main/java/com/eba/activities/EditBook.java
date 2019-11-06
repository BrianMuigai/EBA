package com.eba.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.eba.R;
import com.eba.models.Book;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.MessageFormat;
import java.util.Date;

public class EditBook extends BaseActivity {

    private EditText mTitle;
    private EditText currentPage;
    private EditText description;

    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        me = FirebaseAuth.getInstance().getCurrentUser();
        if (isLoggedIn){
            TextView nameView = findViewById(R.id.name);
            nameView.setText(me.getDisplayName());
        }
        TextView date = findViewById(R.id.date);
        date.setText(new Date().toString().substring(0, 10));
        mTitle = (EditText) findViewById(R.id.title);
        description = findViewById(R.id.description);
        currentPage = findViewById(R.id.current_page);

        book = new Book(this);
        book.fromIntent(getIntent());

        mTitle.setText(book.getTitle());
        currentPage.setText(MessageFormat.format("{0}", book.getCurrentPage()));
        description.setText(book.getDescription());
    }

    private boolean isValidData() {
        TextInputLayout nameInput, descriptionInput;
        nameInput = findViewById(R.id.name_input);
        descriptionInput = findViewById(R.id.description_input);

        if (mTitle.getText().toString().isEmpty()) {
            nameInput.setErrorEnabled(true);
            nameInput.setError("Please enter a valid title");
            descriptionInput.setErrorEnabled(false);
            return false;
        } else if (description.getText().toString().isEmpty()) {
            descriptionInput.setErrorEnabled(true);
            descriptionInput.setError("Please describe this task");
            nameInput.setErrorEnabled(false);
            return false;
        }
        return true;
    }

    public void onDoneClick(View view) {
        if (isValidData()) {
            Intent intent = new Intent();

            book.setDescription(description.getText().toString());
            book.setTitle(mTitle.getText().toString());
            book.setCurrentPage(Integer.valueOf(currentPage.getText().toString()));
            book.toIntent(intent);
            Log.d("EditBook", "Book: "+ book.toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void onCancelClick(View view) {
        setResult(RESULT_CANCELED, null);
        finish();
    }
}
