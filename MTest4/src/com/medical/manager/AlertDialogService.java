package com.medical.manager;



import com.zby.medical.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ËùÓÐµ¯³ö¿ò
 * @author Administrator
 *
 */


public class AlertDialogService {
	public static final int SelectImage = 55;
	
	public static final int WAITOUTOFTIME = 100;
	
	private static LayoutInflater li;
	//DIY confirmÉèÖÃ
	
	
	
	
	//ÊäÈë¿òÏÔÊ¾
	//µÈ´ý¿ò
	public static Dialog getWait(Context context, String str) {
//		LinearLayout ll=getLinear(context,str);
//		
//		//@android:style/Theme.Translucent
//				//ÎÄ×Ö¾ÓÖÐ¶Ô³Æ
//				View v = new View(context);
//				v.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
//				ll.addView(v);
//				ll.setPadding(30, 0, 30, 0);
//		Dialog d = getDialog(context);
//		d.setContentView(ll);
//		//Ä£ºý±³¾°
//		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		initLayoutInflater(context);
		View v=li.inflate(R.layout.alert_wait, null);
		TextView tv = (TextView) v.findViewById(R.id.textView_message);
		tv.setText(str);
		Dialog d = getDialog(context);
		d.setContentView(v);
		//Ä£ºý±³¾°
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		return d;
	}
	
	public static Dialog getWait(Context context, int strId) {
//		LinearLayout ll=getLinear(context,str);
//		
//		//@android:style/Theme.Translucent
//				//ÎÄ×Ö¾ÓÖÐ¶Ô³Æ
//				View v = new View(context);
//				v.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
//				ll.addView(v);
//				ll.setPadding(30, 0, 30, 0);
//		Dialog d = getDialog(context);
//		d.setContentView(ll);
//		//Ä£ºý±³¾°
//		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		initLayoutInflater(context);
		View v=li.inflate(R.layout.alert_wait, null);
		TextView tv = (TextView) v.findViewById(R.id.textView_message);
		tv.setText(context.getString(strId));
		Dialog d = getDialog(context);
		d.setContentView(v);
		//Ä£ºý±³¾°
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		return d;
	}
	
	public static Dialog getMsg(Context context,String msg){
		initLayoutInflater(context);
		View v=li.inflate(R.layout.alert_msg, null);
		v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TextView tv=(TextView) v.findViewById(R.id.textView);
		Button b=(Button) v.findViewById(R.id.confirm_button);
		tv.setText(msg);
		final Dialog d = getDialog(context);
		d.setContentView(v);
		//Ä£ºý±³¾°
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(d!=null&&d.isShowing()){
					d.dismiss();
				}
				
			}
		});
		return d;
	}
	
//	
//	
	public static Dialog getMsg(Context context,int id){
		initLayoutInflater(context);
		View v=li.inflate(R.layout.alert_msg, null);
		v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TextView tv=(TextView) v.findViewById(R.id.textView);
		Button b=(Button) v.findViewById(R.id.confirm_button);
//		fontService.setTypeface(tv);
//		fontService.setTypeface(b);
		tv.setText(id);
		final Dialog d = getDialog(context);
		d.setContentView(v);
		//Ä£ºý±³¾°
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(d!=null&&d.isShowing()){
					d.dismiss();
				}
				
			}
		});
		return d;
	}
	
	public static Dialog getMsg(Context context,int id,OnClickListener myOnClickListener){
		initLayoutInflater(context);
		View v=li.inflate(R.layout.alert_msg, null);
		v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TextView tv=(TextView) v.findViewById(R.id.textView);
		Button b=(Button) v.findViewById(R.id.confirm_button);
//		fontService.setTypeface(tv);
//		fontService.setTypeface(b);
		tv.setText(id);
		final Dialog d = getDialog(context);
		d.setContentView(v);
		//Ä£ºý±³¾°
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		b.setOnClickListener(myOnClickListener);
		return d;
	}
	
	
	
	public static Dialog getMsg(Context context,String id,OnClickListener myOnClickListener){
		initLayoutInflater(context);
		View v=li.inflate(R.layout.alert_msg, null);
		v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TextView tv=(TextView) v.findViewById(R.id.textView);
		Button b=(Button) v.findViewById(R.id.confirm_button);
//		fontService.setTypeface(tv);
//		fontService.setTypeface(b);
		tv.setText(id);
		final Dialog d = getDialog(context);
		d.setContentView(v);
		//Ä£ºý±³¾°
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		b.setOnClickListener(myOnClickListener);
		return d;
	}
	
	
	
