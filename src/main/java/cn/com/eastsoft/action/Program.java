package cn.com.eastsoft.action;

import cn.com.eastsoft.ui.MainJFrame;
import cn.com.eastsoft.util.Connect;
import cn.com.eastsoft.util.ToolUtil;

public class Program
{
	public static boolean downLoadFile(String filedir, Connect telnet, String hostIP)
	{
		String original = null;
		String target = null;
		// 默认网关系统应用文件
		original = filedir + "\\gateway.zip";
		target = "gateway.zip";
		try
		{
			ToolUtil.copyFile(original, target);
		} catch (Exception e)
		{
			e.printStackTrace();
			MainJFrame.showMssage("请检查" + filedir + "是否存在！\n");
			return false;
		}
		try
		{
			MainJFrame.showMssage("正在提取" + filedir + "，请稍后。。。\n");
			Thread.sleep(1000);
		} catch (InterruptedException e1)
		{
			e1.printStackTrace();
			return false;
		}
		// 根据具体的文件判断大约耗时
		String gettime = ToolUtil.calculateTime(target);
		if (gettime == null)
		{
			MainJFrame.showMssage("检查" + target + "是否在当前目录中！\n");
			return false;
		}
		MainJFrame.showMssage("正在下载" + filedir + "，大约" + gettime + "秒！\n");

		telnet.sendCommand("cd /mnt/temp");
		telnet.sendCommand("tftp -gr kill_gateway.sh " + hostIP);
		telnet.sendCommand("chmod 775 kill_gateway.sh");
		telnet.sendCommand("./kill_gateway.sh");
		telnet.sendCommand("cd /");

		String dn = telnet.sendCommand("dn  " + hostIP);
		if (dn.contains("timeout"))
		{
			MainJFrame.showMssage("TFTP未打开，无法下载文件！\n");
			return false;
		}
		if (dn.contains("server error"))
		{
			MainJFrame.showMssage("TFTP端错误，请检查TFTP！\n");
			return false;
		}
		downloadGatewayStart(telnet, hostIP);
		telnet.sendCommand("cd /");
		telnet.sendCommand("rm -f /mnt/temp/kill_gateway.sh");
		if (dn.contains("网关程序更新完毕"))
		{
			telnet.sendCommand("sync");
			MainJFrame.showMssage(filedir + "下载成功！\n");
		} else
		{
			MainJFrame.showMssage(filedir + "下载失败！\n");
			return false;
		}
		
		ToolUtil.deleteFileByFilename(target);
		return true;
	}

	private static boolean downloadGatewayStart(Connect telnet, String hostIP)
	{
		String original = "conf\\gateway_start.sh";
		String target = "gateway_start.sh";
		try
		{
			ToolUtil.copyFile(original, target);
		} catch (Exception e)
		{
			e.printStackTrace();
			MainJFrame.showMssage("请检查" + target + "是否存在！\n");
			return false;
		}
		telnet.sendCommand("cd /");
		telnet.sendCommand("cd /mnt/data/osgi");
		telnet.sendCommand("tftp -gr gateway_start.sh " + hostIP);
		telnet.sendCommand("sync");
		telnet.sendCommand("chmod 775 gateway_start.sh");
		telnet.sendCommand("cd /");
		ToolUtil.deleteFileByFilename(target);
		return true;
	}
}
