package com.medical.constants;

import android.graphics.Color;

public class AppConstants {
	
	/**
	 * 服务器项目前缀地址
	 */
	public static String serverAction = "http://115.29.34.192/";
	
	public static int  jiangeTime = 120*1000; //两次记录的间隔时间；毫秒级
	
	public static int alert_dialog_time =10* 60 * 1000;//弹提示的间隔时间
	public static int alert_low_dialog_time =10* 60 * 1000;//弹提示的间隔时间
	
	/**
	 * 自动连接间隔时间
	 */
	public static int auto_link_time =  3 * 1000;
	
	/**
	 * 是否支持BLE
	 */
	public static final boolean isBle = true;
	
	/**
	 * 是否是测试协议的版本
	 */
	public final static boolean isDebug = false;
	
	public static final String folder = "BATWBABY/images";//图片防止地址
	public static final String folder_ad = "BATWBABY/ADimages";//广告地址
	public static final String fileFolder = "BATWBABY";//项目名
	
	/**
	 * 绿色
	 */
	public static int color1 = Color.rgb(145, 219, 29);
	
	
	 /**
	 * 橙色
	 */
	public static int color2 = Color.rgb(255, 165, 57);
	 
	 
	   /**
	 * 红色
	 */
	public static  int color3 = Color.rgb(247, 20, 20);
	
	public static int temper_min = 3500;
	
	/**
	 * 低温警报 温度
	 */
	public static final int temper_min_alert= 3551;
	
	
	public static final String PASSWORD_SAVE  = "passwordSave";
	
	//短信验证，old版本
//	public static final String APP_SMS_KEY ="54d3fcf2290d";
//	public static final String APP_SMS_SECRET = "171f4c745c728672e2429392204094e4";
	public static final String APP_SMS_KEY ="11b91d29ec5c0";
	public static final String APP_SMS_SECRET = "4dc1be42718373c6ada3bbd221bd0743";

	
	public static String phone_mac = "";
	
	/**
	 * 是否是在线模式
	 */
	public static boolean isLineMode  = false;

}
