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

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.medical.constants.AppConstants;
import com.medical.constants.AppString;
import com.medical.manager.NetworkManager;
import com.medical.view.ToastNew;
import com.zby.medical.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>Description: 注册用户界面 </p>
 * @author zhujiang
 * @date 2015-1-12
 * 系统注册页面
 */
public class UserRegisterActivity extends Activity {
	
//	private EditText et_sn, et_account, et_password, et_password2, et_phone, et_verification , et_username;
//	private TextView tv_countDown;
//	private Button btn_verificcation;
//	private NetworkManager mNetworkManager;
//	
//	private final int handler_register_success = 11;
//	private final int handler_register_fail = 12;
//	
//	private Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch(msg.what) {
//				case handler_register_success:
//					Toast.makeText(UserRegisterActivity.this, R.string.register_success, 3).show();
//					break;
//				case handler_register_fail:
//					String str = (String) msg.obj;
//					Toast.makeText(UserRegisterActivity.this, str, 3).show();
//					break;
//			}
//		}
//	};
//	
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_register);
//		initViews();
//		mNetworkManager = new NetworkManager(this);
//		SMSSDK.initSDK(this, "52bfb962351e", "4eba0aa1957469fdc317e6899d9ca414");
//	}
//	
//	
//	private void initViews() {
//		et_sn = (EditText) findViewById(R.id.editText_sn);
//		et_account = (EditText) findViewById(R.id.editText_account);
//		et_password = (EditText) findViewById(R.id.editText_password);
//		et_password2 = (EditText) findViewById(R.id.editText_passwordConfirm);
//		et_username = (EditText) findViewById(R.id.editText_username);
//		et_phone = (EditText) findViewById(R.id.editText_phone);
//		et_verification = (EditText) findViewById(R.id.editText_verification);
//		tv_countDown = (TextView) findViewById(R.id.textView_downcount);
//		btn_verificcation = (Button) findViewById(R.id.btn_verification);
//	}
//	
//
//	public void btn_register(View v) {
//		final String sn = et_sn.getText().toString();
//		if(sn ==null || sn.equals("")) {
//			ToastNew.makeText(this, R.string.sn_empty, 3).show();
//			et_sn.requestFocus();
//			return;
//		}
////		final String account = et_account.getText().toString();
////		if(account==null || account.equals("")) {
////			ToastNew.makeText(this, R.string.phone_error, 3).show();
////			return;
////		}
//		final String password = et_password.getText().toString();
//		if(password ==null || password.length()<5) {
//			ToastNew.makeText(this, R.string.password_short, 3).show();
//			et_password.requestFocus();
//			return ;
//		}
//		String password2 = et_password2.getText().toString();
//		if(password2 ==null || password2.equals("")) {
//			ToastNew.makeText(this, R.string.password_empty, 3).show();
//			et_password2.requestFocus();
//			return ;
//		}
//		
//		if(!password.equals(password2)) {
//			ToastNew.makeText(this, R.string.password_unlike, 3).show();
//			et_password.setText("");
//			et_password2.setText("");
//			et_password.requestFocus();
//			return;
//		}
//		
////		String phone = et_phone.getText().toString();
////		if(phone ==null || phone.equals("")) {
////			ToastNew.makeText(this, R.string.phone_error, 3).show();
////			et_phone.requestFocus();
////			return ;
////		}
//		final String realName = et_username.getText().toString();
//		if(realName ==null || realName.equals("")) {
//			ToastNew.makeText(this, R.string.name_empty, 3).show();
//			et_username.requestFocus();
//			return;
//		}
//		//----------------测试----------
////		new Thread(new Runnable() {
////			
////			@Override
////			public void run() {
////				// TODO Auto-generated method stub
////				registerUser("86", "13802581254",  password, realName, sn);
////			}
////		}).start();
//		//---------------------------------
//		RegisterPage registerPage = new RegisterPage();
//		registerPage.setRegisterCallback(new EventHandler() {
//			public void afterEvent(int event, int result, Object data) {
//			// 解析注册结果
//				if (result == SMSSDK.RESULT_COMPLETE) {
//					@SuppressWarnings("unchecked")
//					HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//					final String country = (String) phoneMap.get("country");
//					final String phone = (String) phoneMap.get("phone"); 
//					//将校验后的手机号  填写到输入框
//					et_account.setText(phone);
//					new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							registerUser(country, phone,  password, realName, sn);
//						}
//					}).start();
//			
//						// 提交用户信息
//					
//				}
//			}
//		});
//		registerPage.show(this);
//	}
//	
////	public void btn_verification(View v) {
////		//打开注册页面
////		RegisterPage registerPage = new RegisterPage();
////		registerPage.setRegisterCallback(new EventHandler() {
////			public void afterEvent(int event, int result, Object data) {
////			// 解析注册结果
////				if (result == SMSSDK.RESULT_COMPLETE) {
////					@SuppressWarnings("unchecked")
////					HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
////					String country = (String) phoneMap.get("country");
////					String phone = (String) phoneMap.get("phone"); 
////			
////						// 提交用户信息
////					registerUser(country, phone);
////					
////				}
////			}
////		});
////		registerPage.show(this);
////	}
//	
//	public void btn_back(View v) {
//		finish();
//	}
//	
//	
//	private void registerUser(String conntry, String phone, String password, String name, String sn){
//		JSONObject json = new JSONObject();
////			try {
////				json.put("smsRegisterParams.phone", phone);
////				json.put("smsRegisterParams.zone", conntry);
////				json.put("smsRegisterParams.code", "123");
////				json.put("smsRegisterParams.sn", sn);
////				json.put("smsRegisterParams.realName", name);
////				json.put("smsRegisterParams.password", password);
////				json.put("smsRegisterParams.autoLogin", false);
////				json.put("smsRegisterParams.mobile", android.os.Build.MANUFACTURER +" " + android.os.Build.DEVICE);
////				json.put("smsRegisterParams.mobileSystem", android.os.Build.VERSION.RELEASE);//手机版本
////				json.put("smsRegisterParams.deviceToken", AppConstants.phone_mac);
////			} catch (JSONException e1) {
////				// TODO Auto-generated catch block
////				e1.printStackTrace();
////			}
////			JSONObject result = mNetworkManager.userRegister(json);
//			JSONObject result = mNetworkManager.userRegister(phone, conntry, "123", sn, name, password, false);
//			if(mNetworkManager.isNetworkSuccess(result)) {
//				mHandler.sendEmptyMessage(handler_register_success);
//			} else {
//				String str = getString(R.string.register_fail);
//				if(result!=null) {
//					try {
//						str = result.getString(AppString.json_msg);
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//					}
//				} 
//				Message msg = mHandler.obtainMessage();
//				msg.what = handler_register_fail;
//				msg.obj = str;
//				mHandler.sendMessage(msg);
//			}
//	}
//	
//	
	

}
