package com.example.medical;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.medical.constants.AppConstants;
import com.medical.constants.AppString;
import com.medical.help.LanguageHelp;
import com.medical.help.MyImage;
import com.medical.help.SetupData;
import com.medical.help.MyImage.ScalingLogic;
import com.medical.manager.NetworkManager;
import com.zby.medical.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * <p>
 * Description: 引导图
 * </p>
 * 
 * @author zhujiang
 * @date 2014-5-28
 */
public class SettingGuideActivity extends Activity {

	private static final String TAG = "GuideViewActivity";

	private TextView tv_message;
	private Button btn_skip;
	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ViewGroup group;
	private ImageView imageView;
	private ImageView[] imageViews;

	// 滑动到边上时结束
	// private ImageView imageView_guideOver;

	private final int handler_show = 11;
	private final int handler_btn_show = 12;
	private final int handler_btn_dismiss = 13;

	private SetupData mSetupData;
	private NetworkManager mNetworkManager;
	
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide);
		initView();
		addListeners();
		imageViews = new ImageView[pageViews.size()];
		// group是R.layou.main中的负责包裹小圆点的LinearLayout.
		group = (ViewGroup) findViewById(R.id.layout_viewGroup);

		viewPager = (ViewPager) findViewById(R.id.guidePages);
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(SettingGuideActivity.this);
			imageView.setLayoutParams(new LayoutParams(30, 30));
			imageView.setPadding(50, 10, 50, 10);
			imageViews[i] = imageView;
			if (i == 0) {
				// 默认选中第一张图
				imageViews[i].setBackgroundResource(R.drawable.img_green_point);
			} else {
				imageViews[i].setBackgroundResource(R.drawable.gray_point);
			}
			group.addView(imageViews[i]);
		}

		viewPager.setAdapter(new GuidePageAdapter());
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());

		// title_TextView = (TextView) findViewById(R.id.title_textView);
		// title_TextView.setText(titlestring[index]);
		// if (index == 6 || (index == 5 && pageid<2) ) {
		// startBtn.setVisibility(View.VISIBLE);
		// }else {
		// startBtn.setVisibility(View.GONE);
		// }
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				break;
			case handler_btn_show:
				btn_skip.setVisibility(View.VISIBLE);
				break;
			case handler_btn_dismiss:
				btn_skip.setVisibility(View.INVISIBLE);
				break;
			case handler_show:
				String showStr = msg.arg1+"S";
				tv_message.setText(showStr);
				if(msg.arg1==1) {
					gotoLogin();
				}
				// switch(msg.arg1) {
				// case 0:
				// showStr = getString(R.string.guide_info1);
				// break;
				// case 1:
				// showStr = getString(R.string.guide_info2);
				// break;
				// case 2:
				// showStr = getString(R.string.guide_info3);
				// break;
				// case 3:
				// showStr = getString(R.string.guide_info4);
				// break;
				// case 4:
				// showStr = getString(R.string.guide_info5);
				// break;
				// case 5:
				// showStr = getString(R.string.guide_info6);
				// break;
				// case 6:
				// showStr = getString(R.string.guide_info7);
				// break;
				// case 7:
				// showStr = getString(R.string.guide_info8);
				// break;
				// }
				break;
			}
		}
	};

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}

	String pahtEnvir;

	/**
	 * 初始化控件
	 */
	/**
     * 
     */
	private void initView() {
		btn_skip = (Button) findViewById(R.id.btn_skip);
		btn_skip.setText(R.string.skip_now);
		btn_skip.setVisibility(View.GONE);
		 ImageView view1 = new ImageView(this);
		 ImageView view2 = new ImageView(this);
		 ImageView view3 = new ImageView(this);
		 ImageView view4 = new ImageView(this);
//		 ImageView view5 = new ImageView(this);
//		 ImageView view6 = new ImageView(this);
//		 ImageView view7 = new ImageView(this);
//		 ImageView view8 = new ImageView(this);
//		 ImageView view9 = new ImageView(this);
		
		 view1.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
				 R.drawable.guide1, 400,300, new ScalingLogic()));
		 view2.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
				 R.drawable.guide2, 400,300, new ScalingLogic()));
		 view3.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
				 R.drawable.guide3, 400,300, new ScalingLogic()));
		 view4.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
				 R.drawable.guide4, 400,300, new ScalingLogic()));
