package com.hhkj.gas.www.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ImageRdy;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffB;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffQj;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.BaseApplication;
import com.hhkj.gas.www.common.P;
import com.jph.takephoto.model.TImage;

import java.util.ArrayList;
import java.util.HashMap;
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
    public int isExitsId(String id,String staffId) {
        String sql = "select id from staff_stand where id=? and staffId=?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[] {id,staffId });
            count = cursor.getCount();
            P.c("获得的数据是"+count);
//            while (cursor.moveToNext()) {
//
//            }
            cursor.close();

        } catch (Exception e) {
            P.c("判断"+e.getMessage());
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
            db.execSQL("insert into staff_stand(id,staffId,staffName,staffTel,staffAdd,staffTime,opt,status,send) values(?,?,?,?,?,?,?,?,?)", new Object[]{stand.getId(), stand.getNo(), stand.getStaffName()
                    , stand.getTel(), stand.getAdd(), stand.getTime(), stand.getName(), stand.getOrderStatus(),false
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
               bean.setStaffTag(getString(cursor,"staffTag"));

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

                item.setName(cursor.isNull(cursor.getColumnIndex("type"))?"":getString(cursor,"type"));
                item.setValue(cursor.isNull(cursor.getColumnIndex("value"))?"":getString(cursor,"value"));
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
           staffQjs.clear();
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
     *
     * @param staffQjs
     * @param bean
     */
    public void getStaffPrint(Map<String,String> map,ReserItemBean bean){
        map.clear();
        String sql = "select staffLine,personLine,personPhoto from staff_stand_line where standId = ? and staffId = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] { bean.getId(),bean.getNo() });
            int count = cursor.getCount();
            if(count!=0){
                if(cursor.moveToFirst()){
                    map.put("staffLine",getString(cursor,"staffLine"));
                    map.put("personLine",getString(cursor,"personLine"));
                    map.put("personPhoto",getString(cursor,"personPhoto"));
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
     * 获得单据是否是合格状态
     * @param bean
     * @return
     */
    public int getStaffCount(){

        String sql = "select count(*) as num from staff_stand where send=0";
        Cursor cursor = null;
        String result = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql,null );
            if(cursor.getCount()!=0) {

                if (cursor.moveToFirst()){
                    count = getInt(cursor,"num");
                }

            }
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  count;
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
            db.execSQL("insert into staff_stand_image_values(id,standId,staffId,name,path,send) values(?,?,?,?,?,?)",new Object[]{item.getId(),bean.getId(),bean.getNo(),item.getTag(),images.get(i).getCompressPath(),false});
        }
        if(handler!=null){
            handler.sendEmptyMessage(5);
        }
    }
    /**无法安检
     */
    public void setStandCt(int status,String tag,ReserItemBean bean){
        db.execSQL("update staff_stand set status=? ,staffTag = ? where staffId = ? and id = ?",new Object[]{status,tag,bean.getNo(),bean.getId()});
    }
    public void resetItem(ReserItemBean bean){
        db.execSQL("update staff_stand_item set chk=? where  standId=? and staffId = ? ",new Object[]{false,bean.getId(),bean.getNo()});
    }
    public void staff_print(ReserItemBean bean,int staffPrint,String path){
            if(isExitsPrint(bean.getId(),bean.getNo())==0){
                //添加
                String sql = null;
                switch (staffPrint){
                    case  0:
                        sql = "insert into staff_stand_line(standId,staffId,staffLine,send) values(?,?,?,?)";
                        break;
                    case  1:
                        sql = "insert into staff_stand_line(standId,staffId,personLine,send) values(?,?,?,?)";
                        break;
                    case  2:
                        sql = "insert into staff_stand_line(standId,staffId,personPhoto,send) values(?,?,?,?)";
                        break;
                }

                db.execSQL(sql,new Object[]{bean.getId(),bean.getNo(),path,0});
            }else{
                String sql = null;

                switch (staffPrint){
                    case  0:
                        sql = "update staff_stand_line set staffLine=? where standId = ? and staffId = ?";
                        break;
                    case  1:
                        sql = "update staff_stand_line set personLine=? where standId = ? and staffId = ?";
                        break;
                    case  2:
                        sql = "update staff_stand_line set personPhoto=? where standId = ? and staffId = ?";
                        break;
                }


                db.execSQL(sql,new Object[]{path,bean.getId(),bean.getNo()});
            }
    }
    public boolean getCanSend(){
        String sql = "select count(*) from staff_stand_line as l join staff_stand as s on s.id = l.standId and s.staffTag ='Y'";
        Cursor cursor = null;
        boolean result = false;
        try {
            cursor = db.rawQuery(sql, new String[] { });
            if(cursor.getCount()!=0) {

                if (cursor.moveToFirst()){
                    result = true;
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


    public int isExitsPrint(String id,String staffId) {
        String sql = "select * from staff_stand_line where standId=? and staffId=?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[] {id,staffId });
            count = cursor.getCount();
            P.c("获得的数据是"+count);
//            while (cursor.moveToNext()) {
//
//            }
            cursor.close();

        } catch (Exception e) {
            P.c("判断"+e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return count;
    }
    public boolean standIsend(ReserItemBean bean){
//select send from staff_stand where id=? and staffId = ?
        String sql = "select send from staff_stand where id=? and staffId = ?";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo()});
            count = cursor.getCount();
            if(count!=0){
                if(cursor.moveToFirst()){
                    return  getBoolean(cursor,"send");
                }
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  false;
    }

    /**
     * 未上传的安检图片
     * @param bean
     * @return
     */
    public ArrayList<ImageRdy> photoIsend(ReserItemBean bean){
        ArrayList<ImageRdy> irs = new ArrayList<ImageRdy>();
        String sql = "select id,path from staff_stand_image_values  where send=0 and standId = ? and staffId = ?;";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo()});
            count = cursor.getCount();
            if(count!=0){
                while(cursor.moveToNext()){
                    ImageRdy rdy = new ImageRdy();
                    rdy.setId(getString(cursor,"id"));
                    rdy.setPath(getString(cursor,"path"));
                    irs.add(rdy);
                }
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
      return  irs;
    }


    public Map<String,String> linePrint(ReserItemBean bean) {
        Map<String,String> map  = new HashMap<>();
        String sql = "select staffLine,personLine,personPhoto, send from staff_stand_line  where standId=? and staffId=?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo() });
            if(cursor.moveToFirst()){
                map.put("staffLine",getString(cursor,"staffLine"));
                map.put("personLine",getString(cursor,"personLine"));
                map.put("personPhoto",getString(cursor,"personPhoto"));
                map.put("send",getString(cursor,"send"));
            }
            cursor.close();

        } catch (Exception e) {
            P.c("判断"+e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return map;
    }
    public boolean canLinePrint(ReserItemBean bean) {

        String sql = "select staffLine,personLine,personPhoto, send from staff_stand_line  where standId=? and staffId=?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo() });
            if(cursor.moveToFirst()){
                if(getString(cursor,"staffLine")==null){
                    return  false;
                }
                if(getString(cursor,"personLine")==null){
                    return  false;
                }
                if(getString(cursor,"personPhoto")==null){
                    return  false;
                }
            }
            cursor.close();

        } catch (Exception e) {
            P.c("判断"+e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return true;
    }


    /**
     * 修改图片组发送情况
     * @param bean
     * @param path
     */
    public void changeSIV(ReserItemBean bean,String path,String id){
        db.execSQL("update staff_stand_image_values set send =?   where standId = ? and staffId = ? and path = ? and id=?",new Object[]{true,bean.getId(),bean.getNo(),path,id});
    }

    /**
     *修改签名组发送情况
     * @param bean
     * @param path
     */
    public void changeLS(ReserItemBean bean,int path){
        db.execSQL("update staff_stand_line set send =?   where standId = ? and staffId = ?",new Object[]{path,bean.getId(),bean.getNo()});
    }

    /**
     * 修改基本信息
     * @param bean
     * @param path
     */
    public void changeSs(ReserItemBean bean){
        db.execSQL("update staff_stand set send =?   where id = ? and staffId = ?",new Object[]{true,bean.getId(),bean.getNo()});
    }

    /**
     * 创建隐患单
     */
    public void addProblem(ReserItemBean bean,ArrayList<StaffTxtItem> sts){
        for(int i=0;i<sts.size();i++){
            db.execSQL("insert into staff_stand_pr_l(standId,staffId,txtNo,txtView) values(?,?,?,?)",new Object[]{bean.getId(),bean.getNo(),sts.get(i).getId(),sts.get(i).getTxt()});
        }
        db.execSQL("insert into staff_stand_pr_s(standId,staffId,send) values(?,?,?)",new Object[]{bean.getId(),bean.getNo(),0});

    }


    public boolean isExitPro(ReserItemBean bean){
//select send from staff_stand where id=? and staffId = ?
        String sql = "select * from staff_stand_pr_l where standId = ? and staffId = ?";
        Cursor cursor = null;
        int count = 0;
        boolean isExit = false;
        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo()});
            count = cursor.getCount();
            if(count!=0){
                isExit = true;
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  isExit;
    }
    /**
     * 读取隐患单数据
     */
    public void getProblemList(ArrayList<StaffTxtItem> stb,ReserItemBean bean){
        stb.clear();;
        String sql = "select standId,staffId,txtNo,txtView from staff_stand_pr_l where standId = ? and staffId = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo()});
                while(cursor.moveToNext()){
                    StaffTxtItem rdy = new StaffTxtItem();
                    rdy.setId(getString(cursor,"txtNo"));
                    rdy.setTxt(getString(cursor,"txtView"));
                    stb.add(rdy);
                }

            cursor.close();

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        //开始进行再次读取
        for(int i=0;i<stb.size();i++){
            setProblemList(stb.get(i),bean);
        }

    }
    private void setProblemList(StaffTxtItem stb,ReserItemBean bean){
        String sql = "select path from staff_stand_pr_l_values where standId = ? and staffId = ? and txtNo =?";
        Cursor cursor = null;
        ArrayList<StaffImageItem> imgs = new ArrayList<>();
        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo(),stb.getId()});
            while(cursor.moveToNext()){
                StaffImageItem rdy = new StaffImageItem();
                rdy.setPath(getString(cursor,"path"));
                imgs.add(rdy);
            }
            cursor.close();
            stb.setImageItems(imgs);
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }

    /**
     * 添加隐患图片列表
     * @param item
     * @param bean
     * @param images
     * @param handler
     */
    public void addDetailProImages(String item, ReserItemBean bean, ArrayList<TImage> images,Handler handler){
        for(int i=0;i<images.size();i++){
            db.execSQL("insert into staff_stand_pr_l_values(standId,staffId,txtNo,path,send) values(?,?,?,?,?)",new Object[]{bean.getId(),bean.getNo(),item,images.get(i).getCompressPath(),false});
        }
        if(handler!=null){
            handler.sendEmptyMessage(50);
        }
    }

    /**
     * 操作隐患列表的其他数据
     */
    public void prother(String colName,String value,ReserItemBean bean){
            db.execSQL("update staff_stand_pr_s set "+colName+" = ? where standId=? and staffId = ?",new Object[]{value,bean.getId(),bean.getNo()});
    }

    /**
     * 获得隐患其他数据
     */
    public void getProStand(Map<String,String> map ,ReserItemBean bean){
        map.clear();
        String sql = "select proNo,startTime,endTime,staffLine,personLine,personPhoto,bis from staff_stand_pr_s where standId = ? and staffId = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo()});
            if(cursor.getCount()!=0){
                if(cursor.moveToFirst()){
                    map.put("proNo",getString(cursor,"proNo"));
                    map.put("startTime",getString(cursor,"startTime")==null?"____-__-__":getString(cursor,"startTime"));
                    map.put("endTime",getString(cursor,"endTime")==null?"____-__-__":getString(cursor,"endTime"));
                    map.put("staffLine",getString(cursor,"staffLine"));
                    map.put("personLine",getString(cursor,"personLine"));
                    map.put("personPhoto",getString(cursor,"personPhoto"));
                    map.put("bis",getString(cursor,"bis")==null?"0":"1");
                }
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

    public boolean canProStand(ReserItemBean bean){

        String sql = "select proNo,startTime,endTime,staffLine,personLine,personPhoto from staff_stand_pr_s where standId = ? and staffId = ?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo()});
            if(cursor.getCount()!=0){
                if(cursor.moveToFirst()){

                    if(getString(cursor,"startTime")==null){
                        return  false;
                    }
                    if(getString(cursor,"endTime")==null){
                        return  false;
                    }

                    if(getString(cursor,"staffLine")==null){
                        return  false;
                    }
                    if(getString(cursor,"personLine")==null){
                        return  false;
                    }
                    if(getString(cursor,"personPhoto")==null){
                        return  false;
                    }


                }
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  true;
    }
    /**
     * 修改隐患单图片组发送情况
     * @param bean
     * @param path
     */
    public void changePSIV(ReserItemBean bean,String path,String id){
        db.execSQL("update staff_stand_pr_l_values set send =?   where standId = ? and staffId = ? and path = ? and txtNo=?",new Object[]{true,bean.getId(),bean.getNo(),path,id});
    }

    /**
     *修改隐患单签名组发送情况
     * @param bean
     * @param path
     */
    public void changePLS(ReserItemBean bean,int path){
        db.execSQL("update staff_stand_pr_s set send =?   where standId = ? and staffId = ?",new Object[]{path,bean.getId(),bean.getNo()});
    }
    public void changePLSB(ReserItemBean bean,String value){
        db.execSQL("update staff_stand_pr_s set proNo =?   where standId = ? and staffId = ?",new Object[]{value,bean.getId(),bean.getNo()});
    }
    public void changePLSBI(ReserItemBean bean,boolean value){
        db.execSQL("update staff_stand_pr_s set bis =?   where standId = ? and staffId = ?",new Object[]{value,bean.getId(),bean.getNo()});
    }
    /**
     * 未上传的隐患图片
     * @param bean
     * @return
     */
    public ArrayList<ImageRdy> photoProIsend(ReserItemBean bean){
        ArrayList<ImageRdy> irs = new ArrayList<ImageRdy>();
        String sql = "select txtNo,path from staff_stand_pr_l_values  where send=0 and standId = ? and staffId = ?;";
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo()});
            count = cursor.getCount();
            if(count!=0){
                while(cursor.moveToNext()){
                    ImageRdy rdy = new ImageRdy();
                    rdy.setId(getString(cursor,"txtNo"));
                    rdy.setPath(getString(cursor,"path"));
                    irs.add(rdy);
                }
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return  irs;
    }
    public Map<String,String> ProLinePrint(ReserItemBean bean) {
        Map<String,String> map  = new HashMap<>();
        String sql = "select startTime,endTime,proNo,staffLine,personLine,personPhoto,send,bis from staff_stand_pr_s  where standId=? and staffId=?";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(sql, new String[] {bean.getId(),bean.getNo() });
            if(cursor.moveToFirst()){
                map.put("proNo",getString(cursor,"proNo"));
                map.put("startTime",getString(cursor,"startTime"));
                map.put("endTime",getString(cursor,"endTime"));
                map.put("staffLine",getString(cursor,"staffLine"));
                map.put("personLine",getString(cursor,"personLine"));
                map.put("personPhoto",getString(cursor,"personPhoto"));
                map.put("send",getString(cursor,"send"));
                map.put("bis",getString(cursor,"bis")==null?"0":"1");


            }
            cursor.close();

        } catch (Exception e) {
            P.c("判断"+e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return map;
    }
    public void clear(){

        db.execSQL("DELETE FROM staff_stand");
        db.execSQL("DELETE FROM staff_stand_image");
        db.execSQL("DELETE FROM staff_stand_image_values");
        db.execSQL("DELETE FROM staff_stand_item");
        db.execSQL("DELETE FROM staff_stand_tab");
        db.execSQL("DELETE FROM staff_stand_qj");
        db.execSQL("DELETE FROM staff_stand_line");
        db.execSQL("DELETE FROM staff_stand_pr_s");
        db.execSQL("DELETE FROM staff_stand_pr_l_values");
        db.execSQL("DELETE FROM staff_stand_pr_l");

    }
}
