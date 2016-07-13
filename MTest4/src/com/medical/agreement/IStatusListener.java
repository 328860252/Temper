package com.medical.agreement;

public interface IStatusListener {
	
	public boolean onDeviceSwitch(boolean isOpen);
	
	public int onTemper(int temper);
	
	public int onAlertTemper(int temper);

}
