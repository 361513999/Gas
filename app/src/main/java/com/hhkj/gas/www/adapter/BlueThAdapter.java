package com.hhkj.gas.www.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.DetailBItem;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class BlueThAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<BluetoothDevice> rbs;
    public BlueThAdapter(Context context, ArrayList<BluetoothDevice> rbs){
        this.context = context;
        this.rbs = rbs;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<BluetoothDevice> rbs){
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
            TextView item0,item1;
    }
    int select = -1;
    public void select(int select){
        this.select = select;
        notifyDataSetChanged();
    }
    public BluetoothDevice getDevice(){
        if(select!=-1){
            return  rbs.get(select);
        }
        return  null;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.blueth_item, null);
            viewHolder.item0 = (TextView) convertView.findViewById(R.id.item0);
            viewHolder.item1 = (TextView) convertView.findViewById(R.id.item1);
            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }
        if(select==position){
            viewHolder.item0.setTextColor(context.getResources().getColor(R.color.green_n));
        }else{
            viewHolder.item0.setTextColor(context.getResources().getColor(R.color.font_normal));
        }
        BluetoothDevice dev = rbs.get(position);
        viewHolder.item0.setText(dev.getName());
        viewHolder.item1.setText(dev.getAddress());

        return  convertView;
    }
}
