package com.hhkj.gas.www.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.hhkj.gas.www.R;
import com.hhkj.gas.www.adapter.HeadChildsItemAdapter;
import com.hhkj.gas.www.bean.HeadChild;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.common.Common;
import com.hhkj.gas.www.common.FileUtils;
import com.hhkj.gas.www.common.P;
import com.hhkj.gas.www.common.SharedUtils;
import com.hhkj.gas.www.common.U;
import com.zc.http.okhttp.OkHttpUtils;
import com.zc.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import okhttp3.Call;
import okhttp3.MediaType;

public class HeadChildsList {
    private Context context;

    private IDialog dlg;

    private  LayoutInflater inflater;
    private SharedUtils sharedUtils;
    private ArrayList<ReserItemBean> ribs;
    private Handler startHandler;
    public HeadChildsList(Context context, SharedUtils sharedUtils,ArrayList<ReserItemBean> ribs,Handler startHandler) {
        this.context = context;
        this.ribs = ribs;
        this.startHandler = startHandler;
        this.sharedUtils = sharedUtils;

       inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        switch (msg.what){
            case 1:
                itemAdapter.updata(rbs);
                break;
        }
        }
    };

    /**
     * 指派
     * @param toUser
     */
    private void send(String toUser) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ribs.size(); i++) {
            ReserItemBean rtb = ribs.get(i);
            if (rtb.isOpen()) {
                builder.append(rtb.getId() + ",");
            }
        }
        final String temp = builder.toString();
        if (temp.length() > 0) {

            //在这里进行领取任务
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("toKen", sharedUtils.getStringValue("token"));
                jsonObject.put("cls", "Gas.SecurityOrder");
                jsonObject.put("method", "AssignOrder");
                JSONObject object = new JSONObject();
                object.put("OrderIds", temp);
                object.put("StaffId", toUser);
                jsonObject.put("param", object.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {



                    try {
                        JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                        if(jsonObject.getBoolean("Success")){
                                //指派成功
                            String []strs = temp.split(",");
                            for(int i=0;i<ribs.size();i++){
                                for(int j=0;j<strs.length;j++){
                                    if(ribs.get(i).getId().equals(strs[j])){
                                        ribs.remove(i);
                                    }
                                }
                            }

                            startHandler.sendEmptyMessage(5);
                            cancle();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }
    private  HeadChildsItemAdapter itemAdapter;
    private ArrayList<HeadChild> rbs = new ArrayList<>();
    public Dialog showSheet() {
        dlg = new IDialog(context, R.style.head_pop_style);
        final LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.head_child_layout, null);
        TextView sure = (TextView) layout.findViewById(R.id.sure);
        TextView cancle = (TextView) layout.findViewById(R.id.cancle);
        ListView childs_list = (ListView) layout.findViewById(R.id.childs_list);
          itemAdapter = new HeadChildsItemAdapter(context,rbs);
        childs_list.setAdapter(itemAdapter);
        childs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                itemAdapter.select(i);
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index =itemAdapter.getSelect();
                if(index!=-1){
                    send(rbs.get(index).getId());
                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancle();
            }
        });
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("toKen",sharedUtils.getStringValue("token"));
            jsonObject.put("cls","Gas.Staff");
            jsonObject.put("method","GetStaffList");
            jsonObject.put("param","");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        OkHttpUtils.postString().url(U.VISTER(U.BASE_URL)+U.LIST).mediaType(MediaType.parse("application/json; charset=utf-8")).content(jsonObject.toString()).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                P.c("错误了"+e.getLocalizedMessage());
            }

            @Override
            public void onResponse(String response, int id) {



                try {
                    JSONObject jsonObject = new JSONObject(FileUtils.formatJson(response));
                    if(jsonObject.getBoolean("Success")){
                        rbs.clear();
                        String result = jsonObject.getString("Result");
                        JSONArray jsonArray = new JSONArray(result);
                        int len = jsonArray.length();
                        for(int i=0;i<len;i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            HeadChild child  = new HeadChild();
                            child.setId(object.getString("Id"));
                            child.setName(object.getString("StaffName"));
                            rbs.add(child);
                        }
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


        dlg.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                // TODO Auto-generated method stub

            }
        });
        dlg.setCanceledOnTouchOutside(true);
        dlg.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface arg0) {
                // TODO Auto-generated method stub

            }
        });
        dlg.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                // TODO Auto-generated method stub

            }
        });
        dlg.setContentView(layout);
        dlg.show();
        return dlg;
    }
    public void cancle(){
        if(dlg!=null){
            dlg.cancel();
            dlg = null;
        }
    }
}
