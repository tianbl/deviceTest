package cn.com.eastsoft.ui.powerline;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import cn.com.eastsoft.action.PowerLine;
import cn.com.eastsoft.action.powerlineImpl.PowerAdapter;
import cn.com.eastsoft.action.powerlineImpl.WirelessRouteAndExpander;
import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.sql.ServerInfo;
import cn.com.eastsoft.util.Connect;
import cn.com.eastsoft.util.Ping;
import cn.com.eastsoft.util.ProgramDataManag;

//网关测试部分
public class DeviceTest extends JPanel {

    private int index_Of_Clicked_Button;
    private Map<String, String> macMap = null;
    private MainJFrame mainJFrame;
    private GeneralSet generalSet;
    private ServerSet serverSet;

    private JButton allTest_JButton;
    private JButton switchButton;
    // 分项测试按钮组
    private JButton[] signalTest_JButton;
    private String[] buttonTitle = {"1.信息设置","2.测试WAN和LAN口", "3.测试载波", "4.测试wifi"};

    private ButtonActionListener buttonActionListener;

    private ServerInfo mysqlOperation = null; // 获取资源信息
    private PowerLine powerLine;

    public DeviceTest() {
        super();

        {   //做一些必要的初始化
            buttonActionListener = new ButtonActionListener();
            this.setLayout(null);
        }

        {
            System.out.println("**********");
            generalSet = GeneralSet.getInstance();
            generalSet.setBounds(0, 0, 800, 180);
            generalSet.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "测试信息设置",
                    TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
            this.add(generalSet);
            serverSet = ServerSet.getInstance();
        }
        { // 单项测试按钮组
            allTest_JButton = new JButton("一键测试");
            allTest_JButton.setBounds(10, 200, 150, 30);
            allTest_JButton.setBackground(Color.GREEN);
            allTest_JButton.addActionListener(buttonActionListener);
            this.add(allTest_JButton);

            switchButton = new JButton("重新选择测试设备");
            switchButton.setBounds(200, 200, 150, 30);
            switchButton.setBackground(Color.GREEN);
            switchButton.addActionListener(buttonActionListener);
            this.add(switchButton);

            signalTest_JButton = new JButton[buttonTitle.length];

            int y = 240;
            for (int i = 0; i < buttonTitle.length; i++) {
                signalTest_JButton[i] = new JButton(buttonTitle[i]);
                int buttonLength = 130;
                signalTest_JButton[i].setBounds(10 + (10 + buttonLength) * i, y, buttonLength, 30);
                signalTest_JButton[i].addActionListener(buttonActionListener);
                this.add(signalTest_JButton[i]);
                /*
                 * if((i+1)%4==0){ y+=40; }
				 */
            }
        }

        MainJFrame.showMssageln("程序当运行路径：" + getNowPath());
        if (!Ping.isSameSegment(generalSet.getDevice_IP(),generalSet.getLocal_IP())) {
            MainJFrame.showMssageln("本机IP和设备不再同一网段！");
        }
    }

    public void set_module(int selectModule){

        Map<String, String> map = ProgramDataManag.getConfigData("deviceTest.conf");
        //设置选择的设备
        if(0==selectModule){
            powerLine = new PowerAdapter();
            signalTest_JButton[3].setVisible(false);
            generalSet.udpPort_JText.setText(map.get("udpPort"));
            MainJFrame.getInstance().setTitle("电力线适配器测试");
            MainJFrame.showMssageln(selectModule + "选择电力线适配器"+map.get("udpPort"));
        }else if(1==selectModule){

            powerLine = new WirelessRouteAndExpander();
            signalTest_JButton[3].setVisible(true);
            generalSet.udpPort_JText.setText(map.get("deviceUdpPort"));
            MainJFrame.showMssageln(selectModule + "选择电力线无线路由器" + map.get("deviceUdpPort"));
            MainJFrame.getInstance().setTitle("电力线无线路由器测试");
        }else if(2==selectModule){
            powerLine = new WirelessRouteAndExpander();
            signalTest_JButton[3].setVisible(true);
            generalSet.udpPort_JText.setText(map.get("deviceUdpPort"));
            MainJFrame.showMssageln(selectModule + "选择电力线无线扩展器" + map.get("deviceUdpPort"));
            MainJFrame.getInstance().setTitle("电力线无线扩展器测试");
        }
        powerLine.setModuleSelected(selectModule);
    }

