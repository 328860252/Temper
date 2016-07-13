package com.medical.help;



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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 所有弹出框
 * @author Administrator
 *
 */


public class AlertDialogService {
	public static final int SelectImage = 55;
	
	public static final int WAITOUTOFTIME = 100;
	
	private static LayoutInflater li;
	//DIY confirm设置
	
	public static AlertConfirmBin getAlertConfrim(Context context,String str){
		initLayoutInflater(context);
		View v=li.inflate(R.layout.alert_confirm, null);
		TextView tv=(TextView) v.findViewById(R.id.textView);
		Button cancel_Button=(Button) v.findViewById(R.id.cancel_button);
		Button confrim_Button=(Button) v.findViewById(R.id.confirm_button);
		tv.setText(str);
		Dialog d=getDialog(context);
		d.setContentView(v);
		
		AlertConfirmBin bin=new  AlertConfirmBin();
		bin.setD(d);
		bin.setTv(tv);
		bin.setCancel_Button(cancel_Button);
		bin.setConfrim_Button(confrim_Button);
		return bin;
	}
	
//	public static DiyConfirmBin getDiyConfrim(Activity a) {
//		DiyConfirmBin dcb=new DiyConfirmBin();
//		initLayoutInflater(a);
//		final Dialog d=getDialog(a);
//		
//		View v=li.inflate(R.layout.diy_confirm, null);
//		v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		
//		Button store=(Button) v.findViewById(R.id.store_button);
//		Button cooking=(Button) v.findViewById(R.id.cooking_button);
//		Button asc=(Button) v.findViewById(R.id.sac_button);
////		fontService.setTypeface(store);
////		fontService.setTypeface(cooking);
////		fontService.setTypeface(asc);
//		d.setContentView(v);
//		
//		dcb.setD(d);
//		dcb.setStore(store);
//		dcb.setCooking(cooking);
//		dcb.setStoreAndCooking(asc);
//		
//		return dcb;
//	}
	
	//输入框
	
	//输入框显示
	//等待框
	public static Dialog getWait(Context context, String str) {
		LinearLayout ll=getLinear(context,str);
		
		//@android:style/Theme.Translucent
		
		Dialog d = getDialog(context);
		d.setContentView(ll);
		//模糊背景
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		return d;
	}
	
	//等待框
	public static Dialog getWait1(Context context, String str) {
		
		LinearLayout ll=new LinearLayout(context);

		//ll.setBackgroundResource(R.drawable.button_white_on);
		ll.setBackgroundColor(0x00000000);
		ll.setGravity(Gravity.CENTER);
		RotateImage ri=getRotateImage(context);
		TextView tv=getTextView(context,str);
		ll.addView(ri);
		ll.addView(tv);
		
		//@android:style/Theme.Translucent
		Dialog d = getDialog(context);
		d.setContentView(ll);
		
		return d;
	}
	
	//每段完成后的提示，只在正在烹饪界面用
//	public static Dialog getMsg(Context context, String msg, final RawService rawService) {
//		initLayoutInflater(context);
//		View v=li.inflate(R.layout.alert_msg, null);
//		v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//		TextView tv=(TextView) v.findViewById(R.id.textView);
//		Button b=(Button) v.findViewById(R.id.confirm_button);
//		tv.setText(msg);
//		final Dialog d = getDialog(context);
//		d.setContentView(v);
//		//模糊背景
//		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//		rawService.startCyc();
//		d.setOnKeyListener(new OnKeyListener() {
//			
//			@Override
//			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
//				rawService.stop();
//				return false;
//			}
//		});
//		b.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if(d!=null&&d.isShowing()){
//					rawService.stop();
//					d.dismiss();
//				}
//			}
//		});
//		return d;
//	}
	
