package cn.com.eastsoft.util;

import cn.com.eastsoft.ui.MainJFrame;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by baolei on 2015/11/12.
 */
public class SSHClient {
    private Session session =null;
    private ChannelExec openChannel =null;
    private String user;
    private String pwd;
    private String host;
    private int port;

    public SSHClient(){
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(pwd);
            session.connect();
            MainJFrame.showMssageln("SSH session创建成功");
        }catch (Exception e){
            MainJFrame.showMssageln("ssh 连接失败！");
            e.printStackTrace();
        }
    }


    public boolean closeConnect(){
        if(openChannel!=null&&!openChannel.isClosed()){
            openChannel.disconnect();
        }
        if(session!=null&&session.isConnected()){
            session.disconnect();
        }
        MainJFrame.showMssageln("SSH session连接断开");
        return true;
    }

    public String executeCmd(String command){
        StringBuffer result = new StringBuffer();
        try{
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
        }catch (Exception e){
            System.out.println(command+"命令执行异常！");
            e.printStackTrace();
        }
        return result.toString();
    }

    public int executtingCmd(String command){
        StringBuffer result = new StringBuffer();
        int exitStatus = 0;
        try{
            openChannel = (ChannelExec) session.openChannel("exec");
            openChannel.setCommand(command);
            openChannel.setInputStream(null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(openChannel.getInputStream()));
            openChannel.connect();
            String buf = null;
            while ((buf = reader.readLine()) != null) {
//                result+= new String(buf.getBytes("gbk"),"UTF-8")+"    \r\n";
//                result.append(buf+"\n");
                System.out.println(buf);
            }
            reader.close();
            if (openChannel.isClosed()) {
                exitStatus = openChannel.getExitStatus();
            }
        }catch (Exception e){
            System.out.println(command+"命令执行异常！");
            e.printStackTrace();
        }
        return exitStatus;
    }
}
