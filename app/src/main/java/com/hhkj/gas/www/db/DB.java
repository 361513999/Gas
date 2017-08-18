package com.hhkj.gas.www.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.BaseApplication;
import com.hhkj.gas.www.common.P;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/18/018.
 */

public class DB {
    public   static DB dao;
    private static DBHelper dbHelper;
    private static SQLiteDatabase db;

    private void DB() {
    }
    /**
     * 单列数据库操作对象
     *
     * @return
     */
    public static synchronized DB getInstance() {
        if (dao == null) {
            synchronized (DB.class) {
                if (dao == null) {
                    dao = new DB();
                    dbHelper = new DBHelper(BaseApplication.application);
                    db = dbHelper.getWritableDatabase();
                }
            }
        }
        return dao;
    }
    /**
     *是否本地存在这个任务单
     * @param id
     * @return
     */
    public int isExitsId(String id,String staffId) {
        String sql = "select count(id) from staff_stand where id=? and staffId=?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[] { id,staffId });
              count = cursor.getCount();
//            while (cursor.moveToNext()) {
//
//            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return count;
    }

    public void addStaff(ReserItemBean stand,ArrayList<StaffImageItem> staffImages,ArrayList<DetailStaff> dss){
        db.beginTransaction();
        db.execSQL("insert into staff_stand(id,staffId,staffName,staffTel,staffAdd,staffTime,opt,status) values(?,?,?,?,?,?,?,?)",new Object[]{stand.getId(),stand.getNo(),stand.getStaffName()
            ,stand.getTel(),stand.getAdd(),stand.getTime(),stand.getName(),stand.getOrderStatus()
        });
        int imageLen = staffImages.size();
        for(int i=0;i<imageLen;i++){
           StaffImageItem imageItem =  staffImages.get(i);
            db.execSQL("insert into staff_stand_image(id,standId,staffId,name) values(?,?,?,?)",new Object[]{imageItem.getId(),stand.getId(),stand.getNo(),imageItem.getTag()});
        }
        int staffLen = dss.size();
        for(int i=0;i<staffLen;i++){
            DetailStaff staff =  dss.get(i);
            if(staff.getItem()!=null){
                //单项

                db.execSQL("insert into staff_stand_item(id,standId,staffId,name,sort) values(?,?,?,?,?)",new Object[]{staff.getItem().getId(),stand.getId(),stand.getNo(),staff.getItem().getTxt(),i});
            }else {
               ArrayList<StaffTxtItem> list =  staff.getItems();
                for(int j=0;j<list.size();j++){
                    StaffTxtItem sti =  list.get(j);

                    db.execSQL("insert into staff_stand_item(id,standId,staffId,name,sort,grp) values(?,?,?,?,?,?)",new Object[]{sti.getId(),stand.getId(),stand.getNo(),sti.getTxt(),i,staff.getItems_tag()});
                }

            }


        }
        //加载栏目

        db.setTransactionSuccessful();
        db.endTransaction();
    }


}
