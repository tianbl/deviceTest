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
import cn.com.eastsoft.util.ProgramDataManag;

//网关升级部分
public class VersionUpdate extends JPanel implements ActionListener{
	
	private JLabel hwVersion_Jlabel;	//硬件版本号
	private JTextField hwVersion_JTextField;
	private JLabel fwVersion_JLabel;	//路由器固件版本号
	private JTextField fwVersion_JTextField;
	private JLabel gwVersion_JLabel;	//网关版本号
	private JTextField gwVersion_JTextField;
	
	private JLabel gatewayIP_JLabel;
	private JTextField gatewayIP_JTextField;
	
	private JLabel localIP_JLabel;
	private JTextField localIP_JTextField;
	
	private JButton refresh_JButton;
	private JButton update_button;
	private String update_button_name="检查路由器版本更新";
	private JButton fileChooser;
	private String updateFileName;
	private String realPath;
	public VersionUpdate(){
		super();
		this.setLayout(null);
		
		{
			gatewayIP_JLabel = new JLabel("智能路由器网关IP");
			gatewayIP_JLabel.setBounds(20,10,100,30);
			gatewayIP_JTextField = new JTextField();
			gatewayIP_JTextField.setBounds(130, 10, 100, 30);
			this.add(gatewayIP_JLabel);
			this.add(gatewayIP_JTextField);
//			gatewayIP_JTextField.setText(Para.Gateway_IP);
			
			localIP_JLabel = new JLabel("本机IP");
			localIP_JTextField = new JTextField();
			try{
				localIP_JTextField.setText(InetAddress.getLocalHost().getHostAddress());
			}catch (UnknownHostException e){
				MainJFrame.showMssage("获取本机IP地址失败！\n");
			}
			
			localIP_JLabel.setBounds(300,10,50,30);
			localIP_JTextField.setBounds(350, 10, 100, 30);
			this.add(localIP_JLabel);
			this.add(localIP_JTextField);
			
			JLabel fileLabel = new JLabel("选择升级文件");
			fileLabel.setBounds(500, 10, 80, 30);
			fileChooser = new JButton("...");
			fileChooser.setBounds(580, 10, 200, 30);
			fileChooser.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					JFileChooser jfc=new JFileChooser();
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
					jfc.showDialog(new JLabel(), "选择");
					File file=jfc.getSelectedFile();
					if(file.isDirectory()){
						MainJFrame.showMssageln("请选择一个文件...");
						
					}else if(file.isFile()){
						MainJFrame.showMssageln("文件:" + file.getAbsolutePath());
					}
					updateFileName = jfc.getSelectedFile().getName();
					realPath = file.getAbsolutePath();
					fileChooser.setText(updateFileName);
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
			hwVersion_Jlabel.setBounds(50,50,120,30);
			hwVersion_JTextField.setBounds(130, 50, 320, 30);
			this.add(hwVersion_Jlabel);
			this.add(hwVersion_JTextField);
			
			fwVersion_JLabel = new JLabel("路由器固件版本号");
			fwVersion_JTextField = new JTextField();
			fwVersion_JLabel.setBounds(10,90,100,30);
			fwVersion_JTextField.setBounds(130, 90, 320, 30);
			this.add(fwVersion_JLabel);
			this.add(fwVersion_JTextField);
			
			gwVersion_JLabel = new JLabel("网关版本号");
			gwVersion_JTextField = new JTextField();
			gwVersion_JLabel.setBounds(50,130,100,30);
			gwVersion_JTextField.setBounds(130, 130, 320, 30);
			this.add(gwVersion_JLabel);
			this.add(gwVersion_JTextField);
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
		if(update_button_name.equals(arg0.getActionCommand())){
			new Thread(new Runnable(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					saveVersion();
					updateGateway(gatewayIP_JTextField.getText(),localIP_JTextField.getText());
				}
			}).start();
		}else if("刷新".equals(arg0.getActionCommand())){
			MainJFrame.showMssage("刷新,重新获取本机IP...\n");
			try{
				localIP_JTextField.setText(InetAddress.getLocalHost().getHostAddress());
			}catch (UnknownHostException e){
				MainJFrame.showMssage("获取本机IP地址失败！\n");
			}
			String gip = gatewayIP_JTextField.getText();
			String loip = localIP_JTextField.getText();
			if(gip.length()>7&&loip.length()>7){
				gip = gip.substring(0, 7);
				loip = loip.substring(0, 7);
			}
			if(!gip.equals(loip)){
				MainJFrame.showMssageln("本机和网关不再同一网段...");
			}
		}
	}
	
