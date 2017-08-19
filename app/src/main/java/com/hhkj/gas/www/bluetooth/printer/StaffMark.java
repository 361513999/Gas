package com.hhkj.gas.www.bluetooth.printer;

import android.content.Context;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bean.DetailStaff;
import com.hhkj.gas.www.bean.ReserItemBean;
import com.hhkj.gas.www.bean.StaffB;
import com.hhkj.gas.www.bean.StaffImageItem;
import com.hhkj.gas.www.bean.StaffQj;
import com.hhkj.gas.www.bluetooth.printer.PrintDataMaker;
import com.hhkj.gas.www.common.P;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/17/017.
 */

/**
 * 安检打印
 */
public class StaffMark implements PrintDataMaker {
    private PrinterWriter printerWriter;


    @Override
    public List<byte[]> getPrintData(int type) {
        ArrayList<byte[]> data = new ArrayList<>();
        try {
            printerWriter =  new PrinterWriter80mm(255, 600);
            printerWriter.setAlignCenter();
            data.add(printerWriter.getDataAndReset());
            //打印logo
            ArrayList<byte[]> logo = printerWriter.getImageByte(context.getResources(), R.mipmap.logo);
            //data.addAll(logo);

            printerWriter.setAlignLeft();
            printerWriter.printLine();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            //开始文字
            printerWriter.setAlignCenter();
            printerWriter.setEmphasizedOn();
            printerWriter.print("珠海港兴管道天然气有限公司");
            printerWriter.setEmphasizedOff();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.setFontSize(1);
            printerWriter.print("户内管安全检查工作单");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.setFontSize(0);
            printerWriter.print("工作编号:"+bean.getNo());
            printerWriter.printLineFeed();
            printerWriter.setAlignRight();
            printerWriter.print("第一次打印");
            printerWriter.printLineFeed();
            printerWriter.setAlignLeft();
            //开始写客户
            printerWriter.writeH(70);
            printerWriter.printTP("客户姓名:"+bean.getName());
            printerWriter.print("联系电话:"+bean.getTel());
            printerWriter.printLineFeed();
            printerWriter.print("预约时间:"+bean.getTel());
            printerWriter.printLineFeed();

            printerWriter.printAdd("地址:"+bean.getAdd(),3);
//            printerWriter.printAdd("地址:广东省深圳市宝安区西乡街道宝源路(就是要让地址长长的，看看会怎样)",2);
            printerWriter.printLine();
            printerWriter.printLineFeed();

            printerWriter.print("序号"+printerWriter.getEmp(8)+"项目"+printerWriter.getEmp(7)+"检查情况"+printerWriter.getEmp(1));
            //
            printerWriter.printLineFeed();
            printerWriter.printStaffItems(dss);
            printerWriter.writeH(70);
           // printerWriter.setLineHeight(10);
            printerWriter.printLineFeed();
            printerWriter.print("序号"+printerWriter.getEmp(1)+"燃气表"+printerWriter.getEmp(1)+"品牌型号"+printerWriter.getEmp(2)+"机表读数"+printerWriter.getEmp(2)+"运行情况"+printerWriter.getEmp(1));
            if(staffBs.size()==0){
                printerWriter.print(printerWriter.getEmpOne(2)+"1"+printerWriter.getEmpOne(4)+"表一");
                printerWriter.printLineFeed();
                printerWriter.print(printerWriter.getEmpOne(2)+"2"+printerWriter.getEmpOne(4)+"表二");
                printerWriter.printLineFeed();
            }else if (staffBs.size()==1){
                StaffB b0 =  staffBs.get(0);
                String v0 = "";
                String v1 = "";
                if(b0.getName().length()>=10){
                    v0 =  b0.getName().substring(0,10);
                }else{
                    v0 =  b0.getName()+printerWriter.getEmpOne(10-b0.getName().length());
                }
                if(b0.getValue().length()>=8){
                    v1 =  b0.getValue().substring(0,8);
                }else{
                    v1 =  b0.getValue()+printerWriter.getEmpOne(8-b0.getValue().length());
                }
                String ck = b0.isCheck()?"正常":"不正常";

                printerWriter.print(printerWriter.getEmpOne(2)+"1"+printerWriter.getEmpOne(4)+"表一"+printerWriter.getEmpOne(3)+v0+printerWriter.getEmpOne(2)+v1+printerWriter.getEmpOne(4)+ck+printerWriter.getEmpOne(2));
                printerWriter.printLineFeed();
                printerWriter.print(printerWriter.getEmpOne(2)+"2"+printerWriter.getEmpOne(4)+"表二");
                printerWriter.printLineFeed();
            }else if(staffBs.size()>1){
                {
                    StaffB b0 =  staffBs.get(0);
                    String v0 = "";
                    String v1 = "";
                    if(b0.getName().length()>=10){
                        v0 =  b0.getName().substring(0,10);
                    }else{
                        v0 =  b0.getName()+printerWriter.getEmpOne(10-b0.getName().length());
                    }
                    if(b0.getValue().length()>=8){
                        v1 =  b0.getValue().substring(0,8);
                    }else{
                        v1 =  b0.getValue()+printerWriter.getEmpOne(8-b0.getValue().length());
                    }
                    String ck = b0.isCheck()?"正常":"不正常";
                    printerWriter.print(printerWriter.getEmpOne(2)+"1"+printerWriter.getEmpOne(4)+"表一"+printerWriter.getEmpOne(3)+v0+printerWriter.getEmpOne(2)+v1+printerWriter.getEmpOne(4)+ck+printerWriter.getEmpOne(2));
                    printerWriter.printLineFeed();
                }
                {
                    StaffB b0 =  staffBs.get(1);
                    String v0 = "";
                    String v1 = "";
                    if(b0.getName().length()>=10){
                        v0 =  b0.getName().substring(0,10);
                    }else{
                        v0 =  b0.getName()+printerWriter.getEmpOne(10-b0.getName().length());
                    }
                    if(b0.getValue().length()>=8){
                        v1 =  b0.getValue().substring(0,8);
                    }else{
                        v1 =  b0.getValue()+printerWriter.getEmpOne(8-b0.getValue().length());
                    }
                    String ck = b0.isCheck()?"正常":"不正常";
                    printerWriter.print(printerWriter.getEmpOne(2)+"2"+printerWriter.getEmpOne(4)+"表二"+printerWriter.getEmpOne(3)+v0+printerWriter.getEmpOne(2)+v1+printerWriter.getEmpOne(4)+ck+printerWriter.getEmpOne(2));
                    printerWriter.printLineFeed();
                }


            }

            printerWriter.printLineFeed();
            //安检
            printerWriter.print("序号"+printerWriter.getEmp(1)+"燃气具"+printerWriter.getEmp(1)+"安装位置"+printerWriter.getEmp(8)+"运行情况"+printerWriter.getEmp(1));
            printerWriter.printStaffQj(staffQjs);
            printerWriter.printLineFeed();
            printerWriter.setFontSize(1);
            printerWriter.setAlignCenter();
            printerWriter.print("安检结果:合格");
            printerWriter.printLineFeed();
            printerWriter.setAlignLeft();
            printerWriter.setFontSize(0);
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.print("施工申明:上述项目已完工,并检验合格。");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.print("施工员签名:");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();

            printerWriter.printLineFeed();
            printerWriter.print("客户申明:");
            printerWriter.printLineFeed();
            printerWriter.print("本人确认上述工程已完成,承诺遵守政府相关管道燃气的法律、法规和规章,并依照珠海港兴管道天然气有限公司的相关指引,安全使用管道燃气。");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.print("客户签名:");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.printAdd("营业厅:珠海斗门区湖心路诚丰新园2栋一楼1298-1304号商铺",4);
            printerWriter.print("服务热线:7718888"+printerWriter.getEmp(4)+"抢险电话:2128866");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.printLine();
            P.c("打印宽度"+printerWriter.getLineWidth());

            printerWriter.printLineFeed();
            printerWriter.printLineFeed();


            data.add(printerWriter.getDataAndReset());
            printerWriter.feedPaperCutPartial();
            data.add(printerWriter.getDataAndClose());
            return  data;
        } catch (IOException e) {
            return new ArrayList<>();
        }

    }
    private Context context;
    private ReserItemBean bean;
    private ArrayList<StaffImageItem> staffImages;//图片
    private ArrayList<DetailStaff> dss ;//栏目
    private ArrayList<StaffB> staffBs ;//燃气表
    private ArrayList<StaffQj> staffQjs;//器具
    public StaffMark(Context context,ReserItemBean bean,ArrayList<StaffImageItem> staffImages,ArrayList<DetailStaff> dss,ArrayList<StaffB> staffBs,ArrayList<StaffQj> staffQjs  ){
        this.context = context;
        this.bean = bean;
        this.staffImages = staffImages;
        this.dss = dss;
        this.staffBs = staffBs;
        this.staffQjs = staffQjs;
    }
}
