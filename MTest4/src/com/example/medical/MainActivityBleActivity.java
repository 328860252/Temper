package com.example.medical;

import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnKeyListener;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.medical.agreement.BleBin;
import com.medical.agreement.BluetoothLeService;
import com.medical.agreement.ConnectionInterface;
import com.medical.agreement.DeviceControlBin;
import com.medical.agreement.DeviceStatusBin;
import com.medical.agreement.IStatusListener;
import com.medical.application.MyApplication;
import com.medical.bluetooth.BluetoothConnModel;
import com.medical.constants.AppConstants;
import com.medical.constants.AppString;
import com.medical.help.AlertConfirmBin;
import com.medical.help.ClsUtils;
import com.medical.help.MyByte;
import com.medical.help.MyDecimalHelp;
import com.medical.help.Myhex;
import com.medical.help.RingHelp;
import com.medical.help.SetupData;
import com.medical.help.SocketHelper;
import com.medical.manager.NetworkManager;
import com.medical.sql.HistoryBin;
import com.medical.sql.HistorySQLService2;
import com.medical.view.TasksCompletedView;
import com.medical.view.ToastNew;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;
import com.zby.medical.R;

public class MainActivityBleActivity extends Activity {
	
	//public static ConnectionInterface mConnectInterface;
	private static final int activity_bluetooth = 10;
	private static final int activity_OpenBlueTooth = 11;
	
	private BluetoothAdapter btAdapter;

	   public static final String INCOMING_MSG = "INCOMING_MSG";
	    public static final String OUTGOING_MSG = "OUTGOING_MSG";
	    public static final String ALERT_MSG = "ALERT_MSG";
	    public static final String KEY_ECHO_PREF = "KEY_ECHO_PREF";
	    public static final String DEVICE_ADDRESS = "device_address";
	    public static final String DISCONNECT_DEVICE_ADDRESS = "disconnected_device_address";
	   
	private TasksCompletedView mTasksView;
	private CheckBox cb_switch ;
	private TextView tv_monitor, tv_ele;
	private int monitorStatus;
	private ImageView  iv_ele;
	private ImageView iv_bluetooth;
	private TextView tv_connect, tv_title, tv_setting, tv_temper, tv_history;
	
	private int voltage = 0;
	private int alarmStatus;
	
	private HistorySQLService2 hsqlService;
	private SetupData mSetupData;
	private NotificationManager notificationManager;
	private NetworkManager mNetworkManager;
	
	private int alert_temper;
	
	private AlertConfirmBin alertConfrimBin;
	private AlertConfirmBin alertCloseBin;
	
	/**
	 * �Ƿ��Ѿ��˳����
	 */
	private boolean isDestroy;
	
	
	private boolean isFirst;
	
	long lastTime;//��¼�ϴ� ���浽��ʷ��¼�е�����
	public static String mac="AABBCCDDEEFF";
	
	
	
	 private PowerManager.WakeLock wakeLock = null; 
	
	
	//public static BluetoothConnModel mConnService;
	public static ConnectionInterface mConnService;
	private BluetoothLeService mBluetoothLeService;
	
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE =3;
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";
    
    public static final int handler_Link=101;
    public static final int handler_LinkSuccess = 102;
		 public static final int handler_LinkFailure = 103;
		 public static final int handler_linkLost =104;
		 private final int handler_voltage = 105;
		 private final int handler_toast = 855;
		 private final int handler_switch_on = 106;
		 private final int handler_switch_off = 107;
		 private final int handler_switch_web = 108;//�ƶ�����
		 private final static int handler_login_unuse = 110;
		 
		 Thread linkThraed;
		 Thread adsThread;
		 private Thread pushWarnThread;
		 
		 private PushAgent mPushAgent;
		 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		mSetupData = SetupData.getSetupData(this);
		String phoneMac = mSetupData.read("phone_mac", "");
		if(phoneMac.equals("")) {
			phoneMac = SocketHelper.getLocalMacAddress(this);
		}
		AppConstants.phone_mac = phoneMac;
		
		initViews();
		mSetupData = SetupData.getSetupData(this);
		initData();
		hsqlService = new HistorySQLService2(this);
		
		setAbout();
		
		
		
		//�Ӵ������Ҫ�£���ֹ��������
		acquireWakeLock(this);
		
		mNetworkManager = new NetworkManager(this);
		registerReceiver(mReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		startDownloadThread();
		startCheckVersionThread();
		
		mPushAgent = PushAgent.getInstance(this);
		mPushAgent.onAppStart();
		mPushAgent.enable(MyApplication.mRegisterCallback);
		String device_token = UmengRegistrar.getRegistrationId(this);
		System.out.println("umeng" + device_token);
		
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
					//����Android v4����bug����2.3������ϵͳ��Builder����������Notification����û������RemoteView������Ҫ��Ӵ˴���
					mNotification.contentView = myNotificationView;
					return mNotification;
				default:
					//Ĭ��Ϊ0������д��builder_id�������ڣ�Ҳʹ��Ĭ�ϡ�
					return super.getNotification(context, msg);
				}
			}
		};
		mPushAgent.setMessageHandler(messageHandler);

	}
	
	 //��ȡ��  
    public void acquireWakeLock(Context context) {  
        if (wakeLock == null) {  
            PowerManager powerManager = (PowerManager)(context.getSystemService(Context.POWER_SERVICE));  
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");  
            wakeLock.acquire();  
        }  
    }  
  
    //�ͷ���  
    public void releaseWakeLock() {  
        if(wakeLock != null && wakeLock.isHeld()){  
            wakeLock.release();  
            wakeLock = null;  
        }  
    }  
	
	private void initViews() {
		mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
		tv_monitor = (TextView) findViewById(R.id.textView_moonitor);
		iv_bluetooth = (ImageView) findViewById(R.id.imageView_bluetooth);
		cb_switch = (CheckBox) findViewById(R.id.checkBox_switch);
		tv_connect = (TextView) findViewById(R.id.textView_connect);
		tv_ele = (TextView) findViewById(R.id.textView_ele);
		iv_ele = (ImageView) findViewById(R.id.imageView_ele);
		
		tv_temper = (TextView) findViewById(R.id.textView_alert);
		tv_title = (TextView) findViewById(R.id.textView_title);
		tv_history = (TextView) findViewById(R.id.textView_history);
		tv_setting = (TextView) findViewById(R.id.textView_setting);
	}
	
	private void initData(){
		initBLEService();
		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		//mConnectInterface = new BluetoothBin(handler, this);
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(btAdapter != null) {
			if(!btAdapter.isEnabled()) {
				Intent enabler=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enabler, activity_OpenBlueTooth);
			}
		}
		DeviceStatusBin.getInstance().setListener(statusListener);
		AppConstants.alert_dialog_time = mSetupData.readInt("alert_between_time", 3000);
	}
	
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case handler_toast:
					ToastNew.makeTextMid(MainActivityBleActivity.this,R.string.link_auto, 3).show();
					break;
				case handler_Link:
					ToastNew.makeTextMid(MainActivityBleActivity.this, R.string.linking, 3).show();
					break;
				case handler_LinkSuccess:
					ToastNew.makeTextMid(MainActivityBleActivity.this,R.string.link_success, 3).show();
					DeviceStatusBin.getInstance().setDeviceSwitch(true);
					isShowToast = false;
					mac = mSetupData.read(SetupData.LastBluetooth);
					mConnService.write(new byte[]{ (byte) 0xaa, (byte) 0x51, (byte) 0x52, (byte) 0x53, (byte) 0x54, (byte) 0x00 });
					break;
				case handler_LinkFailure:
					if(isShowToast) {
						ToastNew.makeTextMid(MainActivityBleActivity.this, R.string.link_fail, 3).show();
						isShowToast = false;
					}
					iv_bluetooth.setBackgroundResource(R.drawable.btn_bluetooth_off);
					tv_connect.setTextColor(getResources().getColor(R.color.text_dark));
					break;
				case handler_linkLost:
					if (mTasksView.getValue()>3200 && mTasksView.getValue()<3600) {
						//断开提醒
						showNextAlertDialog(3);
					}
					DeviceStatusBin.getInstance().setDeviceSwitch(false);
					ToastNew.makeTextMid(MainActivityBleActivity.this, R.string.link_lost, 3).show();
					mTasksView.setValue(0);
					if(mConnService!=null) {
						mConnService.stopConncet();
					}
					break;
