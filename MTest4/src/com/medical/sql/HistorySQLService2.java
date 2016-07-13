package com.medical.sql;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.medical.HistoryChartActivity;
import com.example.medical.HistoryListActivity;



import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * <p>Description: �޸�����</p>
 * @author zhujiang
 * @date 2014-9-9
 */
public class HistorySQLService2 implements Historyolumn{
	
	
	private SQLiteHelp sqLite;

	private HistorySQLService2(){};
	
	public  HistorySQLService2(Context mContext) {
		sqLite = new SQLiteHelp(mContext);
	}
	
	public static String getTableSql() {
		String createSql = "create table "+ Historyolumn.Table+"("
				+ Historyolumn.Id + " integer primary key autoincrement, "
				+ Historyolumn.BluetoothMac + " text,"
				+ Historyolumn.DateTime + "  DATETIME  DEFAULT CURRENT_TIMESTAMP,"
				+ Historyolumn.Value + " text,"
				+ Historyolumn.IsOnline+ " integer,"
				+ Historyolumn.IsShow + " integer,"
				+ Historyolumn.PhoneAccount + " text"
				+ ") ";
		return createSql;
	}
	
	public List<HistoryBin> getListBy7Day(String address, String dateTime) {
		List<HistoryBin> list = new ArrayList<HistoryBin>();
		SQLiteDatabase sdb =  sqLite.getReadableDatabase();
		Log.v("medical", "��ѯ" + dateTime+"֮�������");
		String sql = "select * from "+ Historyolumn.Table + " where " + Historyolumn.BluetoothMac + "='" + address  + "' and "+ Historyolumn.DateTime+" >'"+ dateTime+"' order by "+ Id +" ";
		Cursor mCursor = sdb.rawQuery(sql, null);
		String lastBinDate="";
		HistoryBin bin;
		while (mCursor.moveToNext()) {
			bin = cursor2bin(mCursor);
			String nowDate = bin.getDate();
			list.add(compareDate(lastBinDate,bin));
			lastBinDate = nowDate;
		}
		sdb.close();
		return list;
	}
	
	/**
	 * ���ϸ���ģ� ʱ����бȽ�
	 *     Ҫ�� ����һ���㣬 ������ͬһ�죬  ���ú�ɫɫ����ʾ��  ��ʾ ������ Сʱ������
	 *     				����ͬһ�� ��  ������ͬһ��Сʱ�ڣ� ��ʾ   ��ɫ���壬 Сʱ������
	 *                     ����ͬһ�죬ͬһСʱ�� ��ɫ���壬��ʾ  Сʱ����
	 *        
	 * @param lastDate
	 * @param bin
	 * @return
	 */
	private HistoryBin compareDate(String lastDate, HistoryBin bin) {
		if(lastDate.equals("")) {
			bin.setDate(bin.getDate().subSequence(8, 16).toString());
			bin.setShowStatus(2);
			return bin;
		} 
		if(lastDate.substring(8, 10).equals(bin.getDate().substring(8, 10))) {
			if(lastDate.substring(11, 13).equals(bin.getDate().substring(11, 13))) {
				bin.setDate(bin.getDate().subSequence(11, 16).toString());
				bin.setShowStatus(0);
			} else {
				bin.setDate(bin.getDate().subSequence(11, 16).toString());
				bin.setShowStatus(1);
			}
		} else {
			bin.setDate(bin.getDate().subSequence(8, 16).toString());
			bin.setShowStatus(2);
		}
		return bin;
	}
	
	/**
	 * ���� �� ����  �� ����ģʽ�£��ɼ�������
	 * @param bin
	 * @return
	 */
	public long insertByMac(HistoryBin bin) {
		SQLiteDatabase sdb =  sqLite.getWritableDatabase();
//		long id= sdb.insert(Historyolumn.Table, null, bin2ContentValues(bin));
//		sdb.close();
		
		
		Cursor mCursor = sdb.query(Historyolumn.Table, new String[]{Historyolumn.Id}, Historyolumn.BluetoothMac + " =? and " + Historyolumn.DateTime +" =?", new String[]{bin.getBluetoothMac(), bin.getDate().substring(0, 16)}, null, null, null);
		//��������
		long id ;
		if(mCursor.moveToNext()) {
			id = mCursor.getLong(mCursor.getColumnIndex(Historyolumn.Id));
			sdb.update(Historyolumn.Table, bin2ContentValues(bin), Historyolumn.Id+  "=? ", new String[]{""+bin.getId()});
		} else {
			id = sdb.insert(Historyolumn.Table, null, bin2ContentValues(bin));
		}
		mCursor.close();
		sqLite.close();
		return id;
	}
	
