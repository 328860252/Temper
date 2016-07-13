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
			Log.i("tag", "��SD��");
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
			
			// ��һ��������һ��webbook����Ӧһ��Excel�ļ�
			HSSFWorkbook wb = new HSSFWorkbook();
			// �ڶ�������webbook�����һ��sheet,��ӦExcel�ļ��е�sheet
			HSSFSheet sheet = wb.createSheet(fileName);
			sheet.setColumnWidth(0, 5000);
			sheet.setColumnWidth(1, 3000);
			sheet.setColumnWidth(2, 5500);
			// ����������sheet����ӱ�ͷ��0��,ע���ϰ汾poi��Excel����������������short
			HSSFRow row = sheet.createRow((int) 0);
			// ���Ĳ���������Ԫ�񣬲�����ֵ��ͷ ���ñ�ͷ����
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // ����һ�����и�ʽ

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
//			cell.setCellValue("����");
//			cell.setCellStyle(style);

			// ���岽��д��ʵ������ ʵ��Ӧ������Щ���ݴ����ݿ�õ���
			for (int i = 0; i < list.size(); i++)
			{
				row = sheet.createRow((int) i + 1);
				HistoryBin stu = (HistoryBin) list.get(i);
				// ���Ĳ���������Ԫ�񣬲�����ֵ
				row.createCell((short) 0).setCellValue(stu.getBluetoothMac());
				row.createCell((short) 1).setCellValue(""+ (Integer.parseInt(stu.getValue())/100.d)+"��C");
				row.createCell((short) 2).setCellValue(stu.getDate());
			}
			// �����������ļ��浽ָ��λ��
				FileOutputStream fout = new FileOutputStream(sdPath);
				wb.write(fout);
				fout.close();
		}
		return sdPath;
		
		
		
	}


}
