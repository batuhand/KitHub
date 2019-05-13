package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionToServer extends Thread {

    int port = 8080;
    String IP = "127.0.0.1";
    Socket sock;
    PrintWriter out;
    BufferedReader in;
    
    ConnectionToServer(String ip) throws IOException {
        IP=ip;
        sock = new Socket(IP, 8080);
        out = new PrintWriter(sock.getOutputStream());
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }
    
    synchronized public String sendMsg(String msg) {
        out.write(msg);
        out.write("\n");
        out.flush();
        System.out.println("Client Send: "+msg);
        return receiveMsg();
    }
    
    synchronized public String sendMsg(String msg,int i) {
        Notify.time=System.currentTimeMillis()+2000;
        return sendMsg(msg);
    }
    
    public String receiveMsg(){
        String msg;
        try {
            msg=in.readLine();
            System.out.println("Client receive msg:"+msg);
            return msg;
        } catch (IOException ex) {
            System.out.println("ex receive msg: "+ex);
            return "NOK";
        }
    }
    void exit() throws IOException {
        sendMsg("EXT ,aaa,");
    }
}