//		 if(getResources().getConfiguration().locale.getLanguage().toLowerCase().equals("en"))
//		 {
//			 view1.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide1_en, 400,300, new ScalingLogic()));
//			 view3.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide3_en, 400,300, new ScalingLogic()));
//			 view4.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide4_en, 400,300, new ScalingLogic()));
//			 view6.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide6_en, 400,300, new ScalingLogic()));
//			 view7.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide7_en, 400,300, new ScalingLogic()));
//			 view8.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide8_en, 400,300, new ScalingLogic()));
//		 } else {
//			 view1.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide1_cn, 400,300, new ScalingLogic()));
//			 view3.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide3_cn, 400,300, new ScalingLogic()));
//			 view4.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide4_cn, 400,300, new ScalingLogic()));
//			 view6.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide6_cn, 400,300, new ScalingLogic()));
//			 view7.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide7_cn, 400,300, new ScalingLogic()));
//			 view8.setBackgroundDrawable(MyImage.decodeFileBitmapDrawable(getResources(),
//			 R.drawable.guide8_cn, 400,300, new ScalingLogic()));
//		 }
		 pageViews = new ArrayList<View>();
		 pageViews.add(view1);
		 pageViews.add(view2);
		 pageViews.add(view3);
		 pageViews.add(view4);
//		 pageViews.add(view5);
//		 pageViews.add(view6);
//		 pageViews.add(view7);
//		 pageViews.add(view8);

		// 在最后一张,就划不动了, 要达到 最后一张,滑动跳转效果 , 就多加了一张图。
		// 当滑到最后多加的一张,就跳转
		// pageViews.add(view8);
		// imageView_guideOver = (ImageView)
		// findViewById(R.id.imageView_guideOver);
	}

	
	

	/**
	 * 本地文件夹保存路径
	 * 
	 * @param localPath
	 * @param url
	 * @return
	 */

	/**
	 * 添加监听
	 */
	private void addListeners() {
		// imageView_guideOver.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// finish();
		// }
		// });
	}
	
	
	public void btn_skip(View v) {
		finish();
	}
	
	private void gotoLogin() {
		if(!isFinishing()) {
			Intent intent =new Intent(this, UserLoginActivity.class);
			startActivity(intent);
			finish();
		}
	}

	// 按下时， 横坐标
	private int isScrolledCount;

	/** 指引页面Adapter */
	class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub
			if (pageViews.get(arg1).getParent() == null) {
				((ViewPager) arg0).addView(pageViews.get(arg1));
			}
			return pageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}
	}

	/**
	 * 指引页面改监听器, 小圆点显示正处于第几个页面
	 */
	class GuidePageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			// 最后一页划不动。
			// 进入最后一页，arg0 会出现一次
			// 然后滑动，arg0 就会多次出现
			if (arg0 == (imageViews.length - 1)) {
				isScrolledCount++;
				if (isScrolledCount > 1) {// 表示着界面往右滑动了
					finish();
				}
			} else {
				isScrolledCount = 0;
			}
		}

		@Override
		public void onPageSelected(int arg0) {
			// for (int i = 0; i < imageViews.length; i++) {
//			Message msg = handler.obtainMessage();
//			msg.what = handler_show;
//			msg.arg1 = arg0;
//			handler.sendMessage(msg);
			if(arg0 == (imageViews.length-1)) {
				handler.sendEmptyMessage(handler_btn_show);
			} else {
				handler.sendEmptyMessage(handler_btn_dismiss);
			}
		}

	}

}