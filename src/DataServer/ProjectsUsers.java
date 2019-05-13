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

public class ProjectsUsers {

    public static final int PROJECT_ID = 0;
    public static final int NICK = 1;
    public static final int DIRECTORY_LOCATION = 2;
    public static ArrayList<ProjectsUsers> list = new ArrayList<>();
    public static int len = 3;

    public ProjectsUsers(String dizi[]) {
        data = new String[len];
        if (dizi.length == 2) {
            data[0] = dizi[0];
            data[1] = dizi[1];
            data[2] = "";
        } else if (dizi.length == 3) {
            data[0] = dizi[0];
            data[1] = dizi[1];
            data[2] = dizi[2];
        }

    }

    public String data[];

    //TODO
    public static String getDırectoryLoc(String name, String projectID) {
        String result = "NOK";
        for (ProjectsUsers user : ProjectsUsers.list) {
            if (user.data[NICK].equals(name)) {
                if (user.data[PROJECT_ID].equals(projectID)) {
                    if (user.data[DIRECTORY_LOCATION].isEmpty()) {
                        break;
                    }
                    return user.data[DIRECTORY_LOCATION];
                }
            }
        }
        return result;
    }

    public static void setDırectoryLoc(String name, String projectID, String location) {
        String result = "NOK";
        for (ProjectsUsers user : ProjectsUsers.list) {
            if (user.data[NICK].equals(name)) {
                if (user.data[PROJECT_ID].equals(projectID)) {
                    user.data[DIRECTORY_LOCATION] = location;
                    break;
                }
            }
        }
    }

    public static boolean isJoinable(String nick, String id) {
        for (ProjectsUsers user : list) {
            if (user.data[NICK].equals(nick) && user.data[PROJECT_ID].equals(id)) {
                return false;
            }
        }
        return true;
    }

    public static void saveAllData() {
        try {
            FileRW frw = new FileRW("\\data\\projectsusers.bin");
            frw.setData(stackAllDataForFile());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void readAllData() {
        try {
            FileRW frw = new FileRW("\\data\\projectsusers.bin");
            parseStringFromFile(frw.getData());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void parseStringFromFile(String allData) {
        //TODO
        int begin = 0, end = 0, count = 0;
        String dizi[] = new String[4];
        while (true) {
            end = allData.indexOf(ServerThread.sepValue, begin + 1);
            if (end < 0) {
                break;
            }

            String parse = allData.substring(begin + ServerThread.sepValue.length(), end);
            dizi[count] = parse;

            begin = end;
            count++;
            if (count == len) {
                list.add(new ProjectsUsers(dizi));
                count = 0;
                dizi = new String[4];
            }
            if (begin < 0) {
                break;
            }
        }
    }

    private static String stackAllDataForFile() {
        String data = ServerThread.sepValue;
        for (ProjectsUsers user : list) {
            for (String str : user.data) {
                data += str + ServerThread.sepValue;
            }
        }
        return data;
    }
    public static void main(String[] args) {
        String aa="1";
        String bb="n";
        System.out.println(""+aa.compareTo(bb));
    }
}
