package com.example.medical;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.location.BDLocationListener;
import com.medical.constants.AppConstants;
import com.medical.constants.AppString;
import com.medical.help.SetupData;
import com.medical.manager.NetworkManager;
import com.zby.medical.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * �ٶȵ�ͼ��λ
 * @author Administrator
 *
 */
public class LocationActivity extends Activity{
	
	// ��λ���
		LocationClient mLocClient;
		public MyLocationListenner myListener = new MyLocationListenner();
		private LocationMode mCurrentMode;
		BitmapDescriptor mCurrentMarker;

		MapView mMapView;
		BaiduMap mBaiduMap;

		boolean isFirstLoc = true;// �Ƿ��״ζ�λ
		private Marker mMarker;
		private BitmapDescriptor mBitmapDescriptor;
		private InfoWindow mInfoWindow;
		
		
		private SetupData mSetupData;
		private NetworkManager mNetworkManager;
		
		/**
		 * ��ȡ��������locationThread
		 */
		private Thread locationThread;
		
		/**
		 * ��ʾ�豸����
		 * @param savedInstanceState
		 */
		private final static int handler_location = 11;
		
		private BaiduSDKReceiver mBaiduReceiver;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_location);
			mCurrentMode = LocationMode.NORMAL;
//   ����ģʽ  ��ͨ ����  ��������ģʽ			
//						mCurrentMode = LocationMode.COMPASS;
//						mBaiduMap
//								.setMyLocationConfigeration(new MyLocationConfiguration(
//										mCurrentMode, true, mCurrentMarker));
//  ����location��  ͼƬ			
//						mCurrentMarker = BitmapDescriptorFactory
//								.fromResource(R.drawable.btn_add);
//						mBaiduMap
//								.setMyLocationConfigeration(new MyLocationConfiguration(
//										mCurrentMode, true, mCurrentMarker));

			// ��ͼ��ʼ��
			mMapView = (MapView) findViewById(R.id.bmapView);
			mBaiduMap = mMapView.getMap();
			// ������λͼ��
			mBaiduMap.setMyLocationEnabled(true);
			// ��λ��ʼ��
			mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// ��gps
			option.setCoorType("bd09ll"); // �����������
			option.setScanSpan(1000);
			mLocClient.setLocOption(option);
			mLocClient.start();
			
