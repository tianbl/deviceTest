package cn.com.eastsoft.action.powerlineImpl;

import cn.com.eastsoft.action.PowerLine;
import cn.com.eastsoft.action.plMessage.ExtenderRes;
import cn.com.eastsoft.action.plMessage.ReqMessage;
import cn.com.eastsoft.action.plMessage.ResMessage;
import cn.com.eastsoft.socket.UDPClient;
import cn.com.eastsoft.sql.ServerInfo;
import cn.com.eastsoft.sql.serverInfoImpl.MysqlOperation;
import cn.com.eastsoft.sql.serverInfoImpl.XlsOperation;
import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.ui.powerline.DeviceTest;
import cn.com.eastsoft.ui.powerline.GeneralSet;
import cn.com.eastsoft.ui.powerline.ServerSet;
import cn.com.eastsoft.util.*;

import javax.swing.*;
import java.util.Map;

/**
 * Created by tianbaolei on 15-11-9.
 */
public class WirelessRouteAndExpander extends PowerLine {

    @Override
    public boolean info_set(Map<String,String> qrcodeInfo) {
        MainJFrame.showMssageln("电力线无线路由器 信息设置");

        String[] formate={"id","sn","MAClabel","gid","pwd","devicekey","mac_1","mac_3","mac_5","mac_6"};
        ServerInfo serverInfo = null;
        if(ServerSet.getInstance().isLocalSelected()){
            serverInfo = new XlsOperation(ServerSet.getInstance().getRealPath());
        }else {
            serverInfo = new MysqlOperation();
        }
        Map deviceInfo = serverInfo.getServerInfo("sn",GeneralSet.getInstance().getQrCodeString());

        //SN、D-KEY、PLC MAC、DAK
        MainJFrame.showMssageln("设置SN、D-KEY...");
//        SSHClient sshClient = new SSHClient();
        String[] setSnKey = {"snkey set --sn=","snkey set --dkey="};
        String[] infoKey = {"sn","devicekey"};
        for(int i=0;i<setSnKey.length;i++){
            if(null==deviceInfo){
                MainJFrame.showMssageln("未查询到数据");
            }else {
                MainJFrame.showMssageln(setSnKey[i]+deviceInfo.get(infoKey[i]));
            }
        }


        //set PLC MAC、DAK
        UDPClient udpClient = UDPClient.getInstance();
        generalSet = GeneralSet.getInstance();
        int port = generalSet.getUdpPort();

        String[] infoKeyUdp = {"mac_1","pwd"};
        String[] messageType = {"0E","1E"};
        ReqMessage reqMessage = new ReqMessage();
        for(int i=0;i<messageType.length;i++){
            String contenet = deviceInfo.get(infoKeyUdp[i]).toString();
            reqMessage.setType(messageType[i]);
            reqMessage.setContent(contenet);
            byte[] send = reqMessage.getMessage();
            MainJFrame.showMssageln("设置信息内容："+contenet+"\n" +
                    "发送报文："+ ResMessage.parseByte2HexStr(send, 0, send.length));
            byte[] receive = sendMessage(udpClient, generalSet.getDevice_IP(), port, send);
            MainJFrame.showMssageln("收到报文："+ResMessage.parseByte2HexStr(receive,0,receive.length));
            if("00010001".equals(ResMessage.parseByte2HexStr(receive, 0, 4))){
                MainJFrame.showMssageln(infoKeyUdp[i]+"设置成功");
            }
        }
        return false;
    }

