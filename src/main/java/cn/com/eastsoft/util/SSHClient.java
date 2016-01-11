package cn.com.eastsoft.util;

import cn.com.eastsoft.ui.MainJFrame;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by baolei on 2015/11/12.
 */
public class SSHClient {
    private Session session = null;
    private ChannelExec openChannel = null;
    private String user;
    private String pwd;
    private String host;
    private int port;

    private static SSHClient instance;

    public static SSHClient getInstance(String user, String pwd, String host, int port) {
        if (user != null && pwd != null && host != null && port != 0) {
            if (null == instance) {
                instance = new SSHClient(user, pwd, host, port);
            } else if (!user.equals(instance.getUser()) || !pwd.equals(instance.getPwd())
                    || !host.equals(instance.getHost()) || port != instance.getPort()) {
                MainJFrame.showMssageln("SSH连接设置发生变化，重新创建SSH客户端");
                instance.closeConnect();
                instance = new SSHClient(user, pwd, host, port);
            }
        }
        return instance;
    }

    public SSHClient(String user, String pwd, String host, int port) {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(pwd);
            session.connect();
            MainJFrame.showMssageln("SSH session创建成功");
            this.user = user;
            this.pwd = pwd;
            this.host = host;
            this.port = port;
        } catch (Exception e) {
            MainJFrame.showMssageln("ssh 连接失败，请检查参数设置和网络连接！");
            e.printStackTrace();
        }
    }


    public boolean closeConnect() {
        if (openChannel != null && !openChannel.isClosed()) {
            openChannel.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        MainJFrame.showMssageln("SSH session连接断开");
        return true;
    }

    public String executeCmd(String command) {
        StringBuffer result = new StringBuffer();
        try {
            openChannel = (ChannelExec) session.openChannel("exec");
            openChannel.setCommand(command);
            openChannel.setInputStream(null);
//            int exitStatus = openChannel.getExitStatus();
//            System.out.println(exitStatus);
//            InputStream in = openChannel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(openChannel.getInputStream()));
            openChannel.connect();
            String buf = null;
            while ((buf = reader.readLine()) != null) {
//                result+= new String(buf.getBytes("gbk"),"UTF-8")+"    \r\n";
//                result.append(buf.getBytes());
                result.append(buf + "\n");
//                System.out.println(buf);
            }
//            in.close();
            reader.close();
        } catch (Exception e) {
            System.out.println(command + "命令执行异常！");
            e.printStackTrace();
        } finally {
            openChannel.disconnect();
        }
        return result.toString();
    }

    public String executeCmd(String command,Map<String,String> error) {
        StringBuffer result = new StringBuffer();
        try {
            openChannel = (ChannelExec) session.openChannel("exec");
            openChannel.setCommand(command);
            openChannel.setInputStream(null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(openChannel.getInputStream()));
            BufferedReader errorMsgReader = new BufferedReader(new InputStreamReader(openChannel.getErrStream()));
            openChannel.connect();
            String buf = null;
            StringBuffer sb = new StringBuffer();
            while ((buf = errorMsgReader.readLine()) != null) {
                sb.append(buf+"\n");
            }
            error.put("errorMsg",sb.toString());
            while ((buf = reader.readLine()) != null) {
                result.append(buf + "\n");
            }
            reader.close();
        } catch (Exception e) {
            System.out.println(command + "命令执行异常！");
            e.printStackTrace();
        } finally {
            openChannel.disconnect();
        }
        return result.toString();
    }

    public int executtingCmd(String command) {
        StringBuffer result = new StringBuffer();
        int exitStatus = 0;
        try {
            openChannel = (ChannelExec) session.openChannel("exec");
            openChannel.setCommand(command);
            openChannel.setInputStream(null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(openChannel.getInputStream()));
            openChannel.connect();
            String buf = null;
            while ((buf = reader.readLine()) != null) {
//                result+= new String(buf.getBytes("gbk"),"UTF-8")+"    \r\n";
//                result.append(buf+"\n");
                MainJFrame.showMssageln(buf);
            }
            reader.close();
            if (openChannel.isClosed()) {
                exitStatus = openChannel.getExitStatus();
            }
        } catch (Exception e) {
            System.out.println(command + "命令执行异常！");
            e.printStackTrace();
        }
        return exitStatus;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
