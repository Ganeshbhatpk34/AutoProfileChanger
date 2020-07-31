package com.ganapthi.autoprofiler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class ProximityDetector extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("onReceive", "in onReceive");

		String name, profile;

		// Key for determining whether user is leaving or entering
		String k = LocationManager.KEY_PROXIMITY_ENTERING;

		boolean state = intent.getBooleanExtra(k, false);

		// get the name and the profile for the location
		name = intent.getStringExtra("name");
		profile = intent.getStringExtra("profile");

		if (state) {
			// set the audio manager
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			
			if (profile.equalsIgnoreCase("silent"))
				audioManager.setRingerMode(audioManager.RINGER_MODE_SILENT);
			
			else if (profile.equalsIgnoreCase("vibrate"))
				audioManager.setRingerMode(audioManager.RINGER_MODE_VIBRATE);
			
			else if (profile.equalsIgnoreCase("loud"))
				audioManager.setRingerMode(audioManager.RINGER_MODE_NORMAL);
			
			//Toast.makeText(context, name + " profile is active", 600).show();

		} else {
			// Other custom Notification
			Toast.makeText(context,"You are out of range" + name, 600).show();
		}

	}

}
