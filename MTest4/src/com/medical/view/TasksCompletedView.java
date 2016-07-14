package com.medical.view;

import com.zby.medical.R;
import com.medical.constants.AppConstants;
import com.medical.help.MyDecimalHelp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.View;


/**
 * @author naiyu(http://snailws.com)
 * @version 1.0
 */
public class TasksCompletedView extends View {

	// 画实心圆的画笔
	private Paint mCirclePaint;
	// 画圆环的画笔
	private Paint mRingPaint;
	// 画字体的画笔
	private Paint mTextPaint;
	// 圆形颜色
	private int mCircleColor;
	// 圆环颜色
	private int mRingColor;
	// 半径
	private float mRadius;
	// 圆环半径
	private float mRingRadius;
	// 圆环宽度
	private float mStrokeWidth;
	// 圆心x坐标
	private int mXCenter;
	// 圆心y坐标
	private int mYCenter;
	// 字的长度
	private float mTxtWidth;
	// 字的高度
	private float mTxtHeight;
	// 总进度
	private int mTotalProgress = 100;
	// 当前进度
	private int mProgress;
	
	private int value, alert_value=3850;

	Color circle1, circle2,circle3;
	
	public TasksCompletedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获取自定义的属性
		initAttrs(context, attrs);
		initVariable();
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.TasksCompletedView, 0, 0);
		mRadius = typeArray.getDimension(R.styleable.TasksCompletedView_radius, 80);
		mStrokeWidth = typeArray.getDimension(R.styleable.TasksCompletedView_strokeWidth, 10);
		mCircleColor = typeArray.getColor(R.styleable.TasksCompletedView_circleColor, 0xFFFFFFFF);
		//mRingColor = typeArray.getColor(R.styleable.TasksCompletedView_ringColor, 0xFFFFFFFF);
		
		
		mRingRadius = mRadius + mStrokeWidth / 2;
	}

	private void initVariable() {
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setColor(mCircleColor);
		//mCirclePaint.setStyle(Paint.Style.FILL);//圆心也画满
		mCirclePaint.setStyle(Paint.Style.STROKE);
		
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setColor(mRingColor);
		mRingPaint.setStyle(Paint.Style.STROKE);
		mRingPaint.setStrokeWidth(mStrokeWidth);
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Paint.Style.FILL);
		//字体颜色
		//mTextPaint.setARGB(200, 150, 150, 150);
		//字体大小
		mTextPaint.setTextSize(mRadius / 3);
		
		FontMetrics fm = mTextPaint.getFontMetrics();
		mTxtHeight = (int) Math.ceil(fm.descent - fm.ascent);
		
	}

	@Override
	protected void onDraw(Canvas canvas) {

		mXCenter = getWidth() / 2;
		mYCenter = getHeight() / 2;
		
		//canvas.drawCircle(mXCenter, mYCenter, mRadius, mCirclePaint);
		
		if (mProgress >= 0 ) {
			RectF oval = new RectF();
			oval.left = (mXCenter - mRingRadius);
			oval.top = (mYCenter - mRingRadius);
			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint); //
//			canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);
			//温度值精确到小数点后一位
			String txt = ""+ MyDecimalHelp.getRoundFolat1(value/100.0);
			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
			canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);
		} 
//		else {
//			RectF oval = new RectF();
//			oval.left = (mXCenter - mRingRadius);
//			oval.top = (mYCenter - mRingRadius);
//			oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
//			oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
//			canvas.drawArc(oval, -90, ((float)mProgress / mTotalProgress) * 360, false, mRingPaint); //
////			canvas.drawCircle(mXCenter, mYCenter, mRadius + mStrokeWidth / 2, mRingPaint);
//			
//			String txt = "";
//			mTxtWidth = mTextPaint.measureText(txt, 0, txt.length());
//			canvas.drawText(txt, mXCenter - mTxtWidth / 2, mYCenter + mTxtHeight / 4, mTextPaint);
//		}
	}
	
	public void setProgress(int progress) {
		mProgress = progress;
//		invalidate();
		postInvalidate();
	}
	
	/**
	 * 温度值得 100倍数
	 * @param value  如37.5 就 3755
	 */
	public void setValue(int value) {
		if(value<3500 || value>4100) {
			mProgress =0;
		} else {
			mProgress = Math.round( ((value - 3500) /8.0f));
		}
		this.value = value;
//		if(value <alert_value || value <3850) { //比两个都小
//			this.setBackgroundResource(R.drawable.img_temper1);
//			mRingColor = AppConstants.color1;
//		} else if(value >=3850 && value >=alert_value) { //比两个都大
//			this.setBackgroundResource(R.drawable.img_temper3);
//			mRingColor = AppConstants.color3;
//		}  else { //比期中一个小， 比另一个大
//			this.setBackgroundResource(R.drawable.img_temper2);
//			mRingColor = AppConstants.color2;
//		} 
		if(value <=3700) { //比两个都小
			this.setBackgroundResource(R.drawable.img_temper1);
			mRingColor = AppConstants.color1;
		} else if(value >=3800 ) { //比两个都大
			this.setBackgroundResource(R.drawable.img_temper3);
			mRingColor = AppConstants.color3;
		}  else { //比期中一个小， 比另一个大
			this.setBackgroundResource(R.drawable.img_temper2);
			mRingColor = AppConstants.color2;
		} 
		mRingPaint.setColor(mRingColor);
		mTextPaint.setColor(mRingColor);
		postInvalidate();
	}
	
	public void setAlertValue(int alertValue) {
		this.alert_value = alertValue;
	}
	
	public int getValue() {
		return this.value;
	}
}
