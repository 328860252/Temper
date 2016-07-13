package com.medical.bluetooth;

import android.os.Build;

public class DeviceDependency {
	public static boolean shouldUseSecure() {
		if (Build.MANUFACTURER.equals("Xiaomi")) {
			if (Build.MODEL.equals("2013022") && Build.VERSION.RELEASE.equals("4.2.1")) {
				return true;
			}
		}
		return false;
	}
}
