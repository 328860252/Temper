/*
 * Copyright (c) Guangzhou Waytronic Electronics 
 * 
 * 		Waytronic specializing in intelligent terminal application research and extension,
 * 	intelligent speech technology research and design. 
 * 		Intelligent business was established in 2011, research and development and 
 *	a variety of intelligent mobile phone listing for individual center products. 
 *	Intelligence division to provide a series of intelligent terminal product solutions, 
 *	with each brand enterprise, realizing the intelligent products. Provide 
 * 	customized software, hardware customization, customer service support and a series 
 *	of cooperation model.
 *
 *		 http://www.wt-smart.com 
 *		 http://www.w1999c.com
 */
package com.medical.manager;

import com.medical.agreement.DeviceStatusBin;
import com.medical.agreement.IStatusListener;
import com.medical.bluetooth.BluetoothConnModel;
import com.medical.help.SetupData;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * <p>Description: </p>
 * @author zhujiang
 * @date 2014-6-16
 */
public class MyAlarmService extends Service {
	
	private static final String TAG = "MyAlarmService";
	
	public static BluetoothConnModel mConnService;
	private BluetoothAdapter btAdapter;
	
	private SetupData mSetupData;

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.v(TAG,"service oncreate");
//		if(receive == null) {
//			Log.v(TAG,"service register broadcast");
//			receive = new AlarmBroadcastReceive();
//			IntentFilter filter = new IntentFilter(AlarmBroadcastReceive.Action_Alarm);
//			filter.addAction(AlarmBroadcastReceive.ACTION_TIME_CHANGED);
//			filter.addAction("android.intent.action.TIME_TICK");
//			this.registerReceiver(receive, filter);
//		}
		if (mConnService == null){
			mConnService = new BluetoothConnModel(this, handler);
			mConnService.startSession();
		}
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(btAdapter != null) {
			if(!btAdapter.isEnabled()) {
				btAdapter.enable();
			}
		}
		//DeviceStatusBin.getInstance().setListener(statusListener);
		super.onCreate();
	}

	private Handler handler = new Handler() {
		
	};
	
//	IStatusListener statusListener = new IStatusListener() {
//		
//		@Override
//		public int onTemper(int temper) {
//			// TODO Auto-generated method stub
//			//mTasksView.setValue(temper);
////			if(alert_temper > 3850) {  //需要跳过的是 3850，
////				if(temper>=3850 && temper<alert_temper) {// 位于3850 和 警报中间
////					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time) {
////						return -1;
////					}
////					lastAlertTime = System.currentTimeMillis();
////					showNextAlertDialog();
////				} else if(temper >=alert_temper){
////					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time  && secondAlert) {
////						return -1;
////					}
////					lastAlertTime = System.currentTimeMillis();
////					secondAlert = true;
////					showNextAlertDialog();
////				}
////			}  else if(alert_temper== 3850) { //两个值相等，直接警报
////				if(temper>=3850) {
////					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time ) {
////						return -1;
////					}
////					lastAlertTime = System.currentTimeMillis();
////					showNextAlertDialog();
////				}
////			} else if(alert_temper<3850) {//警报值小雨3850， 需要跳过的是警报值
////				if(temper >=alert_temper && temper<3850) { //   警报<= <3850
////					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time) {
////						return -1;
////					}
////					lastAlertTime = System.currentTimeMillis();
////					showNextAlertDialog();
////				} else if(temper >=3850) {
////					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time  && secondAlert) {
////						return -1;
////					}
////					lastAlertTime = System.currentTimeMillis();
////					secondAlert = true;
////					showNextAlertDialog();
////				} else if(temper<AppConstants.temper_min_alert) {//低温警报
////					showLowAlertDialog(getString(R.string.temper_low), false);
////				}
////			}
//			return 0;
//		}
//
//		@Override
//		public boolean onDeviceSwitch(boolean isOpen) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public int onAlertTemper(int temper) {
//			// TODO Auto-generated method stub
//			return 0;
//		}
//	};
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.v(TAG,"service.start");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flags = Service.START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.v(TAG,"destroy and  restart");
		Intent service = new Intent(this, MyAlarmService.class);
		startService(service);
		super.onDestroy();
	}

	@Override
	public ComponentName startService(Intent service) {
		// TODO Auto-generated method stub
		System.out.println("222");
		return super.startService(service);
	}

	@Override
	public boolean stopService(Intent name) {
		// TODO Auto-generated method stub
		System.out.println("333");
		return super.stopService(name);
	}
	

}
