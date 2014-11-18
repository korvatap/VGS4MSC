package fi.oulu.tol.vgs4msc;

import fi.oulu.tol.vgs4msc.MainService.MainServiceBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class VGSActivity extends Activity{
	
	private MainService mMainService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startService(new Intent(this, MainService.class));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		   super.onStart();
	}
	
	@Override
	protected void onStop() {
	   super.onStop();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		   @Override
		   public void onServiceConnected(ComponentName name, IBinder binder) {
		      MainServiceBinder voteBinder = (MainServiceBinder)binder;
		      mMainService = voteBinder.getService();
		   }
		   
		   @Override
		   public void onServiceDisconnected(ComponentName name) {
			   mMainService = null;
		   }
		};

}
