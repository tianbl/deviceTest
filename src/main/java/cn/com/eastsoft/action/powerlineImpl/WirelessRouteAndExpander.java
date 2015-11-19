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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tianbaolei on 15-11-9.
 */
public class WirelessRouteAndExpander extends PowerLine {

    @Override
    public boolean info_set(Map<String, String> qrcodeInfo) {
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>1.信息设置<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        String[] formate = {"id", "sn", "MAClabel", "dak", "devicekey", "mac_1", "mac_3", "mac_5", "mac_6"};
        ServerInfo serverInfo = null;
        if (ServerSet.getInstance().isLocalSelected()) {
            serverInfo = new XlsOperation(ServerSet.getInstance().getRealPath());
        } else {
            serverInfo = new MysqlOperation();
        }
        Map deviceInfo = serverInfo.getServerInfo("sn", qrcodeInfo.get("sn"));

        if (null == deviceInfo) {
            MainJFrame.showMssageln("数据失败，无法获数据库中对应本设备的数据！");
            return false;
        } else {
            if("U".equals(deviceInfo.get("MAClabel"))){
                MainJFrame.showMssageln("本条条码对应资源数据已经使用,结束操作！");
                return false;
            }
            boolean snEqual = qrcodeInfo.get("sn").equals(deviceInfo.get("sn"));
            boolean macEqual = qrcodeInfo.get("mac").equals(deviceInfo.get("mac_1"));
            boolean dakEqual = true;
            if (qrcodeInfo.get("dak") != null || !"".equals(qrcodeInfo.get("dak"))) {
                dakEqual = qrcodeInfo.get("dak").equals(deviceInfo.get("dak"));
            }
            if (!(snEqual && macEqual && dakEqual)) {
                MainJFrame.showMssageln("数据库信息与二维码标签信息不一致,结束操作！");
                return false;
            }
        }
        //SN、D-KEY、PLC MAC、DAK
        MainJFrame.showMssageln("---(1)设置SN、D-KEY---");
        SSHClient sshClient = new SSHClient(connectParamSet.getUser(), connectParamSet.getPwd(),
                generalSet.getDevice_IP(), connectParamSet.getSshPort());
        String[] setSnKey = {"snkey set --sn=", "snkey set --dkey="};
        String[] querySnKey = {"snkey show --sn", "snkey show --dkey"};
        String[] infoKey = {"sn", "devicekey","mac_1","dak"};
        MainJFrame.showMssageln("清除production分区...");
        sshClient.executtingCmd("mtd erase production");
        String res = sshClient.executeCmd("snkey set --dkey=" + deviceInfo.get(infoKey[1]) + " --sn=" + deviceInfo.get(infoKey[0]));
        if ("ok".equals(res) || null == res || "".equals(res)) {
            MainJFrame.showMssageln("设置成功,校验设置结果...");
            for (int i = 0; i < setSnKey.length; i++) {
                String tmp = (String) deviceInfo.get(infoKey[i]);
                res = sshClient.executeCmd(querySnKey[i]);
                MainJFrame.showMssage("查询"+infoKey[i]+"设置结果 " + res);
                if (res != null && res.contains(tmp)) {
                    MainJFrame.showMssageln(infoKey[i] + "设置成功!");
                } else {
                    MainJFrame.showMssageln(infoKey[i] + "设置操作成功,但数据设置错误！");
                }
            }
        } else {
            MainJFrame.showMssageln("设置失败！\n执行脚本如下，检查脚本是否正确");
            MainJFrame.showMssageln("snkey set --dkey=" + deviceInfo.get(infoKey[1]) + " --sn=" + deviceInfo.get(infoKey[0]));
            return false;
        }


//        set PLC MAC、DAK
        MainJFrame.showMssageln("---(2)设置PLC MAC、DAK---");
        UDPClient udpClient = UDPClient.getInstance();
        generalSet = GeneralSet.getInstance();
        int port = connectParamSet.getUdpPort();
        String ip = connectParamSet.getIP();
        String[] infoKeyUdp = {"mac_1","dak"};
        String[] byname = {"PLC MAC","DAK"};
        String[] messageType = {"0E","1E"};
        ReqMessage reqMessage = new ReqMessage();
        for(int i=0;i<messageType.length;i++){
            String contenet = deviceInfo.get(infoKeyUdp[i]).toString();
            reqMessage.setType(messageType[i]);
            reqMessage.setContent(contenet);
            byte[] send = reqMessage.getMessage();
            MainJFrame.showMssageln("设置"+byname[i]+"："+contenet+"\n" +
                    "发送报文："+ ResMessage.parseByte2HexStr(send, 0, send.length));
            byte[] receive = sendMessage(udpClient, ip, port, send);
            MainJFrame.showMssageln("收到报文："+ResMessage.parseByte2HexStr(receive,0,receive.length));
            if("00010001".equals(ResMessage.parseByte2HexStr(receive, 0, 4))){
                MainJFrame.showMssageln(byname[i]+"设置成功");
            }
        }

        MainJFrame.showMssageln("校验PLC MAC、DAK的设置结果...");
        byte[][] queryMessage = {{0x02,0x00,0x00},{0x1F,0x00,0x00}};
        for(int i=0;i<queryMessage.length;i++){
            MainJFrame.showMssageln("发送"+byname[i]+"查询报文"+
                    ResMessage.parseByte2HexStr(queryMessage[i],0,queryMessage[i].length));
            byte[] receive = sendMessage(udpClient, ip, port, queryMessage[i]);
            MainJFrame.showMssageln("收到报文："+ResMessage.parseByte2HexStr(receive,0,receive.length));
            ResMessage resMessage = new ResMessage(receive);
            if(resMessage.queryRes.equals(deviceInfo.get(infoKey[i+2]))){
                MainJFrame.showMssageln(byname[i]+"设置正确");
            }else {
                MainJFrame.showMssageln(resMessage.queryRes+"==="+deviceInfo.get(infoKey[i+2]));
                return false;
            }
        }

//        设置数据库
        MainJFrame.showMssageln("设置数据库,将wan、lan、wifi MAC地址写入数据库，并标注本条数据为已使用");
        Map<String, String> mac = new HashMap<>();
        String deviceMac = sshClient.executeCmd("link show");
        deviceMac = deviceMac.replace("\n","");
        String[] macArray = deviceMac.split(" ");
        MainJFrame.showMssageln("#link show\n"+deviceMac);
        if(null==deviceMac||"".equals(deviceMac)){
            MainJFrame.showMssageln("无法获取设备MAC（wan、lan、wifi）地址！");
            return false;
        }
        for(String str:macArray){
            String tmp = str.substring(str.indexOf(":")+1);
            String key = str.substring(0,str.indexOf(":"));
            mac.put(key,tmp.replace(":", ""));
        }
        if(serverInfo.setUsed("sn", (String) deviceInfo.get("sn"), mac)>0){
            MainJFrame.showMssageln("数据库字段设置成功！");
        }else {
            MainJFrame.showMssageln("数据库字段设置失败！");
            return false;
        }
        sshClient.closeConnect();
        MainJFrame.showMssageln("信息设置成功！");
        return true;
    }

