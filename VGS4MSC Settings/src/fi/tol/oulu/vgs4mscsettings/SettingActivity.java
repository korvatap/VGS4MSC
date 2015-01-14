package fi.tol.oulu.vgs4mscsettings;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends Activity {
	
    EditText ipAddress;
    EditText port;
    Button startService;
    Button shutdownService;
    Button saveInfo;
    
    public static final String SHUTDOWN_SERVICE = "fi.oulu.tol.VGS4MSC.action.SHUTDOWN";
    public static final String START_SERVICE = "fi.oulu.tol.VGS4MSC.action.START";
    public static final String NETWORK_INFO = "fi.oulu.tol.VGS4MSC.action.NETWORKINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        
        ipAddress = (EditText) findViewById(R.id.ipAddress);
        port = (EditText) findViewById(R.id.port);
        startService = (Button) findViewById(R.id.startServiceBtn);
        shutdownService = (Button) findViewById(R.id.shutdownServiceBtn);
        saveInfo = (Button) findViewById(R.id.SaveBtn);
  
        shutdownService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent();
                i.setAction(SHUTDOWN_SERVICE);
                sendBroadcast(i);
            }
        });
        
        startService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent();
                i.setAction(START_SERVICE);
                sendBroadcast(i);
            }
        });
        
        saveInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Intent i = new Intent();
            	i.setAction(NETWORK_INFO);
            	i.putExtra("IP", ipAddress.getText().toString());
            	i.putExtra("PORT", port.getText().toString());
            	i.setType("text/plain");
            	Log.d("TESTI", i.getAction());
            	sendBroadcast(i);
            }
        });
 
    }
}
