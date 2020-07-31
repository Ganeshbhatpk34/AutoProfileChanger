package com.ganapthi.autoprofiler;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service implements LocationListener {
	
	private  final String proximityIntentAction = "com.nightowl.profiler.Proximity";

	SQLiteDatabase db;

	GPSTracker gps;

	DatabaseAccess db_access;

	LocationManager locationManager;

	private double longitude;

	private double latitude;

	private String name;

	private String profile;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		db_access = new DatabaseAccess(this);
		db = db_access.openDatabase();
		gps = new GPSTracker(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 400, 1, this);

		Cursor c = db_access.retrieveLocation(db);

		while (c.moveToNext()) {
			int longitude1 = c.getColumnIndex("longitude");
			int latitude1 = c.getColumnIndex("latitude");
			int name1 = c.getColumnIndex("name");
			int profile1 = c.getColumnIndex("profile");

			this.longitude = c.getDouble(longitude1);
			this.latitude = c.getDouble(latitude1);
			this.name = c.getString(name1);
			this.profile = c.getString(profile1);

			addProximityAlert(this.latitude, this.longitude, this.name,
					this.profile);
		}

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Timer timer = new Timer();
		MyTimerTask task = new MyTimerTask();

		Log.d("locationService", "inside on start");
		timer.scheduleAtFixedRate(task, 0, 6000);

	}

	public void addProximityAlert(double latitude, double longitude,
			String name, String profile) {

		// Context context = getApplicationContext();
		Intent intent = new Intent(proximityIntentAction);

		// store name and profile for that location
		// in the intent required by the service
		intent.putExtra("name", name);
		intent.putExtra("profile", profile);

		try {
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 0, intent,
					PendingIntent.FLAG_ONE_SHOT);

			locationManager.addProximityAlert(longitude, latitude, 1, -1,
					pendingIntent);

			Toast.makeText(getApplicationContext(), "alert added for " + name,
					600).show();

			Log.d("Current Loc",
					locationManager.getLastKnownLocation(
							LocationManager.GPS_PROVIDER).toString());
			// getApplicationContext().sendBroadcast(intent);
			IntentFilter filter = new IntentFilter(proximityIntentAction);
			registerReceiver(new ProximityDetector(), filter);
		}

		catch (Exception e) {
			// TODO Auto-generated catch block
			e.getMessage();
			e.printStackTrace();
		}
	}

	/*
	 * timer class executes every 10 mins
	 */
	public class MyTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
		 Log.v("Service","Location changed");
		    if (location != null) {
		            Log.d("Location changed : Lat: ", ""+location.getLatitude());
		            Log.d("Location changed : Lon: ", ""+location.getLongitude());
		    }

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