    @Override
    public boolean wan_Lan_test() {
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>.WAN口和LAN口测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        generalSet = GeneralSet.getInstance();
        int numOfping = generalSet.getNumOfPing();
        String[] targetIP = {generalSet.getDevice_IP(),generalSet.getDefaultgw_IP()};
        String[] title = {"电力线适配器IP", "公司网关"};
        for (int i = 0; i < targetIP.length; i++) {
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
        MainJFrame.showMssageln("电力线无线路由器 载波测试");
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

    @Override
    public boolean wifi_test(){
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>电力线无线路由器 wifi测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        DeviceTest deviceTest = MainJFrame.getInstance().getDeviceTest();
        String wifiInterface = Ping.getInterface("netsh wlan show interfaces");
        //String wifiInterface = "无线网络连接";
        if (false == "WLAN".equals(wifiInterface) && false == wifiInterface.contains("无线网络连接")) {
            wifiInterface = "WLAN";
            MainJFrame.showMssageln("请到电脑中\"控制面板->网络和 Internet->网络连接\"中将无线连接名字改为WLAN");
            int i = JOptionPane.showConfirmDialog(deviceTest,
                    "请按信息输出提示修改无线名称，然后点击“是”继续！", "提示",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (0 != i) {
                return false;
            }
        }
        MainJFrame.showMssageln("电脑存在无线接口：" + wifiInterface);
//        {
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

        UDPClient udpClient = UDPClient.getInstance();
        byte[] bytes = udpClient.sendPacket("192.168.1.253", 9998, ReqMessage.hexStringToBytes("9f0102000001a31f"));
        ExtenderRes extenderRes = new ExtenderRes(bytes);
        String ssid = extenderRes.getSsid();
        MainJFrame.showMssageln("（ip写死在程序里，完成前需改动）广播报文9f 01 02 00 00 01 a3 1f，获取ssid："+ssid);
        //检测wifi，并获取信号强度
        String wifi_connectIP = null;
        String wifiStrength = Ping.getWifiStrength(ssid);
        if (null != wifiStrength) {
            MainJFrame.showMssageln(ssid + "wifi信号强度：" + wifiStrength);
        } else {
            MainJFrame.showMssageln("检测不到有关wifi " + ssid + "的信息...");
        }

        String connectWifi = "netsh wlan connect name=\"" + ssid + "\" ssid=\"" + ssid + "\" interface=\""
                + wifiInterface + "\"";
        String disconnect = "netsh wlan disconnect";
//         添加配置文件
        new XmlManager().modifyNode("", ssid);
        String addConfig = "netsh wlan add profile filename=" + ToolUtil.getNowPath() + "\"\\Eastsoft_wifi.xml\"";
        String deleteConfig = "netsh wlan delete profile name=" + ssid;
        if (Ping.executeCmd(addConfig)) {
            // MainJFrame.showMssageln("");
        }
//
//
//        // wifi尝试3次连接，
        MainJFrame.showMssageln("连接wifi将进行3次连接尝试...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < 3; i++) {
            MainJFrame.showMssageln("正在进行第" + (i + 1) + "次连接");
            if (false == Ping.executeCmd(connectWifi)) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (i >= 2) {
                    String alarmInfo = "请自行连接无线" + ssid + ",然后继续...";
                    int select = JOptionPane.showConfirmDialog(deviceTest, alarmInfo, "提示", JOptionPane.YES_NO_CANCEL_OPTION);
                    if (0 != i) {
                        MainJFrame.showMssageln("无法连接wifi：" + ssid);
                        Ping.executeCmd(deleteConfig);
                        return false;
                    } else {
                        break;
                    }
                } else {
                    MainJFrame.showMssageln("连接失败还将进行" + (3 - i - 1) + "次尝试...");
                }
            } else {
                break;
            }
        }

        // 尝试3次 获取ip，以减少因为获取不到ip导致的虚假测试失败
        for (int i = 0; i < 3; i++) {
            wifi_connectIP = Ping.getWifiIP(generalSet.getLocal_IP());
            if (null == wifi_connectIP) {
                MainJFrame.showMssageln("第" + (i + 1) + "次尝试获取wifiIP失败，等待5秒再次尝试...");
            } else {
                MainJFrame.showMssageln("成功获取到wifi IP:" + wifi_connectIP);
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // 删除测试wifi配置文件

        if (null == wifi_connectIP) {
            MainJFrame.showMssageln("无法获取连接到的wifiIP,请先手动连接路由器网关的wifi再继续测试！");
            Ping.executeCmd(disconnect);
            Ping.executeCmd(deleteConfig);
            return false;
        }

        if (Ping.ping(generalSet.getDevice_IP(), wifi_connectIP, generalSet.getNumOfPing(), 3000)) {
            Ping.executeCmd(disconnect);
            MainJFrame.showMssageln("wifi测试 成功...");
//            Ping.executeCmd(deleteConfig);
        } else {
            Ping.executeCmd(disconnect);
//            Ping.executeCmd(deleteConfig);
            MainJFrame.showMssageln("wifi测试失败...");
        }
        Ping.executeCmd(deleteConfig);
        return false;
    }

    public byte[] sendMessage(UDPClient udpClient,String ip,int port,byte[] sendBuf) {
        byte[] bytes = udpClient.sendPacket(ip, port, sendBuf);
        byte[] lenbyte = new byte[2];
        lenbyte[0] = bytes[2];
        lenbyte[1] = bytes[1];
        int length = Integer.parseInt(ResMessage.parseByte2HexStr(lenbyte, 0, 2), 16);
//        MainJFrame.showMssageln("接收到数据长度："+length);
        byte[] bt = new byte[3 + length];
        for (int i = 0; i < 3 + length; i++) {
            bt[i] = bytes[i];
        }
        return bt;
    }
}
