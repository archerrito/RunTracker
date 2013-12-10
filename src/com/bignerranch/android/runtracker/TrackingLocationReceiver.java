package com.bignerranch.android.runtracker;

import android.content.Context;
import android.location.Location;

public class TrackingLocationReceiver extends LocationReceiver {
	
	//good place to call insertLocation(Location) method in RunManager
	@Override
	protected void onLocationReceived(Context c, Location loc) {
		RunManager.get(c).insertLocation(loc);
		//finally register it in the manifest
	}

}
