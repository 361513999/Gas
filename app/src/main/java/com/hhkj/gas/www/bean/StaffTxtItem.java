package com.hhkj.gas.www.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/16/016.
 */

public class StaffTxtItem implements Serializable {
    private String id;
    private String txt;
    private boolean check;

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
