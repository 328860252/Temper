package com.medical.agreement;

import android.content.IntentSender.SendIntentException;


public class DeviceControlBin {
	
	
	private ConnectionInterface mConnectionInterface;
	
	private DeviceControlBin(){};
	
	private static DeviceControlBin instance;
	
	public static DeviceControlBin getInstance() {
		if(instance == null) {
			instance = new DeviceControlBin();
		}
		return instance;
	}
	

	public ConnectionInterface getmConnectionInterface() {
		return mConnectionInterface;
	}

	public void setmConnectionInterface(ConnectionInterface mConnectionInterface) {
		this.mConnectionInterface = mConnectionInterface;
	}
	
	private boolean checkLink() {
		if(mConnectionInterface!=null && mConnectionInterface.isLink()) return true;
		return false;
	}
	
	public void sendSwitchClose() {
		if(checkLink()) {
			mConnectionInterface.write(new byte[] {0x31,0x32,0x33,0x3,0x35,0x36});
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			mConnectionInterface.write(new byte[] {0x31,0x32,0x33,0x3,0x35,0x36});
			
		}
	}
	
	public void sendAlertTemper(int value)  {
		if(checkLink()) {
			byte t1 = (byte) 0xFF;
			byte t2 = (byte) 0xFF;
			if(value>3750) {
				t2 = (byte) (value-3750);
			} else if(value <3750) {
				t1 = (byte) (3750 - value);
			} else {
				t2 = (byte) 0x00;
			}
			mConnectionInterface.write(new byte[] {(byte) 0x91, t1,t1, (byte) 0x99,t2,t2});
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
			}
			mConnectionInterface.write(new byte[] {(byte) 0x91, t1,t1, (byte) 0x99,t2,t2});
		}
	}
	
}
