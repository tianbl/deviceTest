package cn.com.eastsoft.ui.powerline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.util.Connect;
import cn.com.eastsoft.util.Ping;
import cn.com.eastsoft.util.ProgramDataManag;
import cn.com.eastsoft.util.SSHClient;

//网关升级部分
public class VersionUpdate extends JPanel implements ActionListener {

    private JLabel hwVersion_Jlabel;    //硬件版本号
    private JTextField hwVersion_JTextField;
    private JLabel fwVersion_JLabel;    //软件版本号
    private JTextField fwVersion_JTextField;
//	private JLabel gwVersion_JLabel;	//网关版本号
//	private JTextField gwVersion_JTextField;

    private JLabel deviceIP_JLabel;
    private JTextField deviceIP_JTextField;

    private JLabel localIP_JLabel;
    private JTextField localIP_JTextField;

    private JButton refresh_JButton;
    private JButton update_button;
    private String update_button_name = "检查设备软硬件版本更新";
    private JButton fileChooser;
    private String updateFileName;
    private String realPath;

    public VersionUpdate() {
        super();
        this.setLayout(null);

        {
            deviceIP_JLabel = new JLabel("被测设备IP");
            deviceIP_JLabel.setBounds(50, 10, 100, 30);
            deviceIP_JTextField = new JTextField();
            deviceIP_JTextField.setBounds(130, 10, 100, 30);
            this.add(deviceIP_JLabel);
            this.add(deviceIP_JTextField);
//			gatewayIP_JTextField.setText(Para.Gateway_IP);

            localIP_JLabel = new JLabel("本机IP");
            localIP_JTextField = new JTextField();
            try {
                localIP_JTextField.setText(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                MainJFrame.showMssage("获取本机IP地址失败！\n");
            }

            localIP_JLabel.setBounds(300, 10, 50, 30);
            localIP_JTextField.setBounds(350, 10, 100, 30);
            this.add(localIP_JLabel);
            this.add(localIP_JTextField);

            JLabel fileLabel = new JLabel("选择升级文件");
            fileLabel.setBounds(500, 10, 80, 30);
            fileChooser = new JButton("...");
            fileChooser.setBounds(580, 10, 200, 30);
            fileChooser.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // TODO Auto-generated method stub
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    jfc.showDialog(new JLabel(), "选择");
                    File file = jfc.getSelectedFile();
                    if (file.isDirectory()) {
                        MainJFrame.showMssageln("请选择一个文件...");

                    } else if (file.isFile()) {
                        MainJFrame.showMssageln("文件:" + file.getAbsolutePath());
                        updateFileName = file.getName();
                        if(updateFileName.contains("(")){
                            int i = JOptionPane.showConfirmDialog(MainJFrame.getInstance(),
                                    "请将升级固件名称中的'('和‘)'去掉！", "提示",
                                    JOptionPane.DEFAULT_OPTION);
                            realPath = null;
                            updateFileName = null;
                            fileChooser.setText("...");
                        }else {
                            updateFileName = jfc.getSelectedFile().getName();
                            realPath = file.getAbsolutePath();
                            fileChooser.setText(updateFileName);
                        }
                    }
                }
            });
            this.add(fileLabel);
            this.add(fileChooser);

