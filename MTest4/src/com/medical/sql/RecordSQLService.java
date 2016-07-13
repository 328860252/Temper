package com.medical.sql;



import java.util.ArrayList;
import java.util.List;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * <p>Description: 记录</p>
 * @author zhujiang
 * @date 2014-9-9
 */
public class RecordSQLService implements RecordColumn{
	
	private SQLiteHelp sqLite;

	private RecordSQLService(){};
	
	public  RecordSQLService(Context mContext) {
		sqLite = new SQLiteHelp(mContext);
	}
	
	public static String getTableSql() {
		String createSql = "create table "+ RecordColumn.Table+"("
				+ RecordColumn.Id + " integer primary key autoincrement, "
				+ RecordColumn.DeviceMac + " text,"
				+ RecordColumn.HistoryData +" text,"
				+ RecordColumn.DateTime + " DATETIME  DEFAULT CURRENT_TIMESTAMP,"
				+ RecordColumn.Title + " text,"
				+ RecordColumn.Message + " text,"
				+ RecordColumn.Image + " text"
				+ ") ";
		return createSql;
	}
	
	
	
	public long insert(RecordBin bin) {
		SQLiteDatabase sdb =  sqLite.getWritableDatabase();
		long id= sdb.insert(RecordColumn.Table, null, bin2ContentValues(bin));
		sdb.close();
		return id;
	}
	
	public void update(RecordBin bin) {
		SQLiteDatabase sdb =  sqLite.getWritableDatabase();
		sdb.update(RecordColumn.Table, bin2ContentValues(bin), RecordColumn.Id +" =? ", new String[]{""+ bin.getId()});
		sdb.close();
	}
	
	public RecordBin  cursor2bin (Cursor mCursor) {
		RecordBin mCmdBin = new RecordBin();
		mCmdBin.setId(mCursor.getInt(mCursor.getColumnIndex(RecordColumn.Id)));
		mCmdBin.setDeviceMac(mCursor.getString(mCursor.getColumnIndex(RecordColumn.DeviceMac)));
		mCmdBin.setDateTime(mCursor.getString(mCursor.getColumnIndex(RecordColumn.DateTime)));
		mCmdBin.setTitle(mCursor.getString(mCursor.getColumnIndex(RecordColumn.Title)));
		mCmdBin.setMessage(mCursor.getString(mCursor.getColumnIndex(RecordColumn.Message)));
		mCmdBin.setImgPath(mCursor.getString(mCursor.getColumnIndex(RecordColumn.Image)));
		mCmdBin.setHistoryData(mCursor.getString(mCursor.getColumnIndex(RecordColumn.HistoryData)));
		return mCmdBin;
	}
	
	public ContentValues bin2ContentValues(RecordBin mSwicthBin) {
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(RecordColumn.DeviceMac, mSwicthBin.getDeviceMac());
		mContentValues.put(RecordColumn.DateTime, mSwicthBin.getDateTime());
		mContentValues.put(RecordColumn.Title, mSwicthBin.getTitle());
		mContentValues.put(RecordColumn.Message, mSwicthBin.getMessage());
		mContentValues.put(RecordColumn.Image, mSwicthBin.getImgPath());
		mContentValues.put(RecordColumn.HistoryData, mSwicthBin.getHistoryData());
		
		return mContentValues;
	}
	
	
	
	/**
	 * 查询   设备的 历史记录 
	 * @param address
	 * @return
	 */
	public List<RecordBin> queryAll() {
		
		String sql = "select * from " + RecordColumn.Table  + " group by id;" ;
		SQLiteDatabase mSqLiteDatabase= sqLite.getReadableDatabase();
		List<RecordBin> list = new ArrayList<RecordBin>();
		Cursor mCursor = mSqLiteDatabase.rawQuery(sql, null);
		RecordBin bin;
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
	
	
	/**
	 * 查询   设备的 历史记录 
	 * @param switchId
	 * @param address
	 * @param price
	 * @param power
	 * @param str   yyyy-MM-DD HH:MM:SS  的 起始时间 和结束时间
	 * @return
	 */
	public List<RecordBin> queryDay2Day(String string, String mac) {
		//String[] str = new String[]{string+" 00:00:00",  string+" 59:59:59"};
		String sql = "select * from " + RecordColumn.Table  + " where "+ RecordColumn.DeviceMac+ " ='" + mac +"' and "+ RecordColumn.HistoryData +" = '"+ string+ "' order by "+RecordColumn.DeviceMac+" and id;" ;
		SQLiteDatabase mSqLiteDatabase= sqLite.getReadableDatabase();
		List<RecordBin> list = new ArrayList<RecordBin>();
		Cursor mCursor = mSqLiteDatabase.rawQuery(sql, null);
		RecordBin bin;
		while (mCursor.moveToNext()) {
			bin = cursor2bin(mCursor);
			list.add(bin);
		}
		mSqLiteDatabase.close();
		mCursor.close();
		return list;
	}
}
