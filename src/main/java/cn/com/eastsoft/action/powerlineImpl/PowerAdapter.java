package cn.com.eastsoft.action.powerlineImpl;

import cn.com.eastsoft.action.PowerLine;
import cn.com.eastsoft.action.plMessage.ReqMessage;
import cn.com.eastsoft.action.plMessage.ResMessage;
import cn.com.eastsoft.socket.UDPClient;
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
        Map deviceInfo = serverInfo.getServerInfo("sn",GeneralSet.getInstance().getQrCodeString());

        MainJFrame.showMssageln("开始进行信息设置...");
        //获取udp客户端
        UDPClient udpClient = UDPClient.getInstance();
        generalSet = GeneralSet.getInstance();
        int port = generalSet.getUdpPort();

        String[] infoKey = {"mac_1","sn","devicekey","pwd"};
        String[] messageType = {"0E","0F","1B","1E"};
        ReqMessage reqMessage = new ReqMessage();
        for(int i=0;i<messageType.length;i++){
            String contenet = deviceInfo.get(infoKey[i]).toString();
            reqMessage.setType(messageType[i]);
            reqMessage.setContent(contenet);
            byte[] send = reqMessage.getMessage();
            MainJFrame.showMssageln("设置信息内容："+contenet+"\n" +
                    "发送报文："+ResMessage.parseByte2HexStr(send,0,send.length));
            byte[] receive = sendMessage(udpClient, generalSet.getDevice_IP(), port, send);
            MainJFrame.showMssageln("收到报文："+ResMessage.parseByte2HexStr(receive,0,receive.length));
            if("00010001".equals(ResMessage.parseByte2HexStr(receive, 0, 4))){
                MainJFrame.showMssageln(infoKey[i]+"设置成功");

            }
        }

        MainJFrame.showMssageln("");
        //查询设置的信息是否正确  mac、sn、d-key、dak
        MainJFrame.showMssageln("对设置的信息进行检查...");
        byte[][] queryMessage = {{0x02,0x00,0x00},{0x10,0x00,0x00},
                {0x1C,0x00,0x00},{0x1F,0x00,0x00}};
        for(int i=0;i<queryMessage.length;i++){
            MainJFrame.showMssageln("发送"+infoKey[i]+"查询报文"+
                    ResMessage.parseByte2HexStr(queryMessage[i],0,queryMessage[i].length));
            byte[] receive = sendMessage(udpClient, generalSet.getDevice_IP(), port, queryMessage[i]);
            MainJFrame.showMssageln("收到报文："+ResMessage.parseByte2HexStr(receive,0,receive.length));
            ResMessage resMessage = new ResMessage(receive);
            if(resMessage.queryRes.equals(deviceInfo.get(infoKey[i]))){
                MainJFrame.showMssageln(infoKey[i]+"设置正确");
            }else {
                MainJFrame.showMssageln(resMessage.queryRes+"==="+deviceInfo.get(infoKey[i]));
                return false;
            }
        }
        MainJFrame.showMssageln("信息设置完成");
        return true;
    }

    @Override
    public boolean wan_Lan_test() {
        MainJFrame.showMssageln(">>>>>>>>>>>>>>>>>>>>>.LAN口测试<<<<<<<<<<<<<<<<<<<<<<<<<<<");

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
