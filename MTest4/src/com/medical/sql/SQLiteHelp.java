package com.medical.sql;


import java.util.Random;

import com.example.medical.HistoryChartActivity;

import android.content.ContentValues;
import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelp extends SQLiteOpenHelper {

	public SQLiteHelp(Context context) {
		super(context, "medical", null, 9);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		create(db);

	}

	private void create(SQLiteDatabase db) {
		db.execSQL(HistorySQLService2.getTableSql());
		db.execSQL(BluetoothSQLService.getTableSql());
		db.execSQL(RecordSQLService.getTableSql());
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		 String sql = "DROP TABLE IF EXISTS " + Historyolumn.Table;
		 String sq2 = "DROP TABLE IF EXISTS " + BluetoothColumn.Table;
		 String sq3 = "DROP TABLE IF EXISTS " + RecordColumn.Table;
		 arg0.execSQL(sql);
		 arg0.execSQL(sq2);
		 arg0.execSQL(sq3);
		 create(arg0);
	}
	
//	//ï¿½ï¿½ï¿½ï¿½
//	public ContentValues bin2ContentValues(HistoryBin mSwicthBin) {
//		ContentValues mContentValues = new ContentValues();
//		mContentValues.put(SwitchId, mSwicthBin.getSwitchId());
//		mContentValues.put(Time, mSwicthBin.getTime());
//		mContentValues.put(Datetime, mSwicthBin.getDatetime());
//		mContentValues.put(Address, mSwicthBin.getAddress());
//		mContentValues.put(UniqueString, mSwicthBin.getUniqueStr());
//		return mContentValues;
//	}
	
	
	public ContentValues bin2ContentValues(HistoryBin mSwicthBin) {
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(Historyolumn.Id, mSwicthBin.getId());
		mContentValues.put(Historyolumn.BluetoothMac, mSwicthBin.getBluetoothMac());
		mContentValues.put(Historyolumn.IsShow, mSwicthBin.getShowStatus());
		mContentValues.put(Historyolumn.DateTime, mSwicthBin.getDate());
		mContentValues.put(Historyolumn.Value, mSwicthBin.getValue());
		return mContentValues;
	}
//	
////²âÊÔÊý¾Ý
	public void insertDB(SQLiteDatabase db) {
		HistoryBin bin ;
		for(int i =2; i<200; i++) {
			bin= new HistoryBin();
			java.util.Date date = new java.util.Date();
			date.setDate(date.getDate()-i);
			bin.setDate(HistoryChartActivity.sdf.format(date));
			bin.setBluetoothMac("AABBCCDDEEFF");
			bin.setValue(""+(3545+i));
			bin.setShowStatus(0);
			insert(bin, db);
		}
	}
	
	public void insert(HistoryBin mHistoryBin , SQLiteDatabase db) {
		String sql = "replace into table2 ("+Historyolumn.Id +", " +Historyolumn.BluetoothMac+", " +Historyolumn.IsShow+", " +Historyolumn.DateTime+", " +Historyolumn.Value +") values "
				+ " (null, " + mHistoryBin.getBluetoothMac() + ", '" + mHistoryBin.getShowStatus()+"', '" + mHistoryBin.getDate() 
				+ "', "  + mHistoryBin.getValue() + "');";
		//Cursor mCursor = mSqLiteDatabase.rawQuery(sql, null);
		db.execSQL(sql);
	}

}