//				case ConnectionInterface.GetData:
//					byte[] buf = (byte[]) msg.obj;
//					int temperValue;
//					if(buf.length== 3&& (buf[0]==(-86))) {
//						 temperValue = (MyByte.byteToInt(buf[1]) * 256 + MyByte.byteToInt(buf[2]));
//						 System.out.println("���ճɹ�" + temperValue+" " + (Myhex.buffer2String(buf)) +buf[0]);
//						if(temperValue >3500 && temperValue <4100) {
//							DeviceStatusBin.getInstance().setTemper(temperValue);
//							long nowTimeMillis = System.currentTimeMillis();
//							if(nowTimeMillis - lastTime> AppConstants.jiangeTime) {
//								HistoryBin hbin = new HistoryBin();
//								hbin.setBluetoothMac(MainActivity2.mac);
//								hbin.setValue(""+temperValue);
//								hbin.setShowStatus(0);
//								Date d =new Date();
//								d.setDate(d.getDate());
//								d.setHours(d.getHours());
//								hbin.setDate(HistoryActivity.sdf.format(d));
//								lastTime = nowTimeMillis;
//								System.out.println( " ��ӳɹ� " +hsqlService.insert(hbin) + " " + hbin.getValue() + " " + hbin.getDate());
//							}
//						} else {
//							DeviceStatusBin.getInstance().setTemper(temperValue);
//						}
//					}
//					break;
			  case MESSAGE_READ:	            
				  parseData((byte[])msg.obj);
				  break;	            
	            case MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
	                               Toast.LENGTH_SHORT).show();
	                break;
				case 333:
					DeviceStatusBin.getInstance().setTemper(msg.arg1);
					break;
				case MESSAGE_WRITE:
					byte[] buff= (byte[]) msg.obj;
					System.out.println("д��" + Myhex.buffer2String(buff));
					break;
				case handler_voltage:
					showVoltageImage(voltage);
					break;
					
				case handler_switch_on:
						cb_switch.setChecked(true);
						tv_monitor.setText(R.string.monitor_open);
						monitorStatus= 1;
						tv_monitor.setTextColor(AppConstants.color3);
						iv_bluetooth.setBackgroundResource(R.drawable.btn_bluetooth_on);
						tv_connect.setTextColor(getResources().getColor(R.color.text_blue));
						break;
				case handler_switch_off:
					cb_switch.setChecked(false);
						tv_monitor.setTextColor(Color.BLACK);
						tv_monitor.setText(R.string.monitor_close);
						monitorStatus=0;
						iv_bluetooth.setBackgroundResource(R.drawable.btn_bluetooth_off);
						tv_connect.setTextColor(getResources().getColor(R.color.text_dark));
						break;
				case handler_switch_web:
						cb_switch.setChecked(false);
						tv_monitor.setTextColor(AppConstants.color3);
						tv_monitor.setText(R.string.monitor_web);
						monitorStatus=2;
						iv_bluetooth.setBackgroundResource(R.drawable.btn_bluetooth_off);
						tv_connect.setTextColor(getResources().getColor(R.color.text_dark));
					break;
				case handler_login_unuse:
					Toast.makeText(MainActivityBleActivity.this, R.string.login_unuse, 3).show();
					break;
			}
				
		}
	};
	
	long nowTimeMillis;
	
	
	/**
	 * 
	 * @param buf
	 */
	private void parseData(byte[] buf) {
		int value;
		int ele;//����
		 System.out.println("���ճɹ�" + " " + (Myhex.buffer2String(buf,buf.length)));
		switch(buf[0]) {
		case (byte) 0xAA://�¶�
			value = (MyByte.byteToInt(buf[1]) * 256 + MyByte.byteToInt(buf[2]));
			//if(value>3200 && value <4500) {//ֻ����Ч���¶� �� �ż�¼
				DeviceStatusBin.getInstance().setTemper(value);
				nowTimeMillis = System.currentTimeMillis();
				if(nowTimeMillis - lastTime> AppConstants.jiangeTime) {
					lastTime = nowTimeMillis;
					new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							HistoryBin hbin = new HistoryBin();
							hbin.setBluetoothMac(MainActivityBleActivity.mac);
							hbin.setValue(""+DeviceStatusBin.getInstance().getTemper());
							hbin.setShowStatus(0);
							Date d =new Date();
							d.setDate(d.getDate());
							d.setHours(d.getHours());
							hbin.setDate(HistoryChartActivity.sdf.format(d));
							if(AppConstants.isLineMode) {
								System.out.println( " �������");
								//������¶����ݣ� ��������100������ �������ϵ��������� С�� , ���Խ��¶�  ����100.0d
								startSyncThread(hbin, ""+voltage, ""+MyDecimalHelp.getRoundFolat2(alert_temper/100.0f), ""+alarmStatus);
							} else {//����ģʽ
								hbin.setOnline(false);
								//lastTime = nowTimeMillis;
								System.out.println( " ������߳ɹ� " + 	hsqlService.insertByMac(hbin)+" " + hbin.getValue() + " " + hbin.getDate());
							}
							
						}}).start();
					
				} else{
					System.out.println( " ���ʱ��δ��" + (nowTimeMillis - lastTime));
				}
