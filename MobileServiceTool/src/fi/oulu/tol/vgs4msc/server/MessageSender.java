package fi.oulu.tol.vgs4msc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.UUID;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class MessageSender {

		private String serverAddress = null;
		private String serverPort = null;
		private UUID serverUUID = null;
		
		private MessageServerObserver proxyObserver= null;
		private Context mContext = null;
		private static final String TAG = "MessageServerProxy";
		
		public MessageSender(Context c, MessageServerObserver obs) {
			mContext = c;
			proxyObserver = obs;
		}
		
	    public void setServerAddress(String serverAddress) {
	    	this.serverAddress = serverAddress;
	    }
	    
	    public String getServerAddress() {
	    	return serverAddress;
	    }
	    
		public void setServerPort(String serverPort) {
			this.serverPort = serverPort;
		}

		public void sendMessage(String message, String type) {
			if(checkNetwork()) {
				new UploadMessageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, type, message);
			}
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
		
		private class UploadMessageTask extends AsyncTask<String, Void, Void> {
			@Override
			protected Void doInBackground(String ... text) {
				String msg;
				// message muotoa UUID,TYPE,MESSAGE
				msg = serverUUID.toString() + "," + text[0].toString() + "," + text[1].toString();

				InetAddress target = null;
				DatagramSocket socket = null;
				byte[] data = null;
				DatagramPacket packet = null;
				
				try {
					target = InetAddress.getByName(serverAddress);
					
				} catch(UnknownHostException e) {
					Log.d(TAG, "Unknown Host exception" + e.getStackTrace().toString());
				}
				
				try {
					socket = new DatagramSocket();
                } catch (SocketException e) {
                	Log.d(TAG, "Socket Exception" + e.getStackTrace().toString());
                }
				
				data = msg.getBytes();
				packet = new DatagramPacket(data, data.length, target, Integer.parseInt(serverPort));
				
				try {
					socket.send(packet);
					proxyObserver.messageSend();
				} catch (IOException e) {
					Log.d(TAG, "Message send failed" + e.getStackTrace().toString());
				}
				
				socket.close();
				return null;
			}
		}

		public void handshake() {
			sendMessage("0", "hs");
		}

		public String getServerPort() {
			return serverPort;
		}

		public void setServerUUID(String uuid) {
			serverUUID = UUID.fromString(uuid);
		}
		
}
	
