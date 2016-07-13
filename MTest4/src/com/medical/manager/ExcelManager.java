package com.medical.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.medical.constants.AppConstants;
import com.medical.sql.HistoryBin;
import com.medical.sql.HistorySQLService2;

public class ExcelManager {
	
	public static final String suffix="xls";
	
	private HistorySQLService2 sqlService ;
	private Context context;
	private Handler handler;
	public ExcelManager(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		sqlService = new HistorySQLService2(context);
	}
	
//	public List<HistoryBin> getMacList() {
//		return sqlService.get;
//	}
//	
//	public int getMacCount() {
//		return sqlService.getMacCount();
//	}
	
	
	public  String createExcel(String fileName , List<HistoryBin> list) throws Exception
	{
		String sdPath="";
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.i("tag", "有SD卡");
//			 sdPath = Environment.getExternalStorageDirectory()
//					.getCanonicalPath()+ "/"+name+"."+suffix;
			sdPath=Environment.getExternalStorageDirectory().getCanonicalPath();
			File folder=new File(sdPath + "/" + AppConstants.fileFolder+ "/");
			if (!folder.exists()) {
				folder.mkdir();
			}
			
			 sdPath = sdPath+"/"+ AppConstants.fileFolder+ "/"+fileName+"."+suffix;
	//		MyHelper.myLog(sdPath);
			File file = new File(sdPath);
			if (!file.exists()) {
				
				file.createNewFile();
			}
			
			// 第一步，创建一个webbook，对应一个Excel文件
			HSSFWorkbook wb = new HSSFWorkbook();
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet sheet = wb.createSheet(fileName);
			sheet.setColumnWidth(0, 5000);
			sheet.setColumnWidth(1, 3000);
			sheet.setColumnWidth(2, 5500);
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			HSSFRow row = sheet.createRow((int) 0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

			HSSFCell cell = row.createCell((short) 0);
			cell.setCellValue("mac");
			cell.setCellStyle(style);
			cell = row.createCell((short) 1);
			cell.setCellValue("temper");
			cell.setCellStyle(style);
			cell = row.createCell((short) 2);
			cell.setCellValue("date");
			cell.setCellStyle(style);
//			cell = row.createCell((short) 3);
//			cell.setCellValue("生日");
//			cell.setCellStyle(style);

			// 第五步，写入实体数据 实际应用中这些数据从数据库得到，
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				HistoryBin stu = (HistoryBin) list.get(i);
				// 第四步，创建单元格，并设置值
				row.createCell((short) 0).setCellValue(stu.getBluetoothMac());
				row.createCell((short) 1).setCellValue(""+ (Integer.parseInt(stu.getValue())/100.d)+"°C");
				row.createCell((short) 2).setCellValue(stu.getDate());
			}
			// 第六步，将文件存到指定位置
				FileOutputStream fout = new FileOutputStream(sdPath);
				wb.write(fout);
				fout.close();
		}
		return sdPath;
		
		
		
	}


}
