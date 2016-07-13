package com.medical.help;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;
//图片处理类
public class MyBitmap {

	//变圆形
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	// 按大小缩放
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		if (bitmap.isRecycled()) {
			bitmap.recycle();
		}
		if (newbmp.isRecycled()) {
			newbmp.recycle();
		}
		return newbmp;
	}

	// 按比例缩放
	public static Bitmap zoomBitmap(Bitmap bitmap, float f) {
		if (bitmap == null) {
			return null;
		}
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		// float scaleWidht = ((float) w / width);
		// float scaleHeight = ((float) h / height);
		matrix.postScale(f, f);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	// 从SDK卡上获取图片
	public static Bitmap getBitmap2SDK(String path) {
		try {
			if (path == null) {
				return null;
			}
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);

			opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
			opts.inJustDecodeBounds = false;

			try {
				Bitmap bmp = BitmapFactory.decodeFile(path, opts);
				return bmp;
			} catch (OutOfMemoryError err) {
			}
		} catch (Exception e) {
			return null;
		}
		return null;

	}
	
	
	/**
	 * 从SD卡上获取赌片
	 * @param path 地址
	 * @return 
	 */
	public static BitmapDrawable getBitmapDrawable2SDK(String path) throws FileNotFoundException {
			if (path == null) {
				return null;
			}
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);

			opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
			opts.inJustDecodeBounds = false;

			Bitmap bmp = BitmapFactory.decodeFile(path, opts);
			if(bmp==null) {
				return null;
			}
			BitmapDrawable bd = new BitmapDrawable(bmp);
			bmp =null;
			return bd;
	}


	// 帮助方法

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	//
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	
	
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	        bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 12;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	}
	
	
//	public static Bitmap Url2BitMap(String url) {   
//		   URL myFileUrl = null;   
//		   Bitmap bitmap = null;   
//		   try {   
//		    myFileUrl = new URL(url);   
//		   } catch (MalformedURLException e) {   
//		    e.printStackTrace();   
//		   }   
//		   try {   
//		    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();   
//		    conn.setDoInput(true);   
//		    conn.connect();   
//		    InputStream is = conn.getInputStream();   
//		    bitmap = BitmapFactory.decodeStream(is);   
//		    is.close();   
//		   } catch (IOException e) {   
//		    e.printStackTrace();   
//		   }   
//		   return bitmap;   
//		} 
	
	
	private static  String srcDir;
//	public static Bitmap Url2BitMap(String url) throws IOException {   
//		srcDir = Environment.getExternalStorageDirectory().getCanonicalPath();
//		srcDir = srcDir + MenuShowActivity.ImportImagePath;
//		String srcPath = srcDir + "/"
//				+ url.split("http://polaris.proactivity.ru/")[1];
//		File f = new File(srcPath);
//		// 创建文件夹
//		File parent = new File(f.getParent());
//		if (!parent.exists()) {
//			parent.mkdirs();
//		}
//		// 获得bitmap对象
//		if (f.exists()) {
//			return getBitmap2SDK(srcPath);
//		}
//
//		URL myFileUrl = null;
//		Bitmap bitmap = null;
//		try {
//			myFileUrl = new URL(url);
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		try {
//			HttpURLConnection conn = (HttpURLConnection) myFileUrl
//					.openConnection();
//			conn.setDoInput(true);
//			conn.connect();
//			InputStream is = conn.getInputStream();
//			// bitmap = BitmapFactory.decodeStream(is);
//			// is.close();
//			DataInputStream in = new DataInputStream(is);
//			// if(isheader) {
//			// Drawable d = Drawable.createFromStream(i, "src");
//			// return d;
//			// }
//			FileOutputStream out = new FileOutputStream(f);
//			byte[] buffer = new byte[1024];
//			int byteread = 0;
//			while ((byteread = in.read(buffer)) != -1) {
//				out.write(buffer, 0, byteread);
//			}
//			in.close();
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return Url2BitMap(url);
//	}
//	
//	public static String Url2File(String url) throws IOException {   
//		if (url == null || url.equals("") || url.equals(" ") ) {
//			return "";
//		}
//		try {
//			srcDir = Environment.getExternalStorageDirectory().getCanonicalPath();
//			srcDir = srcDir + MenuShowActivity.ImportImagePath;
//			String srcPath = srcDir + "/"
//					+ url.split("https://wificook.ru/")[1];//https://wificook.ru/upload/iblock/fc0/fc019933b4c4bffc2ae0fedec0d3a4ad.jpg
//			File f = new File(srcPath);
//			// 创建文件夹
//			File parent = new File(f.getParent());
//			if (!parent.exists()) {
//				parent.mkdirs();
//			}
//			// 获得bitmap对象
//			if (f.exists()) {
//				return srcPath;
//			}
//
//			URL myFileUrl = null;
//			Bitmap bitmap = null;
//			try {
//				myFileUrl = new URL(url);
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			}
//			try {
//				HttpURLConnection conn = (HttpURLConnection) myFileUrl
//						.openConnection();
//				conn.setDoInput(true);
//				conn.connect();
//				InputStream is = conn.getInputStream();
//				// bitmap = BitmapFactory.decodeStream(is);
//				// is.close();
//				DataInputStream in = new DataInputStream(is);
//				// if(isheader) {
//				// Drawable d = Drawable.createFromStream(i, "src");
//				// return d;
//				// }
//				FileOutputStream out = new FileOutputStream(f);
//				byte[] buffer = new byte[1024];
//				int byteread = 0;
//				while ((byteread = in.read(buffer)) != -1) {
//					out.write(buffer, 0, byteread);
//				}
//				in.close();
//				out.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return srcPath;
//		} catch (Exception e) {
//			// TODO: handle exception
//			return "";
//		}
//		
//	}

}