    @Override
    public boolean wan_Lan_test() {
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>2.WAN口和LAN口测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        generalSet = GeneralSet.getInstance();
        int numOfping = generalSet.getNumOfPing();
        String[] targetIP = {generalSet.getDevice_IP(), generalSet.getDefaultgw_IP()};
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
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>3.载波测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
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
    public boolean wifi_test() {
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>4.wifi测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");
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

//        UDPClient udpClient = UDPClient.getInstance();
//        byte[] bytes = udpClient.sendPacket("255.255.255.255", generalSet.getUdpPort(), ReqMessage.hexStringToBytes("9f0102000001a31f"));
//        ExtenderRes extenderRes = new ExtenderRes(bytes);
        String ssid = null;
//        MainJFrame.showMssageln("广播报文9f 01 02 00 00 01 a3 1f，获取ssid：" + ssid);
        if(moduleSelected==1){  ///电力线无线路由器
            SSHClient sshClient = new SSHClient(connectParamSet.getUser(), connectParamSet.getPwd(),
                    generalSet.getDevice_IP(), connectParamSet.getSshPort());
            ssid = sshClient.executeCmd("uci get wireless.@wifi-iface[0].ssid").replace("\n","");
            MainJFrame.showMssageln("执行uci get wireless.@wifi-iface[0].ssid命令，获取ssid：" + ssid);
        }else {                 ///电力线无线扩展器
            UDPClient udpClient = UDPClient.getInstance();
            byte[] bytes = udpClient.sendPacket("255.255.255.255", generalSet.getUdpPort(), ReqMessage.hexStringToBytes("9f0102000001a31f"));
            if(null==bytes){
                return false;
            }
            ExtenderRes extenderRes = new ExtenderRes(bytes);
            ssid = extenderRes.getSsid();
            MainJFrame.showMssageln("广播报文9f 01 02 00 00 01 a3 1f，获取ssid：" + ssid);
        }
        //检测wifi，并获取信号强度
        String wifi_connectIP = null;
        String wifiStrength = Ping.getWifiStrength(ssid);
        if (null != wifiStrength) {
            MainJFrame.showMssageln(ssid + "wifi信号强度：" + wifiStrength);
        } else {
            MainJFrame.showMssageln("检测不到有关wifi " + ssid + "的信息...");
            int i = JOptionPane.showConfirmDialog(deviceTest,
                    "检测不到有关wifi " + ssid+",是否继续", "提示",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (0 != i) {
                return false;
            }
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
                    if (0 != select) {
                        MainJFrame.showMssageln("无法连接wifi：" + ssid+",终止wifi测试");
                        Ping.executeCmd(deleteConfig);
                        return false;
                    } else {
                        MainJFrame.showMssageln("wifi测试继续进行...");
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
            MainJFrame.showMssageln("无法获取连接到的wifiIP，请手工测试或者重新操作本步骤！");
            Ping.executeCmd(disconnect);
            Ping.executeCmd(deleteConfig);
            return false;
        }

        if (Ping.ping(generalSet.getDevice_IP(), wifi_connectIP, generalSet.getNumOfPing(), 3000)) {
            Ping.executeCmd(disconnect);
            Ping.executeCmd(deleteConfig);
            MainJFrame.showMssageln("wifi测试 成功...");
        } else {
            Ping.executeCmd(disconnect);
            Ping.executeCmd(deleteConfig);
            MainJFrame.showMssageln("wifi测试失败...");
            return false;
        }
//        Ping.executeCmd(deleteConfig);
        return true;
    }

    public byte[] sendMessage(UDPClient udpClient, String ip, int port, byte[] sendBuf) {
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
