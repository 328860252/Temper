package com.example.medical;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.medical.constants.AppConstants;
import com.medical.constants.AppString;
import com.medical.help.SetupData;
import com.medical.sql.HistoryBin;
import com.medical.sql.HistorySQLService2;
import com.medical.view.ChartView;
import com.medical.view.ChartView2;
import com.medical.view.ChartView2Y;
import com.zby.medical.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

/**
 * 这里是 画图界面
 * @author Administrator
 * @2015-1-22 上午1:09:22
 */
public class HistoryChartActivity extends Activity {
	
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	ChartView2 myView;
	ChartView2Y yView;
	private RelativeLayout  layout_yvalue;
	private LinearLayout layout_history;
	private String datetime;
	private TextView tv_title;
	private SetupData mSetupData;
	/**
	 * X总共移动了多少距离
	 */
	private int xMove;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.temper_history_chart);
		
		//testDb();
		initData();
		layout_history = (LinearLayout) findViewById(R.id.layout_history);
		layout_yvalue = (RelativeLayout) findViewById(R.id.layout_yvalue);
		
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);

		int phone_width = wm.getDefaultDisplay().getHeight();// 屏幕宽度
		
		int paddingTop = (phone_width -1000) / 2;
		//完全不知道为什么 我得linearlayout 会变成 relcativeLayout
//		RelativeLayout.LayoutParams pa = (LayoutParams) layout_history.getLayoutParams();
//		pa.setMargins(0, 200, 0, 0);
//		layout_history.setLayoutParams(pa);
		
		
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) layout_yvalue.getLayoutParams();
//		RelativeLayout.LayoutParams linearParams =  (RelativeLayout.LayoutParams) layout_yvalue.getLayoutParams();
		params.setMargins(0, paddingTop, 0, 0);
		layout_yvalue.setLayoutParams(params);

		
		tv_title = (TextView) findViewById(R.id.textView_title);
		tv_title.setText(datetime);
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				test();
				layout_history.addView(myView);
				
				layout_yvalue.addView(yView);
				yView.setVisibility(View.INVISIBLE);
			}
		});
		
	}
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
			}
		}
	};
	
	List<HistoryBin> list;
	
	private void initData(){
		HistorySQLService2 sql = new HistorySQLService2(this);
		datetime = getIntent().getStringExtra("datetime");
//		if(str==null||str.isEmpty()) {
//			Date d = new Date();
//			str = sdf.format(d).substring(0, 10);
//		}
		mSetupData = SetupData.getSetupData(this);
		if(AppConstants.isLineMode) {//登入模式， 查找联网的数据
			list = sql.queryDay2DayByAccount(datetime,mSetupData.read(AppString.account, ""));
		} else {//离线模式 ， 查询对应设备的数据
			list = sql.queryDay2Day(datetime,mSetupData.read(SetupData.LastBluetooth, ""));
			//list = sql.queryDay2Day(datetime,MainActivity2.mac);
		}
	}
	
	
//	private void testDb( ) {//查询七天的
//		HistorySQLService sql = new HistorySQLService(this);
//		Date d = new Date();
//		d.setDate(d.getDate()-7);
//		d.setHours(0);
//		d.setMinutes(0);
//		d.setSeconds(0);
//		System.out.println("xxxxxx"+MainActivity2.mac);
//		 list = sql.getListBy7Day(MainActivity2.mac, sdf.format(d) );
//		 System.out.println("xxxxxx"+list.size());
//		if(list.size()==0) {
//			HistoryBin bin ;
//			for(int i =1; i<20; i++) {
//				bin= new HistoryBin();
//				bin.setBluetoothMac(MainActivity.mac);
//				bin.setShowStatus(0);
//				bin.setValue( "" + (3500 + (i %10)*50));
//				java.util.Date date = new java.util.Date();
//				date.setDate(date.getDate()-i);
//				bin.setDate(sdf.format(date).toString());
//				sql.insert(bin);
//				//db.insert(Table_Name, "CommandTest", bin2ContentValues(bin));
//			}
//		}
//	}

	private int currentX;
	private int currentY;
	
	/**
	 * 给图片加载数据
	 */
	private void test() {
		int alert_value = getIntent().getIntExtra(TemperAlertActivity1.SHARED_ALERT, 3750);
		 myView=new ChartView2(this, alert_value);
		 myView.SetInfo(
			        new String[]{"3500","3600","3700","3800","3900","4000", "4100"},   //Y轴刻度
			       list,  //数据
			        ""
			);
		 
		 //这个界面只画了一个  Y轴， 静止不动的
		 yView=new ChartView2Y(this, 1);
		 yView.SetInfo(
			        new String[]{"3500","3600","3700","3800","3900","4000", "4100"},   //Y轴刻度
			       list,  //数据
			        ""
			);
		myView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
			          { 
			              currentX = (int) event.getRawX(); 
			              //currentY = (int) event.getRawY(); 
			              break; 
			          }   
			          case MotionEvent.ACTION_MOVE:
			          { 
			              int x2 = (int) event.getRawX(); 
			              int y2 = (int) event.getRawY(); 
			              if(xMove+(currentX-x2)>=0) {
			            	  xMove += (currentX -x2);
			            	  v.scrollBy(currentX - x2 ,0); 
			            	  currentX = x2; 
			              }
			              if(xMove>80) {//滑动到左边时， 让Y周不滑动
			            	  yView.setVisibility(View.VISIBLE);
			              } else {
			            	  yView.setVisibility(View.INVISIBLE);
			              }
			              //currentY = y2;
			              break; 
			          }    
			          case MotionEvent.ACTION_UP:
			          { 
			              break; 
			          } 
				}  
				return true;
			}	
		});
	}
	
	public void btn_back(View v){
		finish();
	}

	public void btn_record(View v){
		Intent intent = new Intent(this,  HistoryRecord.class);
		intent.putExtra("datetime", datetime);
		startActivity(intent);
	}
	
}