//			} else {
//				DeviceStatusBin.getInstance().setTemper(value);
//			}
			break;
		case (byte) 0xCC:
			value = MyByte.byteToInt(buf[1]) * 256 + MyByte.byteToInt(buf[2]);
			switch(value) {
			case 404:
				ele = 100;
				break;
			case 394:
				ele = 80;
				break;
			case 384:
				ele = 60;
				break;
			case 374:
				ele = 40;
				break;
			case 364:
				ele = 20;
				break;
			case 354:
				ele = 10;
				break;
			case 334:
				ele = 0;
				break;
			default:
				if(value<=344){
					DeviceStatusBin.getInstance().setDeviceSwitch(false);
					mConnService.write(new byte[]{ (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36 });
				
				}
				return;
			}
			voltage = ele;
			showVoltageImage(voltage);
			break;
		case (byte) 0xbb:
			//Toast.makeText(MainActivity2.this, "У�����ͳɹ�", Toast.LENGTH_SHORT).show();
			break;
		case (byte) 0xdd:
			if(buf[1] == ((byte) 0xee)) {
				 value = MyByte.byteToInt(buf[2]); 
				 switch(value) {
				 case 1:
					// showLowAlertDialog(getString(R.string.temper_low), false);
					 showNextAlertDialog(1);
					 break;
				 case 2:
					 //showLowAlertDialog(getString(R.string.temper_low), false);
					 break;
				 case 3:
					// showNextAlertDialog();
					 break;
				 case 4:
					 showNextAlertDialog(2);
					 break;
				 }
				 alarmStatus=value;
			}
			break;
		case (byte) 0xee:
			if(buf[1] == ((byte) 0xa0)) {
				
			}
			break;
		case (byte) 0xe0:
			 value = MyByte.byteToInt(buf[1]); 
				 switch(value) {
				 case 1:
					// showLowAlertDialog(getString(R.string.temper_low), false);
					 showNextAlertDialog(1);
					 break;
				 case 2:
					 //showLowAlertDialog(getString(R.string.temper_low), false);
					 break;
				 case 3:
					// showNextAlertDialog();
					 break;
				 case 4:
					 showNextAlertDialog(2);
					 break;
				 }
				 alarmStatus=value;
				 
				 
				 //����
				 int valueEle = MyByte.byteToInt(buf[4]) * 256 + MyByte.byteToInt(buf[5]);;//����
					switch(valueEle) {
					case 404:
						ele = 100;
						break;
					case 394:
						ele = 80;
						break;
					case 384:
						ele = 60;
						break;
					case 374:
						ele = 40;
						break;
					case 364:
						ele = 20;
						break;
					case 354:
						ele = 10;
						break;
					case 334:
						ele = 0;
						break;
					default:
						if(value<=344){
							DeviceStatusBin.getInstance().setDeviceSwitch(false);
							mConnService.write(new byte[]{ (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36 });
						
						}
						return;
					}
					voltage = ele;
					showVoltageImage(voltage);
				 break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
//		menu.add(0, 1, 1, "�����¶�");
//		menu.add(0, 2, 2, "����ʱ��");
//		menu.add(0, 3, 3, "С֪ʶ");
		return true;
	}
	
	
	
	
	private void initBLEService(){
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
	}
	
	private final ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName componentName,
				IBinder service) {
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();
			if (!mBluetoothLeService.initialize()) {
				finish();
			}
//			// Automatically connects to the device upon successful start-up
//			// initialization.
//			mBluetoothLeService.connect(mDeviceAddress);
			mConnService = new BleBin(MainActivityBleActivity.this, handler, mBluetoothLeService);
//			Message msg = handler.obtainMessage();
//			msg.what = ConnectionInterface.LinkSuccess;
//			msg.obj = 
//			handler.sendEmptyMessage(ConnectionInterface.LinkSuccess);
		}

		@Override
		public void onServiceDisconnected(ComponentName componentName) {
			mBluetoothLeService = null;
		}
	};
	
	
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
			} else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
					.equals(action)) {
				handler.sendEmptyMessage(handler_linkLost);
				if(isFirst) {
					//mConnService.connect(mac, "");
					isFirst = false;
				}
			} else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				if(mBluetoothLeService!=null) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							mBluetoothLeService.setReceiver3();
						}
					}).start();
				}
				handler.sendEmptyMessage(handler_LinkSuccess);
				final String mac = intent.getStringExtra("mac");
				//�������ӳɹ����Զ���������
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
					}
				}).start();
				// Show all the supported services and characteristics on the
				// user interface.
			} else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) { //��������
				String buffer = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
				Log.d("tag", "��������"+ buffer);
				if(mBluetoothLeService!=null) {
					byte[] buff = Myhex.hexStringToByte(buffer);
					Message msg = handler.obtainMessage();
					msg.what = MESSAGE_READ;
					msg.obj = buff;
					handler.sendMessage(msg);
				}
			}
		}
	};
	
	
	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}
	
	
	
	
	
	
	
	/**
	 * ����ʱ�䴰��
	 */
	private AlertDialog.Builder dialog_builder;
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case 1:
			Intent intent = new Intent(this, TemperAlertActivity1.class);
			startActivity(intent);
			break;
		case 2:
			dialog_builder = new AlertDialog.Builder(this);
			dialog_builder.setTitle(getString(R.string.interval_alarm));
			CharSequence[] items = new CharSequence[] {getString(R.string.interval_none), getString(R.string.interval_10) , getString(R.string.interval_30), getString(R.string.interval_60) , getString(R.string.interval_120)};
			int selectitem=0;
			switch(AppConstants.alert_dialog_time) {
			case 3000:
				selectitem = 0;
				break;
			case 600*1000:
				selectitem = 1;
				break;
			case 1800*1000:
				selectitem = 2;
				break;
			case 3600*1000:
				selectitem = 3;
				break;
			case 7200*1000:
				selectitem = 4;
				break;
			default:
				selectitem = 0;
				break;
			}
			dialog_builder.setSingleChoiceItems(items, selectitem, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					int time =0;
					switch(which) {
					case  0:
						time =3000;
						break;
					case  1:
						time =10 * 60 * 1000;
						break;
					case 2:		
						time =30 * 60 * 1000;
						break;
					case 3:
						time =60 * 60 * 1000;
						break;
					case 4:
						time =120 * 60 * 1000;
						break;
					default: 
					break;
					}
					dialog.dismiss();
					mSetupData.saveInt("alert_between_time", time);
					AppConstants.alert_dialog_time = time;
				}
			});
			dialog_builder.show();
			break;
		case 3:
				dialog = new Dialog(this, R.style.bg_null);
				dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
				
				WindowManager.LayoutParams lp2 = dialog.getWindow().getAttributes();
				lp2.dimAmount = 0.5f;
				dialog.getWindow().setAttributes(lp2);
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
				View v=  LayoutInflater.from(this).inflate(R.layout.dialog_normal_layout, null);
				v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				dialog.setContentView(v);
				Button confirm_btn = (Button) v.findViewById(R.id.positiveButton);
				Button cancel_btn = (Button) v.findViewById(R.id.negativeButton);
				 title_textView = (TextView) v.findViewById(R.id.title);
				TextView message_textView = (TextView) v.findViewById(R.id.message);
				//title_textView.setText(getString(R.string.info_alert)+(alertValue/100.0) +"��C" );
				//������ذ�ť�� ��ʾ���ڲ���ʧ
				dialog.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (keyCode == KeyEvent.KEYCODE_BACK
								&& event.getRepeatCount() == 0) {
							return true;
						} else {
							return false;
							
						}
					}
				});
				cancel_btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(dialog.isShowing()) {
							dialog.dismiss();
						}
					}
				});
				confirm_btn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(dialog.isShowing()) {
							dialog.dismiss();
						}
					}
				});
			if(dialog!=null && !dialog.isShowing()) {
				if(!this.isFinishing()) {
					dialog.show();
				}
			}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case activity_bluetooth:
			if(Activity.RESULT_OK == resultCode) {
				handler.sendEmptyMessage(handler_Link);
				isShowToast = true;
//				if(mConnectInterface.isLink()) {
//					mConnectInterface.stopConncet();
//				}
				if (mConnService != null && mConnService.isLink()) mConnService.stopConncet();
				mac = data.getStringExtra(DeviceListBleActivity.EXTRA_DEVICE_ADDRESS);
				//mConnectInterface.connect(mac, "");
				mSetupData.save(SetupData.LastBluetooth, mac);
				if (BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac) == null)
		    	{
		    		Log.e("tag", "adapter is not exist");
		    		return;
		    	}
		    	BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
		    	isFirst=true;
				mConnService.connect(mac, "");
			}
			break;
		case activity_OpenBlueTooth:
			if( resultCode == Activity.RESULT_OK) {
				//openFindBluetooth(0);
			} else {
				ToastNew.makeTextMid(MainActivityBleActivity.this, getString(R.string.noBluetooth1),
						Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}
	
	
	private void showVoltageImage(int voltage){
		switch(voltage) {
		case 100:
			iv_ele.setBackgroundResource(R.drawable.img_ele_100);
			break;
		case 80:
			iv_ele.setBackgroundResource(R.drawable.img_ele_80);
			break;
		case 60:
			iv_ele.setBackgroundResource(R.drawable.img_ele_60);
			break;
		case 40:
			iv_ele.setBackgroundResource(R.drawable.img_ele_40);
			break;
		case 20:
			iv_ele.setBackgroundResource(R.drawable.img_ele_20);
			break;
		case 10:
			iv_ele.setBackgroundResource(R.drawable.img_ele_10);
			break;
		default:
			iv_ele.setBackgroundResource(R.drawable.img_ele_0);
		}	
		tv_ele.setText(voltage+"%");
	}

	/**
	 * �� �����¶Ⱦ��� ����, ���޸�Ϊ�����˵�
	 * @param v
	 */
	public void btn_temper(View v){
		Intent intent;
		if(AppConstants.isDebug) {
			 intent = new Intent(this, TemperAlertActivityTest.class);
		} else {
			 intent = new Intent(this, TemperAlertActivity1.class);
		}
		startActivity(intent);
		//openOptionsMenu();
	}
	
	/**
	 * ����ʷ��¼����
	 * @param v
	 */
	public void btn_history(View v){
		Intent intent = new Intent(this, HistoryListActivity.class);
		//Intent intent = new Intent(this, HistoryActivity.class);
		intent.putExtra(TemperAlertActivity1.SHARED_ALERT, alert_temper);
		startActivity(intent);
	}
	
	public void btn_switch(View v) {
		CheckBox cb = (CheckBox) v;
		cb.setChecked(true);
		if(mConnService!=null && mConnService.isLink()) {
			if(alertCloseBin ==null) {
				alertCloseBin = com.medical.help.AlertDialogService.getAlertConfrim(this,getString(R.string.is_off));
				alertCloseBin.getCancel_Button().setOnClickListener(new android.view.View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						alertCloseBin.getD().dismiss();	
						if(notificationManager!=null) {
							notificationManager.cancelAll();
						}
					}
				});
				alertCloseBin.getConfrim_Button().setOnClickListener(new android.view.View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						alertCloseBin.getD().dismiss();
						if(notificationManager!=null) {
							notificationManager.cancelAll();
						}
						DeviceStatusBin.getInstance().setDeviceSwitch(false);
						mConnService.write(new byte[]{ (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36 });
						//�÷����߳����꣬�ӳ�һ��Ͽ�
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								mConnService.stopConncet();
							}
						}).start();
						mTasksView.setProgress(0);
					}
				});
			}
			alertCloseBin.getTv().setText(getString(R.string.is_off));
			alertCloseBin.getCancel_Button().setText(getString(R.string.cancel));
			alertCloseBin.getConfrim_Button().setText(getString(R.string.confirm));
			alertCloseBin.getD().show();
		} else {
			cb.setChecked(false);
			ToastNew.makeTextMid(this, R.string.nolink, 2).show();
		}
	}
	
	public void btn_bluetooth(View v){
		Intent intent = new Intent(this, DeviceListBleActivity.class);
		startActivityForResult(intent, activity_bluetooth);
	}
	
	public void btn_knowledge(View view){
//		Message msg = handler.obtainMessage();
//		byte[] buffer = new byte[] { (byte) 0xAA, (byte) (3660/256), (byte) (3660%256)};
//		//byte[] buffer = new byte[] { (byte) 0xdd, (byte) 0xee,(byte) (i>3550?1:4) };
//		msg.what= MESSAGE_READ;
//		msg.obj = buffer;
//		handler.sendMessage(msg);
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
		
//		if(dialog == null) {
//			dialog = new Dialog(this, R.style.bg_null);
//			dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//			
//			WindowManager.LayoutParams lp2 = dialog.getWindow().getAttributes();
//			lp2.dimAmount = 0.5f;
//			dialog.getWindow().setAttributes(lp2);
//			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
//					| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
//			View v=  LayoutInflater.from(this).inflate(R.layout.dialog_normal_layout, null);
//			v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			dialog.setContentView(v);
//			Button confirm_btn = (Button) v.findViewById(R.id.positiveButton);
//			Button cancel_btn = (Button) v.findViewById(R.id.negativeButton);
//			 title_textView = (TextView) v.findViewById(R.id.title);
//			TextView message_textView = (TextView) v.findViewById(R.id.message);
//			//title_textView.setText(getString(R.string.info_alert)+(alertValue/100.0) +"��C" );
//			//������ذ�ť�� ��ʾ���ڲ���ʧ
//			dialog.setOnKeyListener(new OnKeyListener() {
//				
//				@Override
//				public boolean onKey(DialogInterface dialog, int keyCode,
//						KeyEvent event) {
//					// TODO Auto-generated method stub
//					if (keyCode == KeyEvent.KEYCODE_BACK
//							&& event.getRepeatCount() == 0) {
//						return true;
//					} else {
//						return false;
//						
//					}
//				}
//			});
//			cancel_btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(dialog.isShowing()) {
//						dialog.dismiss();
//					}
//					if(notificationManager!=null) {
//						notificationManager.cancelAll();
//					}
//				}
//			});
//			confirm_btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(dialog.isShowing()) {
//						dialog.dismiss();
//					}
//					if(notificationManager!=null) {
//						notificationManager.cancelAll();
//					}
//				}
//			});
//			
//		}
//		//String msg = getString(R.string.info_alert)+(alertValue/100.0) +"��C" ;
//		//title_textView.setText(msg);
//		//showNotification(msg, true);
//		if(dialog!=null && !dialog.isShowing()) {
//			if(!this.isFinishing()) {
//				dialog.show();
//			}
//		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
//		if(mConnectInterface!=null &&mConnectInterface.isLink()) {
//			mConnectInterface.stopConncet();
//			mConnectInterface=null;
//		}
		if (mConnService != null) mConnService.stopConncet();
		mConnService = null;
		if(notificationManager!=null) {
			notificationManager.cancelAll();
		}
		if(linkThraed!=null) {
			linkThraed.interrupt();
			linkThraed=null;
		}
		if(adsThread!=null) {
			adsThread.interrupt();
			adsThread=null;
		}
		unregisterReceiver(mReceiver);
		unregisterReceiver(mGattUpdateReceiver);
		unbindService(mServiceConnection);
		System.exit(0);
		super.onDestroy();
	}

	
	IStatusListener statusListener = new IStatusListener() {
		
		@Override
		public int onTemper(int temper) {
			// TODO Auto-generated method stub
			if(temper<3200) {
				mTasksView.setValue(3200);
			} else if(temper >4100) {
				mTasksView.setValue(4100);
			} else {
				mTasksView.setValue(temper);
			}
//			if(alert_temper > 3850) {  //��Ҫ�������� 3850��
//				if(temper>=3850 && temper<alert_temper) {// λ��3850 �� �����м�
//					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time) {
//						return -1;
//					}
//					lastAlertTime = System.currentTimeMillis();
//					showNextAlertDialog();
//				} else if(temper >=alert_temper){
//					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time  && secondAlert) {
//						return -1;
//					}
//					lastAlertTime = System.currentTimeMillis();
//					secondAlert = true;
//					showNextAlertDialog();
//				}
//			}  else if(alert_temper== 3850) { //����ֵ��ȣ�ֱ�Ӿ���
//				if(temper>=3850) {
//					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time ) {
//						return -1;
//					}
//					lastAlertTime = System.currentTimeMillis();
//					showNextAlertDialog();
//				}
//			} else if(alert_temper<3850) {//����ֵС��3850�� ��Ҫ�������Ǿ���ֵ
//				if(temper >=alert_temper && temper<3850) { //   ����<= <3850
//					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time) {
//						return -1;
//					}
//					lastAlertTime = System.currentTimeMillis();
//					showNextAlertDialog();
//				} else if(temper >=3850) {
//					if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time  && secondAlert) {
//						return -1;
//					}
//					lastAlertTime = System.currentTimeMillis();
//					secondAlert = true;
//					showNextAlertDialog();
//				} else if(temper<AppConstants.temper_min_alert) {//���¾���
//					showLowAlertDialog(getString(R.string.temper_low), false);
//				}
//			}
			return 0;
		}
		
		
		@Override
		public boolean onDeviceSwitch(boolean isOpen) {
			// TODO Auto-generated method stub
			cb_switch.setChecked(isOpen);
			if(isOpen){
				tv_monitor.setText(R.string.monitor_open);
				tv_monitor.setTextColor(AppConstants.color3);
				iv_bluetooth.setBackgroundResource(R.drawable.btn_bluetooth_on);
				tv_connect.setTextColor(getResources().getColor(R.color.text_blue));
			} else {
				tv_monitor.setTextColor(Color.BLACK);
				tv_monitor.setText(R.string.monitor_close);
				iv_bluetooth.setBackgroundResource(R.drawable.btn_bluetooth_off);
				tv_connect.setTextColor(getResources().getColor(R.color.text_dark));
			}
			return false;
		}
		
		@Override
		public int onAlertTemper(int temper) {
			// TODO Auto-generated method stub
			return 0;
		}
	};
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		alert_temper = mSetupData.readInt(TemperAlertActivity1.SHARED_ALERT, 3750);
		mTasksView.setAlertValue(alert_temper);
		checkLocale();
		super.onResume();
	}
	
	private int oldLocale;
	/**
	 * ��������Ƿ��б仯
	 */
	private void checkLocale() {
		int locoleItem = mSetupData.readInt(AppString.language, AppString.Language_zh);
		if(oldLocale!= locoleItem) {
			tv_temper.setText(R.string.alert_temper);
			tv_connect.setText(R.string.link);
			tv_history.setText(R.string.history);
			tv_title.setText(R.string.app_name);
			tv_setting.setText(R.string.setting);
			switch(monitorStatus) {
			case 1:
				tv_monitor.setText(R.string.monitor_open);
				break;
			case 2:
				tv_monitor.setText(R.string.monitor_web);
				break;
			default:
				tv_monitor.setText(R.string.monitor_close);
				break;
			}
			
			oldLocale = locoleItem;
			
		}
	}
	
	private Dialog dialog;
	private Dialog low_dialog;
	TextView title_textView, tv_message;
	long lastAlertTime = 0;
	long lastLowAlertTime =0;
	
	private RadioGroup rg_time;
	private RadioButton rb_10, rb_30, rb_60, rb_120;
	private Button confirm_btn;
	
	private void showNextAlertDialog(final int isLowAlert) {
		if(low_dialog!=null) {
			low_dialog.dismiss();
			low_dialog=null;
		}
		if(low_dialog == null) {
			low_dialog = new Dialog(this, R.style.bg_null);
			low_dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			low_dialog.setCancelable(false);
			low_dialog.setCanceledOnTouchOutside(false);
			WindowManager.LayoutParams lp2 = low_dialog.getWindow().getAttributes();
			lp2.dimAmount = 0.5f;
			low_dialog.getWindow().setAttributes(lp2);
			low_dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			low_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
					| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
			View v=  LayoutInflater.from(this).inflate(R.layout.alert_next, null);
			v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			low_dialog.setContentView(v);
			tv_message = (TextView) v.findViewById(R.id.textView_message);
			rg_time = (RadioGroup) v.findViewById(R.id.radioGroup_time);
			rb_10 = (RadioButton) v.findViewById(R.id.radioButton_10);
			rb_30 = (RadioButton) v.findViewById(R.id.radioButton_30);
			rb_60 = (RadioButton) v.findViewById(R.id.radioButton_60);
			rb_120 = (RadioButton) v.findViewById(R.id.radioButton_120);
			Button cancel_btn = (Button) v.findViewById(R.id.btn_cancel);
			 confirm_btn = (Button) v.findViewById(R.id.btn_confrim);
			//������ذ�ť�� ��ʾ���ڲ���ʧ
			low_dialog.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method stub
					if (keyCode == KeyEvent.KEYCODE_BACK
							&& event.getRepeatCount() == 0) {
						return true;
					} else {
						return false;
						
					}
				}
			});
			cancel_btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(low_dialog.isShowing()) {
						low_dialog.dismiss();
					}
					if(notificationManager!=null) {
						notificationManager.cancelAll();
					}
					mSetupData.saveInt("alert_between_time", 3000);
					AppConstants.alert_dialog_time = 3000;
				}
			});
			
			
		}
		confirm_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(low_dialog.isShowing()) {
					low_dialog.dismiss();
				}
				if(notificationManager!=null) {
					notificationManager.cancelAll();
				}
				int time =0;
				switch(rg_time.getCheckedRadioButtonId()) {
					case  0:
						time =3000;
						break;
					case  R.id.radioButton_10:
						time =10 * 60 * 1000;
						break;
					case R.id.radioButton_30:		
						time =30 * 60 * 1000;
						break;
					case R.id.radioButton_60:
						time =60 * 60 * 1000;
						break;
					case R.id.radioButton_120:
						time =120 * 60 * 1000;
						break;
					default: 
					break;
				}
				Log.e("tag", "���͸���1���� "+isLowAlert);
				switch (isLowAlert ) {//����
				case 1:
					mSetupData.saveInt("alertLow_between_time", time);
					//AppConstants.alert_dialog_time = time;
					mConnService.write(new byte[]{ (byte) 0xaa, (byte) 0x5a, (byte) 0xa5, (byte) 0x00, (byte) (time/(256*60*1000)), (byte) (time/(60*1000)) });
					break;
				case 2:
					mSetupData.saveInt("alert_between_time", time);
					AppConstants.alert_dialog_time = time;
					mConnService.write(new byte[]{ (byte) 0xaa, (byte) 0x5a, (byte) 0xa5, (byte) 0xff, (byte) (time/(256*60*1000)), (byte) (time/(60*1000)) });
					break;
				case 3:
					
					break;
				}
			}
		});
		int alert_time;
		String str;
		switch (isLowAlert) {
			case 1:
				str = getString(R.string.temper_low);
				alert_time = mSetupData.readInt("alertLow_between_time", 600*1000);
			break;
			case 2:
				str = getString(R.string.temper_height);
				alert_time = AppConstants.alert_dialog_time;
			break;
			default:
				str = getString(R.string.temper_leveainfo);
				alert_time = AppConstants.alert_dialog_time;
			break;
		}
		switch(alert_time) {
//		case 3000:
//			//rb_10.setChecked(true);
//			break;
		case 600*1000:
			rb_10.setChecked(true);
			break;
		case 1800*1000:
			rb_30.setChecked(true);
			break;
		case 3600*1000:
			rb_60.setChecked(true);
			break;
		case 7200*1000:
			rb_120.setChecked(true);		
			break;
		default:
			rb_10.setChecked(true);	
			rb_30.setChecked(false);	
			rb_60.setChecked(false);	
			rb_120.setChecked(false);	
			break;
		}
			if(!this.isFinishing()) {
				low_dialog.show();
			}
			tv_message.setText(""+str);
			showNotification(str, true);
	}
