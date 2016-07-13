package com.medical.view;

import com.zby.medical.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

/**
 * 1 he 2 �߶Ȳ�һ����
 * @author Administrator
 * @2015-2-8 ����5:46:24
 */
public class ChartView extends View{
    public int XPoint=80;    //ԭ���X����
    public int YPoint=600;     //ԭ���Y����
    public int XScale= 90;     //X�Ŀ̶ȳ���
    public int YScale= 100;     //Y�Ŀ̶ȳ���
    public int XLength=380;        //X��ĳ���
    public int YLength= 700;        //Y��ĳ���
    public String[] XLabel;    //X�Ŀ̶�
    public String[] YLabel;    //Y�Ŀ̶�
    public String[] Data;      //����
    public String Title;    //��ʾ�ı���
    
    
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
        super.onDraw(canvas);//��дonDraw����
 
        //canvas.drawColor(Color.WHITE);//���ñ�����ɫ
        Paint paint= new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);//ȥ���
        paint.setColor(Color.BLACK);//��ɫ
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setTextSize(15);
        
        Paint paint1=new Paint();
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setTypeface(Typeface.DEFAULT_BOLD);
        paint1.setAntiAlias(true);//ȥ���
        paint1.setColor(Color.DKGRAY);
        paint.setTextSize(24);  //���������ִ�С
        //����Y��
        canvas.drawLine(XPoint, YPoint-YLength, XPoint, YPoint, paint);   //����
        for(int i=0;i*YScale<YLength ;i++)                
        {
            canvas.drawLine(XPoint,YPoint-i*YScale, XPoint+5, YPoint-i*YScale, paint);  //�̶�
            try
            {
                canvas.drawText(""+(Integer.parseInt(YLabel[i])/100) , XPoint-35, YPoint-i*YScale+5, paint);  //����
            }
            catch(Exception e)
            {
            }
        }
        canvas.drawLine(XPoint,YPoint-YLength,XPoint-3,YPoint-YLength+6,paint);  //��ͷ
        canvas.drawLine(XPoint,YPoint-YLength,XPoint+3,YPoint-YLength+6,paint);            
        //����X��
        canvas.drawLine(XPoint,YPoint,XPoint+XLength,YPoint,paint);   //����
        
        paint.setColor(Color.rgb(255, 165, 57));
        canvas.drawLine(XPoint,YPoint-250,XPoint+XLength,YPoint-250,paint);   //����
        paint.setColor(Color.rgb(247, 20, 20));
        canvas.drawLine(XPoint,YPoint-350,XPoint+XLength,YPoint-350,paint);   //����
        paint.setColor(Color.BLACK);
        
        for(int i=0;i*XScale<XLength;i++)    
        {
            canvas.drawLine(XPoint+i*XScale, YPoint, XPoint+i*XScale, YPoint-5, paint);  //�̶�
            try
            {
                canvas.drawText(XLabel[i] , XPoint+i*XScale-10, YPoint+20, paint);  //����
                //����ֵ
                    if(i>0&&YCoord(Data[i-1])!=-999&&YCoord(Data[i])!=-999)  //��֤��Ч����
                        canvas.drawLine(XPoint+(i-1)*XScale, YCoord(Data[i-1]), XPoint+i*XScale, YCoord(Data[i]), paint);
                    canvas.drawCircle(XPoint+i*XScale,YCoord(Data[i]), 3, paint);
           }
            catch(Exception e)
            {
            }
        }
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint-3,paint);    //��ͷ
        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint+3,paint);  
        paint.setTextSize(16);
        canvas.drawText(Title, 150, 50, paint);
    }
    private int YCoord(String y0)  //�������ʱ��Y���꣬������ʱ����-999
    {
        int y;
        try
        {
            y=Integer.parseInt(y0);
        }
        catch(Exception e)
        {
            return -999;    //�����򷵻�-999
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


