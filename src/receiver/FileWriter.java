package receiver;

import java.io.*;


public class FileWriter implements Runnable{

    private final String filePath;
    private FileOutputStream fileOutputStream;
    private final ReceiverSlidingWindow receiverSlidingWindow;
    private boolean isRunning = false;
    private int packetSize = 0;

    public FileWriter(String filePath, ReceiverSlidingWindow receiverSlidingWindow){
        this.receiverSlidingWindow = receiverSlidingWindow;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        String fileName = receiverSlidingWindow.getFileName();
        try {
            fileOutputStream = new FileOutputStream(new File(filePath+fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        isRunning = true;
        int id = 1;
        try {
            while(isRunning) {
                byte[] buffer = receiverSlidingWindow.get();
                if (packetSize == 0) {
                    packetSize = buffer.length;
                }
                fileOutputStream.write(buffer);
                //System.out.println("write "+id);
                //id++;
                if (buffer.length < packetSize) {
                    isRunning = false;
                }
            }
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
