package fi.oulu.tol.vgs4msc.handlers;

import java.util.UUID;
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
	private String sipAddress = "";
	
	public MSGHandler(Context context, ConnectionObserver obs) {
		mContext = context;
		mMessageSender = new MessageSender(mContext, this);
		cObserver = obs;
		setNetwork("kotikolo.linkpc.net", "8080");
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
                        message.put("userid", mMessageSender.getUserID());
                        message.put("latitude", latitude);
                        message.put("longitude", longitude);
                        message.put("heading", degrees);
                        
                       // JSONArray jarray = new JSONArray();
                       // JSONObject ob = new JSONObject();
                        
                       // ob.put("longitude", longitude);
                       // ob.put("latitude", latitude);
                      //  ob.put("degrees", degrees);
                       // 
                      //  jarray.put(ob);
                        
                        //message.put("location", jarray);
                                           
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
		int [] values = new int[14];
		
		while(hasMessages()) {
			tmp = getLastMessage();
			if (tmp != null) {
			        
			        try {
			                Log.d(TAG, tmp);
                                        JSONObject jsonObject = new JSONObject(tmp);
                                        if(jsonObject.has("heading")) {
                                                if(!mLedService.started()) {
                                                        mLedService.resume();
                                                }
                                                // PARSE VALUE FROM MESSAGE
                                                switch(jsonObject.getString("heading")) {
                                                
                                                        case "left":
                                                                values[13] = 2;
                                                        break;
                                                        
                                                        case "right":
                                                                values[6] = 2;
                                                        break;
                                                        
                                                        case "up":
                                                                values[3] = 2;
                                                                values[7] = 2;
                                                        break;
                                                        
                                                        case "down":
                                                                values[1] = 2;
                                                                values[12] = 2;
                                                        break;
                                                        
                                                        case "left_down":
                                                                values[13] = 2;
                                                                values[11] = 2;
                                                                values[10] = 2;
                                                        break;
                                                        
                                                        case "right_down":
                                                                values[6] = 2;
                                                                values[0] = 2;
                                                                values[2] = 2;
                                                        break;
                                                        
                                                        case "left_up":
                                                                values[8] = 2;
                                                                values[9] = 2;
                                                                values[13] = 2;
                                                        break;
                                                        
                                                        case "right_up":
                                                                values[4] = 2;
                                                                values[5] = 2;
                                                                values[6] = 2;
                                                        break;
                                                        
                                                        default:
                                                        break;
                                                        
                                                }
                                                mLedService.sendDirections(values, 15, 5, 1);

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
		
		mMessageReceiver.setUserID(tokens[0]);
		mMessageSender.setUserID(tokens[0]);
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
			
			
			mMessageReceiver.setUserID("1");
	                mMessageSender.setUserID("1");
	                
	                JSONObject message = new JSONObject();
                        try {
                                message.put("userid", mMessageSender.getUserID());
                                message.put("SIP: ", sipAddress);
                        } catch (JSONException e) {
                                // TODO Auto-generated catch block
                               Log.d(TAG, "JSON ERROR" + e.toString());
                        }
                                
                        mMessageSender.sendMessage(message.toString());
	                
	                cObserver.handshakeReceived();
			
		}

	}

        public void setLinphonAddress(String sipAddress) {
                this.sipAddress = sipAddress;
                
        }
}
