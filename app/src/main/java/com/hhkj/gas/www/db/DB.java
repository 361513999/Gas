package com.hhkj.gas.www.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffB;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffQj;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.BaseApplication;
import com.hhkj.gas.www.common.P;
import com.jph.takephoto.model.TImage;

import java.util.ArrayList;
import java.util.Map;

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
     * 根据字段名字获得数据
     *
     * @param cursor
     * @param indexName
     * @return
     */
    // --------------------------
    // ---------------获取数据处理
    private String getString(Cursor cursor, String indexName) {
        return cursor.getString(cursor.getColumnIndex(indexName));
    }

    private int getInt(Cursor cursor, String indexName) {
        return cursor.getInt(cursor.getColumnIndex(indexName));
    }

    private double getDouble(Cursor cursor, String indexName) {
        return cursor.getDouble(cursor.getColumnIndex(indexName));
    }

    private long getLong(Cursor cursor, String indexName) {
        return cursor.getLong(cursor.getColumnIndex(indexName));
    }

    private boolean getBoolean(Cursor cursor, String indexName) {
        return cursor.getInt(cursor.getColumnIndex(indexName)) == 1 ? true
                : false;
    }
    /**
     *是否本地存在这个任务单
     * @param id
     * @return
     */
    public int isExitsId(String id,String staffId) {
        String sql = "select id from staff_stand where id=? and staffId=?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[] { id,staffId });
              count = cursor.getCount();
            P.c("获得的数据是"+count);
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

    /**
     *
     * @param stand 基本信息
     * @param staffImages 图片组
     * @param dss 安检栏目
     * @param staffBs 安检表
     * @param staffQjs 安检器具
     */
    public void addStaff(ReserItemBean stand, ArrayList<StaffImageItem> staffImages, ArrayList<DetailStaff> dss, ArrayList<StaffB> staffBs, ArrayList<StaffQj> staffQjs){

            db.beginTransaction();
            db.execSQL("insert into staff_stand(id,staffId,staffName,staffTel,staffAdd,staffTime,opt,status) values(?,?,?,?,?,?,?,?)", new Object[]{stand.getId(), stand.getNo(), stand.getStaffName()
                    , stand.getTel(), stand.getAdd(), stand.getTime(), stand.getName(), stand.getOrderStatus()
            });
            //安检图
            int imageLen = staffImages.size();
            for (int i = 0; i < imageLen; i++) {
                StaffImageItem imageItem = staffImages.get(i);
                db.execSQL("insert into staff_stand_image(id,standId,staffId,name) values(?,?,?,?)", new Object[]{imageItem.getId(), stand.getId(), stand.getNo(), imageItem.getTag()});
            }
            //安检项目
            int staffLen = dss.size();
            for (int i = 0; i < staffLen; i++) {
                DetailStaff staff = dss.get(i);
                if (staff.getItem() != null) {
                    //单项
                    db.execSQL("insert into staff_stand_item(id,standId,staffId,name,sort) values(?,?,?,?,?)", new Object[]{staff.getItem().getId(), stand.getId(), stand.getNo(), staff.getItem().getTxt(), i});
                } else {
                    ArrayList<StaffTxtItem> list = staff.getItems();
                    for (int j = 0; j < list.size(); j++) {
                        StaffTxtItem sti = list.get(j);
                        db.execSQL("insert into staff_stand_item(id,standId,staffId,name,sort,grp) values(?,?,?,?,?,?)", new Object[]{sti.getId(), stand.getId(), stand.getNo(), sti.getTxt(), i, staff.getItems_tag()});
                    }
                }
            }
            //安检表
            int sbLen = staffBs.size();
            for (int i = 0; i < sbLen; i++) {
                StaffB sb = staffBs.get(i);
                db.execSQL("insert into staff_stand_tab(id,standId,staffId,type,value) values(?,?,?,?,?)", new Object[]{sb.getId(), stand.getId(), stand.getNo(), sb.getName(), sb.getValue()});
            }
            //燃气具
            int tabLen = staffQjs.size();
            for (int i = 0; i < tabLen; i++) {
                StaffQj tab = staffQjs.get(i);
                db.execSQL("insert into staff_stand_qj(id,standId,staffId,name,position) values(?,?,?,?,?)", new Object[]{tab.getId(), stand.getId(), stand.getNo(), tab.getName(), tab.getPosition()});
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        }

        /**
        * 获得基础数据
        */
    public void updataStand(ReserItemBean bean){
        String sql = "select * from staff_stand where id=? and staffId=?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] { bean.getId(),bean.getNo() });
           if(cursor.moveToFirst()){
               //取第一个
               //id varchar,staffId varchar,staffName varchar,staffTel varchar,
               // staffAdd varchar,staffTime varchar,opt varchar,status int,
               // statudName varchar,printCount int,isSend boolean,staffTag varchar,problem varchar
               bean.setId(getString(cursor,"id"));
               bean.setNo(getString(cursor,"staffId"));
               bean.setStaffName(getString(cursor,"staffName"));
               bean.setTel(getString(cursor,"staffTel"));
               bean.setAdd(getString(cursor,"staffAdd"));
               bean.setTime(getString(cursor,"staffTime"));
               bean.setName(getString(cursor,"opt"));
               bean.setOrderStatus(getInt(cursor,"status"));

           }
            cursor.close();
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * 图片项
     * @param staffImages
     * @param bean
     */
    public void updataStaffImageItem(ArrayList<StaffImageItem> staffImages,ReserItemBean bean){
        staffImages.clear();
        String sql = "select min(v.i),s.id,s.standId,s.staffId,s.name,v.path from staff_stand_image as s left   join staff_stand_image_values as v on v.id=s.id where s.standId=? and s.staffId=? group by s.id";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] { bean.getId(),bean.getNo() });
            while(cursor.moveToNext()){
                StaffImageItem item = new StaffImageItem();
                item.setId(getString(cursor,"id"));
                item.setTag(getString(cursor,"name"));
                item.setPath(getString(cursor,"path"));
                staffImages.add(item);

            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
    /**
     * 图片项,子项全部
     * @param staffImages
     * @param bean
     */
    public void updataStaffImageDlgItem(ArrayList<StaffImageItem> staffImages,String id,ReserItemBean bean){
        staffImages.clear();
        String sql = "select * from staff_stand_image_values where id=? and standId = ? and staffId = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] {id, bean.getId(),bean.getNo() });
            while(cursor.moveToNext()){
                StaffImageItem item = new StaffImageItem();
                item.setI(getInt(cursor,"i"));
                item.setId(getString(cursor,"id"));
                item.setTag(getString(cursor,"name"));
                item.setPath(getString(cursor,"path"));
                staffImages.add(item);

            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
    public void removeStaffImageDlg(int i,Handler handler){
        db.execSQL("delete from staff_stand_image_values where i=?",new Object[]{i});
        if(handler!=null){
            handler.sendEmptyMessage(0);
        }
    }
    /**
     * 安检条目
     * @param txtListMap
     * @param bean
     */
    public void updataStaffTxtItem(Map<String,Object> txtListMap, ReserItemBean bean){
        txtListMap.clear();
        String sql = "select * from staff_stand_item where standId=? and staffId =? ";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] { bean.getId(),bean.getNo() });
            while(cursor.moveToNext()){
                String key = cursor.isNull(cursor.getColumnIndex("grp"))?"":getString(cursor,"grp");
                String tag = getString(cursor,"name");
                StaffTxtItem item0 = new StaffTxtItem();
                item0.setId(getString(cursor,"id"));
                item0.setTxt(tag);
                item0.setCheck(getBoolean(cursor,"chk"));

                if(key.length()==0){
                    //单独的数据
                    txtListMap.put(tag,item0);
                }else{
                    if(txtListMap.containsKey(key)){
                        //存在这个组就添加
                        ((ArrayList<StaffTxtItem>)txtListMap.get(key)).add(item0);
                    }else{
                        ArrayList<StaffTxtItem> txts = new ArrayList<StaffTxtItem>();
                        txts.add(item0);
                        txtListMap.put(key,txts);
                    }
                }
            }

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * 燃气表
     * @param staffImages
     * @param bean
     */
    public void updataStaffBs(ArrayList<StaffB> staffImages,ReserItemBean bean){
        String sql = "select * from staff_stand_tab where standId = ? and staffId = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] { bean.getId(),bean.getNo() });
            while(cursor.moveToNext()){
                StaffB item = new StaffB();
                item.setI(getInt(cursor,"i"));
                item.setId(getString(cursor,"id"));
                item.setName(getString(cursor,"type"));
                item.setValue(getString(cursor,"value"));
                if(cursor.isNull(cursor.getColumnIndex("chk"))){
                    item.setCheck(false);
                }else{
                    item.setCheck(getBoolean(cursor,"chk"));
                }

                staffImages.add(item);

            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * 器具
     * @param staffQjs
     * @param bean
     */
    public void updataStaffQj(ArrayList<StaffQj> staffQjs,ReserItemBean bean){
        String sql = "select * from staff_stand_qj where standId =? and staffId = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] { bean.getId(),bean.getNo() });
            while(cursor.moveToNext()){
                StaffQj item = new StaffQj();
                item.setI(getInt(cursor,"i"));
                item.setId(getString(cursor,"id"));
                item.setName(getString(cursor,"name"));
                item.setPosition(getString(cursor,"position"));
                if(cursor.isNull(cursor.getColumnIndex("chk"))){
                    item.setCheck(false);
                }else{
                    item.setCheck(getBoolean(cursor,"chk"));
                }

                staffQjs.add(item);

            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * 获得单据是否是合格状态
     * @param bean
     * @return
     */
    public String getStafftag(ReserItemBean bean){

        String sql = "select staffTag from staff_stand where id=? and staffid=?";
        Cursor cursor = null;
        String result = null;
        try {
            cursor = db.rawQuery(sql, new String[] { bean.getId(),bean.getNo() });
            if(cursor.getCount()!=0) {

                if (cursor.moveToFirst()){
                    result = getString(cursor,"staffTag");
                }

            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  result;
    }

    /**
     * 更新安检栏目
     * @param id
     * @param chk
     * @param handler
     */
    public void updateStandItemChk(String id, boolean chk, Handler handler){
        db.execSQL("update staff_stand_item set chk=? where  id = ? ",new Object[]{chk,id});
        if(handler!=null){
            handler.sendEmptyMessage(6);
        }
    }

    /**
     * 更新器具
     * @param qjs
     * @param bean
     */
    public void updateQjs(ArrayList<StaffQj> qjs,ReserItemBean bean){
        for(int i=0;i<qjs.size();i++){
            StaffQj qj = qjs.get(i);
            if(qj.getI()!=-1){
                //证明是有i值的，那么执行更新操作
                db.execSQL("update staff_stand_qj set name=?,position=?,chk=? where i=?",new Object[]{qj.getName(),qj.getPosition(),qj.isCheck(),qj.getI()});
            }else{
                //无i值，执行插入操作
                db.execSQL("insert into staff_stand_qj(standId,staffId,name,position,chk) values(?,?,?,?,?)",new Object[]{bean.getId(),bean.getNo(),qj.getName(),qj.getPosition(),qj.isCheck()});
            }
        }
    }
    /**
     * 更新燃气表
     * @param qjs
     * @param bean
     */
    public void updateTab(ArrayList<StaffB> sf,ReserItemBean bean){
        P.c("燃气表数量"+sf.size());
        for(int i=0;i<sf.size();i++){
            StaffB qj = sf.get(i);
            P.c(qj.getName()+"qj"+qj.isCheck());
            if(qj.getI()!=-1){
                //证明是有i值的，那么执行更新操作
                db.execSQL("update staff_stand_tab set type=?,value=?,chk=? where i=?",new Object[]{qj.getName(),qj.getValue(),qj.isCheck(),qj.getI()});
            }else{
                //无i值，执行插入操作
                db.execSQL("insert into staff_stand_tab(standId,staffId,type,value,chk) values(?,?,?,?,?)",new Object[]{bean.getId(),bean.getNo(),qj.getName(),qj.getValue(),qj.isCheck()});
            }
        }
    }

    /**
     * 增加安检图片
     * @param item
     * @param bean
     * @param images
     */
    public void addStaffImages(StaffImageItem item, ReserItemBean bean, ArrayList<TImage> images,Handler handler){
        for(int i=0;i<images.size();i++){
            db.execSQL("insert into staff_stand_image_values(id,standId,staffId,name,path) values(?,?,?,?,?)",new Object[]{item.getId(),bean.getId(),bean.getNo(),item.getTag(),images.get(i).getCompressPath()});
        }
        if(handler!=null){
            handler.sendEmptyMessage(5);
        }
    }


}
