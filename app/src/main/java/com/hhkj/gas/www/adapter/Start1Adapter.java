package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.inter.TimeSelect;
import com.hhkj.gas.www.widget.ChangeTime;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class Start1Adapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<ReserItemBean> rbs;
    private SharedUtils sharedUtils;
    public Start1Adapter(Context context, ArrayList<ReserItemBean> rbs,SharedUtils sharedUtils){
        this.context = context;
        this.rbs = rbs;
        this.sharedUtils = sharedUtils;
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
        TextView item0,item1,item2,item3,item4,item_tag;
        ImageView item_icon,item_edit;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.start1_item, null);
            viewHolder.item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
            viewHolder.item_tag = (TextView) convertView.findViewById(R.id.item_tag);
            viewHolder.item0 = (TextView) convertView.findViewById(R.id.item0);
            viewHolder.item1 = (TextView) convertView.findViewById(R.id.item1);
            viewHolder.item2 = (TextView) convertView.findViewById(R.id.item2);
            viewHolder.item3 = (TextView) convertView.findViewById(R.id.item3);
            viewHolder.item4 = (TextView) convertView.findViewById(R.id.item4);
            viewHolder.item_edit = (ImageView) convertView.findViewById(R.id.item_edit);
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
       switch (it.getOrderStatus()){
           case 3:
               viewHolder.item_icon.setBackgroundResource(R.mipmap.icon_running);
               viewHolder.item_tag.setText("进行中");
               break;
           case 6:
               //6重新安检中，7等待再次执行，8隐患整改中
               viewHolder.item_icon.setBackgroundResource(R.mipmap.icon_check_again);
               viewHolder.item_tag.setText("重新安检中");
               break;
           case 7:
               break;
           case 8:
               viewHolder.item_icon.setBackgroundResource(R.mipmap.icon_zhengtai);
               viewHolder.item_tag.setText("整改中");
               break;
       }

        if(it.getTime().equals("NON")){
            //无时间标记

            viewHolder.item4.setTextColor(context.getResources().getColor(R.color.blue_n));
            viewHolder.item4.setText(context.getString(R.string.nor_item_txt4,"____-__-__ __:__"));
            viewHolder.item_edit.setVisibility(View.VISIBLE);
            viewHolder.item_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangeTime changeTime = new ChangeTime(context,sharedUtils,it);
                    changeTime.setI(new TimeSelect() {
                        @Override
                        public void success(String time) {
                            notifyDataSetChanged();
                        }
                    });
                    changeTime.showSheet();
                }
            });
        }else{
            viewHolder.item_edit.setVisibility(View.GONE);
            viewHolder.item4.setTextColor(context.getResources().getColor(R.color.font_normal));
            viewHolder.item4.setText(context.getString(R.string.nor_item_txt4,it.getTime()));
        }

        return  convertView;
    }
}