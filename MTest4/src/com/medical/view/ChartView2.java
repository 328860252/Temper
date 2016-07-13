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
    public int XPoint=80;    //ԭ���X����
    public int YPoint=650;     //ԭ���Y����
    public int XScale= 90;     //X�Ŀ̶ȳ���
    public int YScale= 100;     //Y�Ŀ̶ȳ���
    public int XLength=380;        //X��ĳ���
    public int YLength= 650;        //Y��ĳ���
  //  public String[] XLabel;    //X�Ŀ̶�
    public String[] YLabel;    //Y�Ŀ̶�
    public List<HistoryBin> Data;      //����
    public String Title;    //��ʾ�ı���
    
    
    
    private int alert_value;//��ɫ������
    
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
        super.onDraw(canvas);//��дonDraw����
 
        //canvas.drawColor(Color.WHITE);//���ñ�����ɫ
        Paint paint= new Paint();
       // paint.setStyle(Paint.Style.STROKE);
    	paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);//ȥ���
        paint.setColor(Color.BLACK);//��ɫ
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        
//        Paint paint1=new Paint();
//        paint1.setStyle(Paint.Style.STROKE);
//        paint1.setTypeface(Typeface.DEFAULT_BOLD);
//        paint1.setAntiAlias(true);//ȥ���
//        paint1.setColor(Color.DKGRAY);
        paint.setTextSize(30);  //���������ִ�С
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
        
        //��ɫ���������ĺ���
        paint.setColor(AppConstants.color3);
        canvas.drawLine(XPoint,YPoint-(int)((alert_value-3500)*(YScale/100.0f)),XPoint+XLength,YPoint-(int)((alert_value-3500)*(YScale/100.0f)),paint);   //����
        //��ɫ����
//        if(alert_value-3850  >0) { 
//        	paint.setColor(AppConstants.color2);
//        	canvas.drawLine(XPoint,YPoint-280,XPoint+XLength,YPoint-280,paint);   //����
//        }
        paint.setColor(Color.BLACK);
        
        HistoryBin bin;
        for(int i=0;i*XScale<XLength;i++)    
        {
            canvas.drawLine(XPoint+i*XScale, YPoint, XPoint+i*XScale, YPoint-5, paint);  //�̶�
            try
            {	bin = Data.get(i);
            	int yMove = 30; //X��� ����ʱ�䣬 Y������仯�� 
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
                canvas.drawText(bin.getDate() , XPoint+i*XScale-xMove, YPoint+yMove, paint);  //ʱ������
                
                
                //�����߽粿�֣� �͵������߽�
                if(bin.getValue().compareTo("3500")<0){
                	bin.setValue("3500");
                }
                if(bin.getValue().compareTo("4100")>0) {
                	bin.setValue("4100");
                	
                }
                
                
                //�����¶�ֵ �Ƿ���ھ���ֵ�� Ȼ����ɫ
                	paint.setColor(AppConstants.color1);
                //����ֵ
                    if(i>0&&YCoord(Data.get(i-1).getValue())!=-999&&YCoord(bin.getValue())!=-999)  //��֤��Ч����
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
//        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint-3,paint);    //��ͷ
//        canvas.drawLine(XPoint+XLength,YPoint,XPoint+XLength-6,YPoint+3,paint);  
//        paint.setTextSize(16);
//        canvas.drawText(Title, 150, 50, paint);
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


