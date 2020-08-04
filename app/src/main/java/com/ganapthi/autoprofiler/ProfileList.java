package com.ganapthi.autoprofiler;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ProfileList extends Activity {

	private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;

	ListView profileList;
	Button add, delete;

	SQLiteDatabase db;
	DatabaseAccess db_access;
	GPSTracker gps;

	ArrayList<String> profileNames;
	ArrayList<String> profileMode;
	String listName, mode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_list);

		profileList = (ListView) findViewById(R.id.listView1);
		add = (Button) findViewById(R.id.add);
		delete = (Button) findViewById(R.id.delete);

		getLocationPermission();

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent profileSaveIntent = new Intent(ProfileList.this, AndroidGPSTrackingActivity.class);
				startActivity(profileSaveIntent);

			}
		});

		delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				db_access.clearLocation(db);
				setList();
			}
		});

		db_access = new DatabaseAccess(this);
		db = db_access.openDatabase();

		setList();

	}

	public void setList(){
		// retrieve location from database
		Cursor c = db_access.retrieveLocation(db);

		profileNames = new ArrayList<String>();
		profileMode = new ArrayList<String>();

		while (c.moveToNext()) {
			int name = c.getColumnIndex("name");
			int profile = c.getColumnIndex("profile");

			mode = c.getString(profile);
			listName = c.getString(name);
			profileNames.add(listName);
			profileMode.add(mode);
		}
		/*
		 * check here if the profileNames is empty if empty then the list view
		 * will be empty.
		 */

		if (profileNames.isEmpty()) {
			// profileNames = new ArrayList<String>();
			// adding some values for testing purpose
			profileNames.add("Sample Profile");
			profileMode.add("profile");

			// Creating the list adapter and populating the list
			final ArrayAdapter<String> listAdapter = new CustomListAdapter(
					this, profileNames, profileMode);

			// set the adapter to the list view
			profileList.setAdapter(listAdapter);

		}

		else {

			// Creating the list adapter and populating the list
			final ArrayAdapter<String> listAdapter = new CustomListAdapter(
					this, profileNames, profileMode);
			// clears previous listView and updates the listView with fresh data

			// listAdapter.clear();
			// listAdapter.notifyDataSetChanged();
			// set the adapter to the list view
			profileList.setAdapter(listAdapter);

			// click on list item to show edit and delete button
			profileList.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
										int position, long id) {
				}
			});

			// on item long click, allows to edit the profile
			profileList.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0,
											   View view, int position, long id) {
					// TODO Auto-generated method stub
					return false;
				}
			});

			// starting the service
			startService(new Intent(this, LocationService.class));
		}
	}

	/*
	 * custom adapter for the listView
	 */
	class CustomListAdapter extends ArrayAdapter<String> {

		TextView profileName, status;
		ArrayList<String> profileList, profileMode;
		Activity c;

		public CustomListAdapter(Context context, ArrayList<String> profileList, ArrayList<String> profileMode) {
			super(context, R.layout.list_item, profileList);
			this.profileList = profileList;
			this.profileMode = profileMode;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_item,
						null);
			}

			profileName = (TextView) convertView.findViewById(R.id.profileName);
			status = (TextView) convertView.findViewById(R.id.status);

			profileName.setText(profileList.get(position).toString());
			status.setText(profileMode.get(position).toString());
			// set the status text to active or inactive depending upon the
			// profile which is on
			return convertView;
		}
	}

	private void getLocationPermission() {
		if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {

		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
							android.Manifest.permission.ACCESS_FINE_LOCATION },
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
