package com.ganapthi.autoprofiler;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AndroidGPSTrackingActivity extends Activity {

	Button btnShowLocation, btnSave, btnShowMap;
	Spinner spinner;
	EditText etLongitude, etLatitude, name;
	Double longitude, latitude;
	String profile_name, profile;

	// GPSTracker class
	GPSTracker gps;

	// location class
	Location loc;

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

					double latitude = gps.getLatitude();
					double longitude = gps.getLongitude();

					// \n is for new line
					Toast.makeText(
							getApplicationContext(),
							"Your Location is - \nLat: " + latitude
									+ "\nLong: " + longitude, Toast.LENGTH_LONG)
							.show();

					String lat = Double.toString(latitude);
					String lon = Double.toString(longitude);

					etLongitude.setText(lat);
					etLatitude.setText(lon);
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
				Intent showMapIntent = new Intent(
						"android.intent.action.MAPSACTIVITY");
				startActivity(showMapIntent);
			}
		});

	}
}