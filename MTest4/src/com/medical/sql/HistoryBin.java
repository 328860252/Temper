package com.medical.sql;

/**
 * <p>Description:  特别注意 
 *     bluetooth ， 在  无网络状态下， 就有mac ， 没有 phoneAccount
 *     			   在有网络的状态，登入成功的状态下， 就有account  也就是用户手机号， 没有mac， 服务器返回的数据是没有mac的</p>
 * @author zhujiang
 * @date 2015-1-21
 */
public class HistoryBin {
	
	private int id;
	private String value="";
	private String date="";
	private int isShowStatus;   //0 是普通，  1是一级显示 橙色字体   时： 分， 2是2级显示   红色字体   天：时：分
	private String BluetoothMac="";
	
	/**
	 * 手机账号
	 */
	private String  phoneAccount = "";
	
	private boolean isOnline ;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @return  //0 是普通，  1是一级显示 橙色字体   时： 分， 2是2级显示   红色字体   天：时：分
	 */
	public int getShowStatus() {
		return isShowStatus;
	}
	/**
	 * @param isShow  //0 是普通，  1是一级显示 橙色字体   时： 分， 2是2级显示   红色字体   天：时：分
	 */
	public void setShowStatus(int isShow) {
		this.isShowStatus = isShow;
	}
	public String getBluetoothMac() {
		return BluetoothMac;
	}
	
	public void setBluetoothMac(String bluetoothMac) {
		BluetoothMac = bluetoothMac;
	}
	/**
	 * 是在线情况的数据点， 还是离线情况下的数据点
	 * @return
	 */
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public String getPhoneAccount() {
		return phoneAccount;
	}
	public void setPhoneAccount(String phoneAccount) {
		this.phoneAccount = phoneAccount;
	}

	
	
}
