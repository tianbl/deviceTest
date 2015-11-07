package test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.io.Udp;

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
        try {
            udpClient();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
