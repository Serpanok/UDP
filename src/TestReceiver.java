import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class TestReceiver {
    public static void main(String[] args) {
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(Integer.parseInt(args[0]));
        } catch (SocketException e) {
            e.printStackTrace();
        }
        byte[] buffer = new byte[100];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while(true){
            try {
                System.out.println(datagramSocket.getReceiveBufferSize());
                datagramSocket.receive(packet);
                byte[] buff = packet.getData();
                int packetLength = packet.getLength();
                System.out.println(packet.getPort());
                for(int i = 0; i < packetLength; ++i) {
                    if (buff[i] != 0)
                        System.out.println(buff[i]);
                }
                System.out.println();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
