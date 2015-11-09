package cn.com.eastsoft.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import cn.com.eastsoft.ui.powerline.GeneralSet;
import cn.com.eastsoft.ui.powerline.DeviceTest;
import cn.com.eastsoft.ui.powerline.VersionUpdate;
import cn.com.eastsoft.ui.powerline.ServerSet;
import cn.com.eastsoft.util.Connect;
import cn.com.eastsoft.util.ProgramDataManag;

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
    private DeviceTest deviceTest;
    private VersionUpdate gateUpdate;
    private ServerSet serverSet;

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
            moduleItem.addItem("电力线适配器");
            moduleItem.addItem("电力线无线路由器");
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
                jtab.add(deviceTest, "  1.设备测试测试    ");

                //升级部分
                gateUpdate = new VersionUpdate();
                jtab.add(gateUpdate, "  2.固件更新   ");

                serverSet = ServerSet.getInstance();
                jtab.add(serverSet, " 3.数据来源设置  ");

                //jtab.setEnabledAt(0, false);
                deviceTest.set_module(moduleItem.getSelectedIndex());
            }

            uiPanel = new JPanel();
            uiPanel.setSize(800, 700);
            uiPanel.setLayout(new GridLayout(2, 1));
            uiPanel.add(jpanel_View);
            uiPanel.add(jtab);
            uiPanel.setVisible(false);

            this.add(uiPanel);
        }
        {
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.out.println("触发windowClosing事件");
                    ProgramDataManag.deleteConf("deviceTest.conf");
                    GeneralSet.getInstance().saveVersion();
                    serverSet.saveVersion();
                    gateUpdate.saveVersion();

                    GeneralSet.getInstance().stopProduct();
                }

                public void windowClosed(WindowEvent e) {
                    //System.out.println("触发windowClosed事件");
                }
            });
        }
    }

    public void switchWindow() {
        System.out.println("切换回到设备选择窗口");
        uiPanel.setVisible(false);
        indexPanel.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        indexPanel.setVisible(false);
        uiPanel.setVisible(true);
        deviceTest.set_module(moduleItem.getSelectedIndex());
    }

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

    public DeviceTest getGatewayTest() {
        return deviceTest;
    }
}
