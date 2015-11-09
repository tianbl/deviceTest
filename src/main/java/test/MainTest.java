package test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.io.Udp;
import cn.com.eastsoft.action.plMessage.ReqMessage;
import cn.com.eastsoft.action.plMessage.ResMessage;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by tianbaolei on 15-11-5.
 */
public class MainTest {
    public static void main(String[] args){
//        try {
//            udpClient();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        message();
        getmessageByte();
    }

    public static void message(){
        String mac = "03 7D 00 00 02 00 C0 DC 6A FF FF FE 01 00 C0 DC " +
                "6A 00 07 FA 76 65 72 5F 30 32 2D 30 39 2D 30 30 " +
                "2D 30 31 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 31 34 30 32 32 34 32 31 00 00 00 00 " +
                "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 76 65 72 5F 30 30 2D 30 30 2D 31 34 " +
                "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " +
                "00 00 00 00 E1 03 00 00 00 00 00 00 00 00 00 00";
        String sn = "11 18 00 31 32 33 34 35 36 37 38 39 30 31 32 33 " +
                "34 35 36 37 38 39 30 31 32 33 34";
        String dekey = "1D 08 00 56 31 41 57 49 39 59 44";
        String dak = "20 10 00 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 50";
        String tm = dak.replace(" ", "");
        System.out.println(tm);

        byte[] bytes = hexStringToBytes(tm);
//        System.out.println(parseByte2HexStr(bytes));
        ResMessage resMessage = new ResMessage(bytes);
        System.out.println("type=="+resMessage.getType());
        System.out.println("leng=="+resMessage.getLength());
        System.out.println(" mac=="+resMessage.getMAC());
        System.out.println("  sn=="+resMessage.getSN());
        System.out.println("dkey=="+resMessage.getD_KEY());
        System.out.println(" dak=="+resMessage.getDAK());
    }

    public static void getmessageByte(){
        ReqMessage reqMessage = new ReqMessage();
//        reqMessage.setType("0E");
//        reqMessage.setContentlen(6);
//        reqMessage.setContent("C0DC6AFFFFFE");

        reqMessage.setType("0F");
        reqMessage.setContentlen(24);
        reqMessage.setContent("123456789012345678901234");

        reqMessage.setType("1E");
        reqMessage.setContentlen(16);
        reqMessage.setContent("ABCDEFGHIJKLMNOP");

        byte[] bytes = reqMessage.getMessage();
        String sn = "1E 10 00 41 42 43 44 45 46 47 48 49 4A 4B 4C 4D 4E 4F 50";
        sn = sn.replace(" ","");
        System.out.println(parseByte2HexStr(bytes));
        System.out.println(sn);

        System.out.println(sn.equals(parseByte2HexStr(bytes)));
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
    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
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

    public static void udpClient() throws UnknownHostException {
        ActorSystem mySystem = ActorSystem.create("mySystem");
        ActorRef udp = Udp.get(mySystem).getManager();

//        InetAddress inetAddress = InetAddress.getByAddress();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("129.1.18.189",8080);
//        Props props = Props.create(Listener.class,udp);
//        ActorRef actorRef = mySystem.actorOf(props,"sender");
//        actorRef.tell("12312312", actorRef);

//        Props props = Props.create(Connected.class,inetSocketAddress);
//        ActorRef actorRef = mySystem.actorOf(props,"sender");

//        Props props = Props.create(SimpleSender.class,inetSocketAddress);
//        ActorRef actorRef = mySystem.actorOf(props,"sender");

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
            System.out.println("jdbc.driver"+properties.getProperty("jdbc.driver"));
            System.out.println("regex="+properties.getProperty("regex1"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
