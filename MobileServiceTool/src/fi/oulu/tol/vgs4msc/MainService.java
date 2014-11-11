package fi.oulu.tol.vgs4msc;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MainService extends Service {
	
	private final VoteServiceBinder binder = new VoteServiceBinder();

	public class VoteServiceBinder extends Binder {
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
        
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.i("LocalService", "Received start id " + startId + ": " + intent);
	        // We want this service to continue running until it is explicitly
	        // stopped, so return sticky.
	        return START_STICKY;
	}

}
