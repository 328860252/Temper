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
	 * 文件夹
	 */
	private static final String  folder = "Medical";
	
	/**
	 * 版本信息
	 */
	private  JSONObject versionInfo = null;
	
	private Builder builder;
	// 下载进度条
		private ProgressBar progressBar;
		// 是否终止下载
		private boolean isInterceptDownload = false;
		// 进度条显示数值
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
				//Toast.makeText(context, "连接超时", Toast.LENGTH_LONG).show();
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
				//Toast.makeText(context, "连接超时，或是无效的数据", Toast.LENGTH_LONG).show();
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
			case 6: //提示下载窗口
				showUpdateDialog(versionInfo);
				break;
			case 7: //已经安装完毕
				showAlreadyDialog();
				break;
			case 8: // 安装apk文件
				progressBar.setVisibility(View.INVISIBLE);
				installApk();
				break;
			case 9: // 更新进度情况
				progressBar.setProgress(progress);
				break;
			case 10://错误
				if(checkDialog!=null && checkDialog.isShowing()) {
					checkDialog.dismiss();
				}
				Toast.makeText(context, R.string.version_update_error, 3).show();
			}
		}
	};
	

	    AlertDialog checkDialog ;
	    
		
	    
	    
	    
	    
	    /**
		 * 登入
		 * @param account 
		 * @param password
		 * @return  
		 * 			retv	Integer	状态: 0表示成功  其他表示失败,详情见错误信息
					msg	String	状态说明
					dataType	Integer	返回数据类型说明 0为字典/类;1为数组
					data		返回的具体数据,具体数据详情见接口详细文档
		 *  	map{"name":  ""}
		 *           登入失败的话   map{"errorCode":   , “errorMessage”： “”  } 
		 *           超时 返回 null
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
		 * 注册信息
		 * @param account
		 * @param password
		 * @param name
		 * @param type  注册类型0，使用手机唯一码自动注册，激活。    1、使用邮箱为账号注册，要邮件确认激活
		 * @return  map{"id":""}
		 * 			注册失败  map{"errorCode":    ,  "errorMessage":  }
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
				nameValuePairs.add(new BasicNameValuePair("smsRegisterParams.mobileSystem", android.os.Build.VERSION.RELEASE));//手机版本
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
		 * 检查版本更新, 这里会弹出提示是否下载， 所以不需要再对这个result做处理了。
		 * @param clientType  客户端类型 IOS/ANDROID
		 * @param versionCode 客户端版本号
		 * 
		 * @return	isUpdate	Boolean	是否有更新
					isForce	Boolean	是否有强制更新
					versionCode	Integer	当前版本号
					versionName	String	当前版本
					versionExplain	String	版本说明
					updateUrl	String	版本地址
					updateDateTime	String	更新时间
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
		 * 刷新当前状态
		 * @param phone
		 * @return  temperature			Double	温度
					voltage				Double	电压
					alarmTemperature	Double	报警温度
					bluetoothIsConnect	Short	蓝牙状态-是否连接
					deviceState			Short	设备状态
					alarmState			Short	报警状态
					dataTime			String	数据时间
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
		 * 同步数据
		 * @param 	dataParams.temperature	温度	Double	√
					dataParams.voltage	电压	Double	√
					dataParams.alarmTemperature	报警温度	Double	√
					dataParams.bluetoothIsConnect	蓝牙状态-是否连接	Short	√
					dataParams.deviceState	设备状态	Short	√
					dataParams.alarmState	报警状态	Short	√
					dataParams.phone	电话号码	String	√
		 *			
		 * @return	temperature	温度	Double	√	
					dataTime	时间	String	√	yyyy-MM-dd HH:mm:ss
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
		 * 查询广告
		 * @param phone
		 * @return
		 * 	logo	Boolean	图标
			name	Boolean	标题
			explain	Integer	简介
			state	String	消息类型 0-文本 1-图片 2-网址 3-视频
			content	String	内容
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
		 * 获取温度警告信息
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
		 * 查询推送信息
		 * @param phone
		 * @return
		 * 	logo	Boolean	图标
			name	Boolean	标题
			explain	Integer	简介
			state	String	消息类型 0-文本 1-图片 2-网址 3-视频
			content	String	内容
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
		 * 获取设备的location
		 * @param phone
		 * @return
		 * 	deviceNum	String	设备终端号
		 *  valid	String	数据有效位（A/V/B），A 表示GPS 数据是有效定位数据，V 表示GPS 数据是无效定位数据B 表示北斗
		 *  longitude	Double	经度
		 *  longitudeFlag	String	经度标志（E：东经 W：西经）
		 *  latitude	Double	纬度
		 *  latitudeFlag	String	纬度标志（N:北纬，S：南纬）
		 *  speed	Double	速度，范围000.00～999.99节，保留两位小数。
		 *  direction	int	方位角，正北为0度，分辨率1度，顺时针方向，字段为空，即角度为0
		 *  dateTime	String	时间     日/月/年
		 *  mcc	String	国家编码
		 *  mnc	String	运营商编码
		 *  lac	String	基站编码 小区编号
		 *  cid	String	基站编码 小区ID
		 *  temperature	Double	温度
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
			Log.i(TAG,url+"发送 http:"+ nameValuePairs.toString());
			JSONObject result = null ; 
			try {
				//请求超时	
				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout); 
				//读取超时
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
				HttpPost httppost = new HttpPost(url);
				if (nameValuePairs != null) {
					// 设置字符集
					HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,charSet);
					// 设置参数实体
					httppost.setEntity(entity);
				}
				setRequestCookies(httppost);
				int res = 0;
				//开始床送
				HttpResponse response = httpclient.execute(httppost);
				appendCookies(response);
				res = response.getStatusLine().getStatusCode();
				Log.v(TAG,"收到数据状态 "+ res );
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
					Log.v(TAG,"收到数据 "+ builder.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(1);
			}
			return result;
		}
		
		/**
		 * 设置请求的Cookie头信息
		 * @param reqMsg
		 */
		private void setRequestCookies(HttpMessage reqMsg) {
			String cookies = mSetupData.read("mycookies");
			if(!TextUtils.isEmpty(cookies)){
				reqMsg.setHeader("Cookie", cookies);
			}
		}
		/**
		 * 把新的Cookie头信息附加到旧的Cookie后面
		 * 用于下次Http请求发送
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
//			Log.i(TAG,url+"发送 "+ obj.toString());
//			JSONObject result = null ; 
//			try {
//				HttpClient httpclient = new DefaultHttpClient();
//				//请求超时	
//				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout); 
//				//读取超时
//				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
//				HttpPost httppost = new HttpPost(url);
////				if (nameValuePairs != null) {
////					// 设置字符集
////					HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,charSet);
////					// 设置参数实体
////					httppost.setEntity(entity);
////				}
//				if(obj!=null) {
//					StringEntity str = new StringEntity(obj.toString());
//					httppost.setEntity(str);
//				}
//				int res = 0;
//				//开始床送
//				HttpResponse response = httpclient.execute(httppost);
//				res = response.getStatusLine().getStatusCode();
//				Log.v(TAG,"收到数据状态 "+ res );
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
//					Log.v(TAG,"收到数据 "+ builder.toString());
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
			
			Log.i(TAG,"发送 "+ nameValuePairs.toString());
//			HttpPost request = new HttpPost(url);  
//			// 绑定到请求 Entry  
//			StringEntity se = new StringEntity(obj.toString());   
//			request.setEntity(se);  
//			// 发送请求  
//			HttpResponse httpResponse = new DefaultHttpClient().execute(request);  
//			// 得到应答的字符串，这也是一个 JSON 格式保存的数据  
//			String retSrc = EntityUtils.toString(httpResponse.getEntity());  
			// 生成 JSON 对象  
			JSONObject result = null ; 
			
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				//请求超时	
				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout); 
				//读取超时
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
				HttpPost httppost = new HttpPost(url);
				// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				if (nameValuePairs != null) {
					// 设置字符集
					HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,charSet);
					// 设置参数实体
					httppost.setEntity(entity);
				}
				setRequestCookies(httppost);
				int res = 0;
				//开始床送
				HttpResponse response = httpclient.execute(httppost);
				res = response.getStatusLine().getStatusCode();
				appendCookies(response);
				Log.v(TAG,"收到数据状态 "+ res );
				if (res == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader2 = new BufferedReader(
							new InputStreamReader(response.getEntity().getContent()));
					for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
						builder.append(s);
					}
					//转为jsonArray格式
				//	Map map = new HashMap(builder.toString());
					if(builder.toString() != null) {
						result = new JSONObject(builder.toString());
					}
					Log.v(TAG,"收到数据 "+ builder.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(1);
			}
			return result;
		}
		
		
		
		
		
		
		/**
		 * 这是把数据传递到 服务器中，然后接收返回的数据
		 * @param nameValuePairs
		 * @param url
		 * @return 数据的JSONObject格式
		 */
		private JSONObject netByJSONObj(List<NameValuePair> nameValuePairs, String url) {
			// 使用HttpPost封装整个SQL语句 //使用HttpClient发送HttpPost对象
			JSONObject jobj = null;
			try {
				HttpClient httpclient = new DefaultHttpClient();
				//请求超时	
				httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeout); 
				//读取超时
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
				HttpPost httppost = new HttpPost(url);
				// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				if (nameValuePairs != null) {
					// 设置字符集
					HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs,charSet);
					// 设置参数实体
					httppost.setEntity(entity);
				}
				int res = 0;
				//开始床送
				HttpResponse response = httpclient.execute(httppost);
				res = response.getStatusLine().getStatusCode();
				if (res == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader2 = new BufferedReader(
							new InputStreamReader(response.getEntity().getContent()));
					for (String s = bufferedReader2.readLine(); s != null; s = bufferedReader2.readLine()) {
						builder.append(s);
			Log.i(TAG, "收到 " + builder.toString() + "  " + (builder==null) + "  " + (builder.toString()==null));
					}
					//转为jsonArray格式
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
		 * 提示更新对话框
		 * 
		 * @param info
		 *            版本信息对象
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
					// 弹出下载框
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
		 * 弹出对话框，提示已经是最新版本了
		 */
		private void showAlreadyDialog() {

			Toast.makeText(context, "已经是最新版本!", Toast.LENGTH_SHORT).show();
		}


		/**
		 * 弹出下载框
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
					// 终止下载
					isInterceptDownload = true;
				}
			});
			builder.create().show();
			// 下载apk
			downloadApk();
		}

		/**
		 * 下载apk
		 */
		private void downloadApk() {
			// 开启另一线程下载
			Thread downLoadThread = new Thread(downApkRunnable);
			downLoadThread.start();
		}

		/**
		 * 从服务器下载新版apk的线程
		 */
		private Runnable downApkRunnable = new Runnable() {
			@Override
			public void run() {
				if (!android.os.Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED)) {
					// 如果没有SD卡
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
						// 服务器上新版apk地址
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
							// 如果文件夹不存在,则创建
							file.mkdir();
						}
						// 下载服务器中新版本软件（写文件）
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
							// 更新进度条
							progress = (int) (((float) count / length) * 100);
							handler.sendEmptyMessage(9);
							if (numRead <= 0) {
								// 下载完成通知安装
								handler.sendEmptyMessage(8);
								break;
							}
							fos.write(buf, 0, numRead);
							// 当点击取消时，则停止下载
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
		 * /** 安装apk
		 */
		private void installApk() {
			// 获取当前sdcard存储路径
			try {
				File apkfile = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()
						+ "/"+ folder +"/"
						+ versionInfo.getString("apkName").toString());
				if (!apkfile.exists()) {
					return;
				}
				Intent i = new Intent(Intent.ACTION_VIEW);
				// 安装，如果签名不一致，可能出现程序未安装提示
				i.setDataAndType(Uri.fromFile(new File(apkfile.getAbsolutePath())),
						"application/vnd.android.package-archive");
				context.startActivity(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/**
		 * 是否返回成功
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
		 * 是否登入失效
		 * @param json   是登入失效
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