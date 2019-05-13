package DataServer;

import Server.ServerThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProjectDb {

    public static final int ID = 0;
    public static final int NAME = 1;
    public static final int MANAGER = 2;
    public static final int FORMAT = 3; //dosya dizin dizinimi
    public static final int DATE = 4;
    public static ArrayList<ProjectDb> list = new ArrayList<>();
    public static int len=5;
    
    public ProjectDb(String dizi[], String date) {
        data= new String[len];
        data[ID] = dizi[1];
        data[MANAGER] = dizi[0];
        data[NAME] = dizi[1];
        data[FORMAT] = dizi[2];
        data[DATE] = date;

    }

    public ProjectDb(String dizi[]) {
        data= new String[len];
        data[ID] = dizi[0];
        data[NAME] = dizi[1];
        data[MANAGER] = dizi[2];
        data[FORMAT] = dizi[3];
        data[DATE] = dizi[4];
    }

    public String data[] ;

    //TODO
    public static String getProjectLists(String nick) {
        String listStr = ServerThread.sepValue;
        for (ProjectsUsers user : ProjectsUsers.list) {
            if (nick.equals(user.data[ProjectsUsers.NICK])) {
                for (ProjectDb pro : ProjectDb.list) {
                    if (user.data[ProjectsUsers.PROJECT_ID].equals(pro.data[ProjectDb.ID])) {
                        listStr += pro.data[ProjectDb.NAME] + ServerThread.sepValue;
                    }
                }
            }
        }
        return listStr;
    }

    public static void setProjectLists(String projects) {
        int begin = 0, end = 0, count = 0;
        ArrayList<String> dizi = new ArrayList<>();
        while (true) {
            end = projects.indexOf(ServerThread.sepValue, begin + ServerThread.sepValue.length());
            if (end < 0) {
                break;
            }
            String parse = projects.substring(begin + ServerThread.sepValue.length(), end);
            dizi.add(parse);
            begin = end;
        }
        while (count < dizi.size()) {
            ProjectDb.list.add(new ProjectDb(new String[]{"", dizi.get(count++), "", "", ""}));
        }

    }

    public static ProjectDb get(int key, String value) {
        for (ProjectDb user : list) {
            if (user.data[key].equals(value)) {
                return user;
            }
        }
        return null;
    }

    public static void saveAllData() {
        try {
            FileRW frw = new FileRW("\\data\\projects.bin");
            frw.setData(stackAllDataForFile());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void readAllData() {
        try {
            FileRW frw = new FileRW("\\data\\projects.bin");
            parseStringFromFile(frw.getData());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void parseStringFromFile(String allData) {
        //TODO
        int begin = 0, end = 0, count = 0;
        String dizi[] = new String[len];
        while (true) {
            end = allData.indexOf(ServerThread.sepValue, begin + ServerThread.sepValue.length());
            if (end < 0) {
                break;
            }

            String parse = allData.substring(begin + ServerThread.sepValue.length(), end);
            dizi[count] = parse;

            begin = end;
            count++;
            if (count == len) {
                list.add(new ProjectDb(dizi));
                count = 0;
                dizi = new String[len];
            }
            if (begin < 0) {
                break;
            }
        }
    }

    private static String stackAllDataForFile() {
        String data = ServerThread.sepValue;
        for (ProjectDb user : list) {
            for (String str : user.data) {
                data += str + ServerThread.sepValue;
            }
        }
        return data;
    }
}
