package com.medical.agreement;

public class DeviceStatusBin {
	
	private int temper;
	
	private boolean deviceSwitch;
	
	private int alertTemper;
	
	private IStatusListener listener;
	
	private static DeviceStatusBin sbin;
	
	private DeviceStatusBin(){};
	
	public static DeviceStatusBin getInstance() {
		if(sbin ==null) {
			sbin = new DeviceStatusBin();
		}
		return sbin;
	};
	

	public int getTemper() {
		return temper;
	}

	public void setTemper(int temper) {
		this.temper = temper;
		if(listener!=null) {
			listener.onTemper(temper);
		}
	}

	public boolean isDeviceSwitch() {
		return deviceSwitch;
	}

	public void setDeviceSwitch(boolean deviceSwitch) {
		this.deviceSwitch = deviceSwitch;
		if(listener!=null) {
			listener.onDeviceSwitch(deviceSwitch);
		}
	}

	public int getAlertTemper() {
		return alertTemper;
	}

	public void setAlertTemper(int alertTemper) {
		this.alertTemper = alertTemper;
		if(listener!=null) {
			listener.onAlertTemper(alertTemper);
		}
	}

	public IStatusListener getListener() {
		return listener;
	}

	public void setListener(IStatusListener listener) {
		this.listener = listener;
	}
	
	

}
