package com.example.medical;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.medical.adapter.StringAdapter;
import com.medical.help.Myhex;
import com.medical.sql.BluetoothRecordBin;
import com.medical.sql.BluetoothSQLService;
import com.medical.view.ToastNew;
import com.zby.medical.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class DeviceListBleActivity extends Activity {

	// Debugging
	private static final String TAG = "DeviceListActivity";
	private static final boolean D = true;
	public final String onlyName = "";// RDA Headset batwbaby dual-spp

	// Return Intent extra
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	// Member fields
	private BluetoothAdapter mBtAdapter;
	private BluetoothManager mBtManager;
	private StringAdapter adapter;
	private List<String> list;
	private ListView listView;
	private BluetoothSQLService bSQLService;

	private static final int handler_scan = 10;
	private static final int handler_scanFinish = 11;
	private static final int handler_refresh = 12;

	private Button btn_scan;
	private ProgressBar pb_scaning;

	private boolean isScaning = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.device_list2);

		openBluetooth();
		list = new ArrayList<String>();
		adapter = new StringAdapter(this, list);
		listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(mDeviceClickListener);

		bSQLService = new BluetoothSQLService(this);
		btn_scan = (Button) findViewById(R.id.btn_scan);
		pb_scaning = (ProgressBar) findViewById(R.id.progressBar_scaining);
		// doMatch();
		handler.sendEmptyMessage(handler_scan);
		doDiscovery();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case handler_scan:
				btn_scan.setVisibility(View.INVISIBLE);
				pb_scaning.setVisibility(View.VISIBLE);
				break;
			case handler_scanFinish:
				btn_scan.setVisibility(View.VISIBLE);
				pb_scaning.setVisibility(View.INVISIBLE);
				break;
			case handler_refresh:
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("NewApi")
	private void openBluetooth() {
		if (mBtAdapter == null) {
			BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
			mBtAdapter = bm.getAdapter();
		}
		if (null != mBtAdapter) {
			if (!mBtAdapter.isEnabled()) {
				mBtAdapter.enable();
				// Intent intent = new Intent(
				// BluetoothAdapter.ACTION_REQUEST_ENABLE);
				// startActivity(intent);
			}
		} else {
			ToastNew.makeTextMid(this, getString(R.string.noBluetooth),
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	public void btn_scan(View v) {
		doDiscovery();
		handler.sendEmptyMessage(handler_scan);
	}

	public void btn_match(View v) {
		mBtAdapter.cancelDiscovery();
		handler.sendEmptyMessage(handler_scanFinish);
		doMatch();
	}

	@SuppressLint("NewApi")
	private BluetoothAdapter.LeScanCallback mBleScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(BluetoothDevice device, int arg1, byte[] arg2) {
			// If it's already paired, skip it, because it's been listed already
			addDevice2List(device);
		}
	};

	/**
	 * 添加到设备中 查看是否已经在设备中
	 * 
	 * @param device
	 */
	private void addDevice2List(BluetoothDevice device) {
		String name = Myhex.replaceBlank(device.getName()).toLowerCase();
		System.out.println(" BLe found搜索到" + name + " " + onlyName + " "
				+ name.indexOf(onlyName.toLowerCase()));
		if (name.contains(onlyName)) {
			Log.v("DeviceList ", "found bluetooth--"+list.size()+"->" + name);
			
			synchronized (list) {
				final String myString = device.getName() + "\n"
						+ device.getAddress();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).equalsIgnoreCase(myString)) {
						return;
					}
				}
				list.add(myString);
				Log.d("devicelist" , "adapter 要刷新了");
				handler.sendEmptyMessage(handler_refresh);
			}
			// String newString = "Smart Home" + "\n" + device.getAddress();
			BluetoothRecordBin bbin = new BluetoothRecordBin();
			bbin.setName(device.getName());
			bbin.setAddress(device.getAddress());
			bSQLService.insert(bbin);
		}
	}

	/**
	 * Start device discover with the BluetoothAdapter
	 */
	@SuppressLint("NewApi")
	private void doDiscovery() {
		list.clear();
		if (D)
			Log.d(TAG, "doDiscovery()");
		if (mBtAdapter == null) {
			BluetoothManager bm = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
			mBtAdapter = bm.getAdapter();
		}
		if (mBtAdapter.isEnabled()) {
			handler.sendEmptyMessage(handler_scan);
			isScaning = true;
			mBtAdapter.startLeScan(mBleScanCallback);
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if (isScaning) {
						handler.sendEmptyMessage(handler_scanFinish);
						isScaning = false;
						mBtAdapter.stopLeScan(mBleScanCallback);
					}
				}
			}, 10000);
		} else {
			if (isScaning) {
				mBtAdapter.stopLeScan(mBleScanCallback);
				isScaning = false;
			}
		}
	}

	/**
	 * 从匹配的记录中获取记录
	 */
	private void doMatch() {
		list.clear();
		Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
		String address = "";
		for (BluetoothDevice device : pairedDevices) {
			String name = Myhex.replaceBlank(device.getName()).toLowerCase();
			System.out.println(" 搜索到" + name + " " + onlyName + " "
					+ name.indexOf(onlyName.toLowerCase()));
			if (name.contains(onlyName)) {
				list.add(name + "\n" + device.getAddress());
				address = device.getAddress();
			}
		}
		adapter.notifyDataSetChanged();

		List<BluetoothRecordBin> blist = bSQLService.getList();
		for (BluetoothRecordBin bbin : blist) {
			findNewDevice(bbin);
		}
	}

	private void findNewDevice(BluetoothRecordBin bbin) {
		for (int i = 0; i < list.size(); i++) {
			System.out.println("比较" + i + list.get(i) + " --"
					+ bbin.getAddress() + "-- "
					+ list.get(i).contains(bbin.getAddress()));
			if (list.get(i).contains(bbin.getAddress())) {
				return;
			}
		}
		list.add(bbin.getName() + "\n" + bbin.getAddress());
		adapter.notifyDataSetChanged();
	}

	// The on-click listener for all devices in the ListViews
	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect
			mBtAdapter.cancelDiscovery();

			// Get the device MAC address, which is the last 17 chars in the
			// View
			String info = ((TextView) v.findViewById(R.id.textView_name)).getText().toString();
			String address = info.substring(info.length() - 17);

			// Create the result Intent and include the MAC address
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

			// Set result and finish this Activity
			setResult(Activity.RESULT_OK, intent);
			System.gc();
			finish();
		}
	};

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
//	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			String action = intent.getAction();
//			System.out.println("found blu " + action);
//			try {
//				// When discovery finds a device
//				if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//					// Get the BluetoothDevice object from the Intent
//					BluetoothDevice device = intent
//							.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//					// If it's already paired, skip it, because it's been listed
//					// already
//					String name = Myhex.replaceBlank(device.getName())
//							.toLowerCase();
//					System.out.println(" 搜索到" + name + " " + onlyName + " "
//							+ name.indexOf(onlyName.toLowerCase()));
//					Log.v("DeviceList ", "found bluetooth--->" + name);
//					if (name.contains(onlyName)) {
//						String myString = device.getName() + "\n"
//								+ device.getAddress();
//						for (int i = 0; i < list.size(); i++) {
//							if (list.get(i).equalsIgnoreCase(myString)) {
//								return;
//							}
//						}
//						// String newString = "Smart Home" + "\n" +
//						// device.getAddress();
//						BluetoothRecordBin bbin = new BluetoothRecordBin();
//						bbin.setName(device.getName());
//						bbin.setAddress(device.getAddress());
//						bSQLService.insert(bbin);
//						list.add(myString);
//						adapter.notifyDataSetChanged();
//					}
//
//				} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
//						.equals(action)) {
//					handler.sendEmptyMessage(handler_scanFinish);
//					if (list.size() == 0) {
//						// String noDevices =
//						// getResources().getText(R.string.none_found).toString();
//						// mNewDevicesArrayAdapter.add(noDevices);
//						// setTitle(noDevices);
//						ToastNew.makeTextMid(DeviceListBleActivity.this,
//								R.string.none_found, 3).show();
//					}
//					// else if (list.getCount() == 1) {
//					// String info = list.get(0);
//					// String address = info.substring(info.length() - 17);
//					//
//					// // Create the result Intent and include the MAC address
//					// Intent intent1 = new Intent();
//					// intent1.putExtra(EXTRA_DEVICE_ADDRESS, address);
//					//
//					// // Set result and finish this Activity
//					// DeviceListActivity2.this.setResult(Activity.RESULT_OK,
//					// intent1);
//					// DeviceListActivity2.this.finish();
//					// }
//				}
//
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//
//		}
//	};

//	private void registerBroadcast() {
//		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//		this.registerReceiver(mReceiver, filter);
//
//		// Register for broadcasts when discovery has finished
//		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//		this.registerReceiver(mReceiver, filter);
//	}

	public void btn_back(View v) {
		finish();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		//registerBroadcast();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mBtAdapter.stopLeScan(mBleScanCallback);
		//this.unregisterReceiver(mReceiver);
	}

}