//	private void showLowAlertDialog(final int alertValue){
//		if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time) {
//			return;
//		}
//		if(isShowLowAlert) {
//			return;
//		}
//		lastAlertTime = System.currentTimeMillis();
//		if(low_dialog == null) {
//			low_dialog = new Dialog(this, R.style.bg_null);
//			low_dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//			
//			WindowManager.LayoutParams lp2 = low_dialog.getWindow().getAttributes();
//			lp2.dimAmount = 0.5f;
//			low_dialog.getWindow().setAttributes(lp2);
//			low_dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//			low_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
//					| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
//			View v=  LayoutInflater.from(this).inflate(R.layout.dialog_low_normal_layout, null);
//			v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			low_dialog.setContentView(v);
//			Button confirm_btn = (Button) v.findViewById(R.id.positiveButton);
//			Button cancel_btn = (Button) v.findViewById(R.id.negativeButton);
//			 title_textView = (TextView) v.findViewById(R.id.title);
//			TextView message_textView = (TextView) v.findViewById(R.id.message);
//			title_textView.setText(getString(R.string.info_alert)+(alertValue/100.0) +"��C" );
//			//������ذ�ť�� ��ʾ���ڲ���ʧ
//			low_dialog.setOnKeyListener(new OnKeyListener() {
//				
//				@Override
//				public boolean onKey(DialogInterface dialog, int keyCode,
//						KeyEvent event) {
//					// TODO Auto-generated method stub
//					if (keyCode == KeyEvent.KEYCODE_BACK
//							&& event.getRepeatCount() == 0) {
//						return true;
//					} else {
//						return false;
//						
//					}
//				}
//			});
//			cancel_btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(low_dialog.isShowing()) {
//						low_dialog.dismiss();
//					}
//					if(notificationManager!=null) {
//						notificationManager.cancelAll();
//					}
//					isShowLowAlert = true;
//					mConnService.write(new byte[]{ (byte) 0x91, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) (alertValue/10 -350) });
//				}
//			});
//			confirm_btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(low_dialog.isShowing()) {
//						low_dialog.dismiss();
//					}
//					if(notificationManager!=null) {
//						notificationManager.cancelAll();
//					}
//				}
//			});
//			
//		}
//		String msg = getString(R.string.info_alert)+(alertValue/100.0) +"��C" ;
//		title_textView.setText(msg);
//		showNotification(msg, true);
//		if(low_dialog!=null && !low_dialog.isShowing()) {
//			if(!this.isFinishing()) {
//				low_dialog.show();
//			}
//		}
//	}
	
	/**
	 * ������ʾ
	 * @param string
	 * @param isShowCancel
	 */
