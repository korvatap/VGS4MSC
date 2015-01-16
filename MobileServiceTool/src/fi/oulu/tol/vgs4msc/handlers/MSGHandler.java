package fi.oulu.tol.vgs4msc.handlers;

import java.util.Vector;

import android.content.Context;
import android.util.Log;

import fi.oulu.tol.vgs4msc.server.MessageReceiver;
import fi.oulu.tol.vgs4msc.server.MessageSender;
import fi.oulu.tol.vgs4msc.server.MessageServerObserver;

public class MSGHandler implements MessageServerObserver {
	private MessageReceiver mMessageReceiver;
	private MessageSender mMessageSender;
	private Vector<String> mMsgList;
	private Context mContext;
	
	public MSGHandler(Context context) {
		mContext = context;
	}
	
	public void initialize() {
		mMessageReceiver = new MessageReceiver();
		mMessageSender = new MessageSender();
		
		mMessageReceiver.initialize(mContext, this);
		mMessageSender.initialize(mContext, this);
	}
	
	public void startServer() {
		mMessageReceiver.start();
	}
	
	public void closeServer() {
		mMessageReceiver.kill();
	}
	
	public void setReceiver(MessageReceiver mr) {
		mMessageReceiver = mr;
	}
	
	public void setSender(MessageSender ms) {
		mMessageSender = ms;
	}
	
	public void sendMessage(String msg, String type) {
		mMessageSender.sendMessage(msg, type);
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
		String tmpTokens[];
		int tmpType;
		while(hasMessages()) {
			tmp = getLastMessage();
			if (tmp != null) {
				tmpTokens = tmp.split(",");
				tmpType = Character.getNumericValue(tmpTokens[1].charAt(0));
				
				switch(tmpType) {
					case 1:
						break;
					case 2:
						break;
					case 3:
						break;
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
		mMessageSender.sendMessage(type, message);
	}

	@Override
	public void messageSend() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void errorNotification(String error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handshake() {
		mMessageSender.handshake();
	}

	@Override
	public void handshakeReceived() {
		String msg = mMessageReceiver.getHandshakeMessage();
		String tokens[] = msg.split(",");
		
		mMessageReceiver.setServerUUID(tokens[0]);
		mMessageSender.setServerUUID(tokens[0]);
		
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
		mMessageReceiver = new MessageReceiver();
		mMessageReceiver.initialize(mContext, this);
		mMessageReceiver.setPort(port);
		startServer();
	}
}
