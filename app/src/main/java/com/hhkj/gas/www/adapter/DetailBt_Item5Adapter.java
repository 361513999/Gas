package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffTxtItem;
import java.util.ArrayList;


/**
 * Created by cloor on 2017/8/9.
 */

public class DetailBt_Item5Adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private  ArrayList<DetailStaff>  rbs;
    private ReserItemBean bean;
    public DetailBt_Item5Adapter(Context context, ArrayList<DetailStaff>  rbs){
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
            TextView item0,item1,t,f;

    }
    String NULL = "□";
    String RIGHT = "√";
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_bt_item5, null);
            viewHolder.item0 = (TextView) convertView.findViewById(R.id.item0);
            viewHolder.item1 = (TextView) convertView.findViewById(R.id.item1);
            viewHolder.t = (TextView) convertView.findViewById(R.id.t);
            viewHolder.f = (TextView) convertView.findViewById(R.id.f);

            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }


        final DetailStaff it = rbs.get(position);
        int index = position+1;
        viewHolder.item0.setText(String.valueOf(index));
        if(it.getItem()!=null){
            //单项
            final StaffTxtItem si = it.getItem();
            viewHolder.item1.setText(si.getTxt());
            if(si.isCheck()){
                viewHolder.t.setText(RIGHT);
                viewHolder.f.setBackgroundResource(R.mipmap.ck_false);
                viewHolder.f.setLayoutParams(new LinearLayout.LayoutParams(40,40));
            }else{
                viewHolder.t.setBackgroundResource(R.mipmap.ck_false);
                viewHolder.t.setLayoutParams(new LinearLayout.LayoutParams(40,40));
                viewHolder.f.setText(RIGHT);
            }
        }else if(it.getItems()!=null){
            //复项
            ArrayList<StaffTxtItem> temps = it.getItems();

//                    viewHolder.item_gc.removeAllViews();
            StringBuilder builder = new StringBuilder();
            boolean flag = false;
            for(int i=0;i<temps.size();i++){
                final StaffTxtItem si =  temps.get(i);
                if(si.isCheck()){
                    flag = true;
                }
                if(i==temps.size()-1){
                    builder.append(si.getTxt());
                }else{
                    builder.append(si.getTxt()+"、");
                }
            }
            if(flag){
                viewHolder.t.setText(RIGHT);
                viewHolder.f.setBackgroundResource(R.mipmap.ck_false);
                viewHolder.f.setLayoutParams(new LinearLayout.LayoutParams(40,40));
            }else{
                viewHolder.t.setBackgroundResource(R.mipmap.ck_false);
                viewHolder.t.setLayoutParams(new LinearLayout.LayoutParams(40,40));
                viewHolder.f.setText(RIGHT);
            }
            viewHolder.item1.setText(it.getItems_tag()+":"+builder.toString());
        }


        return  convertView;
    }


}