//	private void showLowAlertDialog(String string, boolean isShowCancel) {
//		if(System.currentTimeMillis() - lastLowAlertTime < AppConstants.alert_low_dialog_time) {
//			return ;
//		}
//		lastLowAlertTime = System.currentTimeMillis();
//		if(alertConfrimBin ==null) {
//			alertConfrimBin = com.medical.help.AlertDialogService.getAlertConfrim(this,string);
//			alertConfrimBin.getCancel_Button().setOnClickListener(new android.view.View.OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					alertConfrimBin.getD().dismiss();	
//					if(notificationManager!=null) {
//						notificationManager.cancelAll();
//					}
//				}
//			});
//			alertConfrimBin.getConfrim_Button().setOnClickListener(new android.view.View.OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0) {
//					alertConfrimBin.getD().dismiss();
//					if(notificationManager!=null) {
//						notificationManager.cancelAll();
//					}
//				}
//			});
//			alertConfrimBin.getD().setCanceledOnTouchOutside(false);
//			alertConfrimBin.getD().setCancelable(false);
//		}
//		if(isShowCancel) {
//			alertConfrimBin.getCancel_Button().setVisibility(View.VISIBLE);
//		} else {
//			alertConfrimBin.getCancel_Button().setVisibility(View.GONE);
//		}
//		alertConfrimBin.getTv().setText(string);
//		showNotification(string, false);
//		if(!this.isFinishing()) {
//			alertConfrimBin.getD().show();
//		}
//	}
	
