package com.hhkj.gas.www.bean;

import java.io.Serializable;

/**
 * Created by cloor on 2017/8/16.
 */

public class DetailBItem implements Serializable {
    private String name;
    private String local;
    private boolean check;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
