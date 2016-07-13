package com.medical.application;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.zby.medical.R;
import com.medical.constants.AppConstants;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

public class MyApplication extends Application{
	
	private static final String TAG = MyApplication.class.getName();

	private PushAgent mPushAgent;
	
	public static final String CALLBACK_RECEIVER_ACTION = "callback_receiver_action";
	
	public static IUmengRegisterCallback mRegisterCallback;
	
	public static IUmengUnregisterCallback mUnregisterCallback;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());  
		
		umengInit();
	}

	
	
	/**
	 * umeng �����������
	 */
	private void umengInit() {
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.setDebugMode(true);
		
		/**
		 * ��Handler����IntentService�б����ã���
		 * 1. ���������Activity�������Intent.FLAG_ACTIVITY_NEW_TASK
		 * 2. IntentService���onHandleIntent�����ǲ����������߳��У���ˣ��������õ����̣߳���������ʾ;
		 * 	      ���߿���ֱ������Service
		 * */
		UmengMessageHandler messageHandler = new UmengMessageHandler(){
			@Override
			public void dealWithCustomMessage(final Context context, final UMessage msg) {
				new Handler(getMainLooper()).post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						UTrack.getInstance(getApplicationContext()).trackMsgClick(msg, false);
						Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
					}
				});
			}
			
			@Override
			public Notification getNotification(Context context,
					UMessage msg) {
				switch (msg.builder_id) {
				case 1:
					NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
					RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
					myNotificationView.setTextViewText(R.id.notification_title, msg.title);
					myNotificationView.setTextViewText(R.id.notification_text, msg.text);
					myNotificationView.setImageViewResource(R.id.notification_large_icon, getSmallIconId(context, msg));
					myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
					builder.setContent(myNotificationView);
					builder.setAutoCancel(true);
					Notification mNotification = builder.build();
					//����Android v4���bug����2.3������ϵͳ��Builder����������Notification����û������RemoteView������Ҫ��Ӵ˴���
					mNotification.contentView = myNotificationView;
					return mNotification;
				default:
					//Ĭ��Ϊ0������д��builder_id�������ڣ�Ҳʹ��Ĭ�ϡ�
					return super.getNotification(context, msg);
				}
			}
		};
		mPushAgent.setMessageHandler(messageHandler);

		/**
		 * ��Handler����BroadcastReceiver�б����ã���
		 * ���������Activity�������Intent.FLAG_ACTIVITY_NEW_TASK
		 * */
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
			@Override
			public void dealWithCustomAction(Context context, UMessage msg) {
				Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
			}

			@Override
			public void launchApp(Context arg0, UMessage arg1) {
				// TODO Auto-generated method stub
				super.launchApp(arg0, arg1);
			}

			@Override
			public void openActivity(Context arg0, UMessage arg1) {
				// TODO Auto-generated method stub
				super.openActivity(arg0, arg1);
			}

			@Override
			public void openUrl(Context arg0, UMessage arg1) {
				// TODO Auto-generated method stub
				super.openUrl(arg0, arg1);
			}
			
			
		};
		mPushAgent.setNotificationClickHandler(notificationClickHandler);
		
		mRegisterCallback = new IUmengRegisterCallback() {
			
			@Override
			public void onRegistered(String registrationId) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
				sendBroadcast(intent);
			}

		};
		mPushAgent.setRegisterCallback(mRegisterCallback);
		
		mUnregisterCallback = new IUmengUnregisterCallback() {
			
			@Override
			public void onUnregistered(String registrationId) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CALLBACK_RECEIVER_ACTION);
				sendBroadcast(intent);
			}
		};
		mPushAgent.setUnregisterCallback(mUnregisterCallback);
	}
	
	
}
