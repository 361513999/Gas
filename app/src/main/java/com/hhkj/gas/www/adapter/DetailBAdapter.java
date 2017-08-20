package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffQj;

import java.util.ArrayList;

/**
 * Created by cloor on 2017/8/9.
 */

public class DetailBAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<StaffQj> rbs;
    public DetailBAdapter(Context context, ArrayList<StaffQj> rbs){
        this.context = context;
        this.rbs = rbs;
        inflater = LayoutInflater.from(context);
    }
    public void updata(ArrayList<StaffQj> rbs){
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
            ImageView item3;
        EditText item0,item1;
        TextView item2;
    }
    int no = R.mipmap.icon_nor_cb_n;
    int yes = R.mipmap.icon_nor_cb_p;
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_b_item, null);
            viewHolder.item3 = (ImageView) convertView.findViewById(R.id.item3);
            viewHolder.item0 = (EditText) convertView.findViewById(R.id.item0);
            viewHolder.item1 = (EditText) convertView.findViewById(R.id.item1);
            viewHolder.item2 = (TextView) convertView.findViewById(R.id.item2);
            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }
        final StaffQj qj = rbs.get(position);
        viewHolder.item0.setText(qj.getName());
        viewHolder.item1.setText(qj.getPosition());
        viewHolder.item3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbs.remove(position);
                notifyDataSetChanged();
            }
        });
        if(qj.isCheck()){
            viewHolder.item2.setBackgroundResource(yes);
        }else {
            viewHolder.item2.setBackgroundResource(no);
        }
        viewHolder.item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qj.setCheck(!qj.isCheck());
                notifyDataSetChanged();
            }
        });
        viewHolder.item0.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                qj.setName(editable.toString());

            }
        });
        viewHolder.item1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                qj.setPosition(s.toString());
            }
        });
        return  convertView;
    }
}
