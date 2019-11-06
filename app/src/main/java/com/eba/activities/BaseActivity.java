package com.eba.activities;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.eba.BuildConfig;
import com.eba.R;
import com.eba.models.Alarm;
import com.eba.models.Book;
import com.eba.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;

public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    protected final int NEW_ALARM_ACTIVITY = 0;
    protected final int EDIT_ALARM_ACTIVITY = 1;
    protected final int PREFERENCES_ACTIVITY = 2;

    protected static final int RC_SIGN_IN = 222;

    public List<Alarm> tasks;
    public List<Book> books;
    protected FirebaseUser me;
    protected boolean isLoggedIn = (FirebaseAuth.getInstance().getCurrentUser() != null);

    public void initPlaces(){
        String apiKey = getString(R.string.google_maps_key);

        if (apiKey.equals("")) {
            Toast.makeText(this, "No api key", Toast.LENGTH_LONG).show();
            return;
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
    }

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        //new AuthUI.IdpConfig.FacebookBuilder().build(),
        //new AuthUI.IdpConfig.TwitterBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher)      // Set logo drawable
                        .setTheme(R.style.AppTheme)      // Set theme
                        /*.setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")*/
                        .build(),
                RC_SIGN_IN);
    }

}
