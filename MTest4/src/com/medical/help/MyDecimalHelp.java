/*
 * Copyright (c) Guangzhou Waytronic Electronics 
 * 
 * 		Waytronic specializing in intelligent terminal application research and extension,
 * 	intelligent speech technology research and design. 
 * 		Intelligent business was established in 2011, research and development and 
 *	a variety of intelligent mobile phone listing for individual center products. 
 *	Intelligence division to provide a series of intelligent terminal product solutions, 
 *	with each brand enterprise, realizing the intelligent products. Provide 
 * 	customized software, hardware customization, customer service support and a series 
 *	of cooperation model.
 *
 *		 http://www.wt-smart.com 
 *		 http://www.w1999c.com
 */
package com.medical.help;

/**
 * <p>Description: ����ת�������� </p>
 * @author zhujiang
 * @date 2014-12-18
 */
public class MyDecimalHelp {

	/**
	 * ���   ��������� ������С�����һλ����
	 * @param d
	 * @return
	 */
	public static float getRoundFolat1(double d) {
		return (float) ((Math.round(d*10))/10.0);
	}
	
	/**
	 * ���   ��������� ������С�����2λ����
	 * @param d
	 * @return
	 */
	public static float getRoundFolat2(double d) {
		return (float) ((Math.round(d*100))/100.0);
	}
	
	/**
	 * ���   ��������� ������С�����3λ����
	 * @param d
	 * @return
	 */
	public static float getRoundFolat3(double d) {
		return (float) ((Math.round(d*1000))/1000.0);
	}
	
	public static void main(String[] args) {
		System.out.println(getRoundFolat1(18.5/4.0));
		System.out.println(getRoundFolat2(18.5/4.0));
		System.out.println(getRoundFolat3(18.5/4.0));
	}
	
}
