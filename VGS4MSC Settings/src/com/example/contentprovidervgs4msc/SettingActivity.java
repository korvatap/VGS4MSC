package com.example.contentprovidervgs4msc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends Activity {
	
    EditText ipAddress = (EditText) findViewById(R.id.ipAddress);
    EditText port = (EditText) findViewById(R.id.port);
    Button startService = (Button) findViewById(R.id.startServiceBtn);
    Button shutdownService = (Button) findViewById(R.id.shutdownServiceBtn);
    Button saveInfo = (Button) findViewById(R.id.SaveBtn);
    
    public static final String SHUTDOWN_SERVICE = "fi.oulu.tol.VGS4MSC.action.SHUTDOWN";
    public static final String START_SERVICE = "fi.oulu.tol.VGS4MSC.action.START";
    public static final String NETWORK_INFO = "fi.oulu.tol.VGS4MSC.action.NETWORKINFO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
  
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
            	sendBroadcast(i);
            }
        });
 
    }
}
