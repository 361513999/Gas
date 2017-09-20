package com.hhkj.gas.www.adapter;

import android.content.Context;
import android.os.Handler;
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
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffTxtItem;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.db.DB;
import com.hhkj.gas.www.widget.auto.AutoFlowLayout;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


/**
 * Created by cloor on 2017/8/9.
 */

public class StaffItemAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private  ArrayList<DetailStaff>  rbs;
    private ReserItemBean bean;
    private Handler handler;
    public StaffItemAdapter(Context context,  ArrayList<DetailStaff>  rbs, ReserItemBean bean,Handler handler){
        this.context = context;
        this.handler = handler;
        this.rbs = rbs;
        this.bean = bean;

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
        TextView item_c;
        AutoFlowLayout item_gc;

    }
    int no = R.mipmap.icon_nor_cb_n;
    int yes = R.mipmap.icon_nor_cb_p;
    private boolean chk(TextView view){
            return (boolean) view.getTag(R.id.staff_item_id);
    }

    /**
     * s设置背景和tag
     */
    private void setBt(TextView view,boolean bk){
        if(bk){
            view.setTag(R.id.staff_item_id,true);
            view.setBackgroundResource(yes);
        }else{
            view.setTag(R.id.staff_item_id,false);
            view.setBackgroundResource(no);
        }
    }
    private boolean canClick = true;
    public void changeClick(boolean canClick){
        this.canClick = canClick;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null
                || convertView.getTag(R.mipmap.ic_launcher + position) == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_staff_item_layout, null);
            viewHolder.layout0 = (RelativeLayout) convertView.findViewById(R.id.layout0);
            viewHolder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
            viewHolder.item_c = (TextView) convertView.findViewById(R.id.item_c);
            viewHolder.item_t = (TextView) convertView.findViewById(R.id.item_t);
            viewHolder.item_gt = (TextView) convertView.findViewById(R.id.item_gt);
            viewHolder.item_gc = (AutoFlowLayout) convertView.findViewById(R.id.item_gc);

            convertView.setTag(R.mipmap.ic_launcher + position);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.mipmap.ic_launcher
                    + position);
        }


        final DetailStaff it = rbs.get(position);
        int index = position+1;
        if(it.getItem()!=null){
            //单项
            final StaffTxtItem si = it.getItem();
            viewHolder.layout0.setVisibility(View.VISIBLE);
            viewHolder.layout1.setVisibility(View.GONE);

            setBt(viewHolder.item_c,si.isCheck());
            viewHolder.item_t.setText(index+"、"+si.getTxt());
            if(canClick){
                viewHolder.item_c.setEnabled(canClick);
            }
            viewHolder.item_c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tag = DB.getInstance().getStafftag(bean);
                    P.c("tag"+tag);
                    if(tag==null ){
                        //这种状态才能整改
                        P.c("可以更改");
                        DB.getInstance().updateStandItemChk(si.getId(),!chk(viewHolder.item_c),handler);

                    }/*else if(!tag.equals("Y")){
                        DB.getInstance().updateStandItemChk(si.getId(),!chk(viewHolder.item_c),handler);
                    }*/
                    else{
                        //不能更改
                        P.c("不能更改");
                    }
                }
            });
        }else if(it.getItems()!=null){
            //复项

            viewHolder.layout0.setVisibility(View.GONE);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.item_gt.setText(index+"、"+it.getItems_tag());
            ArrayList<StaffTxtItem> temps = it.getItems();

//                    viewHolder.item_gc.removeAllViews();

            for(int i=0;i<temps.size();i++){
                final StaffTxtItem si =  temps.get(i);
                View childView = inflater.inflate(R.layout.gc_item,null);
                TextView gc_item0 = (TextView) childView.findViewById(R.id.gc_item0);
                final TextView gc_item1 = (TextView) childView.findViewById(R.id.gc_item1);
                gc_item0.setText(si.getTxt());
                setBt(gc_item1,si.isCheck());
                viewHolder.item_gc.addView(childView);
                P.c("数量--"+viewHolder.item_gc.getChildCount());
                if(canClick){
                    gc_item1.setEnabled(canClick);
                }
                gc_item1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(DB.getInstance().getStafftag(bean)==null){
                            //这种状态才能整改
                            P.c("复项可以更改");
                            DB.getInstance().updateStandItemChk(si.getId(),!chk(gc_item1),handler);
                        }else{
                            //不能更改
                        }
                    }
                });
            }
        }


        return  convertView;
    }


}