	/**
	 * �����ֻ��˺ŵ����� ��  ������ͬʱ�������
	 * @param bin
	 * @return
	 */
	public long insertByAccount(HistoryBin bin) {
		SQLiteDatabase sdb =  sqLite.getWritableDatabase();
//		long id= sdb.insert(Historyolumn.Table, null, bin2ContentValues(bin));
//		sdb.close();
		
		Cursor mCursor = sdb.query(Historyolumn.Table, new String[]{Historyolumn.Id}, Historyolumn.PhoneAccount + " =? and " + Historyolumn.DateTime +" =?", new String[]{bin.getPhoneAccount(),  bin.getDate().substring(0, 16)}, null, null, null);
		//��������
		long id ;
		if(mCursor.moveToNext()) {
			id = mCursor.getLong(mCursor.getColumnIndex(Historyolumn.Id));
			sdb.update(Historyolumn.Table, bin2ContentValues(bin), Historyolumn.Id+  "=? ", new String[]{""+bin.getId()});
		} else {
			id = sdb.insert(Historyolumn.Table, null, bin2ContentValues(bin));
		}
		mCursor.close();
		sdb.close();
		return id;
	}
	
	/**
	 * ʹ������ ���ٴ��������
	 * @param array
	 * @param phone
	 */
	public void insertByAccount(JSONArray array, String phone) {
		SQLiteDatabase sdb =  sqLite.getWritableDatabase();
		
		JSONObject jobj ;
		HistoryBin historyBin;
		Cursor mCursor = null;
		sdb.beginTransaction();  
		long time1 = System.currentTimeMillis();
		for(int i =0 ; i< array.length(); i++) {
			try {
				jobj = array.getJSONObject(i);
				historyBin = new HistoryBin();
				historyBin.setDate(jobj.getString("dataTime"));
				//������¶����ݣ� ��������100������ �������ϵ��������� С�� 
				final int temper = (int) (Float.valueOf(jobj.getString("temperature")) *100);
//				if(temper<3200|| temper>4100){//�����¶ȷ�Χ�� 35�� ��41��֮��
//					continue;
//				}
				historyBin.setValue(""+temper);
				historyBin.setOnline(true);
				historyBin.setPhoneAccount(phone);
				
				mCursor = sdb.query(Historyolumn.Table, new String[]{Historyolumn.Id}, Historyolumn.PhoneAccount + " =? and " + Historyolumn.DateTime +" =?", new String[]{historyBin.getPhoneAccount(),  historyBin.getDate().substring(0, 16)}, null, null, null);
				//��������
				long id ;
				if(mCursor.moveToNext()) {
					id = mCursor.getLong(mCursor.getColumnIndex(Historyolumn.Id));
					sdb.update(Historyolumn.Table, bin2ContentValues(historyBin), Historyolumn.Id+  "=? ", new String[]{""+historyBin.getId()});
				} else {
					id = sdb.insert(Historyolumn.Table, null, bin2ContentValues(historyBin));
				}
				sdb.setTransactionSuccessful();  
			} catch(Exception e) {
				continue;
			}
		}
		sdb.endTransaction();  
		if(mCursor!=null) {
			mCursor.close();
		}
		sdb.close();
		Log.v("Tag"," historySQl over " + array.length() + " time:" + (System.currentTimeMillis() - time1) );
	}

	
	public HistoryBin  cursor2bin (Cursor mCursor) {
		HistoryBin mCmdBin = new HistoryBin();
		mCmdBin.setId(mCursor.getInt(mCursor.getColumnIndex(Historyolumn.Id)));
		mCmdBin.setBluetoothMac(mCursor.getString(mCursor.getColumnIndex(Historyolumn.BluetoothMac)));
		mCmdBin.setDate(mCursor.getString(mCursor.getColumnIndex(Historyolumn.DateTime)));
		mCmdBin.setValue(mCursor.getString(mCursor.getColumnIndex(Historyolumn.Value)));
		mCmdBin.setShowStatus(mCursor.getInt(mCursor.getColumnIndex(Historyolumn.IsShow)));
		mCmdBin.setOnline(mCursor.getInt(mCursor.getColumnIndex(Historyolumn.IsShow))==1);
		mCmdBin.setPhoneAccount(mCursor.getString(mCursor.getColumnIndex(Historyolumn.PhoneAccount)));
		return mCmdBin;
	}
	
