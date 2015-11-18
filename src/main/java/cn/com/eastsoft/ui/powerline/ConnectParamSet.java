package cn.com.eastsoft.ui.powerline;

import cn.com.eastsoft.action.plMessage.ReqMessage;
import cn.com.eastsoft.action.plMessage.ResMessage;
import cn.com.eastsoft.socket.UDPClient;
import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.util.ProgramDataManag;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by baolei on 2015/11/13.
 */
public class ConnectParamSet extends JPanel implements ActionListener, ItemListener {
    private Map<String, String> paramCache;
    private String[] settitle = {"IP", "udpPort", "user", "pwd","sshPort"};
    private String[] labelTile = {"UDP通信IP","UDP通信端口","SSH连接用户名","SSH连接密钥","SSH服务端口"};
    private JLabel[] titleLabel;
    private JTextField[] paramsField;
    private JButton[] jButtons;
    //    private String[]
    private JCheckBox UDPSet;
    private JCheckBox SSHSet;

    private static ConnectParamSet instance;

    public static ConnectParamSet getInstance() {
        if (null == instance) {
            instance = new ConnectParamSet();
        }
        return instance;
    }

    private ConnectParamSet() {
        super();
        this.setLayout(null);
        UDPSet = new JCheckBox("SSC1661 UDP通讯参数设置");
        SSHSet = new JCheckBox("SSH连接参数设置");
        UDPSet.setBounds(60, 10, 200, 30);
        SSHSet.setBounds(340, 10, 200, 30);
        UDPSet.addItemListener(this);
        SSHSet.addItemListener(this);
        this.add(UDPSet);
        this.add(SSHSet);

        int buttonLength = 100;
        int x = 60;
        int y = 50;
//        jButtons = new JButton[labelTile.length];
        titleLabel = new JLabel[labelTile.length];
        paramsField = new JTextField[labelTile.length];
        for (int i = 0; i < labelTile.length; i++) {
            titleLabel[i] = new JLabel(labelTile[i]);
            paramsField[i] = new JTextField();
            if(i<2){
                titleLabel[i].setBounds(x, y + (45 * (i%2)), 100, 30);
                paramsField[i].setBounds(x+100, y + (45 * (i%2)), 100, 30);
            }else {
                titleLabel[i].setBounds(x+280, y + (45 * (i-2)), 100, 30);
                paramsField[i].setBounds(x+100+280, y + (45 * (i-2)), 100, 30);
            }
            this.add(titleLabel[i]);
            this.add(paramsField[i]);
        }
        JLabel jLabel = new JLabel("SSH连接的目标IP为设备测试中设置的被测设备IP");
        jLabel.setBounds(x+280,y+45*3,400,30);
        this.add(jLabel);
        setVersion();
    }

    private boolean setVersion() {
        paramCache = ProgramDataManag.getConfigData("deviceTest.conf");
        if (null == paramCache) {
            return false;
        }
        for (int i = 0; i < settitle.length; i++) {
//            paramsField[i].setEditable(true);
            paramsField[i].setText(paramCache.get(settitle[i]));
        }
        return true;
    }

    public boolean saveVersion() {  //关闭程序时保存程序部分设置
        paramCache = new HashMap();
        for (int i = 0; i < settitle.length; i++) {
            paramCache.put(settitle[i], paramsField[i].getText());
        }
        ProgramDataManag.updateConf("deviceTest.conf", paramCache);
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonname = e.getActionCommand();
        ReqMessage reqMessage = new ReqMessage();

        GeneralSet generalSet = GeneralSet.getInstance();
        if (buttonname.equals(settitle[0])) {

        } else if (buttonname.equals(settitle[1])) {
            MainJFrame.showMssageln("查询sn ...");
            byte[] receive = sendMessage("100000");
            ResMessage resMessage = new ResMessage(receive);
            MainJFrame.showMssageln("sn：" + resMessage.getSN());
        } else if (buttonname.equals(settitle[2])) {
            MainJFrame.showMssageln("查询d-key ...");
            byte[] receive = sendMessage("1C0000");
            ResMessage resMessage = new ResMessage(receive);
            MainJFrame.showMssageln("d-key：" + resMessage.getD_KEY());
        } else if (buttonname.equals(settitle[3])) {
            MainJFrame.showMssageln("查询dak ...");
            byte[] receive = sendMessage("1F0000");
            ResMessage resMessage = new ResMessage(receive);
            MainJFrame.showMssageln("DAK：" + resMessage.getDAK());
        }
    }

    public byte[] sendMessage(String com) {
        GeneralSet generalSet = GeneralSet.getInstance();
        UDPClient udpClient = UDPClient.getInstance();
        byte[] bytes = udpClient.sendPacket(generalSet.getDevice_IP(), 10000, ReqMessage.hexStringToBytes(com));
        byte[] lenbyte = new byte[2];
        lenbyte[0] = bytes[2];
        lenbyte[1] = bytes[1];
        int length = Integer.parseInt(ResMessage.parseByte2HexStr(lenbyte, 0, 2), 16);
        byte[] bt = new byte[3 + length];
        for (int i = 0; i < 3 + length; i++) {
            bt[i] = bytes[i];
        }
        return bytes;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        paramCache = ProgramDataManag.getConfigData("deviceTest.conf");
//        if (UDPSet.isSelected() && !SSHSet.isSelected()) {
//            MainJFrame.showMssageln("1");
//            for (int i = 0; i < settitle.length; i++) {
//                if (i < 2) {
//                    paramsField[i].setEditable(true);
//                    paramsField[i].setText(paramCache.get(settitle[i]));
//                } else {
//                    paramsField[i].setEditable(false);
//                    paramsField[i].setText("not avalible");
//                }
//            }
//        } else if (!UDPSet.isSelected() && SSHSet.isSelected()) {
//            MainJFrame.showMssageln("2");
//            for (int i = 0; i < settitle.length; i++) {
//                if (i >= 2) {
//                    paramsField[i].setEditable(true);
//                    paramsField[i].setText(paramCache.get(settitle[i]));
//                } else {
//                    paramsField[i].setEditable(false);
//                    paramsField[i].setText("not avalible");
//                }
//            }
//        } else if (UDPSet.isSelected() && SSHSet.isSelected()) {
//            MainJFrame.showMssageln("3");
//            for (int i = 0; i < settitle.length; i++) {
//                paramsField[i].setEditable(true);
//                paramsField[i].setText(paramCache.get(settitle[i]));
//            }
//        }else {
//            for (int i = 0; i < settitle.length; i++) {
//                paramsField[i].setEditable(false);
//                paramsField[i].setText("not avalible");
//            }
//        }
    }

    public String getIP(){
        return paramsField[0].getText();
    }

    public int getUdpPort(){
        String port = paramsField[1].getText();
        return port==null?0:Integer.parseInt(port);
    }

    public String getUser(){
        return paramsField[2].getText();
    }

    public String getPwd(){
        return paramsField[3].getText();
    }

    public int getSshPort(){
        String port = paramsField[4].getText();
        return port==null?0:Integer.parseInt(port);
    }
}
