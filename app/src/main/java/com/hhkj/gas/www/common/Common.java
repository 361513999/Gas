package com.hhkj.gas.www.common;

import android.os.Environment;

/**
 * Created by cloor on 2017/8/6.
 */

public class Common {
    public static String BASE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static String DIR = "/GAS_CACHE/";
    public static String SD = BASE_DIR+DIR;
    public static String APK_LOG = SD+"LOG/";
    public final static String DB_DIR = "data/data/"+BaseApplication.application.getPackageName()+"/databases/";
    public static final String DB_NAME = "store.gas";
}