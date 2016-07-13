package com.example.medical;

import java.util.Date;
import java.util.List;
import com.medical.constants.AppConstants;
import com.medical.constants.AppString;
import com.medical.help.SetupData;
import com.medical.manager.ExcelManager;
import com.medical.sql.HistoryBin;
import com.medical.sql.HistorySQLService2;
import com.zby.medical.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 将历史记录 用天数来分开， 这里是  日期 列表
 * @author Administrator
 * @2015-1-22 上午1:07:44
 */
public class HistoryListActivity extends Activity {
	
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private List<String> list;
	private HistorySQLService2 sqlService;
	private ExcelManager excelManager;
	private SetupData mSetupData;
	int alert_value;
	private final int handler_refresh = 101;
	private final static int handler_history = 102;
	/**
	 * main界面下载数据完成后的广播
	 */
	public final static String Broad_History_Over = "com.medical.history_over";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stu11b
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.history_list);
		initViews();
		alert_value = getIntent().getIntExtra(TemperAlertActivity1.SHARED_ALERT, 3750);
	}
	
	public Handler handler = new Handler() {
		public void handleMessage(Message msg){
			switch(msg.what) {
			case handler_refresh:
				adapter.notifyDataSetChanged();
				break;
			case handler_history:
				if(AppConstants.isLineMode) {//登入模式， 查找联网的数据
					list = sqlService.queryListByDayByAccount(mSetupData.read(AppString.account));
				} else {//离线模式 ， 查询对应设备的数据
					list = sqlService.queryListByDay(mSetupData.read(SetupData.LastBluetooth));
					//list = sqlService.queryListByDay(MainActivity2.mac);
				}
				listView.setAdapter(adapter);
				handler.sendEmptyMessage(handler_refresh);
				break;
			}
		}
	};
	
	private void initViews(){
		listView =(ListView) findViewById(R.id.list_view);
		sqlService = new HistorySQLService2(this);
		mSetupData = SetupData.getSetupData(this);
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(AppConstants.isLineMode) {//登入模式， 查找联网的数据
							list = sqlService.queryListByDayByAccount(mSetupData.read(AppString.account));
						} else {//离线模式 ， 查询对应设备的数据
							list = sqlService.queryListByDay(mSetupData.read(SetupData.LastBluetooth));
							//list = sqlService.queryListByDay(MainActivity2.mac);
						}
						adapter = new ArrayAdapter<String>(HistoryListActivity.this,R.layout.history_list_item, R.id.textView_item, list);
						listView.setAdapter(adapter);
						handler.sendEmptyMessage(handler_refresh);
					}
				});
	//		}
	//	}).start();
//		if(list.size()==0) {
//			HistoryBin bin ;
//			for(int i =2; i<50;i++){
//				bin= new HistoryBin();
//				java.util.Date date = new java.util.Date();
//				date.setDate(date.getDate()-i%5);
//				date.setMinutes(date.getMinutes()+i*5);
//				bin.setDate(HistoryActivity.sdf.format(date));
//				bin.setBluetoothMac("AABBCCDDEEFF");
//				bin.setValue(""+(3545+i*5));
//				bin.setShowStatus(0);
//				System.out.println("添加一个");
//				sqlService.insert(bin);
//			}
//			list = sqlService.queryListByDay(MainActivity2.mac);
//		}
		
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HistoryListActivity.this, HistoryChartActivity.class);
				intent.putExtra("datetime", list.get(arg2));
				intent.putExtra(TemperAlertActivity1.SHARED_ALERT, alert_value);
				startActivity(intent);
				
				//AChatEngine的画法
//				SensorValuesChart schart = new SensorValuesChart();
//				schart.setTitle(list.get(arg2), "hour", "temper");
//				List<HistoryBin> listHistory = sqlService.queryDay2Day(list.get(arg2), MainActivity2.mac);
//				if(listHistory.size()<2) {
//					Toast.makeText(HistoryListActivity.this, "温度记录太少", 3).show();
//					return;
//				}
//				schart.initData(listHistory , alert_value);
//				Intent intent = schart.execute(HistoryListActivity.this);
//				startActivity(intent);
			}
		});
	}
	
	public void btn_back(View v){
		finish();
	}
	
	
	public void btn_export(View v) {
		if(excelManager==null) {
			excelManager = new ExcelManager(this, handler);
		}
		//List<HistoryBin> listHistory = sqlService.queryDay2Day(list.get(arg2), MainActivity2.mac);
		List<HistoryBin> listHistory =  sqlService.queryAll();
		if(listHistory.size()==0) {
			Toast.makeText(
					HistoryListActivity.this,
					getString(R.string.output_data_empty),
					3).show();
			return;
		}
		try {
			Date d= new Date();
			String name= "list" +(d.getMonth()+1) +"月"+d.getDate()+"日"+d.getHours()+"-"+d.getMinutes();
			String path = excelManager.createExcel(name, listHistory);
				if (path != null && !path.equals("")) {
					// MyCustomToast.makeText(LocationSwitchActivity.this,
					// getString(R.string.filePath)+":"+path,
					// Toast.LENGTH_LONG).show();
					new AlertDialog.Builder(
							HistoryListActivity.this)
							.setTitle(
									getString(R.string.file_path))
							.setMessage(path)
							.setPositiveButton(
									getString(R.string.confirm),
									null).show();
				} else {
					new AlertDialog.Builder(
							HistoryListActivity.this)
							.setTitle(
									getString(R.string.file_path))
							.setMessage(
									getString(R.string.sdcard_none))
							.setPositiveButton(
									getString(R.string.confirm),
									null).show();
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(
					HistoryListActivity.this,
					getString(R.string.sdcard_none),
					3).show();
		}
	}
	
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		registerReceiver(historyReceiver, new IntentFilter(Broad_History_Over));
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		unregisterReceiver(historyReceiver);
		super.onStop();
	}

	private BroadcastReceiver historyReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().equals(Broad_History_Over)) {
				handler.sendEmptyMessage(handler_history);
			}
		}
	};
}
	