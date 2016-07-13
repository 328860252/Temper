package com.medical.sql;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BluetoothSQLService implements BluetoothColumn{
	
	private SQLiteHelp sqLite;
	private BluetoothSQLService(){};
	
	public  BluetoothSQLService(Context mContext) {
		sqLite = new SQLiteHelp(mContext);
	}
	
	public static String getTableSql() {
		String createSql = "create table " + BluetoothSQLService.Table + "("
				+ BluetoothColumn.Id + " integer primary key autoincrement, "
				+ BluetoothColumn.Address + " text UNIQUE,"
				+ BluetoothColumn.Name + " text"
				+ ") ";
		return createSql;
	}
	
	public List<BluetoothRecordBin> getList() {
		List<BluetoothRecordBin> list = new ArrayList<BluetoothRecordBin>();
		SQLiteDatabase sdb =  sqLite.getReadableDatabase();
		String sql = "select * from "+ BluetoothColumn.Table +"";
		Cursor mCursor = sdb.rawQuery(sql, null);
		BluetoothRecordBin bin;
		while (mCursor.moveToNext()) {
			bin = cursor2bin(mCursor);
			list.add(bin);
		}
		sdb.close();
		Log.v("medical", "查询" +"之后的数据"+list.size());
		return list;
	}
	
	
	public long insert(BluetoothRecordBin bin) {
		SQLiteDatabase sdb =  sqLite.getWritableDatabase();
		Cursor mCursor = sdb.query(BluetoothColumn.Table, null, BluetoothColumn.Address + "=?" ,
				new String[] {bin.getAddress()}, null, null, null);
		long id;
		if (mCursor.moveToNext()) {
			id = mCursor.getInt(mCursor.getColumnIndex(BluetoothColumn.Id));
		} else {
			id= sdb.insert(BluetoothColumn.Table, null, bin2ContentValues(bin));
		}
		sdb.close();
		return id;
	}

	
	public BluetoothRecordBin  cursor2bin (Cursor mCursor) {
		BluetoothRecordBin mCmdBin = new BluetoothRecordBin();
		mCmdBin.setId(mCursor.getInt(mCursor.getColumnIndex(BluetoothColumn.Id)));
		mCmdBin.setName(mCursor.getString(mCursor.getColumnIndex(BluetoothColumn.Name)));
		mCmdBin.setAddress(mCursor.getString(mCursor.getColumnIndex(BluetoothColumn.Address)));
		return mCmdBin;
	}
	
	public ContentValues bin2ContentValues(BluetoothRecordBin mSwicthBin) {
		ContentValues mContentValues = new ContentValues();
		mContentValues.put(BluetoothColumn.Address, mSwicthBin.getAddress());
		mContentValues.put(BluetoothColumn.Name, mSwicthBin.getName());
		return mContentValues;
	}

}
