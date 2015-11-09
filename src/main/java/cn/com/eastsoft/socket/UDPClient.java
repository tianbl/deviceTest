package cn.com.eastsoft.socket; /**
 *UDPClient
 *@author Winty wintys@gmail.com
 *@version 2008-12-15
 */
import java.io.*;
import java.net.*;

public class UDPClient{

	private static UDPClient instance;
	private DatagramSocket client;
	private DatagramPacket sendPacket;
	private DatagramPacket recvPacket;
	private InetSocketAddress inetSocketAddress;

	private String IP;
	private int port;

	private UDPClient(){
		try {
			client = new DatagramSocket();
		} catch (SocketException e) {
			System.out.println("create udp client failed");
			e.printStackTrace();
		}
	}

	public static UDPClient getInstance(){
		if(instance==null){
			instance = new UDPClient();
		}
		return instance;
	}

	public String sendPacket(String IP,int port,String sendStr) throws IOException {

		if((this.IP==null||this.port==0)||(!(this.IP.equals(IP)&&this.port==port))){
			this.IP = IP;
			this.port = port;
			inetSocketAddress = new InetSocketAddress(IP,port);
		}

		byte[] sendBuf = sendStr.getBytes();
		sendPacket = new DatagramPacket(sendBuf,sendBuf.length,inetSocketAddress);

		client.send(sendPacket);
		System.out.println("sendPacket success,and waiting for receive message");

		byte[] recvBuf = new byte[100];
		recvPacket = new DatagramPacket(recvBuf , recvBuf.length);
		client.receive(recvPacket);
		String recvStr = new String(recvPacket.getData() , 0 ,recvPacket.getLength());
		System.out.println("收到数据:" + recvStr);
		client.close();
		return null;
	}


	public static void main(String[] args)throws IOException{
		UDPClient.getInstance().sendPacket("129.1.18.189", 8088, "13412312341");
//		DatagramSocket client = new DatagramSocket();
//
//		String sendStr = "Hello! I'm Client";
//		byte[] sendBuf;
//		sendBuf = sendStr.getBytes();
//		InetAddress addr = InetAddress.getByName("129.1.18.189");
//		int port = 6060;
//		InetSocketAddress inetSocketAddress = new InetSocketAddress("129.1.18.189",8080);
//		DatagramPacket sendPacket = new DatagramPacket(sendBuf ,
//				sendBuf.length , inetSocketAddress);
//
//		client.send(sendPacket);
//
//		byte[] recvBuf = new byte[100];
//		DatagramPacket recvPacket
//			= new DatagramPacket(recvBuf , recvBuf.length);
//		client.receive(recvPacket);
//		String recvStr = new String(recvPacket.getData() , 0 ,recvPacket.getLength());
//		System.out.println("收到数据:" + recvStr);
//
//		int count = 0;
//		while (true){
//			System.out.println("第"+(++count)+"次接受udp");
//			client.receive(recvPacket);
//			recvStr = new String(recvPacket.getData() , 0 ,recvPacket.getLength());
//			System.out.println("收到数据:" + recvStr+"\n数据长度："+recvPacket.getData().length);
//		}
////		client.close();
	}
}
