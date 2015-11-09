package cn.com.eastsoft.util;

import java.util.Map;

import cn.com.eastsoft.sql.SQLite;
import cn.com.eastsoft.ui.MainJFrame;

public class Database
{
	
	public static boolean downloadFile(Connect telnet,String path,String fileName,String hostIP){
		if(null==path||"".equals(path)){
			path = "./";
		}
		String cd = telnet.sendCommand("cd "+path);
		MainJFrame.showMssageln(cd);
		String tftp = telnet.sendCommand("tftp -g -l "+fileName+" -r "+fileName+" "+hostIP);
		if(tftp.contains("timeout")){
			MainJFrame.showMssageln("传输超时，请检查连接！");
			return false;
		}
		MainJFrame.showMssageln(tftp);
		return true;
	}
	public static boolean dbupdateDB(Connect telnet, String gatewaydb, String hostIP)
	{
		String mtd = telnet.sendCommand("mtd erase production");
		String tftps = telnet.sendCommand("dbupdate " + gatewaydb + " " + hostIP);
		//MainJFrame.showMssageln(tftps);
		if (tftps.contains("timeout"))
		{
			MainJFrame.showMssage("传输超时,检查TFTP是否打开，与路由器的连接是否断开\n");
			return false;
		}else if(tftps.contains("error")){
			MainJFrame.showMssage("传输失败 ,检查TFTP是否打开，与路由器的连接是否断开\n");
			return false;
		}
		String lsGatewayDb = telnet.sendCommand("ls /powerlineImpl/cpp/main");
		if(!lsGatewayDb.contains(gatewaydb)){
			MainJFrame.showMssageln("路由器下载" + gatewaydb + "失败！");
			return false;
		}else{
			MainJFrame.showMssageln("路由器下载" + gatewaydb + "成功！");
			String sync = telnet.sendCommand("sync");
		}
		return true;
	}
	
	public static boolean dbuploadGateway(Connect telnet, String hostIP, String gatewaydb,String path)
	{
		if(null==telnet){
			MainJFrame.showMssageln("智能路由器网关未连接,或者网关连接已中断,请重新连接...");
			return false;
		}
		String dbupload = telnet.sendCommand("dbupload "+gatewaydb+" "+hostIP);
		if(dbupload.contains("can't open '"+gatewaydb+"'")){
			MainJFrame.showMssageln("智能路由器网关中不存在数据库...");
			return false;
		}else if(dbupload.contains("timeout")){
			MainJFrame.showMssageln("连接超时，请检查tftp服务器是否开启，tftp服务器地址（这里是本机IP）是否正确...");
		}else if(dbupload.contains("ok")&&ToolUtil.isFileExist(path+"\\"+gatewaydb)){
			MainJFrame.showMssageln("数据库文件上传至tftp根目录，检验数据库信息...");
		}
		
		try{
			Thread.sleep(2000);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	public static boolean checkGatewayDb(Connect telnet,String gid,String hostIP,String gatewaydb,String path,Map<String, Object> map){
		boolean flag = true;
		dbuploadGateway(telnet,hostIP,gatewaydb,path);
		Map account = SQLite.getAccountByGid(path + "\\" + gatewaydb, gid);
		Map para = SQLite.getPara(path+"\\"+gatewaydb);
		if(null==account||null==para){
			MainJFrame.showMssageln("网关数据库检测失败...");
			flag=false;
		}else{
			if(gid.equals(account.get("gid"))){
				MainJFrame.showMssageln("网关号GID写入成功...");
			}
		}
		return flag;
	}
	
	public static boolean uploadFileOfUsb(Connect telnet,String hostIP){
		if(null==telnet){
			MainJFrame.showMssageln("路由器网关连接不存在...");
			return false;
		}
		String cd = telnet.sendCommand("cd /mnt");
		//MainJFrame.showMssageln(cd);
		String absolute = telnet.sendCommand("find -name testFile.txt").split("\r\n")[1];
		//MainJFrame.showMssageln(absolute);
		if(false==absolute.contains("testFile.txt")){
			MainJFrame.showMssageln("找不文件testFile.txt");
			return false;
		}
		String path = absolute.substring(0, absolute.length()-"testFile.txt".length());
		String smartRouter = telnet.sendCommand("cd "+path);
		//MainJFrame.showMssageln(smartRouter);
		String tftp = telnet.sendCommand("tftp -p -l testFile.txt "+hostIP);
		if(tftp.contains("timeout")){
			MainJFrame.showMssageln("文件传输超时检查主机地址，路由器地址等设置是否正确 ");
			return false;
		}
		MainJFrame.showMssageln(tftp);
		try{
			Thread.sleep(2500);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
}
