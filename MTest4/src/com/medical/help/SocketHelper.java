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
 * 网络工具类
 * @author wt
 */
public class SocketHelper {
	/**
	 * 是否连接到网络
	 * @param mContext
	 * @return 是否连上网络
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
	 * 是否连接WIFI
	 * @param context
	 * @return 连接到wifi
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
	   * 检测是否连接上指定WIFI
	 * @param context
	 * @return  true，表示已经连上指定的AP。
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
	
	// 获取本机IP地址 没连接wifi或者飞行模式 返回null
	// 获取本机IP地址 没连接wifi或者飞行模式 返回null集成4.0版本
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
	 * 获得本机的ip地址， ip4格式
	 * @param ctx
	 * @return 返回本机的ip
	 */
	public static String getLocalIpAddress(Context ctx){

		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);		
		return ip;

	}
	
	
	  /**
	   * 获得网关
	   * @param context
	   * @return 网关
	   */
    public static String getGateWay(Context context){ 
    	WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE); 
    	DhcpInfo dhcpInfo = wifiManager.getDhcpInfo(); 
         
     //dhcpInfo获取的是最后一次成功的相关信息，包括网关、ip等  
    	String ip = intToIp(dhcpInfo.gateway);		
        return ip;      
    }
	
    
	/**
	 * iP格式转换
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
	 * 获得ip的最后一个数
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
	 * @return 本机的MAC
	 */
	public static String getLocalMacAddress(Context context) {  

        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
 
        WifiInfo info = wifi.getConnectionInfo();  

        return info.getMacAddress().replace(":", "");  

   }  

}
