package fi.oulu.tol.vgs4msc;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class CompassSensor implements SensorEventListener {
	
	private float mDegrees;
	private AreaObserver mObserver;

	@Override
	public void onSensorChanged(SensorEvent event) {
		mDegrees = event.values[0];
		mObserver.newDegree();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//NOT USED
	}
	
	public void setObserver(AreaObserver obs) {
		mObserver = obs;
	}
	
	public AreaObserver getObserver(AreaObserver obs) {
		return mObserver;
	}
	
	public float getDegrees() {
		return mDegrees;
	}

}
