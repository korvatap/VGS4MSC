package fi.oulu.tol.vgs4msc;

import fi.oulu.tol.vgs4msc.MainService.MainServiceBinder;
import fi.tol.oulu.vgs4msc.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

public class VGSActivity extends Activity{
	
	private MainService mMainService;
	
	public static final String TAG = "fi.oulu.tol.vgs4msc.VGSActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vgs4activity_layout);
		
		Intent intent = new Intent(this, MainService.class);
		startService(intent);
		
		Log.d("ACTIVITY", "STARTSERVICE CALLED" + intent.getClass());
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStart() {
		   super.onStart();
	}
	
	@Override
    public void onDestroy()
    {
        super.onDestroy();
    }
	
	@Override
	public void onStop() {
	   super.onStop();
	}
	
	@Override
	public void onPause() {
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
