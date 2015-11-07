package test;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.*;
import akka.japi.Procedure;
import akka.util.ByteString;

import java.net.InetSocketAddress;

/**
 * Created by tianbaolei on 15-11-6.
 */
public class Listener extends UntypedActor {
    final ActorRef nextActor;

    public Listener(ActorRef nextActor) {
        this.nextActor = nextActor;

        // request creation of a bound listen socket
        final ActorRef mgr = Udp.get(getContext().system()).getManager();
        mgr.tell(UdpMessage.bind(getSelf(), new InetSocketAddress("129.1.18.189", 8088)),
                getSelf());
    }

    @Override
    public void onReceive(Object msg) {
        System.out.println("****onReceive**********");
        if (msg instanceof Udp.Bound) {
            final Udp.Bound b = (Udp.Bound) msg;
//            InetSocketAddress inetSocketAddress = new InetSocketAddress("129.1.18.189",8080);
//            getSender().tell(UdpMessage.send(ByteString.fromString("1221212"), inetSocketAddress),getSelf());
            getContext().become(ready(getSender()));
            System.out.println("success bounde 8088");
        } else unhandled(msg);
    }

    private Procedure<Object> ready(final ActorRef socket) {
        return new Procedure<Object>() {
            @Override
            public void apply(Object msg) throws Exception {

                System.out.println("****apply**********");


                if (msg instanceof Udp.Received) {

                    System.out.println("*****in apply Udp.Received"+msg);
                    final Udp.Received r = (Udp.Received) msg;
                    // echo server example: send back the data
                    socket.tell(UdpMessage.send(r.data(), r.sender()), getSelf());
                    // or do some processing and forward it on
                    // parse data etc., e.g. using PipelineStage
//                    final Object processed =
//                            nextActor.tell(processed, getSelf());

                } else if (msg.equals(UdpMessage.unbind())) {
                    socket.tell(msg, getSelf());

                } else if (msg instanceof Udp.Unbound) {
                    getContext().stop(getSelf());

                } else unhandled(msg);
            }
        };
    }
}