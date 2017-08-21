package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffQj;
import com.hhkj.gas.www.bean.StaffTxtItem;

import java.util.ArrayList;


/**
 * Created by cloor on 2017/8/9.
 */

public class DetailBt_Item7Adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private  ArrayList<StaffQj>  rbs;
    private ReserItemBean bean;
    public DetailBt_Item7Adapter(Context context, ArrayList<StaffQj>  rbs){
        this.context = context;
        this.rbs = rbs;
        inflater = LayoutInflater.from(context);
    }
    int size = 0;
    public void updata( ArrayList<StaffQj>  rbs){
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
            TextView item0,item1,item2,item3;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_bt_item7, null);
            viewHolder.item0 = (TextView) convertView.findViewById(R.id.item0);
            viewHolder.item1 = (TextView) convertView.findViewById(R.id.item1);
            viewHolder.item2 = (TextView) convertView.findViewById(R.id.item2);
            viewHolder.item3 = (TextView) convertView.findViewById(R.id.item3);

            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }


        final StaffQj it = rbs.get(position);
        int index = position+1;
        viewHolder.item0.setText(String.valueOf(index));

            viewHolder.item1.setText(it.getName());
        viewHolder.item2.setText(it.getPosition());
        if(it.isCheck()){
            viewHolder.item3.setText("正常");
        }else{
            viewHolder.item3.setText("不正常");
        }

        return  convertView;
    }


}
