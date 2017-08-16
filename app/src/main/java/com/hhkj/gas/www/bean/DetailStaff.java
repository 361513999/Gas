package com.hhkj.gas.www.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/16/016.
 */

public class DetailStaff implements Serializable {
    private StaffTxtItem item;
    private ArrayList<StaffTxtItem> items;
    private String items_tag;

    public String getItems_tag() {
        return items_tag;
    }

    public void setItems_tag(String items_tag) {
        this.items_tag = items_tag;
    }

    public StaffTxtItem getItem() {
        return item;
    }

    public void setItem(StaffTxtItem item) {
        this.item = item;
    }

    public ArrayList<StaffTxtItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<StaffTxtItem> items) {
        this.items = items;
    }
}
