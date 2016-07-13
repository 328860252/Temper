package com.medical.help;

import java.util.Locale;

import com.medical.constants.AppString;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * 语言改变
 * @author zhugege
 * @2014-10-11 下午4:37:57
 */
public class LanguageHelp {
	
	public static void switchLanguage(Context context,int langItem) {
		Locale locale = getLanguageShort(langItem);
        Resources resources = context.getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale; // 简体中文
        resources.updateConfiguration(config, dm);
    }
	
	/**
	 * 获得语言的  字母缩写
	 * @param langItem 语言编号
	 * @return 0返回中文 ， 1返回en ， 2繁体返回 zh_tw
	 */
	private static Locale  getLanguageShort(int langItem){
		Locale locale = null;
		switch(langItem) {
		case AppString.Language_zh:
			locale =  Locale.SIMPLIFIED_CHINESE;
			break;
		case AppString.language_en:
			locale = new Locale("en");
			break;
		case AppString.language_ar:
			locale = new Locale("ar");
			break;
		default:
			locale = Locale.getDefault();
			break;
		}
		return locale;
	}

}
