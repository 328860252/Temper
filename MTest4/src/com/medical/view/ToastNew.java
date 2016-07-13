package com.medical.view;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
* �޸�Toast��ʾ�����Toastֻ��ʾ���һ������ʾ3��֮����ʧ
* @author Administrator
*
*/
public class ToastNew {
	
	private static Toast mToast = null; 
	 private static Handler mHandler = new Handler();
	    private static Runnable r = new Runnable() {
	        public void run() {
	            mToast.cancel();
	        }
	    };

	  //�̶�ʱ������Ϊ���������õ�ʱ��1�룬ʹ�õ�Ч���᲻̫�ã����Ծ͹̶���ʾ2.5��
	    public static Toast makeText(Context context, String text, int duration) {
	    	  mHandler.removeCallbacks(r);
		        if (mToast != null) {
		        	mToast.setText(text);
		        	mToast.setDuration(3);
		        } else {
		        	mToast = Toast.makeText(context, text, 3);
		        }
		        mHandler.postDelayed(r, 2555);
		        return mToast;
	    }
	    
	public static Toast makeText(Context context , String text) {
		return makeText(context, text, 3);
	}
	
	
	public static Toast makeText(Context context, int resId) {
		return makeText(context, resId, 3);
	}
	
	public static Toast makeText(Context context, int resId, int duration) {
		 mHandler.removeCallbacks(r);
	        if (mToast != null) {
	        	//mToast.cancel();
	        	//mToast = Toast.makeText(context, resId, 3);
	        	mToast.setText(resId);
	        	mToast.setDuration(3);
	     	    //toast.setView(toastRoot);
	        } else {
	        	mToast = Toast.makeText(context, resId, 3);
	        }
	        mHandler.postDelayed(r, 2555);
	       return mToast;
	}
	
	public static Toast makeTextMid(Context context, int resId, int duration) {
		 mHandler.removeCallbacks(r);
	        if (mToast != null) {
	        	//mToast.cancel();
	        	//mToast = Toast.makeText(context, resId, 3);
	        	mToast.setText(resId);
	        	mToast.setDuration(3);
	        	mToast.setGravity(Gravity.CENTER|Gravity.CENTER, mToast.getXOffset()/2, mToast.getYOffset() / 2);
	        	mToast.setMargin(0, 0.1f);
	     	    //toast.setView(toastRoot);
	        } else {
	        	mToast = Toast.makeText(context, resId, 3);
	        }
	        mHandler.postDelayed(r, 2555);
	       return mToast;
	}

	  //�̶�ʱ������Ϊ���������õ�ʱ��1�룬ʹ�õ�Ч���᲻̫�ã����Ծ͹̶���ʾ2.5��
    public static Toast makeTextMid(Context context, String text, int duration) {
    	  mHandler.removeCallbacks(r);
	        if (mToast != null) {
	        	mToast.setText(text);
	        	mToast.setDuration(3);
	        	mToast.setGravity(Gravity.CENTER|Gravity.CENTER, mToast.getXOffset()/2, mToast.getYOffset() / 2);
	        	mToast.setMargin(0, 0.1f);
	        } else {
	        	mToast = Toast.makeText(context, text, 3);
	        }
	        mHandler.postDelayed(r, 2555);
	        return mToast;
    }
}
