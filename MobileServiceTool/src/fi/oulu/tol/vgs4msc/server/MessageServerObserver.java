package fi.oulu.tol.vgs4msc.server;

public interface MessageServerObserver {
	
	public void messageReceived();
	public void messageSend();
	public void errorNotification(final String error);
	//public void handshake();
	public void messageToSend(String type, String message);
	public void handshakeReceived();

}
