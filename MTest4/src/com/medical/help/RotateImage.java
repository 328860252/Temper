package com.medical.help;

import java.util.ArrayList;
import java.util.List;




import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

//����SurfaceView ��ͣ��ˢ�� ��ת���ͼƬ���ŵ� �����ܽ��� �����ռ�ˢ�µ�Ӱ��
public class RotateImage extends SurfaceView implements SurfaceHolder.Callback {
	private SurfaceHolder holder;
	private MyThread2 myThread;
	private int imageId;
	private Context context;
	public RotateImage(Context context, int imageId) {
		super(context);
		this.imageId=imageId;
		this.context=context;
		

	//	getHolder().setFormat(PixelFormat.TRANSPARENT);// ����Ϊ͸��
		// getHolder().setFormat(PixelFormat.TRANSLUCENT);//��͸��
		setZOrderOnTop(true);//���û���  ����͸��
	    getHolder().setFormat(PixelFormat.TRANSLUCENT);
		getHolder().addCallback(this);
		setFocusable(true);

		// setBackgroundResource(R.drawable.mode_bg);
		// setBackgroundColor(0x00000000);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.v("tag", "surfaceCreated");
		myThread = new MyThread2(getHolder(), imageId, context);// ����һ����ͼ�߳�
		myThread.isRun = true;
		myThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v("tag", "surfaceDestroyed");
		myThread.isRun = false;
		
	}

	public void start() {
		speed=1;
	}

	public void stop() {
		speed=0;

	}

	public void setSpeed(int i) {
		if(speed>15){
			speed=0;
		}
		speed++;
	}
	public static int speed=1;//ת���ٶ�
}

// �߳��ڲ���
class MyThread2 extends Thread {
	private SurfaceHolder holder;
	public boolean isRun;
	private Context context;
	private int imageId;
	private Bitmap b;
	private Paint p;
	
	//��ת����

	private int view_height;//view �߶�
	private int view_width;//
	private int b_height;
	private int b_width;
	private int b_x;
	private int b_y;
	private Matrix mMatrix = new Matrix();
	private Matrix mMatrix1 = new Matrix();
	private Matrix mMatrix2 = new Matrix();
	int rotate=0;
	
	public MyThread2(SurfaceHolder holder, int imageId, Context context) {
		this.imageId = imageId;
		this.holder = holder;
		isRun = true;
		this.context = context;
		p = new Paint();
	}

	@Override
	public void run() {

		while (isRun) {
			Canvas c = null;
			try {
				synchronized (holder) {
					c = holder.lockCanvas();// ����������һ����������Ϳ���ͨ���䷵�صĻ�������Canvas���������滭ͼ�Ȳ����ˡ�
				//	c.drawColor(0x00000000);// ���û���������ɫ
					init(c);
					Matrix mMatrix = getMatrix();			
					clearCanvas(c); 
		          	c.drawBitmap(b, mMatrix, p);

				//	MyLog.log("c:" + (c == null) );

				//	Thread.sleep(500);// ˯��ʱ��Ϊ1��
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (c != null) {
					holder.unlockCanvasAndPost(c);// ����������ͼ�����ύ�ı䡣

				}
			}
		}
	}
	//��ջ���
	private void clearCanvas(Canvas c) {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		c.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
	}

	private Matrix getMatrix() {

		mMatrix1.setRotate(rotate, b_width / 2 + b_x, b_height / 2 + b_y);// ��ת�Ƕȣ����ĵ�X�����ĵ�Y

		mMatrix2.setTranslate(b_x, b_y);

		mMatrix.setConcat(mMatrix1, mMatrix2);

		rotate += RotateImage.speed;
		if (rotate >= 360) {
			rotate = 0;
		}
		return mMatrix;
	}

	private void init(Canvas c) {
		if(b!=null){
			return;
		}
		initB(c);
		initSize() ;
	}
	
	private void initSize() {
			
			b_height=b.getHeight();
			b_width=b.getWidth();
			b_x=view_width/2-b_width/2;
			b_y=view_height/2-b_height/2;
			
			
	}
	private void initB(Canvas c) {
		if (b != null) {
			return;
		}
		Bitmap b2=BitmapFactory.decodeResource(context.getResources(), imageId);
		view_width=c.getWidth();
		view_height=c.getHeight();
		
//		float f1=view_width;
//		float f2=b2.getWidth();
//		
//		float f=f1/f2;
		
		float f=0f;
		if(view_height<view_width){
		//	f=view_height/b2.getHeight();
			f=(float)view_height/(float)b2.getHeight();
		}else{
		//	f=view_width/b2.getWidth();
//			float f1=view_width;
//			float f2=b2.getWidth();
//			
//			f=f1/f2;
			
			f=(float)view_width/(float)b2.getWidth();
		}
		b=MyBitmap.zoomBitmap(b2, f);

	}

}
