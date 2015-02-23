package fi.oulu.tol.vgs4msc.handlers;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uni.oulu.firstprotocol.FirstprotocolMainActivity;
import android.content.Context;
import android.util.Log;
import fi.oulu.tol.vgs4msc.ConnectionObserver;
import fi.oulu.tol.vgs4msc.server.MessageReceiver;
import fi.oulu.tol.vgs4msc.server.MessageSender;
import fi.oulu.tol.vgs4msc.server.MessageServerObserver;

public class MSGHandler implements MessageServerObserver {
	private MessageReceiver mMessageReceiver;
	private MessageSender mMessageSender;
	private Vector<String> mMsgList;
	private Context mContext;
	private FirstprotocolMainActivity mLedService;
	private ConnectionObserver cObserver;
	public static final String TAG = "fi.oulu.tol.vgs4msc.MSGHandler";
	
	public MSGHandler(Context context) {
		mContext = context;
		mMessageReceiver = new MessageReceiver(mContext, this);
		mMessageSender = new MessageSender(mContext, this);
		mLedService = new FirstprotocolMainActivity(mContext);
		mLedService.start();
	}
	
	public void startServer() {
		mMessageReceiver.start();
	}
	
	public void closeServer() {
		mMessageReceiver.kill();
	}
	
	public void closeLedService() {
	        mLedService.stop();
	}
	
	public void setConnectionObserver(ConnectionObserver obs) {
	        cObserver = obs;
        }
        
        public ConnectionObserver getConnectionObserver(ConnectionObserver obs) {
                return cObserver;
        }
	
	public void setReceiver(MessageReceiver mr) {
		mMessageReceiver = mr;
	}
	
	public void setSender(MessageSender ms) {
		mMessageSender = ms;
	}
	
	public void sendMessage(String longitude, String latitude, String degrees) {
	          
	        try {
                        JSONObject message = new JSONObject();
                        message.put("sender", mMessageSender.getUUID());
                        
                        JSONArray jarray = new JSONArray();
                        JSONObject ob = new JSONObject();
                        
                        ob.put("longitude", longitude);
                        ob.put("latitude", latitude);
                        ob.put("degrees", degrees);
                        
                        jarray.put(ob);
                        
                        message.put("location", jarray);
                                           
                        mMessageSender.sendMessage(message.toString());
                } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Log.d(TAG, e.toString());
                }
	}

	@Override
	public void messageReceived() {
		while(mMessageReceiver.hasMessages()) {
			mMsgList.add(mMessageReceiver.getLastMessage());
		}
		processMessages();
	}
	
	
	private void processMessages() {
		String tmp;
		while(hasMessages()) {
			tmp = getLastMessage();
			if (tmp != null) {
			        
			        try {
                                        JSONObject jsonObject = new JSONObject(tmp);
                                        if(jsonObject.has("heading")) {
                                                if(!mLedService.started()) {
                                                        mLedService.resume();
                                                }
                                                // PARSE VALUE FROM MESSAGE
                                                switch(jsonObject.getString("heading")) {
                                                
                                                        case "left":
                                                                //CREATE lValues
                                                        break;
                                                        
                                                        case "right":
                                                              //CREATE lValues
                                                        break;
                                                        
                                                }
                                                //mLedService.sendDirections(lValues, bright, blkTime, freq);

                                        }
                                        
                                        
                                } catch (JSONException e) {
                                        // TODO Auto-generated catch block
                                        Log.d(TAG, e.toString());
                                }
			        
			}
		}
		
	}
	
	public String getLastMessage() {
		String tmp = null;
		if (!mMsgList.isEmpty()) {
			tmp = mMsgList.get(0);
		}
		if (null != tmp) {
			mMsgList.remove(0);
			return tmp;
		} else {
			return null;
		}
	}

	public void messageToSend(String type, String message) {
		mMessageSender.sendMessage(message);
	}

	@Override
	public void messageSend() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void errorNotification(String error) {
		// TODO Auto-generated method stub
		
	}

	//@Override
	//public void handshake() {
	//	mMessageSender.handshake();
	//}

	@Override
	public void handshakeReceived() {
		String msg = mMessageReceiver.getHandshakeMessage();
		String tokens[] = msg.split(",");
		
		mMessageReceiver.setServerUUID(tokens[0]);
		mMessageSender.setServerUUID(tokens[0]);
		cObserver.handshakeReceived();
		
	}
	
	public boolean hasMessages() {
		if(!mMsgList.isEmpty()) {
			return true;
		}
		return false;
	}

	public void setNetwork(String mIpAddress, String mPort) {
		Log.d("TESTING", mIpAddress + mPort);
		mMessageSender.setServerAddress(mIpAddress);
		mMessageSender.setServerPort(mPort);
		
		newPort(mPort);
	}
	
	public void newPort(String port) {
		if(mContext != null) {
			mMessageReceiver = new MessageReceiver(mContext, this);
			mMessageReceiver.setPort(port);
			startServer();
		}

	}
}
