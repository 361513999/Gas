package com.hhkj.gas.www.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/15/015.
 */

public class StaffImageItem implements Serializable {
    private String path;
    private String tag;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
