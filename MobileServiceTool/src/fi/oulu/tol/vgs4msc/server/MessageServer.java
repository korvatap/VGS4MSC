package fi.oulu.tol.vgs4msc.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class MessageServer {

        private static final int MAX_UDP_DATAGRAM_LEN = 1500;
        private static final String TAG = "fi.oulu.tol.vgs4msc.messageserver";
        private boolean bKeepRunning = true;
        private List <String> mMsgList = new ArrayList<String>();
        private String serverPort = "8080";
        private MessageServerObserver proxyObserver= null;
        private Context mContext;
        private String userid = null;
        private String hsMessage;
        private InetAddress senderAddress = null;
        private static DatagramSocket dSocket;
        private Handler timerHandler = new Handler();
        private Timer pollingTimer = new Timer();
        
        private String ipAddress = "kotikolo.linkpc.net";
        
        public MessageServer(Context c, MessageServerObserver obs) {
                mContext = c;
                proxyObserver = obs;
                mMsgList = new Vector<String>();
                try {
                        dSocket = new DatagramSocket(Integer.parseInt(serverPort));
                        dSocket.setReuseAddress(true);
                } catch (NumberFormatException e) {
                        Log.d(TAG, e.toString());
                        e.printStackTrace();
                } catch (SocketException e) {
                        Log.d(TAG, e.toString());
                }
        }
        
        
        private void doPollingDownload() {
                timerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                                pollingTimer.schedule(new PollingTask(), 500);
                        }
                });
        }
        
        public void start() {
              if(serverPort != null && ipAddress != null) {
                      Log.d(TAG, "Starting download task");
                      new DownloadVotingsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
              }
        }
        
        private class DownloadVotingsTask extends AsyncTask<String, Void, String> {
                
                public DownloadVotingsTask() {
                       
                }
                
                private boolean checkUserID(String message) {
                        try {
                                JSONObject jsonObject = new JSONObject(message);
                                if(jsonObject.has("userid")) {
                                        
                                        if(jsonObject.getString("userid").equalsIgnoreCase(userid)) {
                                                return true;
                                        }
                                } 
                        } catch (JSONException e) {
                                Log.d(TAG, e.toString());
                        }
                        
                        return false;
                }

                private boolean isHandshake(String message) {
                        String tokens[] = message.split(",");
                        if(tokens.length >1) {
                                if(Character.getNumericValue(tokens[1].charAt(0)) == 0) {
                                        return true;
                                } 
                        }
                       
                        return false;
                }

                
                @Override
                protected String doInBackground(String... text) {
                        String message;
                        byte[] lmessage = new byte[MAX_UDP_DATAGRAM_LEN];
                        DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
                        Log.i(TAG, "Starting msg receiver");
                        
                        try {
                                Log.i(TAG, "Msg receiver receiving packagess" + dSocket.getLocalPort() + dSocket.getPort());
                                dSocket.receive(packet);
                                Log.i(TAG, "Msg receiver receiving asd");
                                message = new String(lmessage, 0, packet.getLength());
                                Log.d(TAG, message);
                                if(userid == null) {
                                        if(isHandshake(message)) {
                                                hsMessage = message;
                                                proxyObserver.handshakeReceived();
                                                senderAddress = packet.getAddress();
                                        }
                                    } else {
                                        if(checkUserID(message)) {
                                            Log.d(TAG, "Adding message to list and reporting it");
                                            mMsgList.add(message);
                                            proxyObserver.messageReceived();
                                        }
                                    }
                                
                    } catch (SocketException  e) {
                        Log.d(TAG, "UDP SocketException error",e);
                    }
                        
                        catch (IOException d) {
                                Log.d(TAG, "UDP IOException error" + d.getStackTrace().toString());
                        }
                        return null;
                }
                
                @Override
                protected void onPostExecute(String result) {
                        if(!bKeepRunning) {
                             dSocket.close();   
                        } else {
                                doPollingDownload();
                        }
                }
                        
        }
        
        private class PollingTask extends TimerTask {
                @Override
                public void run() {
                        start();
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

        public void sendMessage(String message){
                new UploadMessageTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message);
        }
        
        private class UploadMessageTask extends AsyncTask<String, Void, Void> {
                @Override
                protected Void doInBackground(String ... text) {
                        
                        if(checkNetwork() && ipAddress != null) {
                                byte[] sendData = text[0].toString().getBytes();
                                try {
                                        senderAddress = InetAddress.getByName(ipAddress);
                                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, senderAddress, Integer.parseInt(serverPort));
                                        dSocket.send(sendPacket);
                                        Thread.sleep(100);
                                } catch (UnknownHostException e) {
                                        Log.d(TAG, e.toString());
                                } catch (InterruptedException e) {
                                        Log.d(TAG, e.toString());
                                } catch (IOException e) {
                                        Log.d(TAG, e.toString());
                                }        
                        }
                        return null;
                       
                }
        }

        
        public String getHandshakeMessage() {
                return hsMessage;
        }
        
        public String getSenderAddress() {
                return senderAddress.getHostAddress();
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
        
        public void kill() { 
                bKeepRunning = false;
        }
        
        public void setPort(String port) {
                serverPort = port;
        }
        
        public void setAddress(String ip) {
                ipAddress = ip;
        }
        
        public String getPort() {
                return serverPort;
        }
        
        public void setUserID(String uid) {
                userid = uid;
        }
        
        
        public String getUserID() {
                return userid;
        }


}
