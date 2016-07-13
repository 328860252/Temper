package com.medical.agreement;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Handler;

public class BleBin implements ConnectionInterface{
	
	private Activity mActivity;
	private Handler mHandler;
	private BluetoothLeService mService;
	private BluetoothAdapter mAdapter;
	
	private boolean isLink;
	
	public BleBin (Activity activity, Handler mHandler, BluetoothLeService service) {
		this.mActivity = activity;
		this.mHandler = mHandler;
		this.mService = service;
	}

	@Override
	public void find() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connect(String address, String pwd) {
		// TODO Auto-generated method stub
		isLink = mService.connect(address);
	}

	@Override
	public void stopConncet() {
		// TODO Auto-generated method stub
		if(mService!=null) {
			mService.disconnect();
			mService.close();
		}
	}

	@Override
	public void write(byte[] buffer) {
		// TODO Auto-generated method stub
		mService.writeLlsAlertLevel(buffer);
	}

	@Override
	public void writeAgreement(byte[] buffer) {
		// TODO Auto-generated method stub
		if(buffer!=null) {
			//mService.writeLlsAlertLevel(Encrypt.ProcessCommand(buffer, buffer.length));
			mService.writeLlsAlertLevel(buffer);
		}
	}

	@Override
	public boolean isLink() {
		// TODO Auto-generated method stub
		return mService.isConnected();
	}

	@Override
	public void Reconnect() {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void setDataParse(DataProtocolInterface dataParse) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void onBleDestory() {
		// TODO Auto-generated method stub
		mService.close();
	}

	@Override
	public boolean haveIp() {
		// TODO Auto-generated method stub
		return false;
	}

}