//	
//	
//	
//	
//	
//	
	public static Dialog getDialog(Context context) {
		Dialog d = new Dialog(context, R.style.bg_null);
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		WindowManager.LayoutParams lp2 = d.getWindow().getAttributes();
		lp2.dimAmount = 0.5f;
		d.getWindow().setAttributes(lp2);
		d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
		
//		WindowManager.LayoutParams lp = d.getWindow().getAttributes();
//		lp.dimAmount = 0.55f;
//		d.getWindow().setAttributes(lp);
//		d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		return d;
	}
//	
//	public static Dialog getDialog(Activity context) {
//		Dialog d = new Dialog(context, R.style.bg_null);
//		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//		
//		WindowManager.LayoutParams lp2 = d.getWindow().getAttributes();
//		lp2.dimAmount = 0.5f;
//		d.getWindow().setAttributes(lp2);
//		d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
//				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
//		
////		WindowManager.LayoutParams lp = d.getWindow().getAttributes();
////		lp.dimAmount = 0.55f;
////		d.getWindow().setAttributes(lp);
////		d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//
//		return d;
//	}
//	
//
//
	private static void initLayoutInflater(Context context) {
		if(li==null){
			li=LayoutInflater.from(context);
		}
		
	}
//	
//	private static LinearLayout getLinear(Context context, String str) {
//		LinearLayout ll=new LinearLayout(context);		
//		ll.setBackgroundResource(R.drawable.button_white_on);
//		ll.setPadding(35, 0, 0, 0);
//		ll.setGravity(Gravity.CENTER);
//		
//		RotateImage ri=getRotateImage(context);
//		TextView tv=getTextView(context,str);
//Log.e("bjx", "AlertDialogService.getLinear.str  = " +  str  + " " + tv.getWidth() );
//		ll.addView(ri);
//		ll.addView(tv);
//		return ll;
//	}
//	
//	public static LinearLayout getLinear(Context context, String str, int id) {
//		LinearLayout ll=new LinearLayout(context);		
//		ll.setBackgroundResource(id);
//		ll.setPadding(35, 0, 0, 0);
//		ll.setGravity(Gravity.CENTER);
//		RotateImage ri=getRotateImage(context);
//		TextView tv=getTextView(context,str);
//		tv.setTextColor(Color.WHITE);
//		ll.addView(ri);
//		ll.addView(tv);
//		return ll;
//	}
//
//	private static TextView getTextView(Context context, String str) {
//		TextView tv =new TextView(context);
//		
//		tv.setText(str);
//		tv.setTextColor(0xffF89909);
//		tv.setTextSize(18);
//		tv.setPadding(20, 0, 0, 0);
//		tv.setGravity(Gravity.CENTER);
//		int width = (int)(AppConstantsCoreLib.phone_width * 0.6) ;
//		int height = (int)(AppConstantsCoreLib.phone_height * 0.1) ;
//		System.out.println("alertdialogService  " + width + ":" + height);
//		tv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
//		return tv;
//	}
//
//	private static RotateImage getRotateImage(Context context) {
//		RotateImage ri=new RotateImage(context, R.drawable.wait);
//		ri.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
//		ri.start();
//		return ri;
//	}
//	public static void closeDialog(final Dialog d, final int time) {
//		new Thread(){
//			@Override
//			public void run() {
//				super.run();
//				try {
//					sleep(time);
//					if(d!=null&&d.isShowing()){
//						d.dismiss();
//					}
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		}.start();
//		
//	}
//
//
//	public static void closeDialog(final Dialog d, final int time,final Handler h) {
//		new Thread(){
//			@Override
//			public void run() {
//				super.run();
//				try {
//					for (int i = 0; i < time/100; i++) {
//						sleep(100);
//						if(d==null&& !d.isShowing()){
//							return;
//						}
//					}
//					
//					if(d!=null&&d.isShowing()){
//						d.dismiss();
//						h.sendEmptyMessage(WAITOUTOFTIME);
//					}
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//		}.start();
//		
//	}

	





	

	
	

}
