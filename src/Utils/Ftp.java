package Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.SocketException;

/**
 *
 * @author Rivalzka
 */
public class Ftp extends Thread {

    public static int lastPort;
    int port;
    String ip;
    String path;
    boolean getFile;

    public Ftp(int port, String ip, String path, boolean getFile) {
        this.port = port;
        this.ip = ip;
        this.path = path;
        this.getFile = getFile;
    }

    @Override
    public void run() {
        if (getFile) {
            try {
                ServerSocket sockServer = new ServerSocket();
                sockServer.bind(new InetSocketAddress(ip, port));
                Socket sock = sockServer.accept();
                MyFileTransferProcessor ftp = new MyFileTransferProcessor(sock);
                ftp.receiveFile(path);
//                sockServer.close();
                System.out.println("serversocket Close : " + port);
            } catch (IOException ex) {
                Logger.getLogger(Ftp.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("serversocket Close catch : " + port);

            }
        } else {
            try {
                Socket sock = new Socket(ip, port);
                MyFileTransferProcessor ftp = new MyFileTransferProcessor(sock);
                ftp.sendFile(new File(path));
                sock.close();
            } catch (IOException ex) {
                Logger.getLogger(Ftp.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("this Port: " + port);
            }

        }

    }

    class MyFileTransferProcessor {

        Socket socket;
        InputStream is;
        FileOutputStream fos;
        BufferedOutputStream bos;
        int bufferSize;

        public MyFileTransferProcessor(Socket client) {
            socket = client;
            is = null;
            fos = null;
            bos = null;
            bufferSize = 0;

        }

        public void receiveFile(String filePath) throws FileNotFoundException, SocketException, IOException {
            is = socket.getInputStream();
            bufferSize = socket.getReceiveBufferSize();
            fos = new FileOutputStream(filePath);
            bos = new BufferedOutputStream(fos);
            byte[] bytes = new byte[bufferSize];
            int count;
            while ((count = is.read(bytes)) >= 0) {
                bos.write(bytes, 0, count);
            }
            bos.close();
            fos.close();
//            is.close();
        }

        public void sendFile(File file) throws FileNotFoundException, IOException {
            FileInputStream fis;
            BufferedInputStream bis;
            BufferedOutputStream out;
            byte[] buffer = new byte[8192];
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            out = new BufferedOutputStream(socket.getOutputStream());
            int count;
            while ((count = bis.read(buffer)) > 0) {
                out.write(buffer, 0, count);

            }
            out.close();
            fis.close();
            bis.close();
        }
    }
}
