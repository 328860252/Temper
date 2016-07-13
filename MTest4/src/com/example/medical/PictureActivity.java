package com.example.medical;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.medical.help.MyBitmap;
import com.medical.view.BitmapUtil;
import com.medical.view.DragImageView;
import com.zby.medical.R;

import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * @author Administrator
 * �¶ȱ�����ʾͼƬ
 */
public class PictureActivity extends Activity {
	private int window_width, window_height;// �ؼ����
	private DragImageView dragImageView;// �Զ���ؼ�
	private int state_height;// ״̬���ĸ߶�

	private ViewTreeObserver viewTreeObserver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.picture_show);
		/** ��ȡ��Ҋ����߶� **/
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		dragImageView = (DragImageView) findViewById(R.id.div_main);
//		dragImageView.setAdjustViewBounds(true);
//		dragImageView.setMinimumHeight(window_height/2);
//		dragImageView.setMinimumWidth(window_width/2);
		String path = getIntent().getStringExtra("path");
		Bitmap bmp = BitmapUtil.ReadBitmapBySD(path, window_width, window_height);
		//BitmapDrawable bmp =null;
//		try {
//			bmp = MyBitmap.getBitmapDrawable2SDK(path);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
		if(bmp==null) {
			Toast.makeText(this, "δ�ҵ�ͼƬ", 3).show();
			finish();
			return;
		} else {
			dragImageView.setImageBitmap(bmp);
			//dragImageView.setImageDrawable(bmp);
		}
		// ����ͼƬ
		dragImageView.setmActivity(this);//ע��Activity.
		/** ����״̬���߶� **/
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// ��ȡ״�����߶�
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height-state_height);
							dragImageView.setScreen_W(window_width);
						}

					}
				});

	}
	
	public void btn_back(View v) {
		finish();
	}

	/**
	 * ��ȡ������Դ��ͼƬ
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// ��ȡ��ԴͼƬ
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

}