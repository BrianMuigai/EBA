package com.eba.activities;

import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eba.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DirectionsActivity extends BaseActivity {

    private static final int REQUEST_PLACE = 111;
    private static final String TAG = "DirectionsActivity";
    private Place selectedPlace;
    private TextView destination;

    public static void start(Context context) {
        Intent intent = new Intent(context, DirectionsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);
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
        initPlaces();

        Button direction = findViewById(R.id.btn_direction);
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputLayout inputLayout = findViewById(R.id.destination_input);
                if (selectedPlace != null) {
                    inputLayout.setErrorEnabled(false);
                    String lat = String.valueOf(selectedPlace.getLatLng().latitude);
                    String ln = String.valueOf(selectedPlace.getLatLng().longitude);
                    String query = "google.navigation:q=" + lat + "," + ln;
                    Uri gmmUriIntent = Uri.parse(query);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmUriIntent);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }else{
                    inputLayout.setErrorEnabled(true);
                    inputLayout.setError("Please select your destination");
                }
            }
        });
        destination = findViewById(R.id.destination);

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Set the fields to specify which types of place data to return.
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS,
                        Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(DirectionsActivity.this);
                startActivityForResult(intent, REQUEST_PLACE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            selectedPlace = Autocomplete.getPlaceFromIntent(data);
            String placeName = selectedPlace.getName()+", "+selectedPlace.getAddress();
            destination.setText(placeName);
        }else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // TODO: Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(DirectionsActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            Log.i(TAG, status.getStatusMessage());
        } else if (resultCode == RESULT_CANCELED) {
            // The user canceled the operation.
        }
    }
}
