package com.hhkj.gas.www.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/9/009.
 */

public class ReserItemBean implements Serializable{
    private String no;
    private String name;
    private String tel;
    private String add;
    private String time;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
