package com.medical.agreement;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

public class BluetoothBin implements ConnectionInterface{
	
	private BluetoothChatManager BTchat;
	
	private BluetoothAdapter adapter;
	
	public BluetoothBin(Handler handler,Context context) {
		BTchat = new BluetoothChatManager(context, handler);
	}

	@Override
	public void find() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connect(String address, String pwd) {
		// TODO Auto-generated method stub
		adapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothDevice device = adapter.getRemoteDevice(address);
		BTchat.connect(device);
	}

	@Override
	public void stopConncet() {
		// TODO Auto-generated method stub
		if(BTchat!=null) {
			BTchat.stop();
		}
	}

	@Override
	public void write(byte[] buffer) {
		// TODO Auto-generated method stub
		if(BTchat != null) {
			BTchat.write(buffer);
		}
	}

	@Override
	public void writeAgreement(byte[] buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLink() {
		// TODO Auto-generated method stub
		return BTchat.islink();
	}

	@Override
	public boolean haveIp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Reconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBleDestory() {
		// TODO Auto-generated method stub
		
	};
	
}
