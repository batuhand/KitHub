/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataServer;

import Server.ServerThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rivalzka
 */
public class Task {

    public static final int DATE = 0;
    public static final int WHO = 1;
    public static final int ARC = 2;
    public static final int STATE = 3;
    public static final int PROID = 4;

    public static final String STATE_VOID = "NOK";
    public static final String STATE_AWAITING = "WAI";
    public static final String STATE_FINISH = "FIN";

    public static ArrayList<Task> list = new ArrayList<>();

    public Task() {
    }
    public Task(String[] dizi) {
        data=dizi;
    }
    public String data[] = new String[5];
    
    public static void finishTheTask(String []dizi){
        for (Task user : list) {
            if (user.data[DATE].equals(dizi[1])) {
                user.data[STATE]=STATE_FINISH;
            }

        }
    }
    
    public static void takeTheTask(String []dizi){
        for (Task user : list) {
            if (user.data[DATE].equals(dizi[1])) {
                user.data[STATE]=STATE_AWAITING;
                user.data[WHO]=dizi[0];
            }

        }
    }
    
    public static String stackAllDataForFile(String pro) {
        String data = ServerThread.sepValue;
        for (Task user : list) {
            if (user.data[PROID].equals(pro)) {
                for (String str : user.data) {
                    data += str + ServerThread.sepValue;
                }
            }
        }
        return data;
    }

    public static void create(String[] dizi, String date) {
        Task task = new Task();
        task.data[DATE] = date;
        task.data[ARC] = dizi[0];
        task.data[PROID] = dizi[1];
        task.data[STATE] = STATE_VOID;
        task.data[WHO] = "Nok";
        list.add(task);
    }

    public static void saveAllData() {
        try {
            FileRW frw = new FileRW("\\data\\Task.bin");
            frw.setData(stackAllDataForFile());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void readAllData() {
        try {
            FileRW frw = new FileRW("\\data\\Task.bin");
            parseStringFromFile(frw.getData());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void parseStringFromFile(String allData) {
        //TODO
        int begin = 0, end = 0, count = 0;
        String dizi[] = new String[5];
        while (true) {
            end = allData.indexOf(ServerThread.sepValue, begin + ServerThread.sepValue.length());
            if (end < 0) {
                break;
            }

            String parse = allData.substring(begin + ServerThread.sepValue.length(), end);
            dizi[count] = parse;

            begin = end;
            count++;
            if (count == 5) {
                list.add(new Task(dizi));
                count = 0;
                dizi = new String[5];
            }
            if (begin < 0) {
                break;
            }
        }
    }

    public static String stackAllDataForFile() {
        String data = ServerThread.sepValue;
        for (Task user : list) {
            for (String str : user.data) {
                data += str + ServerThread.sepValue;
            }
        }
        return data;
    }
}
