package cn.com.eastsoft.action.powerlineImpl;

import cn.com.eastsoft.action.PowerLine;
import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.ui.powerline.GeneralSet;
import cn.com.eastsoft.ui.powerline.ServerSet;
import cn.com.eastsoft.util.Connect;
import cn.com.eastsoft.util.Ping;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tianbaolei on 15-11-9.
 */
public class PowerAdapter extends PowerLine{

    @Override
    public boolean info_set() {
        MainJFrame.showMssageln("电力线适配器信息设置");
        return false;
    }

    @Override
    public boolean wan_Lan_test() {
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>.WAN口和LAN口测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        int numOfping = GeneralSet.getInstance().getNumOfPing();
        String gateway_IP = "129.1.88.1";
        String[] targetIP = {gateway_IP, gateway_IP, gateway_IP};
        String[] title = {"电力线适配器IP", "配测设备IP(lan口连接设备)", "公司网关"};
        for (int i = 0; i < targetIP.length; i++) {
            MainJFrame.showMssage(title[i] + "连通性测试    ");
            if(i==1&&!ServerSet.getInstance().isLocalSelected()){
                continue;
            }else if(i==2&&ServerSet.getInstance().isLocalSelected()){
                break;
            }
            if (true == Ping.ping(targetIP[i], numOfping, 3000)) {
                MainJFrame.showMssageln(title[i] + "ping测试通过！\n");
            } else {
                MainJFrame.showMssageln(title[i] + "ping测试不通过\n");
                return false;
            }
        }

        // 全部通过后测试完成，返回true
        MainJFrame.showMssageln("WAN口和LAN口测试通过！");
        return true;
    }

    @Override
    public boolean carrier_test() {
        MainJFrame.showMssageln("电力线适配器载波测试");
        return false;
    }
}
