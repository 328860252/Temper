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
package com.example.medical;

import java.io.File;

import com.medical.help.MyBitmap;
import com.medical.help.MyImage;
import com.medical.help.MyImage.ScalingLogic;
import com.zby.medical.R;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * <p>Description: ´°¿ÚÏÔÊ¾ÍøÂç×´Ì¬ </p>
 * @author zhujiang
 * @date 2015-1-19
 */
public class MessageActivity extends Activity {

	Bitmap  bm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_message2);
		
		String str = getIntent().getStringExtra("message");
		
//		TextView tv = (TextView) findViewById(R.id.textView);
//		tv.setText(""+str);
		
		//ÆÁÄ»¿í¶È
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);

		int phone_width = wm.getDefaultDisplay().getWidth();// ÆÁÄ»¿í¶È
		if(phone_width<10) phone_width =800;
		
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.linearLayout1);
		LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) layout.getLayoutParams();
//		params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
//		params.width =  LinearLayout.LayoutParams.WRAP_CONTENT;
		params.width = (int) (phone_width*0.8);
		params.height = (int) (phone_width*0.7);
		layout.setLayoutParams(params);
		
		
		bm = MyImage.decodeFile(str, 800, 800,
				new ScalingLogic());
//		ImageView iv = (ImageView) findViewById(R.id.imageView_img);
//		iv.setMaxHeight((int)(phone_width*0.8));
//		iv.setMaxWidth((int)(phone_width*0.8));
//		iv.setImageBitmap(bm);
		
		
		layout.setBackgroundDrawable(new BitmapDrawable(bm));
//		bm = MyBitmap.getBitmap2SDK(str);
		  //(new BitmapDrawable(bm));
		//  iv.setImageURI(Uri.fromFile(new File(str)));
	}
	
	
	public void btn_cancel(View v) {
		finish();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(bm!=null) {
			bm.recycle();
		}
		super.onDestroy();
	}
	
	
	

}