	public static Dialog getMsg(Context context,String msg){
		initLayoutInflater(context);
		View v=li.inflate(R.layout.alert_msg, null);
		v.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		TextView tv=(TextView) v.findViewById(R.id.textView);
		Button b=(Button) v.findViewById(R.id.confirm_button);
		tv.setText(msg);
		final Dialog d = getDialog(context);
		d.setContentView(v);
		//模糊背景
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
		//模糊背景
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
		//模糊背景
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
		//模糊背景
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		b.setOnClickListener(myOnClickListener);
		return d;
	}
	
	
	
	
	
	public static Dialog getMsg(Context context,int id,final Thread  myThread){
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
		//模糊背景
		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				myThread.start();
				if(d!=null&&d.isShowing()){
					d.dismiss();
				}
				
			}
		});
		return d;
	}
	
//	private static void initFontService(Context context) {
//		if(fontService==null){
//			fontService=new FontService(context);
//		}
//	}

	
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
	
	public static Dialog getDialog(Activity context) {
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
	
	private static Dialog getNolyDialog(Context context) {
		Dialog d = new Dialog(context, R.style.bg_null);
//		d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
//		
//		WindowManager.LayoutParams lp2 = d.getWindow().getAttributes();
//		lp2.dimAmount = 0.5f;
//		d.getWindow().setAttributes(lp2);
//		d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
//				| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
		
//		WindowManager.LayoutParams lp = d.getWindow().getAttributes();
//		lp.dimAmount = 0.55f;
//		d.getWindow().setAttributes(lp);
//		d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		return d;
	}
	


	private static void initLayoutInflater(Context context) {
		if(li==null){
			li=LayoutInflater.from(context);
		}
		
	}
	
	private static LinearLayout getLinear(Context context, String str) {
		LinearLayout ll=new LinearLayout(context);		
		ll.setBackgroundResource(R.drawable.img_btn_1);
		ll.setPadding(35, 0, 0, 0);
		ll.setGravity(Gravity.CENTER);
		RotateImage ri=getRotateImage(context);
		TextView tv=getTextView(context,str);
Log.e("bjx", "AlertDialogService.getLinear.str  = " +  str);
		ll.addView(ri);
		ll.addView(tv);
		return ll;
	}
	
	public static LinearLayout getLinear(Context context, String str, int id) {
		LinearLayout ll=new LinearLayout(context);		
		ll.setBackgroundResource(id);
		ll.setPadding(35, 0, 0, 0);
		ll.setGravity(Gravity.CENTER);
		RotateImage ri=getRotateImage(context);
		TextView tv=getTextView(context,str);
		tv.setTextColor(Color.WHITE);
		ll.addView(ri);
		ll.addView(tv);
		return ll;
	}

	private static TextView getTextView(Context context, String str) {
		TextView tv =new TextView(context);
		
		tv.setText(str);
		tv.setTextColor(0xffF89909);
		tv.setTextSize(18);
		tv.setPadding(20, 0, 0, 0);
		tv.setGravity(Gravity.CENTER);
		int width = 500 ;
		int height = 150 ;
		tv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
		return tv;
	}

	private static RotateImage getRotateImage(Context context) {
		RotateImage ri=new RotateImage(context, R.drawable.wait);
		ri.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
		ri.start();
		return ri;
	}
	public static void closeDialog(final Dialog d, final int time) {
		new Thread(){
			@Override
			public void run() {
				super.run();
				try {
					sleep(time);
					if(d!=null&&d.isShowing()){
						d.dismiss();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}.start();
		
	}


	public static void closeDialog(final Dialog d, final int time,final Handler h) {
		new Thread(){
			@Override
			public void run() {
				super.run();
				try {
					for (int i = 0; i < time/100; i++) {
						sleep(100);
						if(d==null&& !d.isShowing()){
							return;
						}
					}
					
					if(d!=null&&d.isShowing()){
						d.dismiss();
						h.sendEmptyMessage(WAITOUTOFTIME);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}.start();
		
	}

	





	

	
	

}
