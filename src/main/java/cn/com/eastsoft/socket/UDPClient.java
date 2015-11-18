package cn.com.eastsoft.socket; /**
 * UDPClient
 *
 * @author Winty wintys@gmail.com
 * @version 2008-12-15
 */

import cn.com.eastsoft.ui.MainJFrame;

import java.io.*;
import java.net.*;

public class UDPClient {

    private static UDPClient instance;
    private DatagramSocket client;
    private DatagramPacket sendPacket;
    private DatagramPacket recvPacket;
    private InetSocketAddress inetSocketAddress;

    private String IP;
    private int port;

    private UDPClient() {
        try {
            client = new DatagramSocket();
            client.setSoTimeout(5000);
        } catch (SocketException e) {
            System.out.println("create udp client failed");
            e.printStackTrace();
        }
    }

    public static UDPClient getInstance() {
        if (instance == null) {
            instance = new UDPClient();
        }
        return instance;
    }

    public byte[] sendPacket(String IP, int port, byte[] sendBuf){

        byte[] receByte = null;
        try {
            if ((this.IP == null || this.port == 0) || (!(this.IP.equals(IP) && this.port == port))) {
                this.IP = IP;
                this.port = port;
                inetSocketAddress = new InetSocketAddress(IP, port);
            }
            if (client.isClosed()) {
                client = new DatagramSocket();
            }

//		byte[] sendBuf = sendStr.getBytes();
            sendPacket = new DatagramPacket(sendBuf, sendBuf.length, inetSocketAddress);

            client.send(sendPacket);
            System.out.println("sendPacket success,and waiting for receive message");

            byte[] recvBuf = new byte[256];
            recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
            client.receive(recvPacket);
            receByte = recvPacket.getData();
            System.out.println("收到数据:");
        } catch (Exception e) {
            if(e.getMessage().contains("timed out")){
                MainJFrame.showMssageln("报文传输超时...");
            }else {
                e.printStackTrace();
            }
        } finally {
            return receByte;
        }
    }

    public void close() {
        client.close();
    }
}
