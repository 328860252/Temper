package com.medical.view;

import com.zby.medical.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

/**
 * 1 he 2 高度不一样嘛
 * @author Administrator
 * @2015-2-8 下午5:46:24
 */
public class ChartView extends View{
    public int XPoint=80;    //原点的X坐标
    public int YPoint=600;     //原点的Y坐标
    public int XScale= 90;     //X的刻度长度
    public int YScale= 100;     //Y的刻度长度
    public int XLength=380;        //X轴的长度
    public int YLength= 700;        //Y轴的长度
    public String[] XLabel;    //X的刻度
    public String[] YLabel;    //Y的刻度
    public String[] Data;      //数据
    public String Title;    //显示的标题
    
    
    //mRingColor = Color.rgb(255, 165, 57);
    //mRingColor = Color.rgb(247, 20, 20);
    public ChartView(Context context)
    {
        super(context);
    }
    public void SetInfo(String[] XLabels,String[] YLabels,String[] AllData,String strTitle)
    {
        XLabel=XLabels;
        YLabel=YLabels;
        Data=AllData;
        Title=strTitle;
        XLength = XScale * XLabels.length +5;
    }
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);//重写onDraw方法
 
        //canvas.drawColor(Color.WHITE);//设置背景颜色
        Paint paint= new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);//去锯齿
        paint.setColor(Color.BLACK);//颜色
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(15);
        
        Paint paint1=new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setTypeface(Typeface.DEFAULT_BOLD);
        paint1.setAntiAlias(true);//去锯齿
        paint1.setColor(Color.DKGRAY);
        paint.setTextSize(24);  //设置轴文字大小
        //设置Y轴
        canvas.drawLine(XPoint, YPoint-YLength, XPoint, YPoint, paint);   //轴线
        for(int i=0;i*YScale<YLength ;i++)                
        {
            canvas.drawLine(XPoint,YPoint-i*YScale, XPoint+5, YPoint-i*YScale, paint);  //刻度
            try
            {
                canvas.drawText(""+(Integer.parseInt(YLabel[i])/100) , XPoint-35, YPoint-i*YScale+5, paint);  //文字
            }
            catch(Exception e)
            {
            }
        }
        canvas.drawLine(XPoint,YPoint-YLength,XPoint-3,YPoint-YLength+6,paint);  //箭头
        canvas.drawLine(XPoint,YPoint-YLength,XPoint+3,YPoint-YLength+6,paint);            
        //设置X轴
        canvas.drawLine(XPoint,YPoint,XPoint+XLength,YPoint,paint);   //轴线
        
        paint.setColor(Color.rgb(255, 165, 57));
        canvas.drawLine(XPoint,YPoint-250,XPoint+XLength,YPoint-250,paint);   //轴线
        paint.setColor(Color.rgb(247, 20, 20));
        canvas.drawLine(XPoint,YPoint-350,XPoint+XLength,YPoint-350,paint);   //轴线
        paint.setColor(Color.BLACK);
        
        for(int i=0;i*XScale<XLength;i++)    
        {
            canvas.drawLine(XPoint+i*XScale, YPoint, XPoint+i*XScale, YPoint-5, paint);  //刻度
            try
            {
                canvas.drawText(XLabel[i] , XPoint+i*XScale-10, YPoint+20, paint);  //文字
                //数据值
                    if(i>0&&YCoord(Data[i-1])!=-999&&YCoord(Data[i])!=-999)  //保证有效数据
                        canvas.drawLine(XPoint+(i-1)*XScale, YCoord(Data[i-1]), XPoint+i*XScale, YCoord(Data[i]), paint);
                    canvas.drawCircle(XPoint+i*XScale,YCoord(Data[i]), 3, paint);
           }
            catch(Exception e)
            {
            }
        }
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint-3,paint);    //箭头
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint+3,paint);  
        paint.setTextSize(16);
        canvas.drawText(Title, 150, 50, paint);
    }
    private int YCoord(String y0)  //计算绘制时的Y坐标，无数据时返回-999
    {
        int y;
        try
        {
            y=Integer.parseInt(y0);
        }
        catch(Exception e)
        {
            return -999;    //出错则返回-999
        }
        try
        {
        	int y1 = y -Integer.parseInt(YLabel[0]);
        	int y2 = y1 ;
        	int yy = YPoint-y2;
        	return yy;
        	//int yy = YPoint-y*YScale/Integer.parseInt(YLabel[1]);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        System.out.println("y0");
        return y;
    }
}


