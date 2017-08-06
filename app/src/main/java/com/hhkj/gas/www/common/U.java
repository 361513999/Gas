package com.hhkj.gas.www.common;

/**
 * Created by cloor on 2017/8/6.
 */

public class U {
    private static final String BASE_URL = "/DataService.svc/";
    public static String VISTER(String IP,String url){
        return "http://"+IP+url;
    }
}
