package fi.oulu.tol.vgs4msc;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

final class GPSTracker implements LocationListener {
	
	private final Context mContext;
 
    Location mLocation; // location
    double mLatitude; // latitude
    double mLongitude; // longitude
    
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meter
 
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 1 second
 
    // Declaring a Location Manager
    private LocationManager mLocationManager;
    private boolean mIsGpsEnabled;
    private boolean mIsNetworkEnabled;
    private boolean mIsRunning;
    
    //Observer
    private AreaObserver mObserver;
    
    public GPSTracker(Context context) {
        this.mContext = context;
        
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        start();
    }
    
    public void start() {
        registerForLocationUpdates();
        mIsRunning = true;
    }
    
    public void stop() {
    	unregisterForLocationUpdates();
    	mIsRunning = false;
    }
    
	public void setObserver(AreaObserver obs) {
		mObserver = obs;
	}
	
	public AreaObserver getObserver(AreaObserver obs) {
		return mObserver;
	}
    
    public double getLatitude(){
        if(mLocation != null){
            mLatitude = mLocation.getLatitude();
        }
        return mLatitude;
    }
     

    public double getLongitude(){
        if(mLocation != null){
            mLongitude = mLocation.getLongitude();
        }
        return mLongitude;
    }
     
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
    	if(!mIsNetworkEnabled && !mIsGpsEnabled) {
    		return false;
    	} 
        return true;
    }

	@Override
	public void onLocationChanged(Location location) {
		Log.d("GPS Tracker", "onLocationChanged");
		mLocation = location;
		if(mIsRunning && mObserver != null) {
			mObserver.newLocation();
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		boolean isAvailable = (status == LocationProvider.AVAILABLE);
        if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
        	mIsNetworkEnabled = isAvailable;
        } else if (LocationManager.GPS_PROVIDER.equals(provider)) {
        	mIsGpsEnabled = isAvailable;
        } else {
        	Log.d("GPS Tracker", "Location provider is no longer available!");
        }
	}

	@Override
	public void onProviderEnabled(String provider) {
        if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
        	mIsNetworkEnabled = true;
        } else if (LocationManager.GPS_PROVIDER.equals(provider)) {
        	mIsGpsEnabled = true;
        }
	}

	@Override
	public void onProviderDisabled(String provider) {
		 if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
			 mIsNetworkEnabled = false;
	        } else if (LocationManager.GPS_PROVIDER.equals(provider)) {
	        	mIsGpsEnabled = false;
	        } else {
	        	Log.d("GPS Tracker", "Location provider disabled!");
	        }
	}
	
	private void registerForLocationUpdates() {
		
        mIsGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        if(!mIsGpsEnabled && !mIsNetworkEnabled) {
        	Log.v("GPS Tracker", "GPS NOR NETWORK IS NOT AVAILABLE");
        } else {
        	if(mIsGpsEnabled) {
        		try {
        		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        		} catch (SecurityException e) {
        			Log.e("GPS Tracker", "Security exception for location updates!!!");
        		}
        		Log.d("GPS Tracker", "GPS Enabled");
        		if (mLocationManager != null) {
        			mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        			if(mLocation != null) {
        				mLatitude = mLocation.getLatitude();
        				mLongitude = mLocation.getLongitude();
        			}
        		}
        	} else if (mIsNetworkEnabled) {
        		try {
        			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        		} catch (SecurityException e) {
        			Log.e("GPS Tracker", "Security exception for location updates!!!");
        		}
        		Log.d("GPS Tracker", "NETWORK Enabled");
        		if (mLocationManager != null) {
        			mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        			if(mLocation != null) {
        				mLatitude = mLocation.getLatitude();
        				mLongitude = mLocation.getLongitude();
        			}
        		}
        	}
        }
	}
	
	private void unregisterForLocationUpdates() {
		mLocationManager.removeUpdates(this);
	}

}
