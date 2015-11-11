package cn.com.eastsoft.ui.powerline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.ui.Para;
import cn.com.eastsoft.util.ProgramDataManag;
import cn.com.eastsoft.scanningGun.barcode.BarcodeBuffer;
import cn.com.eastsoft.scanningGun.barcode.BarcodeProducter;

public class GeneralSet extends JPanel {

    private static GeneralSet instance;

    private MainJFrame mainJFrame;
    private JLabel deviceIP_JLabel;
    private JTextField deviceIP_JTextField;

    private JLabel accompanyIP_JLabel;
    private JTextField accompanyIP_JText;

    private JLabel localIP_JLabel;
    private JTextField localIP_JTextField;

    private JLabel defaultgw_JLabel;
    private JTextField defaultgw_JTextField;
    private JLabel qrcode_JLabel;
    private JTextField qrcode_JTextField;

    private JButton linkTest_JButton;
    //private String linkButton_title="";
    private JButton showClear_JButton;
    private JButton refresh_JButton;


    private JButton changeToHex;
    private JLabel numOfPing_JLabel;
    private JTextField numOfPing_JTextField;
    public JLabel udpPort_JLabel;
    public JTextField udpPort_JText;


    //private JTextChange jtextChange;
    private ButtonActionListener buttonActionListener;

    //二维码扫描枪数据 获取，二维码信息生产者的启动和管理
    private BarcodeProducter barcodeProducter;

    public static GeneralSet getInstance() {
        if (null == instance) {
            instance = new GeneralSet();
        }
        return instance;
    }

