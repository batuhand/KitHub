/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import DataServer.TimeToLast;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rivalzka
 */
public class Notify extends Thread {

    static long time;
    static boolean exit = false;

    @Override
    public void run() {
        time = System.currentTimeMillis();
        Client.frame.freshMsg();
        Client.frame.getOnline();
        Client.frame.freshTasks();
        while (!exit) {
            if (System.currentTimeMillis() > time) {
                ArrayList<String> projeler = Client.frame.getProjectsList();
                for (String pro : projeler) {
                    String pdate = Client.cs.sendMsg("WPD " + pro);
                    if (!pdate.equals("NOK") && !pdate.equals("K")) {
                        for (TimeToLast tt : TimeToLast.list) {
                            if (tt.data[TimeToLast.TYPE].equals(TimeToLast.TYPE_PROJECT)) {
                                if (tt.data[TimeToLast.PROJE].equals(pro)) {
                                    if (pdate.compareTo(tt.data[TimeToLast.DATE]) > 0) {
                                        Client.myTray.infoMessage("PROJE GÜNCELLEME", pro + " projesi için " + pdate + " tarihli yeni bir güncelleme mevcuttur.");
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    String mdate = Client.cs.sendMsg("WMD " + pro);
                    if (!mdate.equals("NOK")) {
                        boolean find = false;
                        for (TimeToLast tt : TimeToLast.list) {
                            if (tt.data[TimeToLast.TYPE].equals(TimeToLast.TYPE_MSG)) {
                                find = true;
                                if (tt.data[TimeToLast.PROJE].equals(pro)) {
                                    if (mdate.compareTo(tt.data[TimeToLast.DATE]) > 0) {
                                        Client.myTray.infoMessage("MESAJ", pro + " projesinde yeni mesajınız var.");
                                        if (pro.equals(Client.frame.projectName)) {
                                            Client.frame.freshMsg();
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                        if (!find) {
                            Client.myTray.infoMessage("MESAJ", pro + " projesinde yeni mesajınız var.");
                            if (pro.equals(Client.frame.projectName)) {
                                Client.frame.freshMsg();
                            }
                        }
                    }

                    String tdate = Client.cs.sendMsg("WTD " + pro);
                    if (!tdate.equals("NOK")) {
                        StringTokenizer st = new StringTokenizer(tdate);
                        int myTask = Integer.parseInt(st.nextToken());
                        int taskList = Integer.parseInt(st.nextToken());
                        int doneTask = Integer.parseInt(st.nextToken());

                        int myTaskX = Client.frame.getMyTask(), taskListX = Client.frame.getTaskList(), doneTaskX = Client.frame.getDoneTask();
                        if (taskList > taskListX && pro.equals(Client.frame.projectName)) {
                            Client.myTray.infoMessage("GÖREVLER", pro + " projesine yeni bir eklendi.");
                            Client.frame.freshTasks();
                        }
                        if (taskList < taskListX && pro.equals(Client.frame.projectName)) {
                            if (myTask == myTaskX) {
                                Client.myTray.infoMessage("GÖREVLER", pro + " projesinde görev listesinde değişiklik oldu.");
                            }
                            Client.frame.freshTasks();
                        }

                        if (doneTask > doneTaskX && pro.equals(Client.frame.projectName)) {
                            Client.myTray.infoMessage("GÖREVLER", pro + " projesinde bir görev daha bitirildi.");
                            Client.frame.freshTasks();
                        }

                        if (myTask != myTaskX && pro.equals(Client.frame.projectName)) {
                            Client.frame.freshTasks();
                        }
                    }
                }
                Client.frame.getOnline();
            }

            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Notify.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        String as = "as";
        String bs = "bs";
        System.out.println(as.compareTo(bs));
    }
}
