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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>Description: copy ע������޸Ķ��� </p>
 * @author zhujiang
 * @date 2015-1-12
 */
public class UserForgetPasswordActivity2 extends Activity {
	
	private EditText  et_account, et_password, et_password2,  et_verification ;
	private Button btn_verificcation, btn_register;
	private NetworkManager mNetworkManager;
	
	private final int handler_register_success = 11;
	private final int handler_register_fail = 12;
	
	private final static int handler_downStart = 13;
	private final static int handler_downCount = 14;
	private final static int handler_downOver =15;
	private final static int handler_smsString = 16;
	private final static int handler_register_enable = 17;
	private final static int handler_smsFail = 18;
	private final static int handler_smsSuccess= 19;
	
	private final static int handler_modify_success = 100;
	private final static int handler_modify_fail = 101;
	
	
	
	private String Account;
	private String Password;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			String str;
			switch(msg.what) {
				case handler_register_success:
					Toast.makeText(UserForgetPasswordActivity2.this, R.string.register_success, 3).show();
					break;
				case handler_register_fail:
					 str = (String) msg.obj;
					Toast.makeText(UserForgetPasswordActivity2.this, str, 3).show();
					break;
				case handler_downStart:
					//��ʼ����ʱ��
					btn_verificcation.setText("60s");
					btn_verificcation.setEnabled(false);
					btn_verificcation.setVisibility(View.VISIBLE);
					break;
				case handler_downCount:
					btn_verificcation.setText(msg.arg1+" s");
					break;
				case handler_downOver:
					isDownCount = false;
					btn_verificcation.setText(R.string.verification_get);
					btn_verificcation.setEnabled(true);
					btn_verificcation.setVisibility(View.VISIBLE);
					break;
				case handler_smsString:
					 str = (String) msg.obj;
					Toast.makeText(UserForgetPasswordActivity2.this, str, 3).show();
					isChecked = false;
					et_verification.setText("");
					et_verification.requestFocus();
					btn_register.setEnabled(true);
					btn_register.setText(R.string.register_check);
					btn_register.setVisibility(View.VISIBLE);
					break;
				case handler_register_enable:
					btn_register.setEnabled(true);
					btn_register.setVisibility(View.VISIBLE);
					break;
				case handler_smsFail:
					Toast.makeText(UserForgetPasswordActivity2.this, R.string.verification_error, 3).show();
					isChecked = false;
					et_verification.setText("");
					et_verification.requestFocus();
					btn_register.setEnabled(true);
					btn_register.setVisibility(View.VISIBLE);
					break;
				case handler_smsSuccess:
					Toast.makeText(UserForgetPasswordActivity2.this, R.string.verification_success, 3).show();
					break;
				case handler_modify_success:
					Toast.makeText(UserForgetPasswordActivity2.this, R.string.modify_success, 3).show();
					break;
				case handler_modify_fail:
					Toast.makeText(UserForgetPasswordActivity2.this, R.string.modify_fail, 3).show();
					break;
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forget_password2);
		initViews();
		mNetworkManager = new NetworkManager(this);
		SMSSDK.initSDK(this, AppConstants.APP_SMS_KEY, AppConstants.APP_SMS_SECRET);
	}
	
	
	private void initViews() {
		et_account = (EditText) findViewById(R.id.editText_account);
		et_password = (EditText) findViewById(R.id.editText_password);
		et_password2 = (EditText) findViewById(R.id.editText_passwordConfirm);
		et_verification = (EditText) findViewById(R.id.editText_verification);
		btn_verificcation = (Button) findViewById(R.id.btn_verification);
		btn_register = (Button) findViewById(R.id.button_register);
		
		//����ֻ�ŵ�ֵ�����б䶯�� ��Ҫ����ע��
		et_account.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				isChecked = false;
			}
		});
	}
	

	public void btn_forgetPassword(View v) {
		final String account = et_account.getText().toString();
		if(account==null || account.equals("")) {
			ToastNew.makeText(this, R.string.phone_error, 3).show();
			return;
		}
		final String password = et_password.getText().toString();
		if(password ==null || password.length()<5) {
			ToastNew.makeText(this, R.string.password_short, 3).show();
			et_password.requestFocus();
			return ;
		}
		String password2 = et_password2.getText().toString();
//		if(password2 ==null || password2.equals("")) {
//			ToastNew.makeText(this, R.string.password_empty, 3).show();
//			et_password2.requestFocus();
//			return ;
//		}
		
		if(!password.equals(password2)) {
			ToastNew.makeText(this, R.string.password_unlike, 3).show();
			et_password.setText("");
			et_password2.setText("");
			et_password.requestFocus();
			return;
		}
		
		
		this.Account = account;
		this.Password = password;
		String verivate = et_verification.getText().toString();
		if(verivate.length()<1) {
			ToastNew.makeText(this, R.string.verification_empty).show();
			et_verification.requestFocus();
			return;
		}
		if(isChecked) {//����Ѿ���֤�� ֻ����Ϣ��? �ֻ��û�����Ͳ���Ҫ�ٶ�����֤
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					resetPasswordThread("86", Account, "code" ,Password);
				}
			}).start();
		} else {
			SMSSDK.submitVerificationCode("86", account, verivate);
			btn_register.setEnabled(false);
			btn_register.setVisibility(View.GONE);
		}
	}
	
	
	public void btn_back(View v) {
		finish();
	}
	
	
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		SMSSDK.registerEventHandler(eh);
		super.onStart();
	}


	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		SMSSDK.unregisterEventHandler(eh);
		super.onStop();
	}


	/**
	 * @param country �绰���
	 * @param phone   �ֻ�
	 * @param code   ��֤��
	 * @param password ������
	 */
	private void resetPasswordThread(final String country,final String phone,final String code,final String password) {
				// TODO Auto-generated method stub
				JSONObject result = mNetworkManager.forgetPsd(country, phone, code, password);
				if(mNetworkManager.isNetworkSuccess(result)) {
					mHandler.sendEmptyMessage(handler_modify_success);
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							finish();
						}
					}, 1000);
				} else {
					mHandler.sendEmptyMessage(handler_modify_fail);
				}
				
	}
	
	private boolean isDownCount = true;
	private boolean isChecked = false;
	
	public void btn_verification(View v) {
		//��ע��ҳ��
		final String account = et_account.getText().toString();
		if(account==null || account.equals("")) {
			ToastNew.makeText(this, R.string.phone_error, 3).show();
			return;
		}
		if(isPhoneNumberValid(account)) {
			SMSSDK.getVerificationCode("86", account.trim());
			isDownCount = true;
			btn_verificcation.setEnabled(false);
			btn_verificcation.setVisibility(View.INVISIBLE);
		} else {
			ToastNew.makeText(this, R.string.phone_error, 3).show();
		}
	}
	
	private Thread downCountThread;
	private void startDownCountThread() {
		downCountThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg;
				for(int i=60; i>0; i--) {
					msg = mHandler.obtainMessage();
					msg.what = handler_downCount;
					msg.arg1 = i;
					mHandler.sendMessage(msg);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
				mHandler.sendEmptyMessage(handler_downOver);
			}
		});
		downCountThread.start();
	}
	
	
	
	//@Title: isPhoneNumberValid
	//@Description: ��֤���� �ֻ�� �̻����
	//@author qinyl
	//@date 2014��6��20�� ����3:16:03
	//@param @param phoneNumber
	//@param @return �趨�ļ�
	//@return boolean ��������
	//@throws
	public static boolean isPhoneNumberValid(String phoneNumber) {
		boolean isValid = false;
		String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}d{1}-?d{8}$)|"
				+ "(^0[3-9] {1}d{2}-?d{7,8}$)|"
								+ "(^0[1,2]{1}d{1}-?d{8}-(d{1,4})$)|"
				+ "(^0[3-9]{1}d{2}-? d{7,8}-(d{1,4})$))";
		CharSequence inputStr = phoneNumber;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}
	
	EventHandler eh=new EventHandler(){

		@Override
		public void afterEvent(int event, int result, Object data) {

		   if (result == SMSSDK.RESULT_COMPLETE) {
			   //�ص����
				if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					mHandler.sendEmptyMessage(handler_register_enable);
					//�ύ��֤��ɹ�
					isChecked = true;
					HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
					final String country = (String) phoneMap.get("country");
					final String phone = (String) phoneMap.get("phone"); 
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							resetPasswordThread(country, phone, "code" ,Password);
						}
					}).start();
				}else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
					mHandler.sendEmptyMessage(handler_downStart);
					//��ȡ��֤��ɹ�
					startDownCountThread();
				}else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
					//����֧�ַ�����֤��Ĺ���б�
					
	            } 
          }else{               
        	  Object exception = data;
//        	  mHandler.sendEmptyMessage(handler_register_enable);
//        	  HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//        	  String strError = (String) phoneMap.get("description");
//        	  Message msg= mHandler.obtainMessage();
//        	  msg.what = handler_smsString;
//        	  msg.obj = strError;
//        	  mHandler.sendMessage(msg);
        	  mHandler.sendEmptyMessage(handler_smsFail);
             ((Throwable)exception).printStackTrace(); 
      }
  } 
	}; 

	

}
