package com.medical.sql;

/**
 * <p>Description:  �ر�ע�� 
 *     bluetooth �� ��  ������״̬�£� ����mac �� û�� phoneAccount
 *     			   ���������״̬������ɹ���״̬�£� ����account  Ҳ�����û��ֻ��ţ� û��mac�� ���������ص�������û��mac��</p>
 * @author zhujiang
 * @date 2015-1-21
 */
public class HistoryBin {
	
	private int id;
	private String value="";
	private String date="";
	private int isShowStatus;   //0 ����ͨ��  1��һ����ʾ ��ɫ����   ʱ�� �֣� 2��2����ʾ   ��ɫ����   �죺ʱ����
	private String BluetoothMac="";
	
	/**
	 * �ֻ��˺�
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
	 * @return  //0 ����ͨ��  1��һ����ʾ ��ɫ����   ʱ�� �֣� 2��2����ʾ   ��ɫ����   �죺ʱ����
	 */
	public int getShowStatus() {
		return isShowStatus;
	}
	/**
	 * @param isShow  //0 ����ͨ��  1��һ����ʾ ��ɫ����   ʱ�� �֣� 2��2����ʾ   ��ɫ����   �죺ʱ����
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
	 * ��������������ݵ㣬 ������������µ����ݵ�
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
