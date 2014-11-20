package fi.oulu.tol.vgs4msc;

import fi.oulu.tol.vgs4msc.MainService.MainServiceBinder;
import fi.oulu.tol.vgs4msc.MainService.MyReceiver;
import fi.tol.oulu.vgs4msc.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

public class VGSActivity extends Activity{
	
	private MainService mMainService;
	private MyReceiver mReceiver;
	
	public static final String TAG = "fi.oulu.tol.vgs4msc.VGSActivity";
	private static final String SHUTDOWN_SERVICE = "fi.oulu.tol.VGS4MSC.action.SHUTDOWN";
	private static final String START_SERVICE = "fi.oulu.tol.VGS4MSC.action.START";
	private static final String NETWORK_INFO = "fi.oulu.tol.VGS4MSC.action.NETWORKINFO";
	private String mIpAddress = "127.0.0.1";
	private String mPort = "27015";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vgs4activity_layout);
		
		mReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
        filter.addAction(START_SERVICE);
        filter.addAction(SHUTDOWN_SERVICE);
        registerReceiver(mReceiver, filter);
		
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
		
	public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
	        	
        	Log.d("RECEIVED", intent.getAction());
	        	
        	if(intent.getAction().equals(START_SERVICE)) {
        		context.startService(new Intent(context, MainService.class));
        	} else if(intent.getAction().equals(SHUTDOWN_SERVICE)) {
        		context.stopService(new Intent(context, MainService.class));
        	}
        }

    }

}
