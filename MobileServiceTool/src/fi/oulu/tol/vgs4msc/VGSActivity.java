package fi.oulu.tol.vgs4msc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class VGSActivity extends Activity {
	public static final String TAG = "fi.oulu.tol.vgs4msc.VGSActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStart() {
		   super.onStart();
		   
		   Intent intent = new Intent(this, MainService.class);
		   startService(intent);
		   
		  // Intent intent2 = new Intent(this, com.ford.openxc.webcam.WebcamManager.class);
                 //  startService(intent2);
		   Log.d("ACTIVITY", "STARTSERVICE CALLED" + intent.getClass());
		   finish();
	}
	
	@Override
    public void onDestroy() {
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
}
