package com.medical.manager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zby.medical.R;
import com.medical.constants.AppConstants;
import com.medical.constants.AppString;
import com.medical.help.SetupData;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class NetworkManager {
	
	public static String TAG ="network";
	
	
	private static final String http_post_key = "data";
	
	/**
	 * �ļ���
	 */
	private static final String  folder = "Medical";
	
	/**
	 * �汾��Ϣ
	 */
	private  JSONObject versionInfo = null;
	
	private Builder builder;
	// ���ؽ�����
		private ProgressBar progressBar;
		// �Ƿ���ֹ����
		private boolean isInterceptDownload = false;
		// ��������ʾ��ֵ
		private int progress = 0;
	
	private int timeout = 25000;
	private Context context;
	private String  charSet = "utf-8";
	private Dialog progressDialog;
	
	private SetupData mSetupData;
	
	public NetworkManager(Context context) {
		this.context = context;
		progressDialog =AlertDialogService.getWait(context, R.string.please_wait);
		progressDialog.dismiss();
		mSetupData = SetupData.getSetupData(context);
	}
	
	
	
	Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case 1:
				//Toast.makeText(context, "���ӳ�ʱ", Toast.LENGTH_LONG).show();
				if(progressDialog!=null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				break;
			case 2:
				if(progressDialog!=null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				break;
			case 3:
				progressDialog = AlertDialogService.getWait(context, context.getResources().getString((Integer)(msg.obj)));
				if(progressDialog!=null && !progressDialog.isShowing()){
					progressDialog.show();
				}
				break;
			case 4:
				//Toast.makeText(context, "���ӳ�ʱ��������Ч������", Toast.LENGTH_LONG).show();
				if(progressDialog!=null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				break;
			case 5:
				progressDialog = AlertDialogService.getWait(context, ((String) msg.obj));
				if(progressDialog!=null && !progressDialog.isShowing()){
					progressDialog.show();
				}
				break;
			case 6: //��ʾ���ش���
				showUpdateDialog(versionInfo);
				break;
			case 7: //�Ѿ���װ���
				showAlreadyDialog();
				break;
			case 8: // ��װapk�ļ�
				progressBar.setVisibility(View.INVISIBLE);
				installApk();
				break;
			case 9: // ���½������
				progressBar.setProgress(progress);
				break;
			case 10://����
				if(checkDialog!=null && checkDialog.isShowing()) {
					checkDialog.dismiss();
				}
				Toast.makeText(context, R.string.version_update_error, 3).show();
			}
		}
	};
	

	    AlertDialog checkDialog ;
	    
		
	    
	    
	    
	    
	    /**
		 * ����
		 * @param account 
		 * @param password
		 * @return  
		 * 			retv	Integer	״̬: 0��ʾ�ɹ�  ������ʾʧ��,�����������Ϣ
					msg	String	״̬˵��
					dataType	Integer	������������˵�� 0Ϊ�ֵ�/��;1Ϊ����
					data		���صľ�������,��������������ӿ���ϸ�ĵ�
		 *  	map{"name":  ""}
		 *           ����ʧ�ܵĻ�   map{"errorCode":   , ��errorMessage���� ����  } 
		 *           ��ʱ ���� null
		 */
		public JSONObject userLogin(String account, String password) {
			
			Message msg = new Message();
			msg.what = 3;
			msg.obj = R.string.please_wait;			
			handler.sendMessage(msg);
			
			JSONObject obj = null;
			try {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("loginParams.username", account));
				nameValuePairs.add(new BasicNameValuePair("loginParams.password", password));
				nameValuePairs.add(new BasicNameValuePair("loginParams.mobile", ""+android.os.Build.MANUFACTURER +" " + android.os.Build.DEVICE));
				nameValuePairs.add(new BasicNameValuePair("loginParams.mobileSystem", ""+android.os.Build.VERSION.RELEASE));
				nameValuePairs.add(new BasicNameValuePair("loginParams.deviceToken", ""+AppConstants.phone_mac));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/base/login");
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
						handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
public JSONObject userLoginNoAlert(String account, String password) {
			JSONObject obj = null;
			try {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("loginParams.username", account));
				nameValuePairs.add(new BasicNameValuePair("loginParams.password", password));
				nameValuePairs.add(new BasicNameValuePair("loginParams.mobile", ""+android.os.Build.MANUFACTURER +" " + android.os.Build.DEVICE));
				nameValuePairs.add(new BasicNameValuePair("loginParams.mobileSystem", ""+android.os.Build.VERSION.RELEASE));
				nameValuePairs.add(new BasicNameValuePair("loginParams.deviceToken", ""+AppConstants.phone_mac));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/base/login");
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
						handler.sendEmptyMessage(2);
			}
			return obj;
		}
	    
		/**
		 * ע����Ϣ
		 * @param account
		 * @param password
		 * @param name
		 * @param type  ע������0��ʹ���ֻ�Ψһ���Զ�ע�ᣬ���    1��ʹ������Ϊ�˺�ע�ᣬҪ�ʼ�ȷ�ϼ���
		 * @return  map{"id":""}
		 * 			ע��ʧ��  map{"errorCode":    ,  "errorMessage":  }
		 */
		public JSONObject userRegister(String phone, String zone, String code, String sn, String realName, String password,boolean isAuth) {
			
			Message msg = new Message();
			msg.what = 3;
			msg.obj = R.string.please_wait;			
			handler.sendMessage(msg);
			JSONObject obj = null;
			try {
//				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams", json.toString()));
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.phone", phone));
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.zone", zone));
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.code", code));
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.sn", sn));
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.realName", realName));
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.password", password));
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.autoLogin", "false"));
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.mobile", android.os.Build.MANUFACTURER +" " + android.os.Build.DEVICE));
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.mobileSystem", android.os.Build.VERSION.RELEASE));//�ֻ��汾
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.deviceToken", AppConstants.phone_mac));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/base/smsRegister");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
	

		/**
		 * @param country
		 * @param phone
		 * @param code
		 * @param password
		 * @return
		 */
		public JSONObject forgetPsd(String country, String phone, String code,
				String password) {
			Message msg = new Message();
			msg.what = 3;
			msg.obj = R.string.please_wait;			
			handler.sendMessage(msg);
			
			JSONObject obj = null;
			try {
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("resetPasswordParams.phone", phone));
				nameValuePairs.add(new BasicNameValuePair("resetPasswordParams.zone", country));
				nameValuePairs.add(new BasicNameValuePair("resetPasswordParams.code", code));
				nameValuePairs.add(new BasicNameValuePair("resetPasswordParams.password", password));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/base/resetPassword");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
		/**
		 * @param country
		 * @param phone
		 * @param code
		 * @param password
		 * @return
		 */
		public JSONObject modifyPsd( String phone, String oldpassword,
				String newPassword) {
			Message msg = new Message();
			msg.what = 3;
			msg.obj = R.string.please_wait;			
			handler.sendMessage(msg);
			
			JSONObject obj = null;
			try {
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("modifyPasswordParams.phone", phone));
				nameValuePairs.add(new BasicNameValuePair("modifyPasswordParams.oldPassword", oldpassword));
				nameValuePairs.add(new BasicNameValuePair("modifyPasswordParams.newPassword", newPassword));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/user/modifyPassword");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
		
		/**
		 * ���汾����, ����ᵯ����ʾ�Ƿ����أ� ���Բ���Ҫ�ٶ����result�������ˡ�
		 * @param clientType  �ͻ������� IOS/ANDROID
		 * @param versionCode �ͻ��˰汾��
		 * 
		 * @return	isUpdate	Boolean	�Ƿ��и���
					isForce	Boolean	�Ƿ���ǿ�Ƹ���
					versionCode	Integer	��ǰ�汾��
					versionName	String	��ǰ�汾
					versionExplain	String	�汾˵��
					updateUrl	String	�汾��ַ
					updateDateTime	String	����ʱ��
		 */
		public JSONObject queryVersion( int versionCode) {
//			Message msg = new Message();
//			msg.what = 3;
//			msg.obj = R.string.please_wait;			
//			handler.sendMessage(msg);
			
			JSONObject obj = null;
			try {
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("appVersionParams.clientType", "ANDROID"));
				nameValuePairs.add(new BasicNameValuePair("appVersionParams.versionCode", ""+versionCode));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/more/checkAppVersion");
				if(isNetworkSuccess(obj)) {
					JSONObject data = obj.getJSONObject("data");
					int newCode = data.getInt("versionCode");
					if(newCode>versionCode) {
						String name = data.getString("versionName");
						String message = data.getString("versionExplain");
						String url = data.getString("updateUrl");
						versionInfo = new JSONObject();
						//"http://dd.myapp.com/16891/F52D341007922F48D80F967D9576A082.apk?fsname=com.example.medical_3.22_42.apk&asr=8eff
						versionInfo.put("downloadURL", url);
						versionInfo.put("apkName", name);
						versionInfo.put("message", message);
						handler.sendEmptyMessage(6);
					}
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
		/**
		 * ˢ�µ�ǰ״̬
		 * @param phone
		 * @return  temperature			Double	�¶�
					voltage				Double	��ѹ
					alarmTemperature	Double	�����¶�
					bluetoothIsConnect	Short	����״̬-�Ƿ�����
					deviceState			Short	�豸״̬
					alarmState			Short	����״̬
					dataTime			String	����ʱ��
		 */
		public JSONObject queryStatus( String phone) {
//			Message msg = new Message();
//			msg.what = 3;
//			msg.obj = R.string.please_wait;			
//			handler.sendMessage(msg);
			
			JSONObject obj = null;
			try {
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("phone", phone));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/data/refresh");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
		
		
		
		/**
		 * ͬ������
		 * @param 	dataParams.temperature	�¶�	Double	��
					dataParams.voltage	��ѹ	Double	��
					dataParams.alarmTemperature	�����¶�	Double	��
					dataParams.bluetoothIsConnect	����״̬-�Ƿ�����	Short	��
					dataParams.deviceState	�豸״̬	Short	��
					dataParams.alarmState	����״̬	Short	��
					dataParams.phone	�绰����	String	��
		 *			
		 * @return	temperature	�¶�	Double	��	
					dataTime	ʱ��	String	��	yyyy-MM-dd HH:mm:ss
		 */
		public JSONObject sync( String phone, String temperture, String voltage, String alarmTemperature, String bluetoothIsConnect,
				String deviceState, String alarmState ) {
//			Message msg = new Message();
//			msg.what = 3;
//			msg.obj = R.string.please_wait;			
//			handler.sendMessage(msg);
			
			JSONObject obj = null;
			try {
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("dataParams.phone", phone));
				nameValuePairs.add(new BasicNameValuePair("dataParams.temperature", temperture));
				nameValuePairs.add(new BasicNameValuePair("dataParams.voltage", voltage));
				nameValuePairs.add(new BasicNameValuePair("dataParams.alarmTemperature", alarmTemperature));
				nameValuePairs.add(new BasicNameValuePair("dataParams.bluetoothIsConnect", bluetoothIsConnect));
				nameValuePairs.add(new BasicNameValuePair("dataParams.deviceState", deviceState));
				nameValuePairs.add(new BasicNameValuePair("dataParams.alarmState", alarmState));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/data/sync");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
		
		
		/**
		 * ��ѯ���
		 * @param phone
		 * @return
		 * 	logo	Boolean	ͼ��
			name	Boolean	����
			explain	Integer	���
			state	String	��Ϣ���� 0-�ı� 1-ͼƬ 2-��ַ 3-��Ƶ
			content	String	����
		 */
		public JSONObject queryAds(String phone)  {
			JSONObject obj = null;
			try {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				//nameValuePairs.add(new BasicNameValuePair("phone", phone));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/more/getSplashAd");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
		/**
		 * ��ȡ�¶Ⱦ�����Ϣ
		 * @param phone
		 * @return
		 */
		public JSONObject queryPushWarm(String phone)  {
			JSONObject obj = null;
			try {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				//nameValuePairs.add(new BasicNameValuePair("phone", phone));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/user/pushWarn");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
		/**
		 * ��ѯ������Ϣ
		 * @param phone
		 * @return
		 * 	logo	Boolean	ͼ��
			name	Boolean	����
			explain	Integer	���
			state	String	��Ϣ���� 0-�ı� 1-ͼƬ 2-��ַ 3-��Ƶ
			content	String	����
		 */
		public JSONObject queryRecord(String phone)  {
			JSONObject obj = null;
			try {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("phone", phone));
				obj = httpPostByJsonArray(nameValuePairs, AppConstants.serverAction+"api/data/downrecord");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
		
		/**
		 * ��ȡ�豸��location
		 * @param phone
		 * @return
		 * 	deviceNum	String	�豸�ն˺�
		 *  valid	String	������Чλ��A/V/B����A ��ʾGPS ��������Ч��λ���ݣ�V ��ʾGPS ��������Ч��λ����B ��ʾ����
		 *  longitude	Double	����
		 *  longitudeFlag	String	���ȱ�־��E������ W��������
		 *  latitude	Double	γ��
		 *  latitudeFlag	String	γ�ȱ�־��N:��γ��S����γ��
		 *  speed	Double	�ٶȣ���Χ000.00��999.99�ڣ�������λС����
		 *  direction	int	��λ�ǣ�����Ϊ0�ȣ��ֱ���1�ȣ�˳ʱ�뷽���ֶ�Ϊ�գ����Ƕ�Ϊ0
		 *  dateTime	String	ʱ��     ��/��/��
		 *  mcc	String	���ұ���
		 *  mnc	String	��Ӫ�̱���
		 *  lac	String	��վ���� С�����
		 *  cid	String	��վ���� С��ID
		 *  temperature	Double	�¶�
		 */
		public JSONObject queryDeviceLocationInfo(String phone) {
			JSONObject obj = null;
			try {
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("phone", phone));
				obj = httpPost(nameValuePairs, AppConstants.serverAction+"api/user/getDeviceLocationInfo");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} finally {
				handler.sendEmptyMessage(2);
			}
			return obj;
		}
		
	
		HttpClient httpclient = new DefaultHttpClient();
		
		private synchronized JSONObject httpPost(ArrayList<NameValuePair> nameValuePairs, String url) throws ClientProtocolException, IOException, JSONException {
//			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair(http_post_key, obj.toString()));
			Log.i(TAG,url+"���� http:"+ nameValuePairs.toString());
			JSONObject result = null ; 
			try {
				//����ʱ	
				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout); 
				//��ȡ��ʱ
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
				HttpPost httppost = new HttpPost(url);
				if (nameValuePairs != null) {
					// �����ַ���
					HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,charSet);
					// ���ò���ʵ��
					httppost.setEntity(entity);
				}
				setRequestCookies(httppost);
				int res = 0;
				//��ʼ����
				HttpResponse response = httpclient.execute(httppost);
				appendCookies(response);
				res = response.getStatusLine().getStatusCode();
				Log.v(TAG,"�յ�����״̬ "+ res );
				if (res == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader2 = new BufferedReader(
							new InputStreamReader(response.getEntity().getContent()));
					for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
						builder.append(s);
					}
					if(builder.toString() != null) {
						result = new JSONObject(builder.toString());
					}
					Log.v(TAG,"�յ����� "+ builder.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(1);
			}
			return result;
		}
		
		/**
		 * ���������Cookieͷ��Ϣ
		 * @param reqMsg
		 */
		private void setRequestCookies(HttpMessage reqMsg) {
			String cookies = mSetupData.read("mycookies");
			if(!TextUtils.isEmpty(cookies)){
				reqMsg.setHeader("Cookie", cookies);
			}
		}
		/**
		 * ���µ�Cookieͷ��Ϣ���ӵ��ɵ�Cookie����
		 * �����´�Http������
		 * @param resMsg
		 */
		private void appendCookies(HttpMessage resMsg) {
			Header setCookieHeader=resMsg.getFirstHeader("Set-Cookie");
			if (setCookieHeader != null && !TextUtils.isEmpty(setCookieHeader.getValue())) {
				String setCookie=setCookieHeader.getValue();
				String cookies = mSetupData.read("mycookies");
				cookies=setCookie;
//				if(TextUtils.isEmpty(cookies)){
//					cookies=setCookie;
//				}else{
//					cookies=cookies+"; "+setCookie;
//				}
				mSetupData.save("mycookies", cookies);
			}
		}
		
//		private JSONObject httpPost(JSONObject obj, String url) throws ClientProtocolException, IOException, JSONException {
////			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
////			nameValuePairs.add(new BasicNameValuePair(http_post_key, obj.toString()));
//			Log.i(TAG,url+"���� "+ obj.toString());
//			JSONObject result = null ; 
//			try {
//				HttpClient httpclient = new DefaultHttpClient();
//				//����ʱ	
//				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout); 
//				//��ȡ��ʱ
//				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
//				HttpPost httppost = new HttpPost(url);
////				if (nameValuePairs != null) {
////					// �����ַ���
////					HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,charSet);
////					// ���ò���ʵ��
////					httppost.setEntity(entity);
////				}
//				if(obj!=null) {
//					StringEntity str = new StringEntity(obj.toString());
//					httppost.setEntity(str);
//				}
//				int res = 0;
//				//��ʼ����
//				HttpResponse response = httpclient.execute(httppost);
//				res = response.getStatusLine().getStatusCode();
//				Log.v(TAG,"�յ�����״̬ "+ res );
//				if (res == 200) {
//					StringBuilder builder = new StringBuilder();
//					BufferedReader bufferedReader2 = new BufferedReader(
//							new InputStreamReader(response.getEntity().getContent()));
//					for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
//						builder.append(s);
//					}
//					if(builder.toString() != null) {
//						result = new JSONObject(builder.toString());
//					}
//					Log.v(TAG,"�յ����� "+ builder.toString());
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				handler.sendEmptyMessage(1);
//			}
//			return result;
//		}
		
		private JSONObject httpPostByJsonArray(ArrayList<NameValuePair> nameValuePairs, String url) throws ClientProtocolException, IOException, JSONException {
//			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//			nameValuePairs.add(new BasicNameValuePair("data", obj.toString()));
			//netByJSONObj(nameValuePairs, AppConstants.serverAction+"userRegister");
			
			Log.i(TAG,"���� "+ nameValuePairs.toString());
//			HttpPost request = new HttpPost(url);  
//			// �󶨵����� Entry  
//			StringEntity se = new StringEntity(obj.toString());   
//			request.setEntity(se);  
//			// ��������  
//			HttpResponse httpResponse = new DefaultHttpClient().execute(request);  
//			// �õ�Ӧ����ַ�������Ҳ��һ�� JSON ��ʽ���������  
//			String retSrc = EntityUtils.toString(httpResponse.getEntity());  
			// ���� JSON ����  
			JSONObject result = null ; 
			
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				//����ʱ	
				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout); 
				//��ȡ��ʱ
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
				HttpPost httppost = new HttpPost(url);
				// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				if (nameValuePairs != null) {
					// �����ַ���
					HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,charSet);
					// ���ò���ʵ��
					httppost.setEntity(entity);
				}
				setRequestCookies(httppost);
				int res = 0;
				//��ʼ����
				HttpResponse response = httpclient.execute(httppost);
				res = response.getStatusLine().getStatusCode();
				appendCookies(response);
				Log.v(TAG,"�յ�����״̬ "+ res );
				if (res == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader2 = new BufferedReader(
							new InputStreamReader(response.getEntity().getContent()));
					for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
						builder.append(s);
					}
					//תΪjsonArray��ʽ
				//	Map map = new HashMap(builder.toString());
					if(builder.toString() != null) {
						result = new JSONObject(builder.toString());
					}
					Log.v(TAG,"�յ����� "+ builder.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(1);
			}
			return result;
		}
		
		
		
		
		
		
		/**
		 * ���ǰ����ݴ��ݵ� �������У�Ȼ����շ��ص�����
		 * @param nameValuePairs
		 * @param url
		 * @return ���ݵ�JSONObject��ʽ
		 */
		private JSONObject netByJSONObj(List<NameValuePair> nameValuePairs, String url) {
			// ʹ��HttpPost��װ����SQL��� //ʹ��HttpClient����HttpPost����
			JSONObject jobj = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				//����ʱ	
				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout); 
				//��ȡ��ʱ
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
				HttpPost httppost = new HttpPost(url);
				// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				if (nameValuePairs != null) {
					// �����ַ���
					HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,charSet);
					// ���ò���ʵ��
					httppost.setEntity(entity);
				}
				int res = 0;
				//��ʼ����
				HttpResponse response = httpclient.execute(httppost);
				res = response.getStatusLine().getStatusCode();
				if (res == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader2 = new BufferedReader(
							new InputStreamReader(response.getEntity().getContent()));
					for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
						builder.append(s);
			Log.i(TAG, "�յ� " + builder.toString() + "  " + (builder==null) + "  " + (builder.toString()==null));
					}
					//תΪjsonArray��ʽ
				//	Map map = new HashMap(builder.toString());
					if(builder.toString() != null) {
						jobj = new JSONObject(builder.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(1);
			}
			return jobj;
		}
		
		
		
		
		
		
		
		/**
		 * ��ʾ���¶Ի���
		 * 
		 * @param info
		 *            �汾��Ϣ����
		 */
		private void showUpdateDialog(JSONObject jobj) {
			builder = new Builder(context);
			builder.setTitle(context.getText(R.string.version_update));
			try {
				builder.setMessage(jobj.getString("message"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			builder.setPositiveButton(context.getString(R.string.version_update_now), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// �������ؿ�
					showDownloadDialog();
					Toast.makeText(context, context.getString(R.string.version_update_download), 3);
				}
			});
			builder.setNegativeButton(context.getString(R.string.version_update_cancel), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			checkDialog = builder.create();
			checkDialog.show();
		}
		
		/**
		 * �����Ի�����ʾ�Ѿ������°汾��
		 */
		private void showAlreadyDialog() {

			Toast.makeText(context, "�Ѿ������°汾!", Toast.LENGTH_SHORT).show();
		}


		/**
		 * �������ؿ�
		 */
		private void showDownloadDialog() {
			Builder builder = new Builder(context);
			builder.setTitle(context.getText(R.string.version_update_ing));
			final LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.update_prgress, null);
			progressBar = (ProgressBar) v.findViewById(R.id.pb_update_progress);
			builder.setView(v);
			builder.setNegativeButton(context.getText(R.string.cancel), new OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// ��ֹ����
					isInterceptDownload = true;
				}
			});
			builder.create().show();
			// ����apk
			downloadApk();
		}

		/**
		 * ����apk
		 */
		private void downloadApk() {
			// ������һ�߳�����
			Thread downLoadThread = new Thread(downApkRunnable);
			downLoadThread.start();
		}

		/**
		 * �ӷ����������°�apk���߳�
		 */
		private Runnable downApkRunnable = new Runnable() {
			@Override
			public void run() {
				if (!android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					// ���û��SD��
					Builder builder = new Builder(context);
					builder.setTitle(context.getText(R.string.alert));
					builder.setMessage(context.getText(R.string.alert_nosdcard));
					builder.setPositiveButton(context.getText(R.string.confirm), new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.show();
					return;
				} else {
					try {
						// ���������°�apk��ַ
						URL url = new URL(versionInfo.getString("downloadURL")
								.toString());
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.connect();
						int length = conn.getContentLength();
						InputStream is = conn.getInputStream();
						File file = new File(Environment
								.getExternalStorageDirectory().getAbsolutePath()
								+ "/"+folder+"/");
						if (!file.exists()) {
							// ����ļ��в�����,�򴴽�
							file.mkdir();
						}
						// ���ط��������°汾�����д�ļ���
						String apkFile = Environment.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/"+folder+"/"
								+ versionInfo.getString("apkName").toString();
						File ApkFile = new File(apkFile);
						FileOutputStream fos = new FileOutputStream(ApkFile);
						int count = 0;
						byte buf[] = new byte[1024];
						do {
							int numRead = is.read(buf);
							count += numRead;
							// ���½�����
							progress = (int) (((float) count / length) * 100);
							handler.sendEmptyMessage(9);
							if (numRead <= 0) {
								// �������֪ͨ��װ
								handler.sendEmptyMessage(8);
								break;
							}
							fos.write(buf, 0, numRead);
							// �����ȡ��ʱ����ֹͣ����
						} while (!isInterceptDownload);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(10);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						handler.sendEmptyMessage(10);
						e.printStackTrace();
					}
				}
			}
		};

		/**
		 * /** ��װapk
		 */
		private void installApk() {
			// ��ȡ��ǰsdcard�洢·��
			try {
				File apkfile = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/"+ folder +"/"
						+ versionInfo.getString("apkName").toString());
				if (!apkfile.exists()) {
					return;
				}
				Intent i = new Intent(Intent.ACTION_VIEW);
				// ��װ�����ǩ����һ�£����ܳ��ֳ���δ��װ��ʾ
				i.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())),
						"application/vnd.android.package-archive");
				context.startActivity(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * �Ƿ񷵻سɹ�
		 * @param json
		 * @return
		 */
		public boolean isNetworkSuccess(JSONObject json) {
			if(json==null) return false;
			try {
				if(json.getInt(AppString.json_retv)==0) {
					return true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return false;
			}
			return false;
		}
		
		/**
		 * �Ƿ����ʧЧ
		 * @param json   �ǵ���ʧЧ
		 * @return
		 */
		public boolean isNetworkNoLogin(JSONObject json) {
			if(json==null) return false;
			try {
				if(json.getInt(AppString.json_retv)==2) {
					return true;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				return false;
			}
			return false;
		}
	    
}