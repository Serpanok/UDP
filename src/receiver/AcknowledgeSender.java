package receiver;

import concurrentutils.Channel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;


public class AcknowledgeSender {

    private final int port;
    private Channel channel;
    private DatagramSocket datagramSocket;
    private DatagramPacket packet;
    private boolean isRunning = false;
    private Thread sender = new Thread(new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                Integer index = (Integer)channel.get();
                byte[] buffer = ByteBuffer.allocate(4).putInt(index).array();
                packet.setData(buffer);
                try {
                    datagramSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            datagramSocket.close();
        }
    });

    public AcknowledgeSender(DatagramSocket datagramSocket, int port){
        channel = new Channel();
        this.datagramSocket = datagramSocket;
        this.port = port;
    }

    public void push(Integer index){
        channel.put(index);
    }

    public void init(InetAddress inetAddress){
        packet = new DatagramPacket(new byte[4], 4,inetAddress, port);
        isRunning = true;
        sender.start();
    }

    public void stop(){
        isRunning = false;
    }
}
