package com.medical.help;

import java.util.Locale;

import com.medical.constants.AppString;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * ���Ըı�
 * @author zhugege
 * @2014-10-11 ����4:37:57
 */
public class LanguageHelp {
	
	public static void switchLanguage(Context context,int langItem) {
		Locale locale = getLanguageShort(langItem);
        Resources resources = context.getResources();// ���res��Դ����
        Configuration config = resources.getConfiguration();// ������ö���
        DisplayMetrics dm = resources.getDisplayMetrics();// �����Ļ��������Ҫ�Ƿֱ��ʣ����صȡ�
        config.locale = locale; // ��������
        resources.updateConfiguration(config, dm);
    }
	
	/**
	 * ������Ե�  ��ĸ��д
	 * @param langItem ���Ա��
	 * @return 0�������� �� 1����en �� 2���巵�� zh_tw
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
