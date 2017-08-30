package com.hhkj.gas.www.common;

import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

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
    public static final int SHOW_NUM = 1;
    public static final String STAFF_IMAGE = "IMAGES/";
    public static final String COMMON_DEFAULT = "00000000-0000-0000-0000-000000000000";
    public static final String CACHE_STAFF_IMAGES = SD+STAFF_IMAGE;

    public static void copy(){
        CopyFile cf = new CopyFile();
        cf.copyFile("data/data/com.hhkj.gas.www/databases/"+Common.DB_NAME,Common.BASE_DIR +"/droid4xshare/2.db");
    }
    DisplayImageOptions options =  new DisplayImageOptions.Builder().showImageOnLoading(0)
            .showImageForEmptyUri(0).showImageOnFail(0)
            .cacheInMemory(true).cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .build();
}
