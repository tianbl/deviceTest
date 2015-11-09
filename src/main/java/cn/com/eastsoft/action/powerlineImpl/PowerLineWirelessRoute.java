package cn.com.eastsoft.action.powerlineImpl;

import cn.com.eastsoft.action.PowerLine;
import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.util.Connect;
import cn.com.eastsoft.util.Ping;
import cn.com.eastsoft.util.XmlManager;

import javax.swing.*;

/**
 * Created by tianbaolei on 15-11-9.
 */
public class PowerLineWirelessRoute extends PowerLine {

    @Override
    public boolean info_set() {

        MainJFrame.showMssageln("电力线无线路由器 信息设置");
        return false;
    }

    @Override
    public boolean wan_Lan_test() {
        MainJFrame.showMssageln("电力线无线路由器 wan口和lan口设置");
        return false;
    }

    @Override
    public boolean carrier_test() {
        MainJFrame.showMssageln("电力线无线路由器 载波测试");
        return false;
    }

    @Override
    public boolean wifi_test(){
        MainJFrame.showMssageln("电力线无线路由器 wifi测试");
//        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>3.wifi测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
//
//        String wifiInterface = Ping.getInterface("netsh wlan show interfaces");
//        //String wifiInterface = "无线网络连接";
//        if (false == "WLAN".equals(wifiInterface) && false == wifiInterface.contains("无线网络连接")) {
//            wifiInterface = "WLAN";
//            MainJFrame.showMssageln("请到电脑中\"控制面板->网络和 Internet->网络连接\"中将无线连接名字改为WLAN");
//            int i = JOptionPane.showConfirmDialog(this, "请按信息输出提示修改无线名称，然后点击“是”继续！", "提示",
//                    JOptionPane.YES_NO_CANCEL_OPTION);
//            if (0 != i) {
//                return false;
//            }
//        }
//        MainJFrame.showMssageln("电脑存在无线接口：" + wifiInterface);
//        {
//
//            String disabled = "netsh interface set interface name=\"" + wifiInterface + "\" admin=DISABLED";
//            String enabled = "netsh interface set interface name=\"" + wifiInterface + "\" admin=ENABLED";
//            if (Ping.executeCmd(disabled)) {
//                if (Ping.executeCmd(enabled)) {
//                    MainJFrame.showMssageln("wifi重启成功...");
//                }
//            } else {
//                MainJFrame.showMssageln("无法重启无线...");
//                int i = JOptionPane.showConfirmDialog(this, "无法重启无线网卡，是否继续？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
//                if (0 != i) {
//                    return false;
//                }
//            }
//        }
//
//        String wifi_connectIP = null;
//        if (null == macMap) {
//            macMap = getMac(MainJFrame.getInstance().telnetGateway(gateway_IP, 23));
//        }
//        Connect telnet = MainJFrame.getInstance().telnetGateway(gateway_IP, 23);
//        String getssid = telnet.sendCommand("uci get wireless.@wifi-iface[0].ssid");
//
//        String ssid = null;
//        if (getssid.split("\r\n").length > 2) {
//            ssid = getssid.split("\r\n")[1];
//        } else {
//            MainJFrame.showMssageln("查询无线命令执行结果格式不符合程序要求" + getssid);
//        }
//        telnet.disconnect();
////		String ssid = "Eastsoft_" + wifiMac.substring(wifiMac.length() - 6);
//        //检测wifi，并获取信号强度
//        String wifiStrength = Ping.getWifiStrength(ssid);
//        if (null != wifiStrength) {
//            MainJFrame.showMssageln(ssid + "wifi信号强度：" + wifiStrength);
//        } else {
//            MainJFrame.showMssageln("检测不到有关wifi " + ssid + "的信息...");
//        }
//
//        String connectWifi = "netsh wlan connect name=\"" + ssid + "\" ssid=\"" + ssid + "\" interface=\""
//                + wifiInterface + "\"";
//        String disconnect = "netsh wlan disconnect";
//        // 添加配置文件
//        new XmlManager().modifyNode("", ssid);
//        String addConfig = "netsh wlan add profile filename=" + getNowPath() + "\"\\Eastsoft_wifi.xml\"";
//        String deleteConfig = "netsh wlan delete profile name=" + ssid;
//        if (Ping.executeCmd(addConfig)) {
//            // MainJFrame.showMssageln("");
//        }
//
//
//        // wifi尝试3次连接，
//        MainJFrame.showMssageln("连接wifi将进行3次连接尝试...");
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        for (int i = 0; i < 3; i++) {
//            MainJFrame.showMssageln("正在进行第" + (i + 1) + "次连接");
//            if (false == Ping.executeCmd(connectWifi)) {
//                try {
//                    Thread.sleep(1500);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                if (i >= 2) {
//                    String alarmInfo = "请自行连接无线" + ssid + ",然后继续...";
//                    int select = JOptionPane.showConfirmDialog(this, alarmInfo, "提示", JOptionPane.YES_NO_CANCEL_OPTION);
//                    if (0 != i) {
//                        MainJFrame.showMssageln("无法连接wifi：" + ssid);
//                        Ping.executeCmd(deleteConfig);
//                        return false;
//                    } else {
//                        break;
//                    }
//                } else {
//                    MainJFrame.showMssageln("连接失败还将进行" + (3 - i - 1) + "次尝试...");
//                }
//            } else {
//                break;
//            }
//        }
//
//        // 尝试3次 获取ip，以减少因为获取不到ip导致的虚假测试失败
//        for (int i = 0; i < 3; i++) {
//            try {
//                Thread.sleep(1500);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            wifi_connectIP = Ping.getWifiIP(hostIp);
//            if (null == wifi_connectIP) {
//                MainJFrame.showMssageln("第" + (i + 1) + "次尝试获取wifiIP...");
//            } else {
//                MainJFrame.showMssageln("成功获取到wifi IP:" + wifi_connectIP);
//                break;
//            }
//        }
//
//        // 删除测试wifi配置文件
//
//        if (null == wifi_connectIP) {
//            MainJFrame.showMssageln("无法获取连接到的wifiIP,请先手动连接路由器网关的wifi再继续测试！");
//            Ping.executeCmd(disconnect);
//            Ping.executeCmd(deleteConfig);
//            return false;
//        }
//
//        if (Ping.ping(gateway_IP, wifi_connectIP, generalSet.getNumOfPing(), 3000)) {
//            Ping.executeCmd(disconnect);
//            MainJFrame.showMssageln("wifi测试 成功...");
////            Ping.executeCmd(deleteConfig);
//        } else {
//            Ping.executeCmd(disconnect);
////            Ping.executeCmd(deleteConfig);
//            MainJFrame.showMssageln("wifi测试失败...");
//        }
//        Ping.executeCmd(deleteConfig);
        return false;
    }
}