	/**
	 * �� datetime�� ֻ�����
	 * @param mSwicthBin
	 * @return
	 */
	public ContentValues bin2ContentValues(HistoryBin mSwicthBin) {
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(Historyolumn.BluetoothMac, mSwicthBin.getBluetoothMac());
		mContentValues.put(Historyolumn.IsShow, mSwicthBin.getShowStatus());
		mContentValues.put(Historyolumn.DateTime, mSwicthBin.getDate().substring(0, 16));
		mContentValues.put(Historyolumn.IsOnline, mSwicthBin.isOnline()? 1:0);
		mContentValues.put(Historyolumn.Value, mSwicthBin.getValue());
		mContentValues.put(Historyolumn.PhoneAccount, mSwicthBin.getPhoneAccount());
		return mContentValues;
	}
	
	
	/**
	 * ��ѯ   �豸�� ��ʷ��¼ 
	 * @param switchId
	 * @param address
	 * @param price
	 * @param power
	 * @param str   yyyy-MM-DD HH:MM:SS  �� ��ʼʱ�� �ͽ���ʱ��
	 * @return
	 */
	public List<HistoryBin> queryDay2Day(String string, String mac) {
		String[] str = new String[]{string+" 00:00:00",  string+" 59:59:59"};
		
		//String sql = "select * from " + Historyolumn.Table  + " where "+ Historyolumn.BluetoothMac+ " ='" + mac +"' and "+ Historyolumn.DateTime +" > '"+ str[0]+ "' and datetime < '" + str[1] + "' group by datetime order by  datetime;" ;
		String sql = "select * from " + Historyolumn.Table  + " where "+ Historyolumn.BluetoothMac+ " ='" + mac +"' and "+ Historyolumn.DateTime +" > '"+ str[0]+ "' and datetime < '" + str[1] + "'  order by  datetime;" ;
		SQLiteDatabase mSqLiteDatabase= sqLite.getReadableDatabase();
		List<HistoryBin> list = new ArrayList<HistoryBin>();
		Cursor mCursor = mSqLiteDatabase.rawQuery(sql, null);
		HistoryBin bin;
		String lastBinDate="";
		while (mCursor.moveToNext()) {
			bin = cursor2bin(mCursor);
			String nowDate = bin.getDate();
			list.add(compareDate(lastBinDate,bin));
			lastBinDate = nowDate;
			//list.add(bin);
		}
		mSqLiteDatabase.close();
		mCursor.close();
		return list;
	}
	
	/**
	 * ��ѯ   �豸�� ��ʷ��¼ 
	 * @param address
	 * @return
	 */
	public List<HistoryBin> queryAll() {
		
		String sql = "select * from " + Historyolumn.Table  + " group by id;" ;
		SQLiteDatabase mSqLiteDatabase= sqLite.getReadableDatabase();
		List<HistoryBin> list = new ArrayList<HistoryBin>();
		Cursor mCursor = mSqLiteDatabase.rawQuery(sql, null);
		HistoryBin bin;
		while (mCursor.moveToNext()) {
			bin = cursor2bin(mCursor);
//			String nowDate = bin.getDate();
//			list.add(compareDate(lastBinDate,bin));
//			lastBinDate = nowDate;
			list.add(bin);
		}
		mSqLiteDatabase.close();
		mCursor.close();
		return list;
	}

