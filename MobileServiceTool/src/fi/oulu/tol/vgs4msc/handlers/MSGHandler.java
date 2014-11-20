package fi.oulu.tol.vgs4msc.handlers;

import java.util.List;

import fi.oulu.tol.vgs4msc.server.MessageReceiver;
import fi.oulu.tol.vgs4msc.server.MessageSender;

public class MSGHandler implements MessageObserver{
	private MessageReceiver mMessageReceiver;
	private MessageSender mMessageSender;
	private List<String> mMsgList;
	
	public void sendMessage(String msg) {
		mMessageSender.sendMessage(msg);
	}

	@Override
	public void newMessages() {
		mMsgList = mMessageReceiver.getMessages();
		
	}
}
