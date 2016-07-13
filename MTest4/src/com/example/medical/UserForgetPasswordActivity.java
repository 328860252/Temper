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

import org.json.JSONObject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.medical.manager.NetworkManager;
import com.medical.view.ToastNew;
import com.zby.medical.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>Description: 忘记密码 </p>
 * @author zhujiang
 * @date 2015-1-19
 */
public class UserForgetPasswordActivity extends Activity {
	
//	private NetworkManager mNetworkManager;
//	
//	private final int handler_modify_success = 100;
//	private final int handler_modify_fail = 101;
//	
//	private EditText et_password;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_forget_password);
//		
//		SMSSDK.initSDK(this, "52bfb962351e", "4eba0aa1957469fdc317e6899d9ca414");
//		et_password = (EditText) findViewById(R.id.editText_password);
//		mNetworkManager = new NetworkManager(this);
//	}
//	
//	
//	public  Handler mHandler = new Handler() {
//		public void handleMessage(Message msg) {
//			switch(msg.what) {
//			
//			case handler_modify_success:
//				Toast.makeText(UserForgetPasswordActivity.this, R.string.modify_success, 3).show();
//				break;
//			case handler_modify_fail:
//				Toast.makeText(UserForgetPasswordActivity.this, R.string.modify_fail, 3).show();
//				break;
//			}
//		}
//	};
//	
//	public void btn_back(View v) {
//		finish();
//	}
//	
//	public void btn_forgetPassword(View v) {
//		final String password = et_password.getText().toString();
//		if(password.length()<5) {
//			ToastNew.makeText(this, R.string.password_short).show();
//			et_password.requestFocus();
//			return;
//		}
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
//					new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							resetPasswordThread(country, phone, "code" ,password);
//						}
//					}).start();
//				}
//			}
//		});
//		registerPage.show(this);
//	}
//	
//	/**
//	 * @param country 电话区号
//	 * @param phone   手机
//	 * @param code   验证码
//	 * @param password 新密码
//	 */
//	private void resetPasswordThread(final String country,final String phone,final String code,final String password) {
//				// TODO Auto-generated method stub
//				JSONObject result = mNetworkManager.forgetPsd(country, phone, code, password);
//				if(mNetworkManager.isNetworkSuccess(result)) {
//					mHandler.sendEmptyMessage(handler_modify_success);
//					mHandler.postDelayed(new Runnable() {
//						
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							finish();
//						}
//					}, 1000);
//				} else {
//					mHandler.sendEmptyMessage(handler_modify_fail);
//				}
//				
//	}
	

}