//	private void showAlertDialog(int alertValue){
//		if(dialog == null) {
//			dialog = new Dialog(this, R.style.bg_null);
//			dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//			
//			WindowManager.LayoutParams lp2 = dialog.getWindow().getAttributes();
//			lp2.dimAmount = 0.5f;
//			dialog.getWindow().setAttributes(lp2);
//			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//			dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
//					| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
//			View v=  LayoutInflater.from(this).inflate(R.layout.dialog_normal_layout, null);
//			v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			dialog.setContentView(v);
//			Button confirm_btn = (Button) v.findViewById(R.id.positiveButton);
//			Button cancel_btn = (Button) v.findViewById(R.id.negativeButton);
//			 title_textView = (TextView) v.findViewById(R.id.title);
//			TextView message_textView = (TextView) v.findViewById(R.id.message);
//			title_textView.setText(getString(R.string.info_alert)+(alertValue/100.0) +"��C" );
//			//������ذ�ť�� ��ʾ���ڲ���ʧ
//			dialog.setOnKeyListener(new OnKeyListener() {
//				
//				@Override
//				public boolean onKey(DialogInterface dialog, int keyCode,
//						KeyEvent event) {
//					// TODO Auto-generated method stub
//					if (keyCode == KeyEvent.KEYCODE_BACK
//							&& event.getRepeatCount() == 0) {
//						return true;
//					} else {
//						return false;
//						
//					}
//				}
//			});
//			cancel_btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(dialog.isShowing()) {
//						dialog.dismiss();
//					}
//					if(notificationManager!=null) {
//						notificationManager.cancelAll();
//					}
//				}
//			});
//			confirm_btn.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					if(dialog.isShowing()) {
//						dialog.dismiss();
//					}
//					if(notificationManager!=null) {
//						notificationManager.cancelAll();
//					}
//				}
//			});
//			
//		}
//		String msg = getString(R.string.info_alert)+(alertValue/100.0) +"��C" ;
//		title_textView.setText(msg);
//		showNotification(msg, true);
//		if(dialog!=null && !dialog.isShowing()) {
//			if(!this.isFinishing()) {
//				dialog.show();
//			}
//		}
//	}
	