    class ButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg) {
            // TODO Auto-generated method stub

            String buttonName = arg.getActionCommand();
            final String device_IP = generalSet.getDevice_IP();
            final String host_IP = generalSet.getLocal_IP();

            if("重新选择测试设备".equals(buttonName)){
                System.out.println("配侧设备IP："+generalSet.getAccompany_IP());
                MainJFrame.getInstance().switchWindow();
            }else if ("一键测试".equals(buttonName)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        //generalSet 在构造方法中初始化
                        allTest(generalSet.getQrCode_Info());
                    }
                }).start();
            } else {
                for (int i = 0; i < buttonTitle.length; i++) {
                    if (buttonTitle[i].equals(buttonName)) {
                        final int lastIndex = buttonTitle.length - 1;
                        switch (i) {
                            case 0: {// 信息设置
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() { // 启动线程执行后续操作
                                        powerLine.info_set(generalSet.getQrCode_Info());
                                    }
                                }).start();
                                break;
                            }
                            case 1: {   //WAN和LAN口测试
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() { // 启动线程执行后续操作
//                                        setGatewayInfo(gateway_IP, host_IP);
                                        powerLine.wan_Lan_test();
                                    }
                                }).start();
                                break;
                            }
                            case 2: {   //载波测试
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        carrier_Test(gateway_IP, buttonTitle[3]);
                                        powerLine.carrier_test();
                                    }
                                }).start();
                                break;
                            }
                            case 3: {   //wifi测试
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //MainJFrame.showMssageln(buttonTitle[2]);
//                                        wifi_Test(gateway_IP, host_IP);
                                        powerLine.wifi_test();
                                    }
                                }).start();
                                break;
                            }

                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 测试所有
     * @param qrcodeInfo 二维码信息，存放在map中
     * @return
     */
    public boolean allTest(Map<String,String> qrcodeInfo) {
        String gateway_ip = generalSet.getDevice_IP();
        String localhost_ip = generalSet.getLocal_IP();
        String accompany_ip = generalSet.getAccompany_IP();
        if(null==qrcodeInfo){
            MainJFrame.showMssageln("检测不到二维码信息，可能由二维码格式错误导致！6954176809599");
        }
        if (false == powerLine.info_set(qrcodeInfo)) {
            int i = JOptionPane.showConfirmDialog(this, "信息设置发生错误，是否继续其他测试？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
            if (0 != i) {
                return false;
            }
        }
        if (false == powerLine.wan_Lan_test()) {
            int i = JOptionPane.showConfirmDialog(this, "WAN口和LAN口测试发生错误，是否继续其他测试？", "提示",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (0 != i) {
                return false;
            }
        }
        if (false == powerLine.carrier_test()) {
            int i = JOptionPane.showConfirmDialog(this, "载波测试发生错误，是否继续其他测试？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
            if (0 != i) {
                return false;
            }
        }
        if (false == powerLine.wifi_test()) {
            int i = JOptionPane.showConfirmDialog(this, "wifi测试发生错误？", "提示", JOptionPane.YES_NO_CANCEL_OPTION);
            if (0 != i) {
                return false;
            }
        }
        return true;
    }

    private String getNowPath() {
        File directory = new File(".");
        try {
            return directory.getCanonicalPath();
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }

    private Map<String, String> getMac(Connect connect) {
        if (null == connect) {
            MainJFrame.showMssageln("智能路由器telnet连接失败，无法获取MAC...");
            return null;
        }
        String[] linkShow = connect.sendCommand("link show").split("\r\n");
        Map<String, String> map = new HashMap();
        for (String str : linkShow) {
            if (str.contains("lan") && str.contains("wifi") && str.contains("wan")) {
                String[] mac = str.split(" ");
                for (String tmp : mac) {
                    int index = tmp.indexOf(":");
                    String key = tmp.substring(0, index);
                    String value = tmp.substring(index + 1, tmp.length()).replace(":", "");
                    map.put(key, value);
                }
                break;
            }
        }
        // connect.disconnect();
        MainJFrame.showMssageln("MAC got");
        return map;
    }

    public boolean setGeneralInfo() {
        //generalSet.set
        return true;
    }
}
