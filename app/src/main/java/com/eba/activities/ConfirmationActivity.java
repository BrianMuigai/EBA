package com.eba.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.eba.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConfirmationActivity extends BaseActivity {

    private EditText etEmail;
    private EditText etName;

    public static void start(Context context){
        Intent intent = new Intent(context, ConfirmationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_details_confirmation);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        me = FirebaseAuth.getInstance().getCurrentUser();
        if (me.getDisplayName() != null) {
            etName.setText(me.getDisplayName());
        }
        if (me.getEmail()!= null){
            etEmail.setText(me.getEmail());
        }

        findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()){
                    if (etName.getText().toString() != me.getDisplayName()) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(etName.getText().toString())
                                .build();
                    }
                    MainActivity.start(ConfirmationActivity.this);
                    ConfirmationActivity.this.finish();
                }
            }
        });
    }

    private boolean isValidData() {
        TextInputLayout nameLayout, emailLayout;
        nameLayout = findViewById(R.id.name_layout);
        emailLayout = findViewById(R.id.email_layout);
        if (etName.getText().toString().isEmpty()){
            nameLayout.setErrorEnabled(true);
//            emailLayout.setErrorEnabled(false);
            nameLayout.setError("Please enter your name");
            return false;
//        }else if (etEmail.getText().toString().isEmpty()){
//            emailLayout.setErrorEnabled(true);;
//            nameLayout.setErrorEnabled(false);
//            emailLayout.setError("Please enter your email");
//            return false;
        }
        return true;
    }
}