//			mCurrentMarker = BitmapDescriptorFactory
//					.fromResource(R.drawable.img_location);
//			mBaiduMap
//					.setMyLocationConfigeration(new MyLocationConfiguration(
//							mCurrentMode, true, mCurrentMarker));

			
			//1���������ͼ��1��2000����        5 ��������й�   15��1��500��    20,1��20�� 
			float zoomLevel =  15f;
			MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
			mBaiduMap.animateMapStatus(u);
			
			
			if(mNetworkManager == null) {
				mNetworkManager = new NetworkManager(LocationActivity.this);
				mSetupData = SetupData.getSetupData(this);
			}
			
			// ע�� �ٶ�SDK �㲥������
			IntentFilter iFilter = new IntentFilter();
			iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
			iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
			mBaiduReceiver = new BaiduSDKReceiver();
			registerReceiver(mBaiduReceiver, iFilter);
		}
		
		private Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch(msg.what) {
				case handler_location:
					double[] location =  (double[]) msg.obj;
					deviceLocationShow(location[0], location[1]);
					break;
				}
			}
		};

		/**
		 * ��λSDK������
		 */
		public class MyLocationListenner implements BDLocationListener {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// map view ��ٺ��ڴ����½��յ�λ��
				Log.e("tag", location.getLatitude() + " " + location.getLongitude() + " " + location.getAddrStr());
				if (location == null || mMapView == null)
					return;
//				MyLocationData locData = new MyLocationData.Builder()
//						.accuracy(location.getRadius())
//						// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
//						.direction(100).latitude(location.getLatitude())
//						.longitude(location.getLongitude()).build();
//				mBaiduMap.setMyLocationData(locData);
				if (isFirstLoc) {
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(),
							location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaiduMap.animateMapStatus(u);
					queryAddressByLatlng(ll);
				}
			}

			public void onReceivePoi(BDLocation poiLocation) {
			}
		}
		
		
		/**
		 * ������
		 * ��ʾ�豸���ص�
		 */
		private void deviceLocationShow(double lat, double lon){
			//22.575686  ���� 113.88227
//			LatLng pt1 = new LatLng(22.575666, 113.88227);  
//			LatLng pt2 = new LatLng(22.575556, 113.882278);  
//			LatLng pt3 = new LatLng(22.574, 113.88227);  
//			LatLng pt4 = new LatLng(22.573686, 113.88227);  
//			LatLng pt5 = new LatLng(22.572686, 113.88227);  
//			List<LatLng> pts = new ArrayList<LatLng>();  
//			pts.add(pt1);  
//			pts.add(pt2);  
//			pts.add(pt3);  
//			pts.add(pt4);  
//			pts.add(pt5);  
//			//�����û����ƶ���ε�Option����  
//			//�㣨Dot�������ߣ�Polyline�������ߣ�Arc����Բ��Circle��������Σ�Polygon����
//			OverlayOptions polygonOption = new PolygonOptions()  
//			    .points(pts)  
//			    .stroke(new Stroke(5, 0xAA00FF00))  
//			    .fillColor(0xAAFFFF00);  
//			//�ڵ�ͼ����Ӷ����Option��������ʾ  
//			mBaiduMap.addOverlay(polygonOption);
			
			
			
			if(mMarker == null)  {
				//����Maker����  
				//LatLng point = new LatLng(22.571686, 113.88027);
				LatLng point = new LatLng(lat, lon);  
				//����Markerͼ��  
				mBitmapDescriptor = BitmapDescriptorFactory  
						.fromResource(R.drawable.img_location_device);  
				//����MarkerOption�������ڵ�ͼ�����Marker  
				OverlayOptions option = new MarkerOptions()  
				.position(point)  
				.icon(mBitmapDescriptor).zIndex(9).draggable(true);  
				//�ڵ�ͼ�����Marker������ʾ  
				mMarker = (Marker) mBaiduMap.addOverlay(option);
			} else {
				LatLng llNew = new LatLng(lat,
						lon);
				mMarker.setPosition(llNew);
			}
			
			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				public boolean onMarkerClick(final Marker marker) {
					Button button = new Button(getApplicationContext());
					button.setBackgroundResource(R.drawable.img_marker_bg);
					OnInfoWindowClickListener listener = null;
					if (marker == mMarker) {
						button.setText(R.string.device);
						button.setTextColor(getResources().getColor(R.color.white));
						listener = new OnInfoWindowClickListener() {
							public void onInfoWindowClick() {
								mBaiduMap.hideInfoWindow();
							}
						};
						LatLng ll = marker.getPosition();
						mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button), ll, -47, listener);
						mBaiduMap.showInfoWindow(mInfoWindow);
					}
					return true;
				}
			});
		}
		
		
		Runnable locationRunnable = new Runnable() {
			public void run() {
				while(true) {
					String phone = mSetupData.read(AppString.account);
					//����ģʽ�Ͳ���ѯ�豸��
					if(phone.equals("")|| !AppConstants.isLineMode) return;
					JSONObject result = mNetworkManager.queryDeviceLocationInfo(phone);
					if(mNetworkManager.isNetworkSuccess(result)) {
						try {
							JSONObject data = result.getJSONObject("data");
							double lon = data.getDouble("longitude");
							double lat = data.getDouble("latitude"); 
							String isNorth = data.getString("latitudeFlag");
							String isEast = data.getString("longitudeFlag");
							if(isNorth.toLowerCase().contains("s")) {//S��γΪ-   N��γ+
								lat = -lat;
							}
							if(isEast.toLowerCase().contains("w")) {//E����γ+  W����γ-
								lon = - lon;
							}
							double[] location = new double[] {lat, lon};
							Message msg = mHandler.obtainMessage();
							msg.what = handler_location;
							msg.obj = location;
							mHandler.sendMessage(msg);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						Thread.sleep(120 * 1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						break;
					}
				}
			}
		};
		
		public void btn_back(View v) {
			finish();
		}

		@Override
		protected void onPause() {
			mMapView.onPause();
			super.onPause();
		}

		@Override
		protected void onResume() {
			mMapView.onResume();
			super.onResume();
		}
		
		

		@Override
		protected void onStart() {
			// TODO Auto-generated method stub
			if(locationThread==null) {
				locationThread = new Thread(locationRunnable);
			}
			locationThread.start();
			super.onStart();
		}

		@Override
		protected void onStop() {
			// TODO Auto-generated method stub
			if(locationThread!=null) {
				locationThread.interrupt();
			}
			super.onStop();
		}

		@Override
		protected void onDestroy() {
			// �˳�ʱ��ٶ�λ
			mLocClient.stop();
			// �رն�λͼ��
			mBaiduMap.setMyLocationEnabled(false);
			if(mBitmapDescriptor!=null) {
				mBitmapDescriptor.recycle();
			}
			if(mCurrentMarker!=null) {
				mCurrentMarker.recycle();
			}
			mMapView.onDestroy();
			mMapView = null;
			super.onDestroy();
		}
		
		
		/**
		 * ��γ��  ��ַ��ѯ��
		 */
		GeoCoder mSearch;
		
		/**
		 * ͨ��γ�����  ��ѯ��ַ
		 * ��ѯ�����ڻص����� listener�н��д���
		 * @param latlng ��γ�����
		 */
		private void queryAddressByLatlng(final LatLng latlng) {
			if(mSearch==null) {
				mSearch = GeoCoder.newInstance();
				mSearch.setOnGetGeoCodeResultListener(listener);
			}
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(latlng));
					
				}}).start();
		}
	
		OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
				if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
					// û���ҵ��������
				} else {
					// ��ȡ������������
					Log.e("��γ�ȵõ�λ����Ϣ", arg0.getAddress() + " : "+ arg0.getLocation().longitude+"--" + arg0.getLocation().latitude);
				}
			}

			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
				// TODO Auto-generated method stub
				if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
					// û���ҵ��������
				} else {

					Log.e("λ����Ϣ�õ���γ��", arg0.getLocation().latitude + "----" + arg0.getLocation().longitude);
				}

			}

		};

		
		
		/**
		 * ����㲥�����࣬���� SDK key ��֤�Լ������쳣�㲥
		 */
		public class BaiduSDKReceiver extends BroadcastReceiver {
			public void onReceive(Context context, Intent intent) {
				String s = intent.getAction();
				if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//					if(HttpUtils.isNetworkConnected(WeCareActivity.this)){
//						showToast("key ��֤����! ���� AndroidManifest.xml �ļ��м�� key ����");
//					}
					Log.e("tag", "key ��֤����! ���� AndroidManifest.xml �ļ��м�� key ����");
				} else if (s
						.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
					Log.e("tag", "�������");
				}
			}
		}
}
