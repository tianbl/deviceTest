package cn.com.eastsoft.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import cn.com.eastsoft.ui.powerline.*;
import cn.com.eastsoft.util.Connect;
import cn.com.eastsoft.util.ProgramDataManag;
import test.SocketTest;

//主界用于组织各个功能模块，各模块分开编写
public class MainJFrame extends JFrame implements ActionListener {

    private static MainJFrame instance;

    private JButton selectmodule;
    private JComboBox moduleItem;
    private JPanel indexPanel;

    private JPanel jpanel_View;    //信息显示panel
    private static JTextArea jTextArea_View;
    private JScrollPane jScrollPane_View;    //添加滚动条

    private JPanel uiPanel;      //主功能节目布局在此panel上面
    private JTabbedPane jtab;    //个功能选项卡
    private String[] deviceList = {"电力线适配器","电力线无线路由器","电力线无线无扩展器"};
    private DeviceTest deviceTest;
    private VersionUpdate gateUpdate;
    private ServerSet serverSet;
    private ConnectParamSet connectParamSet;

    public static MainJFrame getInstance() {
        if (instance == null) {
            instance = new MainJFrame();
        }
        return instance;
    }

    private MainJFrame() {
        super();
        setResizable(false);
        setBackground(Color.WHITE);
        this.setTitle("智能路由器网关测试工具");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initGUI();
        System.out.println("GateTestJFrame signal instance...");
    }

    private void initGUI() {
//		this.setIconImage(new ImageIcon(getClass().getClassLoader()
//				.getResource("logo.png")).getImage());
        {
            moduleItem = new JComboBox();
            for(String str:deviceList){
                moduleItem.addItem(str);
            }
            selectmodule = new JButton("选择被测设备");
            moduleItem.setBounds(300, 300, 200, 30);
            selectmodule.setBounds(300, 350, 200, 30);
            selectmodule.addActionListener(this);
            indexPanel = new JPanel();
            indexPanel.setSize(700, 700);
            indexPanel.setLocation(0, 0);
            indexPanel.setLayout(null);
            indexPanel.add(moduleItem);
            indexPanel.add(selectmodule);

            this.setSize(800, 700);
            this.setLocation(200, 0);
            this.setLayout(null);
            this.add(indexPanel);
            this.setVisible(true);
        }
        {
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.out.println("触发windowClosing事件");

                    if (uiPanel != null) {
                        ProgramDataManag.deleteConf("deviceTest.conf");
                        serverSet.saveVersion();
                        gateUpdate.saveVersion();
                        if(null!=connectParamSet){
                            connectParamSet.saveVersion();
                        }
                        GeneralSet.getInstance().saveVersion();
                        GeneralSet.getInstance().stopProduct();
                    }
                }

                public void windowClosed(WindowEvent e) {
                    //System.out.println("触发windowClosed事件");
                }
            });
        }
    }

    private void initPowerLineUI(){

        {   //初始化电力线设备测试界面
//            indexPanel.setVisible(false);
            {    //信息输出区域设置
                jpanel_View = new JPanel();

                jTextArea_View = new JTextArea();
                jTextArea_View.setEditable(false);

                jpanel_View.setBorder(new TitledBorder(UIManager
                        .getBorder("TitledBorder.border"),
                        "信息输出", TitledBorder.LEFT,
                        TitledBorder.TOP, null, new Color(10, 50, 192)));

                jScrollPane_View = new JScrollPane();
                jScrollPane_View.setViewportView(jTextArea_View);
                jpanel_View.setLayout(new GridLayout(1, 1));
                jpanel_View.add(jScrollPane_View);
            }

            {    //标签页设置
                jtab = new JTabbedPane(JTabbedPane.TOP);

                //测试部分
                deviceTest = new DeviceTest();
                jtab.add(deviceTest, "  设备测试测试    ");

                serverSet = ServerSet.getInstance();
                jtab.add(serverSet, "  数据来源设置  ");

                //升级部分
                gateUpdate = new VersionUpdate();
                jtab.add(gateUpdate, "  固件更新   ");

                connectParamSet = ConnectParamSet.getInstance();

                //jtab.setEnabledAt(0, false);
//                deviceTest.set_module(moduleItem.getSelectedIndex());
            }

            uiPanel = new JPanel();
            uiPanel.setSize(800, 700);
            uiPanel.setLayout(new GridLayout(2, 1));
            uiPanel.add(jpanel_View);
            uiPanel.add(jtab);
            uiPanel.setVisible(false);

            this.add(uiPanel);
        }
    }

    public void switchWindow() {
        System.out.println("切换回到设备选择窗口");
        uiPanel.setVisible(false);
        indexPanel.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {    //选择设备时进入指定设备测试界面并做设置
        indexPanel.setVisible(false);
        if(null==uiPanel){
            initPowerLineUI();
        }
        if(moduleItem.getSelectedIndex()<1){
            if(null!=uiPanel){
                jtab.remove(connectParamSet);
                jtab.remove(gateUpdate);
            }
            uiPanel.setVisible(true);
        }else if(moduleItem.getSelectedIndex()<3){
            ///开发时测试
            jtab.add(connectParamSet,"  连接参数设置 ");
            jtab.add(gateUpdate,"  固件更新   ");
            uiPanel.setVisible(true);
        }else {
            switchWindow();
        }
        deviceTest.set_module(moduleItem.getSelectedIndex());
        Para.setRex(moduleItem.getSelectedIndex());
    }

    /**
     * telnet 连接
     * @param gatewayIP
     * @param port
     * @return
     */
    public Connect telnetGateway(String gatewayIP, int port) {
        showMssage("正在连接网关，请稍后...\n");
        Connect telnet;
        telnet = new Connect(gatewayIP, 23);
        if (telnet.getIn() == null) {
            showMssage("网关连接失败,请确认是否连接正确！\n");
            return null;
        }
        showMssage("网关连接成功！\n");
        return telnet;
    }

    public static void showMssage(String str) {
        jTextArea_View.append(str);
        jTextArea_View.setSelectionStart(jTextArea_View.getText().length());
    }

    public static void showMssageln(String str) {
        showMssage(str + "\n");
    }

    public static void clearShow() {
        jTextArea_View.setText("");
    }

    public DeviceTest getDeviceTest() {
        return deviceTest;
    }

    public int getModuleItemSelected(){
        return moduleItem.getSelectedIndex();
    }
}
