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
package com.example.medical;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.medical.constants.AppConstants;
import com.medical.constants.AppString;
import com.medical.help.SetupData;
import com.medical.help.SocketHelper;
import com.medical.manager.NetworkManager;
import com.medical.view.ToastNew;
import com.zby.medical.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * <p>Description: ������� </p>
 * @author zhujiang
 * @date 2015-1-12
 */
public class UserLoginActivity extends Activity {
	
	private EditText et_account, et_password;
	private CheckBox cb_save;
	private SetupData mSetupData;
	private NetworkManager mNetworkManager;
	
	private  final int handler_login_fail = 11;
	private final int  handler_login_success = 12;
	private final int handler_modify_success = 13;
	private final int handler_loginThread = 14;
	
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String str;
			switch(msg.what) {
			case handler_login_fail:
				str = (String) msg.obj;
				Toast.makeText(UserLoginActivity.this, str, 3).show();
				break;
			case handler_login_success:
				Toast.makeText(UserLoginActivity.this, R.string.login_success, 3).show();
				break;
			case handler_modify_success:
				Toast.makeText(UserLoginActivity.this, R.string.login_success, 3).show();
				break;
			case handler_loginThread:
				loginThread();
				break;
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initViews();
		if(mNetworkManager == null) {
			mNetworkManager = new NetworkManager(this);
		}
		if(mSetupData.readBoolean(AppString.isFirst, true)) {
			Intent itnent = new Intent(this, SettingGuideActivity.class);
			startActivity(itnent);
			mSetupData.saveboolean(AppString.isFirst, false);
			return;
		}
		
		if(SocketHelper.isnetworkConnected(this)) {
			if(mSetupData.readBoolean(AppString.isLogin, false)) {
				if(cb_save.isChecked()) {//��ס���룬 �˲Ż��Զ�����
					mHandler.sendEmptyMessage(handler_loginThread);
				}
			}
		} else {
			Intent intent;
			 int currentapiVersion=android.os.Build.VERSION.SDK_INT;
	          if(AppConstants.isBle) {
	        	  intent = new Intent(UserLoginActivity.this, MainActivityBleActivity.class);
	        	  startActivity(intent);
	        	  Toast.makeText(this, R.string.offline, 3).show();
	          } else {
	        	  intent = new Intent(UserLoginActivity.this, MainActivity2.class);
	        	  ToastNew.makeText(this, "��֧��4.3����ϵͳ", 3).show();
			}
			finish();
		}
	}


	private void initViews() {
		et_account = (EditText) findViewById(R.id.editText_account);
		et_password = (EditText) findViewById(R.id.editText_password);
		cb_save = (CheckBox) findViewById(R.id.checkBox_passwordSave);
		
		mSetupData = SetupData.getSetupData(this);
		cb_save.setChecked(mSetupData.readBoolean(AppConstants.PASSWORD_SAVE, false));
		if(cb_save.isChecked()) {
			et_password.setText(mSetupData.read(AppString.password));
		}
		et_account.setText(mSetupData.read(AppString.account));
		cb_save.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				mSetupData.saveboolean(AppConstants.PASSWORD_SAVE, isChecked);
			}
		});
	}
	
	
	public void btn_login(View v) {
		loginThread();
	}
	
	public void btn_passwordForget(View v) {
		Intent intent = new Intent(this, UserForgetPasswordActivity2.class) ;
		startActivity(intent);
	}
	
	public void btn_register(View v) {
		Intent intent = new Intent(UserLoginActivity.this, UserRegisterActivity2.class);
		startActivity(intent);
	}
	
	private void loginThread() {
		final String account  = et_account.getText().toString();
		final String password = et_password.getText().toString();
		if(account==null || account.equals("")) {
			ToastNew.makeText(this, R.string.account_empty, 3).show();
			et_account.requestFocus();
			return ;
		}
		if(password ==null || password.length()<5) {
			ToastNew.makeText(this, R.string.password_short, 3).show();
			et_password.requestFocus();
			return ;
		}
		//if(cb_save.isChecked()) {
			mSetupData.save(AppString.account, account);
			mSetupData.save(AppString.password, password);
		//}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				login(account, password);
			}
		}).start();
	}
	
	
	private void login(String account ,String password) {
		JSONObject result = mNetworkManager.userLogin(account, password);
		if(mNetworkManager.isNetworkSuccess(result)) {
			mSetupData.saveboolean(AppString.isLogin, true);
			AppConstants.isLineMode = true;
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Intent intent ;
					 int currentapiVersion=android.os.Build.VERSION.SDK_INT;
			          if(currentapiVersion>=18 && AppConstants.isBle) {
			        	  intent = new Intent(UserLoginActivity.this, MainActivityBleActivity.class);
			        	  mHandler.sendEmptyMessage(handler_login_success);
			        	  startActivity(intent);
			          } else {
			        	  intent = new Intent(UserLoginActivity.this, MainActivity2.class);
			        	  ToastNew.makeText(UserLoginActivity.this, "��֧��4.3����ϵͳ", 3).show();
					}
					finish();
				}
			});
		} else {
			String str = getString(R.string.login_fail);
			if(result!=null) {
				try {
					str = result.getString(AppString.json_msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
				}
			} 
			Message msg = mHandler.obtainMessage();
			msg.what = handler_login_fail;
			msg.obj = str;
			mHandler.sendMessage(msg);
		}
	}
	
	

}
