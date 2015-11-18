package cn.com.eastsoft.action;

import cn.com.eastsoft.sql.ServerInfo;
import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.ui.powerline.ConnectParamSet;
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
public abstract class PowerLine{

    protected int moduleSelected;
    protected ServerSet serverSet;
    protected GeneralSet generalSet;
    protected ConnectParamSet connectParamSet;

    public PowerLine(){
        generalSet = GeneralSet.getInstance();
        connectParamSet = ConnectParamSet.getInstance();
    }

    public boolean wan_Lan_test(){
        MainJFrame.showMssage("电力线动作基类实现");
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>1.WAN口和LAN口测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");

//        int numOfping = generalSet.getNumOfPing();
        int numOfping = 0;
        String gateway_IP = "129.1.88.1";
        String[] targetIP = {gateway_IP, gateway_IP, gateway_IP};
        String[] title = {"路由器网关", "配测IP(lan口连接设备)", "公司网关"};
        for (int i = 0; i < targetIP.length; i++) {
            MainJFrame.showMssage(title[i] + "连通性测试    ");
            if (true == Ping.ping(targetIP[i], numOfping, 3000)) {
                MainJFrame.showMssageln(title[i] + "ping测试通过！\n");
            } else {
                MainJFrame.showMssageln(title[i] + "ping测试不通过\n");
                return false;
            }
        }

        { // 获取路由器系统时钟，并计算系统时间误差
            MainJFrame.showMssage("准备获取路由器系统时钟...\n");
            Connect connect = MainJFrame.getInstance().telnetGateway(gateway_IP, 23);
            String routeTime = connect.sendCommand("date +%s").split("\r\n")[1];
            long routeTimeLong = Long.parseLong(routeTime, 10);
            long nowTime = Calendar.getInstance().getTimeInMillis();
            long diff = Math.abs(((nowTime / 1000) - routeTimeLong));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
            MainJFrame.showMssage("获取到路由器时间为：" + format.format(new Date(routeTimeLong * 1000)) + "\n");
            if (diff < 300) {
                MainJFrame.showMssageln("智能路由器系统时间误差为(精确到秒)：" + diff + ",小于误差要求5分钟（300秒)");
            } else {
                MainJFrame.showMssageln("智能路由器系统时间误差为(精确到秒)：" + diff + ",超出误差要求5分钟（300秒)");
                return false;
            }
            connect.disconnect();
        }

        // 全部通过后测试完成，返回true
        MainJFrame.showMssageln("WAN口和LAN口测试通过！");
        return true;
    }

    /**
     * 信息设置
     * @return
     */
    public boolean info_set(Map<String,String> qrcodeInfo){
        MainJFrame.showMssage("当前设备无需测试该项目");
        return true;
    }

    public boolean carrier_test(){
        MainJFrame.showMssage("电力线动作基类实现");
        return false;
    }
    public boolean wifi_test(){
//        MainJFrame.showMssage("当前设备无需实现wifi测试");
        return true;
    }

    public int getModuleSelected() {
        return moduleSelected;
    }

    public void setModuleSelected(int moduleSelected) {
        this.moduleSelected = moduleSelected;
    }
}
