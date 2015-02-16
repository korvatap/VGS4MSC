package fi.oulu.tol.vgs4msc;

import fi.oulu.tol.vgs4msc.handlers.CallHandler;
import fi.oulu.tol.vgs4msc.handlers.MSGHandler;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service implements AreaObserver {
	
	private final MainServiceBinder binder = new MainServiceBinder();
	private CompassSensor mCompass;
	private GPSTracker mGps;
	private MSGHandler mMsgHandler;
	private MyReceiver mReceiver;
	private CallHandler mLinListener;
	
	private static final String SHUTDOWN_SERVICE = "fi.oulu.tol.VGS4MSC.action.SHUTDOWN";
	private static final String START_SERVICE = "fi.oulu.tol.VGS4MSC.action.START";
	private static final String NETWORK_INFO = "fi.oulu.tol.VGS4MSC.action.NETWORKINFO";
	public static final String TAG = "fi.oulu.tol.vgs4msc.MainService";
	
	private String mIpAddress = "127.0.0.1";
	private String mPort = "27015";

	public class MainServiceBinder extends Binder {
		MainService getService() {
			return MainService.this;
	   }	
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		
        	mReceiver = new MyReceiver();
        	IntentFilter filter = new IntentFilter();
                filter.addAction(START_SERVICE);
                filter.addAction(SHUTDOWN_SERVICE);
                filter.addAction(NETWORK_INFO);
                registerReceiver(mReceiver, filter);	
        		
                mLinListener = new CallHandler(this);
        	mMsgHandler = new MSGHandler(this);
        	
        	//Give context info to GPS & Compass
        	mGps = new GPSTracker(this);
        	mCompass = new CompassSensor(this);
        	
        	mGps.setObserver(this);
        	mCompass.setObserver(this);
        	
        	mMsgHandler.startServer();
        	mGps.start();
        	mCompass.start();
        	mLinListener.start();
        	
        	if(mGps.canGetLocation()) {
        		Log.d("GPS: ", "Latitude: " + Double.toString(mGps.getLatitude()) + " Longitude: " +  Double.toString(mGps.getLongitude()));
        	} else {
        		Log.d("GPS: ", "CANNOT BE COMPLETED");
        	}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.i("LocalService", "Received start id " + startId + ": " + intent);
	        // We want this service to continue running until it is explicitly
	        // stopped, so return sticky.

	        return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.d("SERVICE", "SAMMUTETTU");
		unregisterReceiver(mReceiver);
		mCompass.stop();
		mGps.stop();
		mMsgHandler.closeServer();
		mLinListener.stop();
	}

	@Override
	public void newDegree() {
	        //mMsgHandler.sendMessage(UUID, GPS&DEGREES, VALUES)
		Log.d("DEGREES: ", Float.toString(mCompass.getDegrees()));
	}
	
	@Override
	public void newLocation() {
	      //mMsgHandler.sendMessage(UUID, GPS&DEGREES, VALUES)
		Log.d("GPSN: ", "Latitude: " + Double.toString(mGps.getLatitude()) + " Longitude: " +  Double.toString(mGps.getLongitude()));
	}
	
	private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
	        	
        	Log.d("RECEIVED", intent.getAction());
	        	
        	if(intent.getAction().equals(SHUTDOWN_SERVICE)) {
        		stopSelf();
        	} else if(intent.getAction().equals(NETWORK_INFO)) {
        		Log.d("RECEIVED", "NETWORKINFO");
        		Bundle extras = intent.getExtras();
        		if(extras != null) {
        			if(extras.containsKey("IP")) {
        				mIpAddress = extras.get("IP").toString();
        			}
        			if(extras.containsKey("PORT")) {
        				mPort = extras.get("PORT").toString();
        			}
        			Log.d(TAG, mIpAddress + " " + mPort);
					mMsgHandler.setNetwork(mIpAddress, mPort);
        		}
        	}
        }

    }
	
}
