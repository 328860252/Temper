package com.medical.constants;

import android.graphics.Color;

public class AppConstants {
	
	/**
	 * ��������Ŀǰ׺��ַ
	 */
	public static String serverAction = "http://115.29.34.192/";
	
	public static int  jiangeTime = 120*1000; //���μ�¼�ļ��ʱ�䣻���뼶
	
	public static int alert_dialog_time =10* 60 * 1000;//����ʾ�ļ��ʱ��
	public static int alert_low_dialog_time =10* 60 * 1000;//����ʾ�ļ��ʱ��
	
	/**
	 * �Զ����Ӽ��ʱ��
	 */
	public static int auto_link_time =  3 * 1000;
	
	/**
	 * �Ƿ�֧��BLE
	 */
	public static final boolean isBle = true;
	
	/**
	 * �Ƿ��ǲ���Э��İ汾
	 */
	public final static boolean isDebug = false;
	
	public static final String folder = "BATWBABY/images";//ͼƬ��ֹ��ַ
	public static final String folder_ad = "BATWBABY/ADimages";//����ַ
	public static final String fileFolder = "BATWBABY";//��Ŀ��
	
	/**
	 * ��ɫ
	 */
	public static int color1 = Color.rgb(145, 219, 29);
	
	
	 /**
	 * ��ɫ
	 */
	public static int color2 = Color.rgb(255, 165, 57);
	 
	 
	   /**
	 * ��ɫ
	 */
	public static  int color3 = Color.rgb(247, 20, 20);
	
	public static int temper_min = 3500;
	
	/**
	 * ���¾��� �¶�
	 */
	public static final int temper_min_alert= 3551;
	
	
	public static final String PASSWORD_SAVE  = "passwordSave";
	
	//������֤��old�汾
//	public static final String APP_SMS_KEY ="54d3fcf2290d";
//	public static final String APP_SMS_SECRET = "171f4c745c728672e2429392204094e4";
	public static final String APP_SMS_KEY ="11b91d29ec5c0";
	public static final String APP_SMS_SECRET = "4dc1be42718373c6ada3bbd221bd0743";

	
	public static String phone_mac = "";
	
	/**
	 * �Ƿ�������ģʽ
	 */
	public static boolean isLineMode  = false;

}
