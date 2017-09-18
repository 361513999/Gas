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
		//0创建中，1等待确认执行，2尚未被领取，3任务执行中，4现场完成，5任务完成，6重新安检中，7等待再次执行，8隐患整改中，9隐患已解除
        //isSend 是否提交, problem 隐患单号  staffTag N 不合格   Y 合格  O 无法安检
    db.execSQL("create table staff_stand(i integer primary key autoincrement,id varchar,staffId varchar,staffName varchar,staffTel varchar,staffAdd varchar,staffTime varchar,opt varchar,status int,statudName varchar,printCount int,isSend boolean,staffTag varchar,problem varchar,send boolean)");
	//拍照列表信息
     db.execSQL("create table staff_stand_image(i integer primary key autoincrement,id varchar,standId varchar,staffId varchar,name varchar,path varchar)");
	db.execSQL("create table staff_stand_image_values(i integer primary key autoincrement,id varchar,standId varchar,staffId varchar,name varchar,path varchar,send boolean)");

		//拍照信息数据表
        //安检条目 grp 分组  chk是否选中
      db.execSQL("create table staff_stand_item(i integer primary key autoincrement,id varchar,standId varchar,staffId varchar,name varchar,sort int,grp varchar,chk boolean)");
		//燃气表
		db.execSQL("create table staff_stand_tab(i integer primary key autoincrement,id varchar,standId varchar,staffId varchar,type varchar,value varchar,chk boolean)");
		//燃气具
		db.execSQL("create table staff_stand_qj(i integer primary key autoincrement,id varchar,standId varchar,staffId varchar,name varchar,position varchar,chk boolean)");
		db.execSQL("CREATE TABLE staff_stand_line(i integer primary key autoincrement,standId varchar,staffId varchar,staffLine varchar,personLine varchar,personPhoto varchar,send int)");
		//安检单信息
		db.execSQL("CREATE TABLE staff_stand_pr_s(i integer primary key autoincrement,proNo varchar,startTime varchar,endTime varchar,standId varchar,staffId varchar,staffLine varchar,personLine varchar,personPhoto varchar,send int,bis boolean);");
  		//安检单列表
		db.execSQL("CREATE TABLE staff_stand_pr_l(i integer primary key autoincrement,standId varchar,staffId varchar,txtNo varchar,txtView varchar)");
		db.execSQL("CREATE TABLE staff_stand_pr_l_values(i integer primary key autoincrement,standId varchar,staffId varchar,txtNo varchar,path varchar,send boolean)");
    db.setTransactionSuccessful();
	db.endTransaction();
	}
	private void drop(SQLiteDatabase db){
		db.beginTransaction();
		db.execSQL("DROP TABLE IF EXISTS staff_stand");
        db.execSQL("DROP TABLE IF EXISTS staff_stand_image");
		db.execSQL("DROP TABLE IF EXISTS staff_stand_image_values");
        db.execSQL("DROP TABLE IF EXISTS staff_stand_item");
        db.execSQL("DROP TABLE IF EXISTS staff_stand_tab");
        db.execSQL("DROP TABLE IF EXISTS staff_stand_qj");
		db.execSQL("DROP TABLE IF EXISTS staff_stand_line");
		db.execSQL("DROP TABLE IF EXISTS staff_stand_pr_s");
		db.execSQL("DROP TABLE IF EXISTS staff_stand_pr_l_values");
		db.execSQL("DROP TABLE IF EXISTS staff_stand_pr_l");
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
