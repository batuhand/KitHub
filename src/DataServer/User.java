/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rivalzka
 */
public class User {

    static final int NICK = 0;
    static final int NAME = 1;
    static final int SURNAME = 2;
    static final int PASSWORD = 3;
    static ArrayList<User> list = new ArrayList<>();

    String userData[] = new String[4];

    public User(String userData[]) {
        this.userData = userData;
    }

    public static boolean add(String userData[]) {
        if(list.isEmpty())list.add( new User(userData));
        for (User user : list) {
            if (user.userData[NICK].toLowerCase().equals(userData[NICK].toLowerCase())) {
                return false;
            }
        }
        User user = new User(userData);
        list.add(user);
        return true;
    }

    public static boolean controlLogin(String data[]) {// data : 0 nick 1 password
        User user = User.get(NICK, data[0]);
        if (user == null) {
            return false;
        }
        if (user.userData[PASSWORD].equals(data[1])) {
            return true;
        }
        return false;
    }

    public static User get(int key, String value) {
        for (User user : list) {
            if (user.userData[key].equals(value)) {
                return user;
            }
        }
        return null;
    }
    
    public static void saveAllData(){
        try {
            FileRW frw=new FileRW("\\data\\user.bin");
            frw.setData(stackAllDataForFile());
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void readAllData(){
        try {
            FileRW frw=new FileRW("\\data\\user.bin");
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
            end = allData.indexOf(",", begin + 1);
            if (end < 0) {
                break;
            }

            String parse = allData.substring(begin + 1, end);
            dizi[count] = parse;

            begin = end;
            count++;
            if(count==4){
                list.add(new User(dizi));
                count=0;
                dizi=new String[4];
            }
            if (begin < 0) {
                break;
            }
        }
    }

    private static String stackAllDataForFile() {
        String data = ",";
        for (User user : list) {
            for (String str : user.userData) {
                data += str + ",";
            }
        }
        return data;
    }

}
