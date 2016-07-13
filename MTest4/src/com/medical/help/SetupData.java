package com.medical.help;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 保存的信息
 *     SetupData.LastIp，最后连接成功的ip
 *     SetupData.PWD  ,最后连接成功的密码
 *     ”ip“， 搜索到的ip对应的设备名字
 *     ”ipMAC“,  搜索到的ip对应的mac
 * @author wt
 * @modify zhujiang 2014/3/21
 */
public class SetupData {

	private static SharedPreferences sp;
	private static Editor editor;
	private final static String SP_NAME = "mydata";
	private final static int MODE = Context.MODE_WORLD_READABLE
			+ Context.MODE_WORLD_WRITEABLE;
	public static final String LastIp = "lastIp";
	public static final String LastPwd = "pwd";
	public static final String LastBluetooth = "lastBluetooth";
	private static SetupData setupData;

	private SetupData(Context context) {
		sp = context.getSharedPreferences(SP_NAME, MODE);
		editor = sp.edit();
	}

	public static SetupData getSetupData(Context context) {
		if (setupData == null) {
			setupData = new SetupData(context);
		}
		return setupData;
	}

	private SetupData() {

	}

	public boolean save(String key, String value) {
		editor.putString(key, value);
		// 亿万不要忘了加commit呐~~~！！！！
		return editor.commit();
	}

	public boolean saveboolean(String key, boolean bo) {
		editor.putBoolean(key, bo);
		// 亿万不要忘了加commit呐~~~！！！！
		return editor.commit();
	}

	public boolean saveInt(String key, int i) {
		editor.putInt(key, i);
		// 亿万不要忘了加commit呐~~~！！！！
		return editor.commit();
	}

	public boolean saveLong(String key, Long i) {
		editor.putLong(key, i);
		// 亿万不要忘了加commit呐~~~！！！！
		return editor.commit();
	}

	public boolean saveDouble(String key, double d) {
		editor.putFloat(key, (float) d);
		// 亿万不要忘了加commit呐~~~！！！！
		return editor.commit();
	}

	public String read(String key) {
		String str = null;
		str = sp.getString(key, "");
		return str;
	}

	public String read(String key, String default1) {
		String str = null;
		str = sp.getString(key, default1);
		return str;
	}

	public double readDouble(String key) {
		float str = 0.0f;
		str = sp.getFloat(key, 0.0f);
		return saveTwo(str);

	}

	public static double saveTwo(double d) {
		d = d * 100.0;
		d = Math.round(d);

		return ((int) d) / 100.0;
	}

	public double readDouble(String key, float f) {
		float str = 0.0f;
		str = sp.getFloat(key, f);
		return str;

	}

	public boolean readBoolean(String key) {
		boolean str;
		str = sp.getBoolean(key, true);
		return str;

	}

	public boolean readBoolean(String key, boolean defValue) {
		boolean str;
		str = sp.getBoolean(key, defValue);
		return str;

	}

	public int readInt(String key, int j) {
		int i = 0;
		i = sp.getInt(key, j);
		return i;
	}

	public int readInt(String key) {
		int i = 0;
		i = sp.getInt(key, 0);
		return i;
	}

	public Long readLong(String key) {
		long l=0;
		long i = sp.getLong(key, l);
		
		return i;
	}
}
