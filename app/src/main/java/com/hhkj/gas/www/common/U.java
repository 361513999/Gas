package com.hhkj.gas.www.common;

import com.hhkj.gas.www.utils.RegExpValidatorUtils;

/**
 * Created by cloor on 2017/8/6.
 */

public class U {

    public static  String BASEIP = "106.14.212.85:8001";
    public static final String BASE_URL = "/DataService.svc/";
    public static final String LOGIN = "StaffLogin";
    public static String VISTER(String url){
        SharedUtils sharedUtils = new SharedUtils(Common.config);
        String ip = sharedUtils.getStringValue("IP");
        if(ip.length()!=0&& RegExpValidatorUtils.isIP(ip)){
            return "http://"+ip+url;
        }
        return "http://"+BASEIP+url;
    }
}
