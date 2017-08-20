package com.hhkj.gas.www.common;

import android.os.Environment;

/**
 * Created by cloor on 2017/8/6.
 */

public class Common {
    public static final int TTIME = 500;
    public static final String config = "CONFIG";
    public static String BASE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static String DIR = "/GAS_CACHE/";
    public static String SD = BASE_DIR+DIR;
    public static String APK_LOG = SD+"LOG/";
    public final static String DB_DIR = "data/data/"+BaseApplication.application.getPackageName()+"/databases/";
    public static final String DB_NAME = "store.gas";
    public static final int DB_VERSION = 1;
    public static final String UNLOGIN = "login";
    public static final int SHOW_NUM = 4;
    public static final String STAFF_IMAGE = "IMAGES/";
    public static final String CACHE_STAFF_IMAGES = SD+STAFF_IMAGE;
    public static void copy(){
        CopyFile cf = new CopyFile();
        cf.copyFile("data/data/com.hhkj.gas.www/databases/"+Common.DB_NAME,Common.BASE_DIR +"/Download/2.db");
    }

}