//	public void showCheckWear(){
//		if(System.currentTimeMillis() - lastAlertTime < AppConstants.alert_dialog_time) {
//			return;
//		}
//		String msg = "���¹��ͼ��������" ;
//		showNotification(msg, true);
//		lastAlertTime = System.currentTimeMillis();
//		dialoga = new AlertDialog.Builder(this)
//		.setTitle("���¹��ͼ��������")
//		.setPositiveButton(getString(R.string.confirm), new android.content.DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				arg0.dismiss();
//				if(notificationManager!=null) {
//					notificationManager.cancelAll();
//				}
//			}
//		})
//		//.setNegativeButton(R.string.cancel, null)
//		.show();
//		if(!isFinishing()) {
//			dialoga.show();
//		}
//	}
	
	
	/**
	 * ����֪ͨ
	 * @param content
	 */
	private void showNotification(String content, boolean isrecly) {
		if(dialog!=null && dialog.isShowing()) {
			return;
		}
		if(notificationManager == null) {
			notificationManager = (NotificationManager) MainActivityBleActivity.this
					.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		}
	
		Notification notification = new Notification(R.drawable.ic_launcher,
				getString(R.string.notifi_title), System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // ����֪ͨ����"Ongoing"��
		//notification.flags |= Notification.FLAG_NO_CLEAR; //
		//notification.flags |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		//notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		int ringSound = RingHelp.getRingRourse(MainActivityBleActivity.this, mSetupData.readInt(AppString.Ring, 0));
		notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" +ringSound);
		if(isrecly) {
			notification.flags |= Notification.FLAG_INSISTENT;
		}
		// �����֪ͨ���е�"���֪ͨ"�����
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		//notification.defaults = Notification.DEFAULT_LIGHTS;
		// ����֪ͨ���¼���Ϣ
		Intent notificationIntent = new Intent(MainActivityBleActivity.this,
				MainActivityBleActivity.class);
		// ����intent ������ģʽ
//		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);// �ؼ���һ������������ģʽ
		PendingIntent contentItent = PendingIntent.getActivity(
				MainActivityBleActivity.this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(MainActivityBleActivity.this,
				"Message", // ֪ͨ������
				content, // ֪ͨ������
				contentItent); // �����֪ͨ��Ҫ��ת��Activity
		// ��Notification���ݸ�NotificationManager��idΪ0
		notificationManager.notify(0, notification);
	}
	
	
	private void setAbout(){
		TextView tv_version = (TextView)findViewById(R.id.textView_version);
		PackageManager pmanager = this.getPackageManager();
		try {
			PackageInfo pinfo = pmanager.getPackageInfo("com.example.hongwai", 0);
			tv_version.setText(getString(R.string.app_name)+ " V"+pinfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	Dialog dialoga;
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
			dialoga = new AlertDialog.Builder(this)
			.setTitle(""+getString(R.string.is_exit))
			.setPositiveButton(getString(R.string.confirm), new android.content.DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					isDestroy = true;
					finish();
					System.exit(0);
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			})

			.setNegativeButton(R.string.cancel, null).show();
		dialoga.show();
	}
	
	
	
	@Override
		protected void onStart() {
			// TODO Auto-generated method stub
		if(linkThraed == null) {
			linkThraed = new Thread(linkRunnable);
		}
		linkThraed.start();
			super.onStart();
		}
	
	
	 @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		 if(linkThraed != null) {
				linkThraed.interrupt();
				linkThraed = null;
			}
		super.onStop();
	}

	/**
		 * ���������㲥
		 */
		private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		        @Override
		        public void onReceive(Context context, Intent intent) {
		            String action = intent.getAction();
		            try {
		            	 if(action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
		                		BluetoothDevice btDevice = intent
		                				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		                		Log.d("tag","Auto paring " + btDevice.getAddress());
		                		try {
		                			ClsUtils.setPin(btDevice.getClass(),
		                					btDevice, ""); // �ֻ��������ɼ������
		                		} catch (Exception e) {
		                			
		                			Log.d("mylog", "setPiN failed!");
		                			e.printStackTrace();
		                		}
		                	} else if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
		                		if(!AppConstants.isLineMode) {
		                			if(SocketHelper.isnetworkConnected(MainActivityBleActivity.this)) {
		                				Intent intenta =  new Intent(MainActivityBleActivity.this, UserLoginActivity.class);
		                				startActivity(intenta);
		                				finish();
		                			}
		                		}
		                	}
					} catch (Exception e) {
						// TODO: handle exception
					}
		        }
		    };

	private boolean isShowToast = false;
	int count= 59;//%20����ѯһ��״̬��%30 ��ѯһ�� ��� 

	Runnable linkRunnable = new Runnable() {//�������Զ������̣߳� ���ڽ��Զ����� �� ���Զ�ȥ��������ѯ��棬�ϲ���һ���ã� 20���Զ����ӣ��Ͳ�ѯһ�η��������
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				count++;
				if(count>10000) count =0;
				String lastmac = mSetupData.read(SetupData.LastBluetooth);
				Log.v("tag", count+"�Զ�����"+lastmac);
				//handler.sendEmptyMessage(handler_toast);
				try {
					Thread.sleep(AppConstants.auto_link_time);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Log.e("tag", "�����Զ�����");
					e.printStackTrace();
					break;
				}
				
				
				//�Զ���ѯ�� ״̬
				if(count%60==0) {
					Log.v("tag", "��ѯ״̬");
					startQueryStatus();
				}
				if(count%65==0) {
					//startADThread();
					Log.v("tag", "��ѯ���");
					startPushWarn();
				}
				if(isShowToast) {//��ʾ�����ڴ��� �ֶ������У� ���˳��Զ�����
					continue;
				}
				if(mConnService!=null && mConnService.isLink()) {
					continue;
				}
				try {
					//if (mConnService != null) mConnService.stopConncet();
					if(lastmac.isEmpty()) {
						Log.e("tag", "û�м���������豸�� �����Զ�����");
						continue;//��ΪҪ��ѯ
					}
					if (BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lastmac) == null)
					{
						Log.e("tag", "adapter is not exist");
						continue;
					}
//					BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lastmac);
					if(!mBluetoothLeService.isConnecting()) {
						mConnService.connect(mac, "");
					}
				} catch(Exception e) {
					//e.printStackTrace();
				}
			}
		}
	};
	
	
	/**
	 * ��ѯ���������
	 */
