package test;

import cn.com.eastsoft.action.plMessage.ReqMessage;
import cn.com.eastsoft.action.plMessage.ResMessage;
import cn.com.eastsoft.socket.UDPClient;
import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.ui.powerline.GeneralSet;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by baolei on 2015/11/10.
 */
public class SocketTest extends JPanel implements ActionListener {

    private String[] settitle = {"mac", "sn", "d-key", "dak"};
    private JButton[] jButtons;
    private JCheckBox set;
    private JCheckBox query;


    public SocketTest() {
        super();
        this.setLayout(null);

        set = new JCheckBox("set");
        query = new JCheckBox("query");
        set.setBounds(10, 50, 150, 30);
        query.setBounds(180, 50, 150, 30);
        this.add(set);
        this.add(query);

        int buttonLength = 100;
        int y = 100;
        jButtons = new JButton[settitle.length];
        for (int i = 0; i < settitle.length; i++) {
            jButtons[i] = new JButton(settitle[i]);
            jButtons[i].setBounds(10 + (10 + buttonLength) * i, y, buttonLength, 30);
            jButtons[i].addActionListener(this);
            this.add(jButtons[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonname = e.getActionCommand();
        ReqMessage reqMessage = new ReqMessage();

        GeneralSet generalSet = GeneralSet.getInstance();
        if (buttonname.equals(settitle[0])) {
            if (set.isSelected()) {
                MainJFrame.showMssageln("尚未实现的操作");

            } else if (query.isSelected()) {
                MainJFrame.showMssageln("查询mac地址...");
                byte[] receive = sendMessage("020000");
                ResMessage resMessage = new ResMessage(receive);
                MainJFrame.showMssageln("mac地址：" + resMessage.getMAC());
            }
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
        try {
            UDPClient udpClient = UDPClient.getInstance();
            byte[] bytes = udpClient.sendPacket(generalSet.getDevice_IP(), 10000, ReqMessage.hexStringToBytes(com));
            byte[] lenbyte = new byte[2];
            lenbyte[0] = bytes[2];
            lenbyte[1] = bytes[1];
            int length = Integer.parseInt(ResMessage.parseByte2HexStr(lenbyte, 0, 2), 16);
            byte[] bt = new byte[3+length];
            for(int i=0;i<3+length;i++){
                bt[i]=bytes[i];
            }
            MainJFrame.showMssageln("收到报文："+ResMessage.parseByte2HexStr(bt,0,bt.length));
            return bytes;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
