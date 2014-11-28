package fi.oulu.tol.vgs4msc.handlers;

import java.util.TimerTask;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.EcCalibratorStatus;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCore.RemoteProvisioningState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.SubscriptionState;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration.AndroidCamera;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.camera2.CameraDevice;
import android.util.Log;

public class CallHandler implements LinphoneCoreListener {
	
	private boolean running;
	private LinphoneCore mLc;
	private Context mContext;
	private static final String TAG = "fi.oulu.tol.vgs4msc.handlers.callhandler";
	private String allowedContact;
	private int currentCameraId;
	
	public CallHandler(Context context) {
		mContext = context;
	}
	
	public void setAllowedContact(String c) {
		allowedContact = c;
	}
	
	public String getAllowedContact() {
		return allowedContact;
	}
	
	public boolean videoEnabled() {
		if(mLc != null && mLc.isVideoEnabled()) {
			return true;
		}
		return false;
	}
	
	public void enableVideo() {
		if(mLc != null && !mLc.isVideoEnabled()) {
			mLc.enableVideo(true, true);
		}
	}
	
	public void setVideoCaptureToGlasses() {
		//TODO mLc.setVideoDevice(int id)
		//currentCameraId = mLc.getVideoDevice();
		//AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
		
		//mLc.setVideoDevice(cameras.length);
	}
	
	public void start() {
		try {
			try {
				mLc = LinphoneCoreFactory.instance().createLinphoneCore(this, mContext);
			} catch (LinphoneCoreException e) {
				Log.e(TAG, "no config ready yet: " + e.getStackTrace().toString());
			}
			
			running = true;
			while (running) {
				mLc.iterate();
				try{
					Thread.sleep(50);
				} catch(InterruptedException ie) {
					Log.e(TAG, "Interrupted! Aborting!" + ie.getStackTrace().toString());
					return;
				}
			}
			
		} catch (Exception e) {
			Log.e(TAG, "Cannot start linphone: " + e.getStackTrace().toString());
		}
		
	}
	
	public void stop() {
		running = false;
	}
	
	@Override
	public void callState(LinphoneCore lc, LinphoneCall call, State cstate, String message) {

		if(cstate == State.IncomingReceived && !call.equals(lc.getCurrentCall()) && allowedContact.equals(call.getRemoteContact())) {
			Log.i(TAG, "new state: " + cstate.toString());
			try {
				lc.acceptCall(call);
			} catch (LinphoneCoreException e) {
				Log.e(TAG, "Failed to accept call. " + e.getStackTrace().toString());
			}
		} else if (cstate == State.CallEnd) {
			running = false;
			mLc.terminateCall(call);
			Log.e(TAG, "Call ended");
		}
	}
	
	@Override
	public void authInfoRequested(LinphoneCore lc, String realm, String username, String Domain) {}
			
	@Override
	public void globalState(LinphoneCore lc, GlobalState state, String message) {}

	@Override
	public void callStatsUpdated(LinphoneCore lc, LinphoneCall call, LinphoneCallStats stats) {}
			
	@Override
	public void callEncryptionChanged(LinphoneCore lc, LinphoneCall call, boolean encrypted, String authenticationToken) {}
			
	@Override
	public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, RegistrationState cstate, String smessage) {}

	@Override
	public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf, String url) {}

	@Override
	public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf) {}

	@Override
	public void textReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneAddress from, String message) {}	

	@Override
	public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {}			

	@Override
	public void isComposingReceived(LinphoneCore lc, LinphoneChatRoom cr) {}

	@Override
	public void dtmfReceived(LinphoneCore lc, LinphoneCall call, int dtmf) {}

	@Override
	public void ecCalibrationStatus(LinphoneCore lc, EcCalibratorStatus status, int delay_ms, Object data) {}
		
	@Override
	public void notifyReceived(LinphoneCore lc, LinphoneCall call, LinphoneAddress from, byte[] event) {}

	@Override
	public void transferState(LinphoneCore lc, LinphoneCall call,State new_call_state) {}		

	@Override
	public void infoReceived(LinphoneCore lc, LinphoneCall call, LinphoneInfoMessage info) {}		

	@Override
	public void subscriptionStateChanged(LinphoneCore lc, LinphoneEvent ev, SubscriptionState state) {}		

	@Override
	public void notifyReceived(LinphoneCore lc, LinphoneEvent ev, String eventName, LinphoneContent content) {}

	@Override
	public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev, PublishState state) {}			

	@Override
	public void configuringStatus(LinphoneCore lc, RemoteProvisioningState state, String message) {}
			
	@Override
	public void show(LinphoneCore lc) {}

	@Override
	public void displayStatus(LinphoneCore lc, String message) {}

	@Override
	public void displayMessage(LinphoneCore lc, String message) {	}

	@Override
	public void displayWarning(LinphoneCore lc, String message) {}

}