	private boolean updateGateway(String gatewayip,String hostip){
		Connect telnet = MainJFrame.getInstance().telnetGateway(gatewayIP_JTextField.getText(), 23);
		if(null==telnet){
			return false;
		}
		
		telnet.sendCommand("cd /powerlineImpl/cpp/main");
		String gw = telnet.sendCommand("./powerlineImpl.exe -v").split("\r\n")[1];
		String hw = telnet.sendCommand("version hw show").split("\r\n")[1];
		String fw = telnet.sendCommand("version fw show").split("\r\n")[1];
		
		//脚本修改后输出的内容不再是"ESHG50"-"v1.1"这种，没有两端的冒号
		//hw = hw.substring(1, hw.length()-1);
		//fw = fw.substring(1, fw.length()-1);
		MainJFrame.showMssageln("hw：" + hw + "\nfw：" + fw + "\ngw：" + gw);
		String hwLast_v = hwVersion_JTextField.getText();
		String fwLast_v = fwVersion_JTextField.getText();
		String gwLast_v = gwVersion_JTextField.getText();
		if(hw.equals(hwLast_v)&&fw.equals(fwLast_v)&&gw.equals(gwLast_v)){
			MainJFrame.showMssageln("路由器网关已是最新版本无需更新！");
			return true;
		}else{
			int i = JOptionPane.showConfirmDialog(this, "路由器网关存在可更新的版本？","提示",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if(0!=i){
				return false;
			}
		}
		if(null==updateFileName||updateFileName.equals("")){
			MainJFrame.showMssageln("先选择升级文件");
			return false;
		}
		
		String setHw = telnet.sendCommand("version hw set "+hwLast_v);
		if(setHw.contains("ok")){
			MainJFrame.showMssageln("硬件版本号设置成功...升级系统");
		}else{
			MainJFrame.showMssageln("硬件版本号设置失败！");
		}
		//MainJFrame.showMssageln("fwupdate "+updateFileName+" "+hostip);
		telnet.sendCommand("cd /tmp");
		String download = telnet.sendCommand("tftp -g -r "+updateFileName+" "+hostip);
		if(download.contains("timeout")){
			MainJFrame.showMssageln("升级文件传输超时!");
			return false;
		}else if(download.contains("error")){
			MainJFrame.showMssageln("发生错误!");
			return false;
		}
		telnet.sendCommandLiner("sysupgrade -n "+updateFileName);
		return true;
	}
	
	private boolean setVersion(){
		
		Map<String,String> map = ProgramDataManag.getConfigData("deviceTest.conf");
		if(null==map){
			return false;
		}
		hwVersion_JTextField.setText(map.get("hwVersion"));
		fwVersion_JTextField.setText(map.get("fwVersion"));
		gwVersion_JTextField.setText(map.get("gwVersion"));
		gatewayIP_JTextField.setText(map.get("routeGateIP_Update"));
		//localIP_JTextField.setText(map.get("hostIP_Update"));
		
		return true;
	}
	public boolean saveVersion(){
		Map<String,String> map = new HashMap();
		map.put("routeGateIP_Update", gatewayIP_JTextField.getText());
		//map.put("hostIP_Update", localIP_JTextField.getText());
		
		map.put("hwVersion", hwVersion_JTextField.getText());
		map.put("fwVersion", fwVersion_JTextField.getText());
		map.put("gwVersion", gwVersion_JTextField.getText());
		
		ProgramDataManag.updateConf("deviceTest.conf", map);
		return true;
	}
}
