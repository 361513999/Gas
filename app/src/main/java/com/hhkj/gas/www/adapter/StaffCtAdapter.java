package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffCtBean;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.widget.auto.AutoFlowLayout;

import java.util.ArrayList;


/**
 * Created by cloor on 2017/8/9.
 */

public class StaffCtAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private  ArrayList<StaffCtBean>  rbs;
    private ReserItemBean bean;
    private Handler handler;
    public StaffCtAdapter(Context context, ArrayList<StaffCtBean>  rbs){
        this.context = context;

        this.rbs = rbs;


        inflater = LayoutInflater.from(context);
    }
    private int size = 0;
    public void updata( ArrayList<StaffCtBean>  rbs,int size){
        this.rbs = rbs;
        this.size = size;
        notifyDataSetChanged();
    }
    public void updata( ArrayList<StaffCtBean>  rbs){
        this.rbs = rbs;
        this.size = rbs.size();
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

        TextView item_t;
        TextView item_c;


    }
    private int select = -1;
    public void select(int index){
        this.select = index;
        notifyDataSetChanged();
    }
    public int getSelect(){
        return  select;
    }
    int no = R.mipmap.icon_nor_cb_n;
    int yes = R.mipmap.icon_nor_cb_p;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.staff_ct_item, null);

            viewHolder.item_c = (TextView) convertView.findViewById(R.id.item_c);
            viewHolder.item_t = (TextView) convertView.findViewById(R.id.item_t);

            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }


          final StaffCtBean it = rbs.get(position);
        int index = position+1;
        viewHolder.item_t.setText(index+"、"+it.getName());

        if(select==position){
            viewHolder.item_c.setBackgroundResource(yes);
        }else{
            viewHolder.item_c.setBackgroundResource(no);
        }






        return  convertView;
    }


}
