package com.hhkj.gas.www.bean;

import java.io.Serializable;

/**
 * Created by cloor on 2017/8/18.
 */

public class StaffQj implements Serializable {
    private String id;
    private String name;
    private String position;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
