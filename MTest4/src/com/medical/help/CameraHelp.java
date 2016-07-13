package com.medical.help;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class CameraHelp {
    public static String strImgPath;
    public static final int CameraId=101;
    
    private static String folder="cooker";
    
  //  private static String fileName;
	  public static void letCamera(Activity mContext, String fileName) {
	        // TODO Auto-generated method stub
	        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	        File out = new File(fileName);
	        if (!out.getParentFile().exists()) {
	            out.getParentFile().mkdirs();
	        }
	        if(out.exists()) {
	        	out.delete();
	        }
	        Log.v("tag","strImgPath:"+strImgPath);
	        Uri uri = Uri.fromFile(out);
	        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	        mContext.startActivityForResult(imageCaptureIntent, CameraId);
	    }
	  
	  
	  public static void letCamera(Activity mContext) {
	        // TODO Auto-generated method stub
	        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	         try {
				strImgPath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/"+folder+"/";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.v("tag", "创建文件夹",e);
			}// 存放照片的文件夹
	         			  
	         String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
	                .format(new Date()) + ".jpg";// 照片命名
	        File out = new File(strImgPath);
	        if (!out.exists()) {
	        	Log.v("tag", "创建文件夹");
	            out.mkdirs();
	        }
	        out = new File(strImgPath, fileName);
	        strImgPath = strImgPath + fileName;// 该照片的绝对路径
	        Log.v("tag","strImgPath:"+strImgPath);
	        Uri uri = Uri.fromFile(out);
	        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	        imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
	        mContext.startActivityForResult(imageCaptureIntent, CameraId);
	        
	    }
}