    private GeneralSet() {
        super();
        this.setLayout(null);
//		/jtextChange = new JTextChange();
        buttonActionListener = new ButtonActionListener();

        {
            int y = 30;

            qrcode_JLabel = new JLabel("二维码标签信息");
            qrcode_JTextField = new JTextField();
            qrcode_JLabel.setBounds(10, y, 100, 30);
            qrcode_JTextField.setBounds(100, y, 400, 30);
//			qrcode_JTextField.getDocument().addDocumentListener(jtextChange);
            //qrcode_JTextField.setText(Para.qrcode);
            this.add(qrcode_JLabel);
            this.add(qrcode_JTextField);

            linkTest_JButton = new JButton("测试路由器网关连接");
            linkTest_JButton.setBounds(530, y, 150, 30);
            linkTest_JButton.addActionListener(buttonActionListener);
            this.add(linkTest_JButton);

            showClear_JButton = new JButton("清空信息台");
            showClear_JButton.setBounds(680, y, 100, 30);
            showClear_JButton.addActionListener(buttonActionListener);
            this.add(showClear_JButton);
        }

        {
            int y = 80;
            numOfPing_JLabel = new JLabel("ping连通次数");
            numOfPing_JTextField = new JTextField();
            numOfPing_JLabel.setBounds(10, y, 100, 30);
            numOfPing_JTextField.setBounds(100, y, 100, 30);
            numOfPing_JTextField.setText("5");
            JLabel jlabel = new JLabel("默认0次，需要通过3次以上");
            jlabel.setBounds(200, y, 300, 30);
            this.add(jlabel);
            this.add(numOfPing_JLabel);
            this.add(numOfPing_JTextField);

            udpPort_JLabel = new JLabel("UDP通信端口");
            udpPort_JText = new JTextField();
            udpPort_JLabel.setBounds(530, y, 100, 30);
            udpPort_JText.setBounds(610, y, 100, 30);
            this.add(udpPort_JLabel);
            this.add(udpPort_JText);
        }

        {
            int y = 130;
            deviceIP_JLabel = new JLabel("设备IP");
            deviceIP_JLabel.setBounds(10, y, 50, 30);
            deviceIP_JTextField = new JTextField();
//            deviceIP_JTextField.setText(Para.Gateway_IP);
            deviceIP_JTextField.setBounds(60, y, 100, 30);
            this.add(deviceIP_JLabel);
            this.add(deviceIP_JTextField);

            //配测设备IP
            accompanyIP_JLabel = new JLabel("陪测设备IP");
            accompanyIP_JLabel.setBounds(170, y, 60, 30);
            accompanyIP_JText = new JTextField();
            accompanyIP_JText.setBounds(240, y, 100, 30);
            this.add(accompanyIP_JLabel);
            this.add(accompanyIP_JText);

            //本机ip
            localIP_JLabel = new JLabel("本机IP");
            localIP_JTextField = new JTextField();
            try {
                localIP_JTextField.setText(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                MainJFrame.showMssage("获取本机IP地址失败！\n");
            }
            localIP_JLabel.setBounds(350, y, 50, 30);
            localIP_JTextField.setBounds(400, y, 100, 30);
            this.add(localIP_JLabel);
            this.add(localIP_JTextField);

			/*defaultgw_JLabel defaultgw_JTextField*/
            defaultgw_JLabel = new JLabel("公司网关地址");
            defaultgw_JTextField = new JTextField();
            defaultgw_JLabel.setBounds(530, y, 100, 30);
            defaultgw_JTextField.setBounds(610, y, 100, 30);
            defaultgw_JTextField.setText("129.1.88.1");
            this.add(defaultgw_JLabel);
            this.add(defaultgw_JTextField);

            refresh_JButton = new JButton("刷新");
            refresh_JButton.setBounds(720, y, 60, 30);
            refresh_JButton.addActionListener(buttonActionListener);
            this.add(refresh_JButton);
        }

        //初始化输入值
        setVersion();
        //自动化测试流程
        beginAutoTest();
    }

    public void beginAutoTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //启用生产者
//				barcodeProducter = new BarcodeProducter();
//				barcodeProducter.startProduct();

                while (true) {
                    /*if(null==mainJFrame){
						mainJFrame = MainJFrame.getInstance();	//获取主框架单例
					}*/
                    try {
                        String queueInfo = BarcodeBuffer.consume();
                        MainJFrame.showMssageln("扫码信息缓冲队列中获取二维码信息：" + queueInfo + "");
                        qrcode_JTextField.setText("");
                        Thread.sleep(300);
                        qrcode_JTextField.setText(queueInfo);
                        Map map = getQrCode_Info();
                        MainJFrame.showMssage("解析得到标签信息如下\n" +
                                "sn:" + map.get("sn") + " gid:" + map.get("gid") + " pwd:" + map.get("pwd") + "\n");
                        MainJFrame.getInstance().getGatewayTest().allTest();
                        //Thread.sleep(2000);
                        qrcode_JTextField.setText("");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    class ButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg) {
            // TODO Auto-generated method stub
            String name = arg.getActionCommand();
            if ("测试路由器网关连接".equals(name)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        MainJFrame.showMssage("测试路由器网关连接被点击\n");
                        if (null == mainJFrame) {
                            mainJFrame = MainJFrame.getInstance();    //获取主框架单例
                        }
                        if (null != mainJFrame.telnetGateway(deviceIP_JTextField.getText(), 23)) {
                            MainJFrame.showMssage("路由器连接成功\n");
                        }
                    }
                }).start();
            } else if ("清空信息台".equals(name)) {
                MainJFrame.clearShow();
            } else if ("刷新".equals(arg.getActionCommand())) {
                MainJFrame.showMssage("刷新,重新获取本机IP...\n");
                try {
                    localIP_JTextField.setText(InetAddress.getLocalHost().getHostAddress());
                } catch (UnknownHostException e) {
                    MainJFrame.showMssage("获取本机IP地址失败！\n");
                }
                String gip = deviceIP_JTextField.getText();
                String loip = localIP_JTextField.getText();
                if (gip.length() > 7 && loip.length() > 7) {
                    gip = gip.substring(0, 7);
                    loip = loip.substring(0, 7);
                }
                if (!gip.equals(loip)) {
                    MainJFrame.showMssageln("本机和网关不再同一网段");
                }
            }
        }

    }

    public void stopProduct() {
        //停止二维码扫描
//		barcodeProducter.stopProduct();
    }

    /**
     * @param str
     * @param isProducer true表示是扫描枪线程,否则表示其他
     * @return
     */
    public int checkCodeInfo(String str, boolean isProducer) {

//		String regex1 = "SN\\w{1,24}USER\\d{1,10}PWD\\d{6}$";
        ///扫码器扫描标签后得到的信息中总是会带有'J'所以正则表达式的规则中包含了'J'

        Pattern pattern1 = Pattern.compile(Para.regex1);
        Pattern pattern2 = Pattern.compile(Para.regex2);
        Matcher matcher1 = pattern1.matcher(str);
        Matcher matcher2 = pattern2.matcher(str);
        if (matcher1.find()) {
            if (isProducer) {
                if (matcher1.start(0) > 0) {//如果不是完全匹配，则去掉干扰字符
                    BarcodeBuffer.product(matcher1.group(0));
                    MainJFrame.showMssageln("由表达式regex1匹配，缓冲区存在干扰字符，已将正确条码信息取出，测试期间请减少输入操作");
                } else {
                    BarcodeBuffer.product(str);
                    MainJFrame.showMssageln("正则表达式1完全匹配");
                }
            }
            return 1;
        } else if (matcher2.find()) {
            if (isProducer) {
                if (matcher2.start(0) > 0) {//如果不是完全匹配，则去掉干扰字符
                    BarcodeBuffer.product(matcher2.group(0));
                    MainJFrame.showMssageln("由表达式regex2匹配，缓冲区存在干扰字符，已将正确条码信息取出，测试期间请减少减少输入操作");
                } else {
                    BarcodeBuffer.product(str);
                    MainJFrame.showMssageln("正则表达式2完全匹配");
                }
            }
            return 2;
        } else {
            return 0;
        }
    }

    public Map<String, String> getQrCode_Info() {
        String qrcodeinfo = qrcode_JTextField.getText();
        if (null == qrcodeinfo || "".equals(qrcodeinfo)) {
            MainJFrame.showMssageln("没有输入网关条码信息,请检查输出确认程序是否已得到二维码信息...");
            return null;
        }

        if (false == (qrcodeinfo.contains("SN") && qrcodeinfo.contains("USER") && qrcodeinfo.contains("PWD"))) {
            return null;
        }

        Map<String, String> map = new HashMap();
        if (checkCodeInfo(qrcodeinfo, false) == 1) {
            Pattern pattern = Pattern.compile(Para.regex1);
            Matcher matcher = pattern.matcher(qrcodeinfo);
            matcher.find();
            map.put("sn", matcher.group(2));
            map.put("gid", matcher.group(4));
            map.put("pwd", matcher.group(6));
        } else if (checkCodeInfo(qrcodeinfo, false) == 2) {
            Pattern pattern = Pattern.compile(Para.regex2);
            Matcher matcher = pattern.matcher(qrcodeinfo);
            matcher.find();
            map.put("sn", matcher.group(2));
            map.put("gid", matcher.group(4));
            map.put("pwd", matcher.group(6));
        }

        return map;
    }

    private boolean setVersion() {
        Map<String, String> map = ProgramDataManag.getConfigData("deviceTest.conf");
        if (null == map) {
            return false;
        }

        numOfPing_JTextField.setText(map.get("pingNum"));
        udpPort_JText.setText(map.get("udpPort"));
        deviceIP_JTextField.setText(map.get("deviceIP"));
        accompanyIP_JText.setText(map.get("accompanyIP"));
        defaultgw_JTextField.setText(map.get("gatewayIP"));
        return true;
    }

    public boolean saveVersion() {  //关闭程序时保存程序部分设置
        Map<String, String> map = new HashMap();
        map.put("pingNum", numOfPing_JTextField.getText());
        map.put("udpPort", udpPort_JText.getText());
        map.put("deviceIP", deviceIP_JTextField.getText());
        map.put("accompanyIP", accompanyIP_JText.getText());
        map.put("gatewayIP", defaultgw_JTextField.getText());   //公司网关

        ProgramDataManag.updateConf("deviceTest.conf", map);
        return true;
    }

    private void toHex() {
//        String aid = plcTestAddr_JText.getText();
//        long aidLong = 0;
//        String[] completions = {"", "0", "00", "000", "0000", "00000", "000000", "0000000"};
//        try {
//            aidLong = Long.parseLong(aid);
//            MainJFrame.showMssageln("将" + aid + "转换成16进制");
//            String addr = Long.toHexString(aidLong);
//            if (addr.length() <= 8) {
//                plcTestAddr_JText.setText(completions[8 - addr.length()] + addr);
//            } else {
//                MainJFrame.showMssageln("转换后的十六进制串口地址输入长度超过允许的8位，请重新检查输入...");
//                JOptionPane.showConfirmDialog(this, "地址转换发生错误，长度超过允许？", "提示",
//                        JOptionPane.YES_NO_CANCEL_OPTION);
//            }
//        } catch (Exception e) {
//            MainJFrame.showMssageln("地址格式错误，或者已经转换成16进制，无需再次转换");
//            e.printStackTrace();
//        }
    }

    public int getNumOfPing() {
        String pingNum = numOfPing_JTextField.getText();
        boolean isempty = (null == pingNum || "".equals(pingNum));
        return isempty == true ? 0 : Integer.parseInt(pingNum);
    }

    public String getDevice_IP() {
        return deviceIP_JTextField.getText();
    }

    public String getLocal_IP() {
        return localIP_JTextField.getText();
    }

    public String getAccompany_IP() {
        return accompanyIP_JText.getText();
    }

    public String getDefaultgw_IP() {
        return defaultgw_JTextField.getText();
    }

    public int getUdpPort() {
        String udpPort = udpPort_JText.getText();
        boolean isempty = (null == udpPort || "".equals(udpPort));
        return isempty == true ? 0 : Integer.parseInt(udpPort);
    }

    public String getQrCodeString(){
        return qrcode_JTextField.getText();
    }
}
