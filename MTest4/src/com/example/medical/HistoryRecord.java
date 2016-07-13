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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.medical.constants.AppConstants;
import com.medical.help.CameraHelp;
import com.medical.help.MyBitmap;
import com.medical.help.SetupData;
import com.medical.sql.RecordBin;
import com.medical.sql.RecordSQLService;
import com.zby.medical.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * Description:用户记录 具体哪天到哪天的记录
 * </p>
 * 
 * @author zhujiang
 * @date 2014-9-18
 */
public class HistoryRecord extends Activity {

	private TextView tv_time;
	private EditText et_message;
	private ImageView iv_image;

	private RecordBin rbin;
	private RecordSQLService recordSQLService;

	private String img_path="";
	private String img_create ="";
	private String datetime="";
	
	private final int  activity_camera = 101;
	private final int activity_crop = 102;
	private SetupData mSetupData;
	private String lastMac= "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.temper_history_record);
		initViews();
		initDates();
	}

	private void initViews() {
		tv_time = (TextView) findViewById(R.id.textView_time);
		et_message = (EditText) findViewById(R.id.editText_message);
		iv_image = (ImageView) findViewById(R.id.imageView_image);
	}

	private void initDates() {
		recordSQLService = new RecordSQLService(this);
		datetime = getIntent().getStringExtra("datetime");
		mSetupData = SetupData.getSetupData(this);
		lastMac = mSetupData.read(SetupData.LastBluetooth, "");
		List<RecordBin> list = recordSQLService.queryDay2Day(datetime,
				lastMac);
		if (list.size() > 0) {
			rbin = list.get(0);
			tv_time.setText(rbin.getDateTime());
			et_message.setText(rbin.getMessage());
			BitmapDrawable bit = null;
			try {
				bit = MyBitmap.getBitmapDrawable2SDK(rbin.getImgPath());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			if(bit!=null) {
				img_path = rbin.getImgPath();
				iv_image.setBackgroundDrawable(bit);
			}
		} else {
			tv_time.setText(HistoryChartActivity.sdf.format(new Date()));
		}
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode) {
			case activity_camera:
			if (resultCode == RESULT_OK) {
				//第一种方式
				BitmapDrawable bit = null;
				img_path = img_create;
				try {
					bit = MyBitmap.getBitmapDrawable2SDK(img_path);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				if(bit!=null) {
					iv_image.setBackgroundDrawable(bit);
				}
				
				//剪切图片
//				img_path = img_create;
//				CropImage(img_path);
				
				
				//第二种方式
//				String sdStatus = Environment.getExternalStorageState();
//				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
//					Log.i("TestFile",
//							"SD card is not avaiable/writeable right now.");
//					return;
//				}
//				  String name = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";// 照片命名
//				//String name = new DateFormat().format("yyyyMMdd_hhmmss",Calendar.getInstance(Locale.CHINA)) + ".jpg";	
//				Toast.makeText(this, name, Toast.LENGTH_LONG).show();
//				Bundle bundle = data.getExtras();
//				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
//			
//				FileOutputStream b = null;
//				 try {
//		        	 img_create = Environment.getExternalStorageDirectory().getCanonicalPath() + "/"+AppConstants.folder+"/";
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					Log.v("tag", "创建文件夹",e);
//				}// 存放照片的文件夹
//		         			  
//		         String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
//		                .format(new Date()) + ".jpg";// 照片命名
//		        File out = new File(img_create);
//		        if (!out.exists()) {
//		        	Log.v("tag", "创建文件夹");
//		            out.mkdirs();
//		        }
//		        img_create = img_create + fileName;// 该照片的绝对路径
//		        img_path = img_create;
//				try {
//					b = new FileOutputStream(img_create);
//					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						b.flush();
//						b.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				iv_image.setImageBitmap(bitmap);
			}
		break;
			case activity_crop:
				String path = data.getStringExtra("path");
				iv_image.setImageURI(Uri.parse(path));
				break;
	
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void btn_back(View v) {
		finish();
	}
	
	public void btn_confirm(View v) {
		if (et_message.getText().toString().trim().equals("")) {
			Toast.makeText(this, R.string.input_first, 3000).show();
			return;
		}
		if(rbin==null) {
			rbin = new RecordBin();
			rbin.setId(-1);
		}
		rbin.setDateTime(HistoryChartActivity.sdf.format(new Date()));
		rbin.setMessage(et_message.getText().toString().trim());
		rbin.setHistoryData(datetime);
		rbin.setDeviceMac(lastMac);
		if (img_path!=null && !img_path.equals("")) {
			rbin.setImgPath(img_path);
		}
		if (rbin.getId() > -1) {
			recordSQLService.update(rbin);
		} else {
			recordSQLService.insert(rbin);
		}
		finish();
	}

	public void btn_image(View v) {
		//第一种方式， 拍出来的不太清晰
		  Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	         try {
	        	 img_create = Environment.getExternalStorageDirectory().getCanonicalPath() + "/"+AppConstants.folder+"/";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.v("tag", "创建文件夹",e);
			}// 存放照片的文件夹
	         			  
	         String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
	                .format(new Date()) + ".jpg";// 照片命名
	        File out = new File(img_create);
	        if (!out.exists()) {
	        	Log.v("tag", "创建文件夹");
	            out.mkdirs();
	        }
	        out = new File(img_create, fileName);
	        img_create = img_create + fileName;// 该照片的绝对路径
	        Log.v("tag","strImgPath:"+img_create);
	        Uri uri = Uri.fromFile(out);
	        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	        startActivityForResult(imageCaptureIntent, activity_camera);
		
		
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
//			startActivityForResult(intent, activity_camera); 
	}
	
	public void btn_show(View v) {
		if(img_path==null || img_path.equals("")){
			Toast.makeText(this, R.string.take_phone, 3000).show();
			return;
		}
		Intent intent = new Intent(this, PictureActivity.class);
		intent.putExtra("path", img_path);
		startActivity(intent);
	}
	
	
	/**
	 * 剪裁图片
	 * @param path 图片路径
	 */
	private void CropImage(String path) {
//		Intent in = new Intent(this, CropImageAcitivity.class);
//		in.putExtra("path", path);
//		startActivityForResult(in, activity_crop);
	}

}
