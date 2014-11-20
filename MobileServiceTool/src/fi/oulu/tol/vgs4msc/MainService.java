package fi.oulu.tol.vgs4msc;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class MainService extends Service implements AreaObserver {
	
	private final MainServiceBinder binder = new MainServiceBinder();
	private CompassSensor mCompass;
	private GPSTracker mGps;
	
	private static final String SHUTDOWN_SERVICE = "fi.oulu.tol.VGS4MSC.action.SHUTDOWN";
	private static final String START_SERVICE = "fi.oulu.tol.VGS4MSC.action.START";
	private static final String NETWORK_INFO = "fi.oulu.tol.VGS4MSC.action.NETWORKINFO";
	public static final String TAG = "fi.oulu.tol.vgs4msc.MainService";
	
	private String mIpAddress = "127.0.0.1";
	private String mPort = "27015";
	
	BroadcastReceiver mReceiver;

	public class MainServiceBinder extends Binder {
		MainService getService() {
			return MainService.this;
	   }	
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SERVICE", "ONBIND");
		return binder;
	}
	
	@Override
	public void onCreate() {
		//Give context info to GPS & Compass
		mGps = new GPSTracker(this);
		mCompass = new CompassSensor(this);
		
		mReceiver = new MyReceiver(this);
		IntentFilter filter = new IntentFilter();
        filter.addAction(SHUTDOWN_SERVICE);
        filter.addAction(START_SERVICE);
        filter.addAction(NETWORK_INFO);
        registerReceiver(mReceiver, filter);
		
		mGps.setObserver(this);
		mCompass.setObserver(this);
		
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
		mGps.stop();
	}

	@Override
	public void newDegree() {
		Log.d("DEGREES: ", Float.toString(mCompass.getDegrees()));
	}
	
	@Override
	public void newLocation() {
		Log.d("GPSN: ", "Latitude: " + Double.toString(mGps.getLatitude()) + " Longitude: " +  Double.toString(mGps.getLongitude()));
	}
	
    public class MyReceiver extends BroadcastReceiver {
    	
    	private MainService mService;

        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	Log.d("RECEIVED", intent.getAction());
        	
        	if(intent.getAction().equals(START_SERVICE)) {
        		//TODO HOW TO START SERVICE
        		
        	} else if(intent.getAction().equals(SHUTDOWN_SERVICE)) {
        		//TODO HOW TO STOP SERVICE
        		
        	} else if(intent.getAction().equals(NETWORK_INFO)) {
        		Log.d("RECEIVED", "NETWORKINFO");
        		if(intent.getStringExtra("IP") != null && intent.getStringExtra("PORT") != null) {
        			mIpAddress = intent.getStringExtra("IP");
        			mPort = intent.getStringExtra("PORT");
        			
        			//TODO UPDATE NETWORK RECEIVER/SENDER WITH NEW SETTINGS
        		}
        	}
        	
        }

        public MyReceiver(MainService s){
        	mService = s;
        }
    }
}
