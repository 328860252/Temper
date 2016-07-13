/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.medical;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.MathHelper;

import com.medical.constants.AppConstants;
import com.medical.sql.HistoryBin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Temperature sensor demo chart.
 */
public class SensorValuesChart extends AbstractDemoChart {
  private static final long HOUR = 3600 * 1000;

  private static final long DAY = HOUR * 24;

  private static final int HOURS = 24;
  
  
  private String title = "title";
  private String xTitle = "xTitle";
  private String yTitle = "yTitle";
  String[] titles;
  //两条线的  颜色
  int[] colors ;
  private int alert_temper;
  
  List<Date[]> x = new ArrayList<Date[]>();
  List<double[]> values = new ArrayList<double[]>();
  
  
  public void setTitle(String title, String xTitle, String yTitle) {
	  this.title = title;
	  this.xTitle = xTitle;
	  this.yTitle = yTitle;
  }
  
  public void initData(List<HistoryBin> historyList, int alert_temper) {
	  //左下角 ,对两条线的解释
	  titles = new String[] { "temper", "alert"};
	  colors = new int[] {AppConstants.color1, AppConstants.color3 };
	  Date[]  dates = new Date[historyList.size()];
	  double[] dvalus = new double[historyList.size()];
	  HistoryBin bin ;
	  for(int i =0 ; i <historyList.size();i++) {
		  bin = historyList.get(i);
		  try {
			dates[i] = HistoryChartActivity.sdf.parse(bin.getDate());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			continue;
		}
		  dvalus[i] = Integer.parseInt(bin.getValue()) /100.d;
	  }
	  x.add(dates);
	  values.add(dvalus);
	  
	  
	  Date[] alert_dates = new Date[2];
	  Date d = new Date(dates[0].getTime()-601000);
	  alert_dates[0] = d;
	  alert_dates[1] = new Date(new Date().getTime()+3600000);
	  
	  double[] alert_value = new double[2];
	  alert_value[0] =  alert_temper/100.d;
	  alert_value[1] =  alert_temper/100.d;
	  x.add(alert_dates);
	  values.add(alert_value);	  
	  
//	  long now = Math.round(new Date().getTime() / DAY) * DAY;
//	  for (int i = 0; i < titles.length; i++) {
//	      Date[] dates = new Date[historyList.size()];
//	      HistoryBin bin ;
//		  for(int j=0 ; j <historyList.size();j++) {
//			  bin = historyList.get(j);
//	    	  try {
//				dates[j] = HistoryActivity.sdf.parse(bin.getDate());
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	    	  if(i==0){
//	    		  dvalus[j] = Integer.parseInt(bin.getValue()) /100.d;
//	    	  }
//	      }
//	      x.add(dates);
//	    }
//	  values.add(dvalus);
//	  double[] alert_value = new double[dvalus.length];
//	  alert_value[0] = alert_temper/100.d;
//	  alert_value[alert_value.length-1] = alert_temper/100.d;
//	  for(int i=1 ; i<alert_value.length-1; i++) {
//		  alert_value[i] = MathHelper.NULL_VALUE;
//	  }
//	  values.add(alert_value);

  }

  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Sensor data";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "The temperature, as read from an outside and an inside sensors";
  }

  /**
   * Executes the chart demo.
   * 
   * @param context the context
   * @return the built intent
   */
  public Intent execute(Context context) {

    PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND  };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles );
    int length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      ((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
    }
    
    System.out.println(" xx " + x.get(0)[0].getTime());
    System.out.println(" xx " + x.get(0).length);
    System.out.println(" xx " + x.get(0)[length-1].getTime());
    
    setChartSettings(renderer, title, xTitle, yTitle,
        x.get(0)[0].getTime()-600000, x.get(0)[0].getTime()+3600000, 34, 42, Color.LTGRAY, Color.LTGRAY);
    renderer.setXLabels(10);
    renderer.setYLabels(10);
    renderer.setShowGrid(true);
    renderer.setXLabelsAlign(Align.CENTER);
    renderer.setYLabelsAlign(Align.RIGHT);
    Intent intent = ChartFactory.getTimeChartIntent(context, buildDateDataset(titles, x, values),
        renderer, "HH:mm");
    return intent;
  }

}
