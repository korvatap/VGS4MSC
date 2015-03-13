package fi.oulu.tol.vgs4msc.handlers;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import uni.oulu.firstprotocol.FirstprotocolMainActivity;
import android.content.Context;
import android.util.Log;
import fi.oulu.tol.vgs4msc.ConnectionObserver;
import fi.oulu.tol.vgs4msc.server.MessageServer;
import fi.oulu.tol.vgs4msc.server.MessageServerObserver;

public class MSGHandler implements MessageServerObserver {
	private MessageServer mMessageServer;
	private Vector<String> mMsgList = new Vector<String>();
	private Context mContext;
	private FirstprotocolMainActivity mLedService;
	private ConnectionObserver cObserver;
	public static final String TAG = "fi.oulu.tol.vgs4msc.MSGHandler";
	private String sipAddress = "";
	
	public MSGHandler(Context context, ConnectionObserver obs) {
		mContext = context;
		cObserver = obs;
		mMessageServer = new MessageServer(mContext, this);
		handShake();
		mMessageServer.start();
		//setNetwork("kotikolo.linkpc.net", "8080");
		
		//mMessageSender = new MessageSender(mContext, this);
		
		mLedService = new FirstprotocolMainActivity(mContext);
		mLedService.start();
	}
	
	public void startServer() {
		//mMessageReceiver.start();
	        mMessageServer.start();
	}
	
	public void closeServer() {
		//mMessageReceiver.kill();
		mMessageServer.kill();
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
	
	public void sendMessage(String longitude, String latitude, String degrees) {
	          
	        try {
                        JSONObject message = new JSONObject();
                        message.put("userid", mMessageServer.getUserID());
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
                                           
                        mMessageServer.sendMessage(message.toString());
                } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        Log.d(TAG, e.toString());
                }
	}

	@Override
	public void messageReceived() {
	        Log.d(TAG, "MessageReceived");
		while(mMessageServer.hasMessages()) {
		        String tmp = mMessageServer.getLastMessage();
		        if(tmp!=null)
		                mMsgList.add(tmp);
		}
		Log.d(TAG, "Processing messages");
		processMessages();
	}
	
	
	private void processMessages() {
		String tmp;
		int [] values = new int[14];
		
		Log.d(TAG, "Processing messages");
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
                                                
                                                        case "stop":
                                                                Log.d(TAG, "Setting values for led machine");
                                                                for(int i =0; i < values.length; i++) {
                                                                        values[i] = 1;
                                                                }
                                                        break;
                                                        
                                                        case "right":
                                                                values[13] = 2;
                                                        break;
                                                        
                                                        case "left":
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
                                                        
                                                        case "right_down":
                                                                values[13] = 2;
                                                                values[11] = 2;
                                                                values[10] = 2;
                                                        break;
                                                        
                                                        case "left_down":
                                                                values[6] = 2;
                                                                values[0] = 2;
                                                                values[2] = 2;
                                                        break;
                                                        
                                                        case "right_up":
                                                                values[8] = 2;
                                                                values[9] = 2;
                                                                values[13] = 2;
                                                        break;
                                                        
                                                        case "left_up":
                                                                values[4] = 2;
                                                                values[5] = 2;
                                                                values[6] = 2;
                                                        break;
                                                        
                                                        default:
                                                        break;
                                                        
                                                }
                                                Log.d(TAG, "sending values for led machine");
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
	        mMessageServer.sendMessage(message);
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
		String msg = mMessageServer.getHandshakeMessage();
		String tokens[] = msg.split(",");
		
		mMessageServer.setUserID(tokens[0]);
		//mMessageSender.setUserID(tokens[0]);
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
                
                if(mContext != null) {
                        mMessageServer = new MessageServer(mContext, this);
                        mMessageServer.setAddress(mIpAddress);
                        mMessageServer.setPort(mPort);
                        startServer();
                        
                        
                        mMessageServer.setUserID("1");
                       // mMessageSender.setUserID("1");
                        
                        JSONObject message = new JSONObject();
                        try {
                               // message.put("userid", mMessageSender.getUserI());
                                message.put("userid", mMessageServer.getUserID());
                                message.put("SIP: ", sipAddress);
                        } catch (JSONException e) {
                                // TODO Auto-generated catch block
                               Log.d(TAG, "JSON ERROR" + e.toString());
                        }
                                
                        //mMessageSender.sendMessage(message.toString());
                        mMessageServer.sendMessage(message.toString()); 
                        
                        cObserver.handshakeReceived(); 
                }
	}
	
	       public void handShake() {
	              
	                if(mContext != null) {
	                        mMessageServer.setUserID("1");
	                       // mMessageSender.setUserID("1");
	                        
	                        JSONObject message = new JSONObject();
	                        try {
	                               // message.put("userid", mMessageSender.getUserID());
	                                message.put("userid", mMessageServer.getUserID());
	                                message.put("SIP: ", sipAddress);
	                        } catch (JSONException e) {
	                                // TODO Auto-generated catch block
	                               Log.d(TAG, "JSON ERROR" + e.toString());
	                        }
	                                
	                        //mMessageSender.sendMessage(message.toString());
	                        mMessageServer.sendMessage(message.toString()); 
	                        
	                        cObserver.handshakeReceived(); 
	                }
	        }

        public void setLinphonAddress(String sipAddress) {
                this.sipAddress = sipAddress;
                
        }
}
