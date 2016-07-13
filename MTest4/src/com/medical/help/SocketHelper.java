package com.medical.help;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * ���繤����
 * @author wt
 */
public class SocketHelper {
	/**
	 * �Ƿ����ӵ�����
	 * @param mContext
	 * @return �Ƿ���������
	 */
	public static boolean isnetworkConnected(Context mContext ){
		ConnectivityManager cwjManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if(cwjManager==null) return false;
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if(info== null) return false;
		if (info != null && info.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * �Ƿ�����WIFI
	 * @param context
	 * @return ���ӵ�wifi
	 */
    public static boolean linkWifi(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo==null) {
        	return false;
        }
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
     
        return false ;
    }

    /**
	   * ����Ƿ�������ָ��WIFI
	 * @param context
	 * @return  true����ʾ�Ѿ�����ָ����AP��
	 */
	public static boolean linkWifi(Context context, String ssid)
	    {
		  WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	        if(wifiInfo!=null && wifiInfo.getSSID()!=null ) {
	        	String wifiSSid = wifiInfo.getSSID().toLowerCase().replace("\"", "");
	        	System.out.println(wifiSSid + "  " + ssid.toLowerCase() + " " + (wifiSSid.equals(ssid.toLowerCase())));
	        	if(wifiSSid.equals(ssid.toLowerCase())){
	        		return true ;
	        	}
	        }
	     
	        return false ;
	    }
    
    

    private String getLanguageEnv() {
        Locale l = Locale.getDefault();
        String language = l.getLanguage();
        String country = l.getCountry().toLowerCase();
        if ("zh".equals(language)) {
            if ("cn".equals(country)) {
                language = "zh-CN";
            } else if ("tw".equals(country)) {
                language = "zh-TW";
            }
        } else if ("pt".equals(language)) {
            if ("br".equals(country)) {
                language = "pt-BR";
            } else if ("pt".equals(country)) {
                language = "pt-PT";
            }
        }
        return language;
    }
	
	// ��ȡ����IP��ַ û����wifi���߷���ģʽ ����null
	// ��ȡ����IP��ַ û����wifi���߷���ģʽ ����null����4.0�汾
//	public static String getLocalIpAddress(Context mContext) {
//		if (!SocketHelper.linkWifi(mContext)) {
//			return null;
//		}
//		String ipv4;
//		try {
//			for (Enumeration<NetworkInterface> en = NetworkInterface
//					.getNetworkInterfaces(); en.hasMoreElements();) {
//				NetworkInterface intf = en.nextElement();
//				for (Enumeration<InetAddress> enumIpAddr = intf
//						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					if (!inetAddress.isLoopbackAddress()
//							&& InetAddressUtils
//									.isIPv4Address(ipv4 = inetAddress
//											.getHostAddress())) {
//						// return inetAddress.getHostAddress().toString();
//						return ipv4;
//					}
//				}
//			}
//		} catch (SocketException ex) {
//			Log.e("WifiPreference IpAddress", ex.toString());
//			return "";
//		}
//		return null;
//	}
	
//	public static String getLocalIpAddress(Context mContext) throws SocketException{ 
//	    for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();en.hasMoreElements();){ 
//	        NetworkInterface intf = en.nextElement(); 
//	        for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){ 
//	            InetAddress inetAddress = enumIpAddr.nextElement(); 
//	            if(!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)){ 
//	                return inetAddress.getHostAddress().toString(); 
//	            } 
//	        } 
//	    } 
//	    return "null"; 
//	} 
	
	/**
	 * ��ñ�����ip��ַ�� ip4��ʽ
	 * @param ctx
	 * @return ���ر�����ip
	 */
	public static String getLocalIpAddress(Context ctx){

		// ��ȡwifi����
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		// �ж�wifi�Ƿ���
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);		
		return ip;

	}
	
	
	  /**
	   * �������
	   * @param context
	   * @return ����
	   */
    public static String getGateWay(Context context){ 
    	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
    	DhcpInfo dhcpInfo = wifiManager.getDhcpInfo(); 
         
     //dhcpInfo��ȡ�������һ�γɹ��������Ϣ���������ء�ip��  
    	String ip = intToIp(dhcpInfo.gateway);		
        return ip;      
    }
	
    
	/**
	 * iP��ʽת��
	 * @param ipint
	 * @return
	 */
	public static String intToIp(int ipint)
	{
	    int b1,b2,b3,b4;
	    b1=(ipint & 0xff000000)>>24;
	    if (b1<0) b1+=0x100;
	    b2=(ipint & 0x00ff0000)>>16;
	    if (b2<0) b2+=0x100;
	    b3=(ipint & 0x0000ff00)>>8;
	    if (b3<0) b3+=0x100;
	    b4= ipint & 0x000000ff;
	    if (b4<0) b4+=0x100;
	    return ""+b4+"."+b3+"."+b2+"."+b1;
	}

	
	
	/**
	 * ���ip�����һ����
	 * @param ip
	 * @return
	 */
	public static String IpNoLast(String ip){
		if(ip.equals("")||ip==null){
			return null;
		}
		int i=ip.lastIndexOf(".");
		String str=ip.substring(0,i+1);
		return str;
	}
	
	
	/**
	 * @param context
	 * @return ������MAC
	 */
	public static String getLocalMacAddress(Context context) {  

        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
 
        WifiInfo info = wifi.getConnectionInfo();  

        return info.getMacAddress().replace(":", "");  

   }  

}
