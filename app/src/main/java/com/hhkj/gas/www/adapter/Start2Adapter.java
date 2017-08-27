package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.SharedUtils;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class Start2Adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ReserItemBean> rbs;
    private SharedUtils sharedUtils;
    public Start2Adapter(Context context, ArrayList<ReserItemBean> rbs,  SharedUtils sharedUtils){
        this.context = context;
        this.rbs = rbs;
        this.sharedUtils =sharedUtils;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<ReserItemBean> rbs){
        this.rbs = rbs;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return rbs.size();
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
        TextView item0,item1,item2,item3,item4,item5;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.start2_item, null);

            viewHolder.item0 = (TextView) convertView.findViewById(R.id.item0);
            viewHolder.item1 = (TextView) convertView.findViewById(R.id.item1);
            viewHolder.item2 = (TextView) convertView.findViewById(R.id.item2);
            viewHolder.item3 = (TextView) convertView.findViewById(R.id.item3);
            viewHolder.item4 = (TextView) convertView.findViewById(R.id.item4);
            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }

        final ReserItemBean it = rbs.get(position);
        viewHolder.item0.setText(context.getString(R.string.nor_item_txt0,it.getNo()));
        viewHolder.item1.setText(context.getString(R.string.nor_item_txt1,it.getName()));
        viewHolder.item2.setText(context.getString(R.string.nor_item_txt2,it.getTel()));
        viewHolder.item3.setText(context.getString(R.string.nor_item_txt3,it.getAdd()));
        viewHolder.item5 = (TextView) convertView.findViewById(R.id.item5);
        if(sharedUtils.getBooleanValue("head")){
            viewHolder.item5.setText("(指派给:"+it.getStaffName()+")");
        }
        if(it.getTime().equals("待定")){
            viewHolder.item4.setTextColor(context.getResources().getColor(R.color.rd_p));
        }else{
            viewHolder.item4.setTextColor(context.getResources().getColor(R.color.font_normal));
        }

        viewHolder.item4.setText(context.getString(R.string.nor_item_txt4,it.getTime()));
        return  convertView;
    }
}
