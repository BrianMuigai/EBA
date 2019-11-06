package com.eba.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends BaseActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, AuthActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createSignInIntent();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                me = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "Logged in!", Toast.LENGTH_SHORT).show();
                isLoggedIn = true;
                ConfirmationActivity.start(AuthActivity.this);
                AuthActivity.this.finish();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...todo
                if (response != null){
                    Log.e("Auth", response.getError().getMessage());
                    Toast.makeText(this, "Login error!!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
