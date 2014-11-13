package fi.oulu.tol.vgs4msc;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service implements AreaObserver {
	
	private final MainServiceBinder binder = new MainServiceBinder();
	private final CompassSensor compass = new CompassSensor();
	private final GPSTracker gps = new GPSTracker(this);
	
	private static final String SHUTDOWN_SERVICE = "fi.oulu.tol.VGS4MSC.action.SHUTDOWN";
	private static final String START_SERVICE = "fi.oulu.tol.VGS4MSC.action.START";
	private static final String NETWORK_INFO = "fi.oulu.tol.VGS4MSC.action.NETWORKINFO";
	
	private String mIpAddress = "127.0.0.1";
	private String mPort = "27015";
	
	BroadcastReceiver mReceiver;
	
    public class MyReceiver extends BroadcastReceiver {
    	
    	private MainService mService;

        @Override
        public void onReceive(Context context, Intent intent) {
        	
        	if(intent.getAction().equals(START_SERVICE)) {
        		//TODO HOW TO START SERVICE
        		
        	} else if(intent.getAction().equals(SHUTDOWN_SERVICE)) {
        		//TODO HOW TO STOP SERVICE
        		
        	} else if(intent.getAction().equals(NETWORK_INFO)) {
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
		mReceiver = new MyReceiver(this);
		IntentFilter filter = new IntentFilter();
        filter.addAction("SHUTDOWN_SERVICE");
        filter.addAction("START_SERVICE");
        filter.addAction(NETWORK_INFO);
        registerReceiver(mReceiver, filter);
		
		compass.setObserver(this);
		gps.setObserver(this);
		if(gps.canGetLocation()) {
			Log.d("GPS: ", "Latitude: " + Double.toString(gps.getLatitude()) + " Longitude: " +  Double.toString(gps.getLongitude()));
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
		gps.stop();
	}

	@Override
	public void newDegree() {
		Log.d("DEGREES: ", Float.toString(compass.getDegrees()));
	}
	
	@Override
	public void newLocation() {
		Log.d("GPS: ", "Latitude: " + Double.toString(gps.getLatitude()) + " Longitude: " +  Double.toString(gps.getLongitude()));
	}

}
