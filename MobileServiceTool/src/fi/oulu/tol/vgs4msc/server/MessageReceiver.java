package fi.oulu.tol.vgs4msc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MessageReceiver extends Thread {

	private static final int MAX_UDP_DATAGRAM_LEN = 65535;
	private static final String TAG = "fi.oulu.tol.vgs4msc.messagereceiver";
	private boolean bKeepRunning = true;
	private List <String> mMsgList;
	private String serverPort = "27015";
	private MessageServerObserver proxyObserver= null;
	private Context mContext;
	private UUID serverUUID = null;
	private String hsMessage;
	
	public void initialize(Context c, MessageServerObserver obs) {
		mContext = c;
		proxyObserver = obs;
		mMsgList = new Vector<String>();
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
	
	public String getHandshakeMessage() {
		return hsMessage;
	}
	
	public boolean hasMessages() {
		if(!mMsgList.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public void messageReceived() {
		proxyObserver.messageReceived();
	}
	
	public void handshakeReceived() {
		proxyObserver.handshakeReceived();
	}
	
	private boolean checkNetwork() {
        ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        Log.d(TAG, "No network, cannot initiate retrieval!");
        return false;
    }
	
	public void run () {
        String message;
        byte[] lmessage = new byte[MAX_UDP_DATAGRAM_LEN];
        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
        DatagramSocket socket = null;
        
        if(checkNetwork()) {
        	try {
    			socket = new DatagramSocket(Integer.parseInt(serverPort));

                while(bKeepRunning) {
                    socket.receive(packet);
                    message = new String(lmessage, 0, packet.getLength());
                    if(serverUUID == null) {
                    	if(isHandshake(message)) {
                    		hsMessage = message;
                    		proxyObserver.handshakeReceived();
                    	}
                    } else {
                    	if(checkUUID(message)) {
                            mMsgList.add(message);
                            proxyObserver.messageReceived();
                    	}
                    }
                }
            } catch (SocketException  e) {
                Log.d(TAG, "UDP SocketException error",e);
            }
        	
        	catch (IOException d) {
        		Log.d(TAG, "UDP IOException error" + d.getStackTrace().toString());
        	}

            if (socket != null) {
                socket.close();
            }
        }

	}
	
	private boolean checkUUID(String message) {
		String tokens[] = message.split(",");
		
		if(tokens[0].equals(serverUUID.toString())) {
			return true;
		}
		return false;
	}

	private boolean isHandshake(String message) {
		String tokens[] = message.split(",");
		if(Character.getNumericValue(tokens[1].charAt(0)) == 0) {
			return true;
		}
		return false;
	}

	public void kill() { 
        bKeepRunning = false;
    }
	
	public void setPort(String port) {
		serverPort = port;
	}
	
	public String getPort() {
		return serverPort;
	}
	
	public void setServerUUID(String uuid) {
		serverUUID = UUID.fromString(uuid);
	}

}
