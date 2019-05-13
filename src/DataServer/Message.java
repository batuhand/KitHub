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
public class Message {

    public static final int FROM = 0;
    public static final int DATE = 1;
    public static final int MSG = 2;
    public static final int PROJECTID = 3;
    public static ArrayList<Message> list = new ArrayList<>();

    public Message(String[] dizi) {
        data = dizi;
    }
    public String data[] = new String[4];

    public static String stackAllDataForFile(String project) {
        String data = ServerThread.sepValue;
        for (Message user : Message.list) {
            if (user.data[Message.PROJECTID].equals(project)) {
                for (String str : user.data) {
                    data += str + ServerThread.sepValue;
                }
            }

        }
        return data;
    }

    public static void saveAllData() {
        try {
            FileRW frw = new FileRW("\\data\\Message.bin");
            frw.setData(stackAllDataForFile());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void readAllData() {
        try {
            FileRW frw = new FileRW("\\data\\Message.bin");
            parseStringFromFile(frw.getData());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void parseStringFromFile(String allData) {
        //TODO
        int begin = 0, end = 0, count = 0;
        String dizi[] = new String[4];
        while (true) {
            end = allData.indexOf(ServerThread.sepValue, begin + ServerThread.sepValue.length());
            if (end < 0) {
                break;
            }

            String parse = allData.substring(begin + ServerThread.sepValue.length(), end);
            dizi[count] = parse;

            begin = end;
            count++;
            if (count == 4) {
                list.add(new Message(dizi));
                TimeToLast tt = TimeToLast.getMsg(dizi[3]);
                if (tt == null) {
                    TimeToLast.addMsg(dizi[3], dizi[1]);
                } else {
                    tt.data[1] = dizi[3];
                    tt.data[2] = dizi[1];
                }
                count = 0;
                dizi = new String[4];
            }
            if (begin < 0) {
                break;
            }
        }
    }

    public static String stackAllDataForFile() {
        String data = ServerThread.sepValue;
        for (Message user : Message.list) {
            for (String str : user.data) {
                data += str + ServerThread.sepValue;
            }
        }
        return data;
    }

}