//	private void startADThread() {
//		//���������ģʽ���Ͳ���ѯ
//		if(!AppConstants.isLineMode) {
//			return;
//		}
//		if(adsThread !=null) {
//			adsThread.interrupt();
//			adsThread = null;
//		}
//		adsThread = new Thread (adRunnable);
//		adsThread.start();
//	}
//	
//	Runnable adRunnable = new Runnable() {
//		
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			String phone = mSetupData.read(AppString.account);
//			JSONObject result = mNetworkManager.queryAds(phone);
//			if(mNetworkManager.isNetworkSuccess(result)) {
//				JSONArray array;
//				try {
//					if(isDestroy) return;
//					array = result.getJSONArray("data");
//					Intent intent = new Intent(MainActivity2.this, MessageActivity.class);
//					intent.putExtra("message", array.toString());
//					startActivity(intent);
//				} catch (JSONException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch(Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	};
	
	
	/**
	 * ��ѯ������Ϣ
	 */
	private void startPushWarn() {
		//���������ģʽ���Ͳ���ѯ
		if(!AppConstants.isLineMode) {
			return;
		}
		if(pushWarnThread !=null) {
			pushWarnThread.interrupt();
			pushWarnThread = null;
		}
		pushWarnThread = new Thread (pushWarnRunnable);
		pushWarnThread.start();
	}
	
	Runnable pushWarnRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String phone = mSetupData.read(AppString.account);
			JSONObject result = mNetworkManager.queryPushWarm(phone);
			if(mNetworkManager.isNetworkSuccess(result)) {
				JSONObject obj;
				try {
					if(isDestroy) return;
					obj = result.getJSONObject("data");
					if(obj.getBoolean("isWarn")) {//����
						String imgName= obj.getString("content");
						//String imgName="img/ide_bg_h.png" ;
						String pathEnvir = Environment.getExternalStorageDirectory()
								.getCanonicalPath() + "/" + AppConstants.folder_ad;
						if(GuideActivity.loadImageFromUrl(pathEnvir, AppConstants.serverAction, imgName)) {
							Intent intent = new Intent(MainActivityBleActivity.this, MessageActivity.class);
							intent.putExtra("message", pathEnvir+"/"+imgName);
							startActivity(intent);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	
	/**
	 * ��ѯ״̬
	 */
	private void startQueryStatus() {
		//���������ģʽ���Ͳ���ѯ
		if(!AppConstants.isLineMode) {
			return;
		} 
		if(mConnService.isLink()) {//����Ѿ����������豸�Ͳ���ѯ
			return;
		}
		new Thread (queryStatus).start();
	}
	
	Runnable queryStatus = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mConnService.isLink()) {//����Ѿ����������豸�Ͳ���ѯ
				return;
			}
			if(isDestroy) return;
			String phone = mSetupData.read(AppString.account);
			JSONObject result = mNetworkManager.queryStatus(phone);
			if(mNetworkManager.isNetworkSuccess(result)) { //��÷������ϵ��¶�״̬
				Log.v("statusThread", "result="+result.toString());
				JSONObject obj;
				try {
					obj = result.getJSONObject("data");
					final int temper = (int) (Float.valueOf(obj.getString("temperature")) *100);
					voltage = obj.getInt("voltage");
					handler.sendEmptyMessage(handler_voltage);

					//���ۿ��أ� �����ƶ˿���
					//int isBlueConn = (int) (obj.getInt("bluetoothIsConnect"));
					//if(isBlueConn==1) {
						handler.sendEmptyMessage(handler_switch_web);
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								mTasksView.setValue(temper);
							}
						});
//					} else {
//						handler.sendEmptyMessage(handler_switch_off);
//					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			
		}
	};
	
	/**
	 * ��ʼͬ�����ݵ�������
	 */
	private void startSyncThread( HistoryBin historyBin,final String voltage,final String alarmTemperature,final String alarmState) {
		if(isDestroy) return;
		if(!mConnService.isLink()) {//û���������豸
			return;
		}
//				// TODO Auto-generated method stub
				String account = mSetupData.read(AppString.account);
				//����100�� �������С��
				JSONObject result = mNetworkManager.sync(account,
						""+MyDecimalHelp.getRoundFolat2( Integer.parseInt(historyBin.getValue()) / 100.0d), voltage, alarmTemperature, "0", "0", alarmState);
				if(mNetworkManager.isNetworkSuccess(result)) {
					String time;
					JSONObject data ;
					try {
						data = result.getJSONObject("data");
						time = data.getString("dataTime");
						//String temper = result.getString("temperature");
						historyBin.setOnline(true);
						historyBin.setDate(time);
						historyBin.setPhoneAccount(account);
						hsqlService.insertByAccount(historyBin);
						return;
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if(mNetworkManager.isNetworkNoLogin(result)) {  //�����Ѿ�ʧЧ
					String password = mSetupData.read(AppString.password, "");
					if(password.equals("")) {//û������
						mSetupData.saveboolean(AppString.isLogin, false);
						Intent intenta =  new Intent(MainActivityBleActivity.this, UserLoginActivity.class);
        				startActivity(intenta);
        				finish();
        				return;
					}
					//��̨����
					result = mNetworkManager.userLoginNoAlert(account, password);
					//��������
					if(mNetworkManager.isNetworkSuccess(result)) { 
						//�����Ѹղŵ����ݴ�����ȥ
						result =  mNetworkManager.sync(account,""+MyDecimalHelp.getRoundFolat2( Integer.parseInt(historyBin.getValue()) / 100.0d), voltage, alarmTemperature, "0", "0", alarmState);
						if(mNetworkManager.isNetworkSuccess(result)) {
							try {
							String time;
							JSONObject data ;
									data = result.getJSONObject("data");
									time = data.getString("dataTime");
									//String temper = result.getString("temperature");
									historyBin.setOnline(true);
									historyBin.setDate(time);
									historyBin.setPhoneAccount(account);
									hsqlService.insertByAccount(historyBin);
									return;
							} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
							}
						}
					} else {//���µ���
						mSetupData.saveboolean(AppString.isLogin, false);
						handler.sendEmptyMessage(handler_login_unuse);
						Intent intenta =  new Intent(MainActivityBleActivity.this, UserLoginActivity.class);
        				startActivity(intenta);
        				finish();
        				return;
					}
				} else {//�ϴ�ʧ�ܣ��������ߵĵ�
					//�������ߵĵ㣬����
					historyBin.setPhoneAccount(account);
					historyBin.setOnline(false);
					hsqlService.insertByAccount(historyBin);
				}
	}
	
	
	/**
	 * ��ʼ���ط����������߳�
	 */
	private void startDownloadThread() {
		if(!AppConstants.isLineMode) {
			return;
		}
		new Thread(new Runnable() {
			
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(isDestroy) return;
				String phone = mSetupData.read(AppString.account);
				JSONObject result = mNetworkManager.queryRecord(phone);
				if(mNetworkManager.isNetworkSuccess(result)) {
					Log.v("downRecordThread", result.toString());
					try {
						JSONArray array = result.getJSONArray("data");
						Intent intent = new Intent(HistoryListActivity.Broad_History_Over);
						hsqlService.insertByAccount(array, phone);
//						JSONObject jobj ;
//						HistoryBin historyBin;
//						Log.v("downRecordThread", "array.size " + array.length());
//						for(int i =0 ; i< array.length(); i++) {
//							jobj = array.getJSONObject(i);
//							historyBin = new HistoryBin();
//							try {
//								historyBin.setDate(jobj.getString("dataTime"));
//								//������¶����ݣ� ��������100������ �������ϵ��������� С�� 
//								final int temper = (int) (Float.valueOf(jobj.getString("temperature")) *100);
////								if(temper<3200|| temper>4100){//�����¶ȷ�Χ�� 35�� ��41��֮��
////									continue;
////								}
//								historyBin.setValue(""+temper);
//								historyBin.setOnline(true);
//								historyBin.setPhoneAccount(phone);
//								hsqlService.insertByAccount(historyBin);
//							} catch(JSONException e) {
//								continue;
//							}
//							if(i%100==0) {
//								sendBroadcast(intent);
//							}
//						}
						sendBroadcast(intent);
						Log.v("downRecordThread", "!!array.size " + array.length());
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		}).start();
	}
	
	
	/**
	 * �鿴�汾
	 */
	public void startCheckVersionThread() {
		new Thread(new Runnable() {
			
			/* (non-Javadoc)
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String phone = mSetupData.read(AppString.account);
				PackageManager pm = getPackageManager();
				try {
					PackageInfo pinfo = pm.getPackageInfo(getPackageName(), 0);
					JSONObject result = mNetworkManager.queryVersion(pinfo.versionCode);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}).start();
	}
	
}