	public List<String> queryListByDay(String mac) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		SQLiteDatabase sdb =  sqLite.getReadableDatabase();
		//strftime('" + "%Y%m%d" + "', "+Historyolumn.DateTime+") as yearDate   group by yearDate order by yearDate desc
		String sql = "select  strftime('" + "%Y-%m-%d" + "', "+Historyolumn.DateTime+") as yearDate from "+ Historyolumn.Table + " where "+ Historyolumn.BluetoothMac +"='"+ mac +"'  group by yearDate order by yearDate desc";
		Cursor mCursor = sdb.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			list.add(mCursor.getString(mCursor.getColumnIndex("yearDate")));
		}
		sdb.close();
		return list;
	}
	
	
	
	/**
	 * ��ѯ   �豸�� ��ʷ��¼ 
	 * @param switchId
	 * @param address
	 * @param price
	 * @param power
	 * @param str   yyyy-MM-DD HH:MM:SS  �� ��ʼʱ�� �ͽ���ʱ��
	 * @return
	 */
	public List<HistoryBin> queryDay2DayByAccount(String string, String account) {
		String[] str = new String[]{string+" 00:00:00",  string+" 59:59:59"};
		
		//String sql = "select * from " + Historyolumn.Table  + " where "+ Historyolumn.PhoneAccount+ " ='" + account +"' and "+ Historyolumn.DateTime +" > '"+ str[0]+ "' and datetime < '" + str[1] + "' group by datetime order by datetime;" ;
		String sql = "select * from " + Historyolumn.Table  + " where "+ Historyolumn.PhoneAccount+ " ='" + account +"' and "+ Historyolumn.DateTime +" > '"+ str[0]+ "' and datetime < '" + str[1] + "'  order by datetime;" ;
		SQLiteDatabase mSqLiteDatabase= sqLite.getReadableDatabase();
		List<HistoryBin> list = new ArrayList<HistoryBin>();
		Cursor mCursor = mSqLiteDatabase.rawQuery(sql, null);
		HistoryBin bin;
		String lastBinDate="";
		while (mCursor.moveToNext()) {
			bin = cursor2bin(mCursor);
			String nowDate = bin.getDate();
			list.add(compareDate(lastBinDate,bin));
			lastBinDate = nowDate;
			//list.add(bin);
		}
		mSqLiteDatabase.close();
		mCursor.close();
		return list;
	}
	
	/**
	 * ��ѯ   �豸�� ��ʷ��¼ 
	 * @param address
	 * @return
	 */
	public List<HistoryBin> queryAllByAccount() {
		
		String sql = "select * from " + Historyolumn.Table  + " group by id;" ;
		SQLiteDatabase mSqLiteDatabase= sqLite.getReadableDatabase();
		List<HistoryBin> list = new ArrayList<HistoryBin>();
		Cursor mCursor = mSqLiteDatabase.rawQuery(sql, null);
		HistoryBin bin;
		while (mCursor.moveToNext()) {
			bin = cursor2bin(mCursor);
//			String nowDate = bin.getDate();
//			list.add(compareDate(lastBinDate,bin));
//			lastBinDate = nowDate;
			list.add(bin);
		}
		mSqLiteDatabase.close();
		mCursor.close();
		return list;
	}

	public List<String> queryListByDayByAccount(String accont) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		SQLiteDatabase sdb =  sqLite.getReadableDatabase();
		//strftime('" + "%Y%m%d" + "', "+Historyolumn.DateTime+") as yearDate   group by yearDate order by yearDate desc
		String sql = "select  strftime('" + "%Y-%m-%d" + "', "+Historyolumn.DateTime+") as yearDate from "+ Historyolumn.Table + " where "+ Historyolumn.PhoneAccount +"='"+ accont +"'  group by yearDate order by yearDate desc";
		Cursor mCursor = sdb.rawQuery(sql, null);
		while (mCursor.moveToNext()) {
			list.add(mCursor.getString(mCursor.getColumnIndex("yearDate")));
		}
		sdb.close();
		return list;
	}

}
