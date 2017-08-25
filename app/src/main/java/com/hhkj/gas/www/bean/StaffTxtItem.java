package com.hhkj.gas.www.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/16/016.
 */

public class StaffTxtItem implements Serializable {
    private String id;
    private String txt;
    private boolean check;
    //这项用户隐患单
    private ArrayList<StaffImageItem>  imageItems;

    public ArrayList<StaffImageItem> getImageItems() {
        return imageItems;
    }

    public void setImageItems(ArrayList<StaffImageItem> imageItems) {
        this.imageItems = imageItems;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
