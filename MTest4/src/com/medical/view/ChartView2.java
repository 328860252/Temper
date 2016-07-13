package com.medical.view;

import java.util.List;

import com.zby.medical.R;
import com.medical.constants.AppConstants;
import com.medical.sql.HistoryBin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.view.View;

public class ChartView2 extends View{
    public int XPoint=80;    //原点的X坐标
    public int YPoint=650;     //原点的Y坐标
    public int XScale= 90;     //X的刻度长度
    public int YScale= 100;     //Y的刻度长度
    public int XLength=380;        //X轴的长度
    public int YLength= 650;        //Y轴的长度
  //  public String[] XLabel;    //X的刻度
    public String[] YLabel;    //Y的刻度
    public List<HistoryBin> Data;      //数据
    public String Title;    //显示的标题
    
    
    
    private int alert_value;//红色禁戒线
    
    //mRingColor = Color.rgb(255, 165, 57);
    //mRingColor = Color.rgb(247, 20, 20);
    public ChartView2(Context context, int alert_value)
    {
        super(context);
        this.alert_value = alert_value;
    }
    public void SetInfo(String[] YLabels,List<HistoryBin> AllData,String strTitle)
    {
        YLabel=YLabels;
        Data=AllData;
        Title=strTitle;
        XLength = XScale * AllData.size() +5;
        if(XLength<500) {
        	XLength = 500;
        }
    }
    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    /* (non-Javadoc)
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);//重写onDraw方法
 
        //canvas.drawColor(Color.WHITE);//设置背景颜色
        Paint paint= new Paint();
       // paint.setStyle(Paint.Style.STROKE);
    	paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);//去锯齿
        paint.setColor(Color.BLACK);//颜色
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        
//        Paint paint1=new Paint();
//        paint1.setStyle(Paint.Style.STROKE);
//        paint1.setTypeface(Typeface.DEFAULT_BOLD);
//        paint1.setAntiAlias(true);//去锯齿
//        paint1.setColor(Color.DKGRAY);
        paint.setTextSize(30);  //设置轴文字大小
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
        
        //红色警报警报的横线
        paint.setColor(AppConstants.color3);
        canvas.drawLine(XPoint,YPoint-(int)((alert_value-3500)*(YScale/100.0f)),XPoint+XLength,YPoint-(int)((alert_value-3500)*(YScale/100.0f)),paint);   //轴线
        //橙色警报
//        if(alert_value-3850  >0) { 
//        	paint.setColor(AppConstants.color2);
//        	canvas.drawLine(XPoint,YPoint-280,XPoint+XLength,YPoint-280,paint);   //轴线
//        }
        paint.setColor(Color.BLACK);
        
        HistoryBin bin;
        for(int i=0;i*XScale<XLength;i++)    
        {
            canvas.drawLine(XPoint+i*XScale, YPoint, XPoint+i*XScale, YPoint-5, paint);  //刻度
            try
            {	bin = Data.get(i);
            	int yMove = 30; //X轴的 日期时间， Y点坐标变化， 
            	int xMove = 35;
            	switch(bin.getShowStatus()) {
	            	case 0:
	            		paint.setColor(Color.BLACK);
	            		yMove = 80;
	            		break;
	            	case 1:
	            		paint.setColor(AppConstants.color2);
	            		yMove = 55;
	            		break;	
	            	case 2:
	            		paint.setColor(AppConstants.color3);
	            		xMove = 42;
	            		yMove=30;
	            		break;
            		default:
            			xMove = 35;
            			paint.setColor(Color.BLACK);
            			break;
            	}
                canvas.drawText(bin.getDate() , XPoint+i*XScale-xMove, YPoint+yMove, paint);  //时间文字
                
                
                //超出边界部分， 就当做到边界
                if(bin.getValue().compareTo("3500")<0){
                	bin.setValue("3500");
                }
                if(bin.getValue().compareTo("4100")>0) {
                	bin.setValue("4100");
                	
                }
                
                
                //根据温度值 是否大于警戒值， 然后变红色
                	paint.setColor(AppConstants.color1);
                //数据值
                    if(i>0&&YCoord(Data.get(i-1).getValue())!=-999&&YCoord(bin.getValue())!=-999)  //保证有效数据
                    canvas.drawLine(XPoint+(i-1)*XScale, YCoord(Data.get(i-1).getValue()), XPoint+i*XScale, YCoord(bin.getValue()), paint);
                    if(Integer.parseInt(bin.getValue())>3850 || Integer.parseInt(bin.getValue())>alert_value) {
                    	paint.setColor(AppConstants.color3);
                    	canvas.drawCircle(XPoint+i*XScale,YCoord(bin.getValue()), 10, paint);
//                    	RectF oval = new RectF(XPoint+i*XScale-5, YCoord(bin.getValue())-5, XPoint+i*XScale+5, YCoord(bin.getValue())+5);
//                    	canvas.drawOval(oval, paint);
                    } else {
                    	paint.setColor(AppConstants.color1);
                    	canvas.drawCircle(XPoint+i*XScale,YCoord(bin.getValue()), 7, paint);
                    }
           }
            catch(Exception e)
            {
            }
        }
//        paint.setColor(Color.BLACK);
//        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint-3,paint);    //箭头
//        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint+3,paint);  
//        paint.setTextSize(16);
//        canvas.drawText(Title, 150, 50, paint);
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
        	int y2 = (int) (y1 * (YScale/100.0f));
        	int yy = YPoint-y2;
        	return yy;
        	//int yy = YPoint-y*YScale/Integer.parseInt(YLabel[1]);
        }
        catch(Exception e)
        {
        	e.printStackTrace();
        }
        return y;
    }
}


