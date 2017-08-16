package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.widget.auto.AutoFlowLayout;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class StaffItemAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private  ArrayList<DetailStaff>  rbs;
    public StaffItemAdapter(Context context,  ArrayList<DetailStaff>  rbs){
        this.context = context;
        this.rbs = rbs;
        inflater = LayoutInflater.from(context);
    }
    private int size = 0;
    public void updata( ArrayList<DetailStaff>  rbs,int size){
        this.rbs = rbs;
        this.size = size;
        notifyDataSetChanged();
    }
    public void updata( ArrayList<DetailStaff>  rbs){
        this.rbs = rbs;
        this.size = rbs.size();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return rbs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder {
        RelativeLayout layout0;
        LinearLayout layout1;
        TextView item_t,item_gt;
        AutoFlowLayout item_gc;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_staff_item_layout, null);
            viewHolder.layout0 = (RelativeLayout) convertView.findViewById(R.id.layout0);
            viewHolder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
            viewHolder.item_t = (TextView) convertView.findViewById(R.id.item_t);
            viewHolder.item_gt = (TextView) convertView.findViewById(R.id.item_gt);
            viewHolder.item_gc = (AutoFlowLayout) convertView.findViewById(R.id.item_gc);
            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }
        DetailStaff it = rbs.get(position);
        int index = position+1;
        if(it.getItem()!=null){
            //单项
            viewHolder.layout0.setVisibility(View.VISIBLE);
            viewHolder.layout1.setVisibility(View.GONE);
            viewHolder.item_t.setText(index+"、"+it.getItem().getTxt());
        }else if(it.getItems()!=null){
            //复项

            viewHolder.layout0.setVisibility(View.GONE);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.item_gt.setText(index+"、"+it.getItems_tag());
            ArrayList<StaffTxtItem> temps = it.getItems();
            for(int i=0;i<temps.size();i++){
                StaffTxtItem si =  temps.get(i);
                View v = inflater.inflate(R.layout.gc_item,null);
                TextView gc_item0 = (TextView) v.findViewById(R.id.gc_item0);
                CheckBox gc_item1 = (CheckBox) v.findViewById(R.id.gc_item1);
                gc_item0.setText(si.getTxt());
                viewHolder.item_gc.addView(v);
            }

        }

        return  convertView;
    }
}
