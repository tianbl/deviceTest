package test;

import cn.com.eastsoft.action.plMessage.ExtenderRes;
import cn.com.eastsoft.action.plMessage.ReqMessage;
import cn.com.eastsoft.action.plMessage.ResMessage;
import cn.com.eastsoft.sql.ServerInfo;
import cn.com.eastsoft.sql.serverInfoImpl.MysqlOperation;
import cn.com.eastsoft.ui.Para;
import cn.com.eastsoft.util.Ping;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.omg.CORBA.SystemException;

import java.io.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tianbaolei on 15-11-5.
 */
public class MainTest {
    public static String regex;
    public static String sn;
    public static String mac;
    public static String ip;
    public static String[] labelInfoKey;
    public static void main(String[] args){
//        regexTest();
//        readProperties();
        sqlTest();
    }

    public static void sqlTest(){
//        ServerInfo serverInfo = new MysqlOperation();
    }

    public static void regexTest(){

        regex = "(IP[:]{0,1})" +
                "(((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?))" +
                "([' ']{0,4}MAC[:]{0,1})([1-9A-F]{12})([SN/' '\\r'\\n']{1,8})(\\d{6})$";
        String qrcodeinfo = "IP:191.169.1.108MAC:AABBCCDDEEFF S/N123456";
        Para.setRex(0);
        Pattern pattern = Pattern.compile(Para.labelInfoRex);
        Matcher matcher = pattern.matcher(qrcodeinfo);
        matcher.find();
        int num = matcher.groupCount();
        System.out.println("匹配数："+num);

        for(int i=1;i<=num;i++){
            System.out.println(i+"："+matcher.group(i));
        }
//        map.put("sn", matcher.group(2));
//        map.put("gid", matcher.group(4));
//        map.put("pwd", matcher.group(6));
    }

    public static void reqTest() throws Exception {

        String data = "9F 91 1E 00 00 01 04 C0 A8 01 FD " +
                "06 00 10 2C 5F 12 1C 0F 45 61 73 74 73 6F 66 74 5F 30 30 30 30 30 30 5F 1F";
        byte[] rece = ReqMessage.hexStringToBytes(data.replace(" ",""));
        ExtenderRes res = new ExtenderRes(rece);
        System.out.println(res.getIp());
        System.out.println(res.getMac());
        System.out.println(res.getSsid());

    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static void readProperties(){
        System.out.println("starting...");
        try {
            String path = MainTest.class.getClassLoader().
                    getResource("resources.properties").getPath();
            System.out.println(path);
            File file = new File(path);
            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            Properties properties = new Properties();
            properties.load(inputStream);
            String[] key = properties.getProperty("powerlineAdapter.labelInfoKey").split("-");
            Para.setRex(0);
            System.out.println("labelinfo：" + Para.labelInfoRex);
            for (String str:key){
                System.out.println(str+"："+ Para.mapRex.get(str));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
