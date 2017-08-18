package com.hhkj.gas.www.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.hhkj.gas.www.common.Common;

public class DBHelper extends SQLiteOpenHelper{
	/**
	 * 数据库操作
	 * @param context
	 * @param name
	 * @param factory
	 * @param versionê
	 */
	private String DATABASE_PATH ; 
	@SuppressWarnings("unused")
	private Context context;
	public DBHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, Common.DB_NAME, null, Common.DB_VERSION);
		 
	}
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		this.context = context;
		DATABASE_PATH ="/data/data/"+context.getPackageName()+"/databases/";

	}	
	private void create (SQLiteDatabase db){
	db.beginTransaction();	
    //详情页基本信息数据
        //isSend 是否提交, problem 隐患单号
    db.execSQL("create table staff_stand(i integer primary key autoincrement,id varchar,staffId varchar,staffName varchar,staffTel varchar,staffAdd varchar,staffTime varchar,opt varchar,status int,statudName varchar,printCount int,isSend boolean,staffTag varchar,problem varchar)");
	//拍照列表信息
     db.execSQL("create table staff_stand_image(i integer primary key autoincrement,id varchar,standId varchar,staffId varchar,name varchar)");
        //拍照信息数据表
      db.execSQL("create table staff_stand_img_values(i integer primary key autoincrement,id varchar,standId varchar,staffId varchar,path varchar);");
        //安检条目 grp 分组  chk是否选中
      db.execSQL("create table staff_stand_item(i integer primary key autoincrement,id varchar,standId varchar,staffId varchar,name varchar,sort int,grp varchar,chk boolean)");


    db.setTransactionSuccessful();
	db.endTransaction();
	}
	private void drop(SQLiteDatabase db){
		db.beginTransaction();
		db.execSQL("DROP TABLE IF EXISTS staff_stand");
        db.execSQL("DROP TABLE IF EXISTS staff_stand_image");
        db.execSQL("DROP TABLE IF EXISTS staff_stand_img_values");
        db.execSQL("DROP TABLE IF EXISTS staff_stand_item");
		db.setTransactionSuccessful();
		db.endTransaction();
		//此处是删除数据表，在实际的业务中一般是需要数据备份的
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		create(db);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		drop(db);
		create(db);
	}
	  
    
    public boolean checkDataBase() {  
        SQLiteDatabase db = null;  
        try {  
            String databaseFilename = DATABASE_PATH + Common.DB_NAME;  
            db = SQLiteDatabase.openDatabase(databaseFilename, null,SQLiteDatabase.OPEN_READWRITE);  
        } catch (SQLiteException e) {  
  
        }  
        if (db != null) {  
            db.close();  
        }  
        return db != null ? true : false;  
    }   
  

}
