package com.medical.view;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
* 修改Toast显示，多个Toast只显示最后一个，显示3秒之后消失
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

	  //固定时间是因为，若是设置的时间1秒，使用的效果会不太好，所以就固定显示2.5秒
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

	  //固定时间是因为，若是设置的时间1秒，使用的效果会不太好，所以就固定显示2.5秒
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
