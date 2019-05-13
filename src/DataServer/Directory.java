/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataServer;

import Client.Client;
import Server.ServerThread;
import Utils.Ftp;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Rivalzka
 */
public class Directory {
    
    public String name;
    public ArrayList<String> files=new ArrayList<>();
    public ArrayList<Directory> dirs=new ArrayList<>();
    
    public static String location;
    
    public Directory(String name){
        this.name=name;
    }
    
    public void folders(File folder, Directory dir) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    Directory dir2 = new Directory(dir.name + "\\" + f.getName());
                    dir.dirs.add(dir2);
                    folders(f, dir2);
                } else {
                    dir.files.add(dir.name + "\\" + f.getName());
                }
            }
        }
    }

    public String filesFormat(Directory dir) {
        Queue q = new Queue();
        q.enqueue(dir);
        String str = ServerThread.sepFormat;
        while (!q.isEmpty()) {
            Directory tmp = q.dequeue();
            str += "*" + tmp.name + ServerThread.sepFormat;
            System.out.println("*" + tmp.name);
            for (Directory d : tmp.dirs) {
                q.enqueue(d);
            }
            for (String s : tmp.files) {
                str += s + ServerThread.sepFormat;
                System.out.println("" + s);
            }
        }

        return str;
    }
    
    public String transferTheFiles(String IP) {
        String msg = "";
        while (true) {
            msg = Client.cs.receiveMsg();
            if (!msg.startsWith("GET")) {
                return "";
            }
            if (msg.equals("NOK")) {
                return msg;
            }

            String dizi[] = parseDataToArray(2, msg.substring(4));
            int lastPort = Integer.parseInt(dizi[0]);
            Ftp.lastPort=lastPort+1;
            String path = location + dizi[1];
            System.out.println("sending file: " + path);
//            File myFile = new File(path);
            new Thread(new Ftp(lastPort, IP, path, false)).start();
        }
    }
    String[] parseDataToArray(int len, String data) {
        int begin = 0, end = 0, count = 0;
        String dizi[] = new String[len];
        while (true) {
            end = data.indexOf(ServerThread.sepValue, begin + ServerThread.sepValue.length());
            if (end < 0) {
                break;
            }

            String parse = data.substring(begin + ServerThread.sepValue.length(), end);
            dizi[count] = parse;

            begin = end;
            count++;
            if (begin < 0) {
                break;
            }
        }
        return dizi;
    }
    class Queue {

        ArrayList<Directory> list = new ArrayList<>();
        int count = 0;

        void enqueue(Directory d) {
            list.add(d);
        }

        Directory dequeue() {
            if (count >= list.size()) {
                return null;
            }
            return list.get(count++);
        }

        boolean isEmpty() {
            return count >= list.size();
        }
    }
}
