package com.example.medical;


import com.medical.constants.AppConstants;
import com.medical.help.SetupData;
import com.medical.view.VerticalSeekBar;
import com.medical.view.ToastNew;
import com.zby.medical.R;

import android.app.Activity;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class TemperAlertActivityTest extends Activity {
	
	public static final String SHARED_ALERT ="alert_number";
	
	private SetupData mSetupData;
	private TextView tv_alert;
	//private VerticalSeekBar sb_bar;
	private SeekBar sb_bar;
	private int alertTemper;
	private ImageView iv_value;//�¶ȼƵ���ʾ��
	private RelativeLayout layout_thermometer2;//�̶�����
	
	private static final int handler_setValue =11;
	private static final int handler_initView = 12;
	
	private float x = 0;//0.1�̶� ռ���ٸ߶� ��ϵ���� 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.temper_alert_test);
		initViews();
		initDatas();
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case handler_setValue:
				sb_bar.setProgress(alertTemper);
				//setAlert(msg.arg1);
				break;
			case handler_initView://
				x = ((layout_thermometer2.getHeight()) / 600.0f);
				setAlert(msg.arg1);
				break;
			}
		}
	};
	
	private void initViews(){
		tv_alert = (TextView) findViewById(R.id.textView_progress);
		//iv_value = (ImageView) findViewById(R.id.iv_value);
		//layout_thermometer2 = (RelativeLayout) findViewById(R.id.layout_thermometer2);
		//sb_bar = (VerticalSeekBar) findViewById(R.id.seekBar_progress);
		sb_bar = (SeekBar) findViewById(R.id.seekBar_test);
		sb_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				alertTemper = progress;
				if(progress==10) {
					tv_alert.setText("1");
				} else {
					tv_alert.setText("0."+progress);
				}
			}
		});
	}
	
	private void initDatas() {
//		mSetupData = SetupData.getSetupData(this);
//		alertTemper = mSetupData.readInt(SHARED_ALERT, 3750);
//		if(alertTemper<3500 || alertTemper > 4100) {
//			alertTemper = 3750;
//			mSetupData.saveInt(SHARED_ALERT, alertTemper);
//		}
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				int height = 0;
//				while (height == 0) {
//					if(layout_thermometer2 != null){
//						height = layout_thermometer2.getHeight();
//					}
//				}
//				System.out.println("height: "+ height);
//				Message msg = handler.obtainMessage();
//				msg.what = handler_initView;
//				msg.arg1 = alertTemper;
//				handler.sendMessage(msg);
//			}
//		}).start();
//		sb_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//			
//			@Override
//			public void onStopTrackingTouch(VerticalSeekBar VerticalSeekBar) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onStartTrackingTouch(VerticalSeekBar VerticalSeekBar) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onProgressChanged(VerticalSeekBar VerticalSeekBar,
//					int progress, boolean fromUser) {
//				// TODO Auto-generated method stub
//				//��3500��ʼ��  4100���� ��  1�̶�=6
//				//�ɶ��� �Ǵ������£�   �����ǵĿ̶�ֵ �� �Ǵ������ϣ����Խ�  �̶���ȡ��,  
//				int value = (100-progress)*6; 
//				//�ȳ�10 ���ڳ�10�� ��Ϊ�� ��ȷ��ʮλ,����λ������
//				alertTemper = 3500+ value/10 * 10;
//				setAlert(alertTemper);
//			}
//		});
	}
	
	public void btn_back(View v) {
		finish();
	}
	
	public void btn_confirm(View v) {
//		if(MainActivity.mConnectInterface!=null && MainActivity.mConnectInterface.isLink()) {
//			byte temper = (byte) ((alertTemper-AppConstants.temper_min)/10);
//			MainActivity.mConnectInterface.write(new byte[] {(byte)0x91, (byte)0x00, (byte)0x00, (byte)0x00,  temper, (byte)0x00});
//			mSetupData.saveInt(SHARED_ALERT, alertTemper);
//			finish();
//		} else {
//			ToastNew.makeTextMid(this, R.string.nolink, 3).show();
//		}
		if(MainActivityBleActivity.mConnService!=null && MainActivityBleActivity.mConnService.isLink()) {
			byte temper = (byte) ((alertTemper-AppConstants.temper_min)/10);
			MainActivityBleActivity.mConnService.write(new byte[] {(byte)0x91, (byte)0xFF, (byte)0xFF, (byte)0xFF,  temper, (byte)0xFF});
			mSetupData.saveInt(SHARED_ALERT, alertTemper);
			finish();
		} else {
			ToastNew.makeTextMid(this, R.string.nolink, 3).show();
		}
	}
	
	public void btn_add(View v) {
		//if(alertTemper>= 4100) return;
		alertTemper +=1;
		Message msg = handler.obtainMessage();
		msg.what = handler_setValue;
		msg.arg1 = alertTemper;
		handler.sendMessage(msg);
	}
	
	public void btn_reduce(View v) {
		//if(alertTemper<= 3500) return;
		alertTemper -=1;
		Message msg = handler.obtainMessage();
		msg.what = handler_setValue;
		msg.arg1 = alertTemper;
		handler.sendMessage(msg);
	}
	
		LayoutParams para;
		private void setAlert(int value) {
			tv_alert.setText(""+ value /100.0);
			  para= iv_value.getLayoutParams();  
			        
			  Log.d("TemperAlertActivity", "layout height0: " + para.height);  
			  //����ռ䳤�ȣ�  37.0��  ��
			 para.height = (int)((value-3500)*x);  
			 iv_value.setLayoutParams(para);  
			 Log.d("TemperAlertActivity", "layout height: " + para.height);  
	
		}
		public void btn_reducetest(View v) {
			if(MainActivityBleActivity.mConnService!=null && MainActivityBleActivity.mConnService.isLink()) {
				//byte temper = (byte) ((alertTemper-AppConstants.temper_min)/10);
				MainActivityBleActivity.mConnService.write(new byte[] {(byte)0xaa, (byte)0x5a, (byte)0xa5, (byte)0x25,  (byte)0x00,(byte) alertTemper});
				//mSetupData.saveInt(SHARED_ALERT, alertTemper);
				//finish();
			} else {
				ToastNew.makeTextMid(this, R.string.nolink, 3).show();
			}
		}
		
		public void btn_addtest(View v){
			if(MainActivityBleActivity.mConnService!=null && MainActivityBleActivity.mConnService.isLink()) {
				//byte temper = (byte) ((alertTemper-AppConstants.temper_min)/10);
				MainActivityBleActivity.mConnService.write(new byte[] {(byte)0xaa, (byte)0x5a, (byte)0xa5, (byte)0x25,  (byte)0x01,(byte) alertTemper});
				//mSetupData.saveInt(SHARED_ALERT, alertTemper);
				//finish();
			} else {
				ToastNew.makeTextMid(this, R.string.nolink, 3).show();
			}
		}
}
