package cn.com.eastsoft.action.powerlineImpl;

import cn.com.eastsoft.action.PowerLine;
import cn.com.eastsoft.sql.ServerInfo;
import cn.com.eastsoft.sql.serverInfoImpl.MysqlOperation;
import cn.com.eastsoft.sql.serverInfoImpl.XlsOperation;
import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.ui.powerline.GeneralSet;
import cn.com.eastsoft.ui.powerline.ServerSet;
import cn.com.eastsoft.util.Connect;
import cn.com.eastsoft.util.Ping;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by tianbaolei on 15-11-9.
 */
public class PowerAdapter extends PowerLine{

    @Override
    public boolean info_set(Map<String,String> qrcodeInfo) {
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>.信息设置<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        String[] formate={"id","sn","MAClabel","gid","pwd","devicekey","mac_1","mac_3","mac_5","mac_6"};
        ServerInfo serverInfo = null;
        if(ServerSet.getInstance().isLocalSelected()){
            serverInfo = new XlsOperation(ServerSet.getInstance().getRealPath());
        }else {
            serverInfo = new MysqlOperation();
        }
        Map map = serverInfo.getServerInfo("sn",GeneralSet.getInstance().getQrCodeString());
        int count = 0;
        for(String str:formate){
            MainJFrame.showMssage(str+"=="+map.get(str)+",");
            if((++count)%4==0){
                MainJFrame.showMssage("\n");
            }
        }
        MainJFrame.showMssage("\n");
        return false;
    }

    @Override
    public boolean wan_Lan_test() {
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>.WAN口和LAN口测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        generalSet = GeneralSet.getInstance();
        int numOfping = GeneralSet.getInstance().getNumOfPing();
        String[] targetIP = {generalSet.getDevice_IP(), generalSet.getAccompany_IP(),
                generalSet.getDefaultgw_IP()};
        String[] title = {"电力线适配器IP", "配测设备IP(lan口连接设备)", "公司网关"};
        for (int i = 0; i < targetIP.length; i++) {
            if(i==1&&!ServerSet.getInstance().isLocalSelected()){
                continue;
            }else if(i==2&&ServerSet.getInstance().isLocalSelected()){
                break;
            }
            MainJFrame.showMssage(title[i] + "连通性测试    ");
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
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>.载波测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        generalSet = GeneralSet.getInstance();
        int numOfping = generalSet.getNumOfPing();
        String pingIP = generalSet.getAccompany_IP();
        MainJFrame.showMssage("载波测试");
        if (true == Ping.ping(pingIP, numOfping, 3000)) {
            MainJFrame.showMssageln("载波测试通过！\n");
        } else {
            MainJFrame.showMssageln("载波测试不通过\n");
            return false;
        }
        return true;
    }
}
