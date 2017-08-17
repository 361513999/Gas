package com.hhkj.gas.www.bluetooth.printer;

import android.content.Context;

import com.hhkj.gas.www.R;
import com.hhkj.gas.www.bluetooth.printer.PrintDataMaker;
import com.hhkj.gas.www.common.P;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/17/017.
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
            data.addAll(logo);

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
            printerWriter.print("工作编号:157941316");
            printerWriter.printLineFeed();
            printerWriter.setAlignRight();
            printerWriter.print("第一次打印");
            printerWriter.printLineFeed();
            printerWriter.setAlignLeft();
            //开始写客户
            printerWriter.writeH(70);
            printerWriter.printTP("客户姓名:吴凡");
            printerWriter.print("联系电话:18813955321");
            printerWriter.printLineFeed();
            printerWriter.print("预约时间:2017-08-18");
            printerWriter.printLineFeed();

            printerWriter.printAdd("地址:知道吗？我昨晚又梦到你了，梦中的你一如既往地帅气，你背对着我，坐在那家我们常去的咖啡馆常坐的位置，我进门径直朝着那个位置走去，却看到了你，我就愣在那儿停顿了好久，然后你转过头来看到了我，你朝我笑，我鼓起勇气试着向你走近，却始终走不到那个位置，眼睁睁地看着你近在咫尺，却偏偏难以靠近，最后直到你消失不见",3);
//            printerWriter.printAdd("地址:广东省深圳市宝安区西乡街道宝源路(就是要让地址长长的，看看会怎样)",2);
            printerWriter.printLine();
            printerWriter.printLineFeed();

            printerWriter.print("序号"+printerWriter.getEmp(8)+"项目"+printerWriter.getEmp(7)+"检查情况"+printerWriter.getEmp(1));
            //
            printerWriter.printLineFeed();
            printerWriter.printStaffItems();
           // printerWriter.setLineHeight(10);
            printerWriter.printLineFeed();
            printerWriter.print("序号"+printerWriter.getEmp(1)+"燃气表"+printerWriter.getEmp(1)+"品牌型号"+printerWriter.getEmp(1)+"机表读数"+printerWriter.getEmp(3)+"运行情况"+printerWriter.getEmp(1));
            printerWriter.print(printerWriter.getEmp(1)+"1"+printerWriter.getEmp(2)+"表一"+printerWriter.getEmp(14)+"不正常"+printerWriter.getEmp(1));
            printerWriter.printLineFeed();
            printerWriter.print(printerWriter.getEmp(1)+"2"+printerWriter.getEmp(2)+"表二"+printerWriter.getEmp(14)+printerWriter.getEmp(1)+"正常"+printerWriter.getEmp(1));
//            printerWriter.printInOneLine("测试",checkTxt,0);
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            //安检

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
            printerWriter.print(printerWriter.getEmp(10)+"施工员签名:");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.print("客户申明:");
            printerWriter.printLineFeed();
            printerWriter.print("本人确认上述工程已完成,承诺遵守政府相关管道燃气的法律、法规和规章,并依照珠海港兴管道天然气有限公司的相关指引,安全使用管道燃气。");
            printerWriter.printLineFeed();
            printerWriter.printLineFeed();
            printerWriter.print(printerWriter.getEmp(10)+"客户签名:");
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
    public StaffMark(Context context){
        this.context = context;
    }
}
