import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;


public class TestSender {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int port = Integer.parseInt(args[0]);
        InetAddress receiverAddress = null;
        DatagramSocket datagramSocket = null;
        try {
             receiverAddress = InetAddress.getByName(args[1]);
             datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            //e.printStackTrace();
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        byte[] buffer = "0123456789".getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, port);
        while(true) {
            try {
                String cons = in.nextLine();
                byte[] bytes = cons.getBytes();
                packet.setData(bytes);
                packet.setLength(bytes.length);
                if (datagramSocket != null) {
                    datagramSocket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
