package com.ganapthi.autoprofiler;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AndroidGPSTrackingActivity extends Activity {

	Button btnShowLocation, btnSave, btnShowMap;
	Spinner spinner;
	EditText etLongitude, etLatitude, name;
	Double longitude, latitude;
	String profile_name, profile;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;

	// GPSTracker class
	GPSTracker gps;

	// location class
	Location loc;
    private SimpleLocation location;

	// SQL object
	SQLiteDatabase db;

	// database object
	DatabaseAccess dbAccess;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnShowLocation = (Button) findViewById(R.id.btnShowLocation);
		spinner = (Spinner) findViewById(R.id.spinner);
		etLongitude = (EditText) findViewById(R.id.etlongitude);
		etLatitude = (EditText) findViewById(R.id.etLatitude);
		name = (EditText) findViewById(R.id.etName);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnShowMap = (Button) findViewById(R.id.btnShowMap);

		dbAccess = new DatabaseAccess(this);
		db = dbAccess.openDatabase();// returns the database

		// save the data from the fields into database
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// store the values from the field in the variables
				longitude = (Double.parseDouble(etLongitude.getText()
						.toString()));
				latitude = (Double.parseDouble(etLatitude.getText().toString()));
				profile = spinner.getSelectedItem().toString();
				profile_name = name.getText().toString();

				// instantiate Location object
				loc = new Location();
				loc.setName(profile_name);
				loc.setProfile_name(profile);
				loc.setLongitude(longitude);
				loc.setLatitude(latitude);

				int rowCheck = dbAccess.store_location(db, loc);
				if (rowCheck == -1) {
					Toast.makeText(getApplicationContext(),
							"Error occured while storing the record", Toast.LENGTH_LONG)
							.show();
				} else {
					Toast.makeText(getApplicationContext(),
							"Success", Toast.LENGTH_SHORT)
							.show();
					Intent profileListIntent = new Intent(AndroidGPSTrackingActivity.this, ProfileList.class);
					startActivity(profileListIntent);
				}
			}
		});

		// show location button click event
		btnShowLocation.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// create class object
				gps = new GPSTracker(AndroidGPSTrackingActivity.this);

				// check if GPS enabled
				if (gps.canGetLocation()) {

                    getLocationPermission();

				} else {
					// can't get location
					// GPS or Network is not enabled
					// Ask user to enable GPS/network in settings
					gps.showSettingsAlert();
				}

			}
		});

		btnShowMap.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
			}
		});

	}

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            location = new SimpleLocation(this);
            etLongitude.setText(String.valueOf(location.getLongitude()));
            etLatitude.setText(String.valueOf(location.getLatitude()));

            longitude = location.getLongitude();
            latitude = location.getLatitude();

			btnShowMap.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.INTERNET},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            default: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.app_name) + " was not allowed to access your location" ,Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}