			/*this.add(fileChooser);*/
        }
        {
            refresh_JButton = new JButton("刷新");
            refresh_JButton.setBounds(580, 50, 60, 60);

            refresh_JButton.addActionListener(this);
            this.add(refresh_JButton);
        }
        {
            hwVersion_Jlabel = new JLabel("硬件版本号");
            hwVersion_JTextField = new JTextField();
            hwVersion_Jlabel.setBounds(50, 50, 120, 30);
            hwVersion_JTextField.setBounds(130, 50, 320, 30);
            this.add(hwVersion_Jlabel);
            this.add(hwVersion_JTextField);

            fwVersion_JLabel = new JLabel("固件版本号");
            fwVersion_JTextField = new JTextField();
            fwVersion_JLabel.setBounds(50, 90, 100, 30);
            fwVersion_JTextField.setBounds(130, 90, 320, 30);
            this.add(fwVersion_JLabel);
            this.add(fwVersion_JTextField);

//			gwVersion_JLabel = new JLabel("网关版本号");
//			gwVersion_JTextField = new JTextField();
//			gwVersion_JLabel.setBounds(50,130,100,30);
//			gwVersion_JTextField.setBounds(130, 130, 320, 30);
//			this.add(gwVersion_JLabel);
//			this.add(gwVersion_JTextField);
        }

        {
            update_button = new JButton(update_button_name);
            update_button.setBounds(130, 180, 150, 50);
            update_button.addActionListener(this);
            this.add(update_button);
        }
        setVersion();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        if (update_button_name.equals(arg0.getActionCommand())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    saveVersion();
                    updateGateway(deviceIP_JTextField.getText(), localIP_JTextField.getText());
                }
            }).start();
        } else if ("刷新".equals(arg0.getActionCommand())) {
            MainJFrame.showMssage("刷新,重新获取本机IP...\n");
            try {
                localIP_JTextField.setText(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                MainJFrame.showMssage("获取本机IP地址失败！\n");
            }
            String ip1 = deviceIP_JTextField.getText();
            String ip2 = localIP_JTextField.getText();
            if (!Ping.isSameSegment(ip1, ip2)) {
                MainJFrame.showMssageln("本机IP和设备不再同一网段！");
            }
        }
    }

    private boolean updateGateway(String gatewayip, String hostip) {
        if (null == updateFileName || updateFileName.equals("")) {
            MainJFrame.showMssageln("先选择升级文件");
            return false;
        }
//		Connect telnet = MainJFrame.getInstance().telnetGateway(deviceIP_JTextField.getText(), 23);
//        GeneralSet generalSet = GeneralSet.getInstance();
        ConnectParamSet connectParamSet = ConnectParamSet.getInstance();
        String ip = deviceIP_JTextField.getText();
        SSHClient sshClient = new SSHClient(connectParamSet.getUser(), connectParamSet.getPwd(),
                ip, connectParamSet.getSshPort());

        String hw = sshClient.executeCmd("version hw show").replace("\n", "");
        String fw = sshClient.executeCmd("version fw show").replace("\n", "");

        //脚本修改后输出的内容不再是"ESHG50"-"v1.1"这种，没有两端的冒号
        MainJFrame.showMssageln("固件执行命令结果如下");
        MainJFrame.showMssageln("hw：" + hw + "\nfw：" + fw + "\n");
        String hwLast_v = hwVersion_JTextField.getText();
        String fwLast_v = fwVersion_JTextField.getText();
//		String gwLast_v = gwVersion_JTextField.getText();
        if (hw.equals(hwLast_v) && fw.equals(fwLast_v)) {
            MainJFrame.showMssageln("当前固件已是最新版本无需更新！");
            return true;
        } else if(hw.contains("error")||fw.contains("error")){
            MainJFrame.showMssageln("发生错误");
            return false;
        }else {
            int i = JOptionPane.showConfirmDialog(this, "存在可更新的固件版本，是否更新？", "提示",
                    JOptionPane.YES_NO_CANCEL_OPTION);
            if (0 != i) {
                return false;
            }
        }

        //MainJFrame.showMssageln("fwupdate "+updateFileName+" "+hostip);
//		telnet.sendCommand("cd /tmp");
        Map<String,String> map = new HashMap<>();
        String download = sshClient.executeCmd("tftp -g -l /tmp/" + updateFileName + " -r " + updateFileName + " " + hostip,map);
        MainJFrame.showMssageln(download);
        String errorMsg = map.get("errorMsg");
        if (errorMsg.contains("timeout")) {
            MainJFrame.showMssageln("升级文件传输超时!仔细检查设置！");
            return false;
        } else if (errorMsg.contains("error")) {
            MainJFrame.showMssageln("升级包下载发生错误!请查看tftp Server程序中的传输记录！");
            return false;
        }else if(errorMsg.contains("-bash: tftp: command not found")){
            MainJFrame.showMssageln("发生错误,当前固件中无tftp命令，无法升级!");
            return false;
        }
        String setHw = sshClient.executeCmd("version hw set " + hwLast_v);
        if (setHw.contains("ok")) {
            MainJFrame.showMssageln("硬件版本号设置成功...\n升级系统...");
        } else {
            MainJFrame.showMssageln("硬件版本号设置失败！");
        }
        sshClient.executtingCmd("sysupgrade /tmp/" + updateFileName);
        return true;
    }

    private boolean setVersion() {

        Map<String, String> map = ProgramDataManag.getConfigData("deviceTest.conf");
        if (null == map) {
            return false;
        }
        hwVersion_JTextField.setText(map.get("hwVersion"));
        fwVersion_JTextField.setText(map.get("fwVersion"));
//		gwVersion_JTextField.setText(map.get("gwVersion"));
        deviceIP_JTextField.setText(map.get("deviceIP"));
        //localIP_JTextField.setText(map.get("hostIP_Update"));

        return true;
    }

    public boolean saveVersion() {
        Map<String, String> map = new HashMap();
//		map.put("routeGateIP_Update", deviceIP_JTextField.getText());
        //map.put("hostIP_Update", localIP_JTextField.getText());
        map.put("hwVersion", hwVersion_JTextField.getText());
        map.put("fwVersion", fwVersion_JTextField.getText());
//		map.put("gwVersion", gwVersion_JTextField.getText());

        ProgramDataManag.updateConf("deviceTest.conf", map);
        return true;
    }
}
