/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import DataServer.Message;
import DataServer.ProjectDb;
import DataServer.ProjectsUsers;
import DataServer.Task;
import DataServer.User;
import Utils.Ftp;
import Utils.Md5ControlUnit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rivalzka
 */
public class ServerThread extends Thread {

    public static String sepValue = "$#%";
    public static String sepFormat = "&$#";
    ServerFrame frame;
    ServerSocket sockServer;
    String IP = "192.168.43.9";//192.168.137.223 10.96.132.192
//    String IP = "127.0.0.1";//192.168.137.223 10.96.132.192
    int port = 8080;
    Server serverMain;
    ArrayList<MyChannel> listChannels;

    ServerThread(Server serverMain) {
        this.serverMain = serverMain;
        listChannels = new ArrayList<>();
        Ftp.lastPort = port + 1;
    }

    @Override
    public void run() {
        frame = new ServerFrame(this);
        try {
            sockServer = new ServerSocket();
            sockServer.bind(new InetSocketAddress(IP, port));
            while (true) {
                Socket newConnection = sockServer.accept();
                System.out.println("baglandı");
                MyChannel channel = new MyChannel(newConnection);
                listChannels.add(channel);
                new Thread(channel).start();
                frame.reList();
            }
        } catch (IOException ex) {
            frame.settext(ex.getMessage());
        }

    }

    class MyChannel extends Thread {

        Socket sock;
        PrintWriter out;
        BufferedReader in;
        String nick = "Anonim";
        boolean exit = false;
        String uupProject = "NOK";

        public MyChannel(Socket sock) {
            this.sock = sock;
            try {
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                out = new PrintWriter(sock.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void run() {
            while (true) {
                try {
                    String msg = in.readLine();
                    System.out.println("Server receive:" + msg);
                    parseData(msg);
                    if (exit) {
                        break;
                    }
                } catch (IOException ex) {
                    ext();
                    break;
                }
            }
        }

        //msg = konu+data
        void parseData(String msg) {
            StringTokenizer st = new StringTokenizer(msg);
            int len = st.countTokens();
            String command = st.nextToken();
            String answer = "K";
            switch (command) {
                case "REG":         //kayıt ol
                    answer = reg(msg.substring(4));
                    break;
                case "LOG":         //giriş yap
                    answer = log(msg.substring(4));
                    break;
                case "EXT":         //çıkış
                    nick = "Anonim";
                    frame.reList();
                    break;
                case "GPL":         //proje listesini al
                    answer = getProList(msg.substring(4));
                    break;
                case "POL":         //proje oluştur
                    createProject(msg.substring(4));
                    break;
                case "JPR":         //projeye katıl
                    answer = joinProject(msg.substring(4));
                    break;
                case "GET":         //dosya gönder
                    sendFile(msg.substring(4));
                    break;
                case "GeT":         //dosya gönder
                    sendFile2(msg.substring(4));
                    break;
                case "GUT":         //güncelleme dosyası gönder
                    sendFileU(msg.substring(4));
                    break;
                case "GuT":         //güncelleme dosyası gönder
                    sendFileU2(msg.substring(4));
                    break;
                case "GPF":         //proje directory format gönder
                    answer = sendProFrmt(msg.substring(4));
                    break;
                case "GLP":         //son portu gönder
                    answer = getLastPort();
                    break;
                case "GDL":         //projenin kullanıcı konumunu gönder
                    answer = getDirectoryLocation(msg.substring(4));
                    break;
                case "SDL":         //projenin kullanıcı konumu kaydet
                    answer = setDirectoryLocation(msg.substring(4));
                    break;
                case "UUP":         //güncelleme yklemesi dosya ismini al
                    answer = updateUpload(msg.substring(4));
                    break;
                case "UUF":         //güncelleme md5 formatını al
                    answer = updateUploadParse(msg.substring(4));
                    break;
                case "GMU":         //güncelleme yapacak olana md5 foratı gönder
                    answer = sendServerMUnit(msg.substring(4));
                    break;
                case "CRE":         // task oluştur
                    createNewTask(msg.substring(4));
                    break;
                case "GTS":         //taskların hepsini projeye göre getir
                    answer = sendTheTasks(msg.substring(4));
                    break;
                case "TTT":         // task ı gorevini üstlen
                    takeTheTask(msg.substring(4));
                    break;
                case "FTT":         // task ı gorevini üstlen
                    finishTheTask(msg.substring(4));
                    break;
                case "SMS":         // mesaj gönderdiğinde
                    sendMssge(msg.substring(4));
                    break;
                case "GSM":         //Bütün mesajları gönder
                    answer = getAllMsg(msg.substring(4));
                    break;
                case "GOL":         //ekip listesini gönder 
                    answer = sendOnlineList(msg.substring(4));
                    break;
                case "LDP":         //proje son tarihini önder
                    answer = lastDatePro(msg.substring(4));
                    break;
                case "WPD":         //proje son güncelleme tarihi
                    answer = whatProjectDate(msg.substring(4));
                    break;
                case "WMD":         //son atılan mesja tarihi
                    answer = whatMsgDate(msg.substring(4));
                    break;
                case "WTD":         //son atılan task tarihi
                    answer = whatTaskDate(msg.substring(4));
                    break;
            }
            sendMsg(answer);
        }

        String whatTaskDate(String data) {
            ArrayList<Task> list = Task.list;
            int len = list.size();
            int myTaskX = 0, taskListX = 0, doneTaskX = 0;
            for (int i = len - 1; i >= 0; i--) {
                Task msg = list.get(i);
                if (msg.data[Task.PROID].equals(data)) {
                    if (msg.data[Task.STATE].equals(Task.STATE_VOID)) {
                        taskListX++;
                    } else if (msg.data[Task.STATE].equals(Task.STATE_FINISH)) {
                        doneTaskX++;
                    } else if (msg.data[Task.STATE].equals(Task.STATE_AWAITING)) {
                        if (msg.data[Task.WHO].equals(nick)) {
                            myTaskX++;
                        }
                    }
                }
            }
//            sendMsg("" + myTaskX + " " + taskListX + " " + doneTaskX);
            return "" + myTaskX + " " + taskListX + " " + doneTaskX;
        }

        String whatMsgDate(String data) {
            ArrayList<Message> list = Message.list;
            int len = list.size();
            for (int i = len - 1; i >= 0; i--) {
                Message msg = list.get(i);
                if (msg.data[Message.PROJECTID].equals(data)) {
//                    sendMsg(msg.data[Message.DATE]);
                    return msg.data[Message.DATE];
                }
            }
            return "NOK";
        }

        String whatProjectDate(String data) {
            ProjectDb pro = ProjectDb.get(ProjectDb.NAME, data);
//            sendMsg(pro.data[ProjectDb.DATE]);
            return pro.data[ProjectDb.DATE];
        }

        String lastDatePro(String data) {
            ProjectDb pro = ProjectDb.get(ProjectDb.NAME, data);
//            sendMsg(pro.data[ProjectDb.DATE]);
            return pro.data[ProjectDb.DATE];
        }

        String sendOnlineList(String data) {
            ProjectDb pro = ProjectDb.get(ProjectDb.NAME, data);
            String id = pro.data[ProjectDb.ID];
            String sep = sepValue;
            String result = sep;
            for (ProjectsUsers users : ProjectsUsers.list) {
                if (users.data[ProjectsUsers.PROJECT_ID].equals(id)
                        && !users.data[ProjectsUsers.NICK].equals(this.nick)) {
                    boolean online = false;
                    for (MyChannel ch : listChannels) {
                        if (users.data[ProjectsUsers.NICK].equals(ch.nick)) {
                            online = true;
                            break;
                        }
                    }
                    if (online) {
                        result += "*" + users.data[ProjectsUsers.NICK] + sep;
                    } else {
                        result += users.data[ProjectsUsers.NICK] + sep;
                    }
                }
            }
//            sendMsg(result);
            return result;
        }

        String getAllMsg(String data) {
//            sendMsg(Message.stackAllDataForFile(data));
            return Message.stackAllDataForFile(data);
        }

        void sendMssge(String data) {
            String[] dat = parseDataToArray(3, data);
            String msgData[] = new String[4];
            msgData[Message.DATE] = getDate();
            msgData[Message.FROM] = dat[0];
            msgData[Message.MSG] = dat[1];
            msgData[Message.PROJECTID] = dat[2];
            Message.list.add(new Message(msgData));
        }

        void finishTheTask(String data) {
            String[] dat = parseDataToArray(2, data);
            Task.finishTheTask(dat);
        }

        void takeTheTask(String data) {
            String[] dat = parseDataToArray(2, data);
            Task.takeTheTask(dat);
        }

        String sendTheTasks(String data) {
            String tasks = Task.stackAllDataForFile(data);
//            sendMsg(tasks);
            return tasks;
        }

        void createNewTask(String data) {
            String[] dat = parseDataToArray(2, data);
            Task.create(dat, getDate());
        }

        void sendFileU(String data) {
            String[] dat = parseDataToArray(2, data);
            int port = Integer.parseInt(dat[0]);
            Ftp.lastPort = port + 1;
            String file = dat[1];
            String location = System.getProperty("user.dir");
            location += "\\KitHubProjects\\";
            new Thread(new Ftp(port, IP, location + file, false)).start();
        }

        void sendFileU2(String data) {
            String[] dat = parseDataToArray(3, data);
            int port = Integer.parseInt(dat[1]);
            Ftp.lastPort = port + 1;
            String file = dat[2];
            String location = System.getProperty("user.dir");
            location += "\\KitHubProjects\\";
            new Thread(new Ftp(port, dat[0], location + file, false)).start();
        }

        String sendServerMUnit(String projectName) {
            String location = System.getProperty("user.dir");
            location += "\\KitHubProjects\\";
            Md5ControlUnit serverUnit = new Md5ControlUnit();
            serverUnit.createMd5ListProjects(location, projectName);
//            sendMsg(serverUnit.formatting());
            return serverUnit.formatting();
        }

        String updateUploadParse(String data) {
            if (!uupProject.equals("NOK")) {
                String location = System.getProperty("user.dir");
                location += "\\KitHubProjects\\";
                Md5ControlUnit serverUnit = new Md5ControlUnit();
                Md5ControlUnit clientUnit = new Md5ControlUnit();
                clientUnit.parse(data);
                serverUnit.createMd5ListProjects(location, uupProject);
                serverUnit.control(clientUnit);
                ArrayList<String> newFileFormat = serverUnit.newFileFormat;
                ArrayList<String> deleteFileFormat = serverUnit.deleteFileFormat;
                for (String str : deleteFileFormat) {
                    int idx = str.indexOf(Md5ControlUnit.sep);
                    if (idx != -1) {
                        str = str.substring(0, idx);
                    }
                    if (!str.startsWith("*")) {
                        System.out.println("deleted file: " + location + str);
                        File file = new File(location + str);
                        file.delete();
                    }
                }
                for (String str : newFileFormat) {
                    int idx = str.indexOf(Md5ControlUnit.sep);
                    if (idx != -1) {
                        str = str.substring(0, idx);
                    }
                    if (!str.startsWith("*")) {
                        System.out.println("deleted file: " + location + str);
                        new Thread(new Ftp(Ftp.lastPort, IP, location + str, true)).start();
                        sendMsg("GET " + ServerThread.sepValue + (Ftp.lastPort++) + ServerThread.sepValue + str + ServerThread.sepValue);
                    } else {
                        File file = new File(location + str.substring(1));
                        file.mkdir();
                    }
                }
//                sendMsg("OK");
                return "OK";
            }
            return "NOK";
        }

        String updateUpload(String data) {
            uupProject = data;
            return "OK";
        }

        String setDirectoryLocation(String data) {
            String[] dizi = parseDataToArray(2, data);
            ProjectDb pro = ProjectDb.get(ProjectDb.NAME, dizi[0]);
            System.out.println("set direc lloc: nick: " + nick + " id: " + pro.data[ProjectDb.ID] + " loc: " + dizi[1]);
            ProjectsUsers.setDırectoryLoc(nick, pro.data[ProjectDb.ID], dizi[1]);
            return "OK";
        }

        String getDirectoryLocation(String name) {
            ProjectDb pro = ProjectDb.get(ProjectDb.NAME, name);
            String loc = ProjectsUsers.getDırectoryLoc(nick, pro.data[ProjectDb.ID]);
//            sendMsg("" + loc);
            return "" + loc;
        }

        String getLastPort() {
//            sendMsg("" + Ftp.lastPort);
            return "" + Ftp.lastPort;
        }

        String sendProFrmt(String data) {
            ProjectDb pro = ProjectDb.get(ProjectDb.NAME, data);
            if (pro == null) {
//                sendMsg("NOK");
                return "NOK";
            } else {
//                sendMsg(pro.data[ProjectDb.FORMAT]);
                return pro.data[ProjectDb.FORMAT];
            }

        }

        void sendFile(String data) {
            String[] dat = parseDataToArray(2, data);
            int port = Integer.parseInt(dat[0]);
            String file = dat[1];
            new Thread(new Ftp(port, IP, file, false)).start();
            Ftp.lastPort = port + 1;
        }

        void sendFile2(String data) {
            String[] dat = parseDataToArray(3, data);
            int port = Integer.parseInt(dat[1]);
            String file = dat[2];
            new Thread(new Ftp(port, dat[0], file, false)).start();
            Ftp.lastPort = port + 1;
        }

        String joinProject(String data) {
            String answer = "NOK";
            for (ProjectDb pro : ProjectDb.list) {
                if (pro.data[ProjectDb.NAME].equals(data)) {
                    ProjectsUsers.list.add(new ProjectsUsers(new String[]{pro.data[ProjectDb.ID], nick}));
                    answer = "OK";
                    break;
                }
            }
            return answer;
        }

        void createProject(String data) {
            InputStream is = null;
            String[] dizi = parseDataToArray(3, data);
            ProjectDb pro = new ProjectDb(dizi, getDate());
            ProjectsUsers.list.add(new ProjectsUsers(new String[]{pro.data[ProjectDb.ID], pro.data[ProjectDb.MANAGER]}));
            ProjectDb.list.add(pro);
            String location = System.getProperty("user.dir");
            location += "\\KitHubProjects";
            File file = new File(location);
            if (!file.isDirectory()) {
                file.mkdir();
            }
            String format = pro.data[ProjectDb.FORMAT];
            int begin = 0, end = 0;
            sendMsg("ID= " + pro.data[ProjectDb.ID]);
            while (true) {
                end = format.indexOf(ServerThread.sepFormat, begin + ServerThread.sepFormat.length());
                if (end < 0) {
                    break;
                }
                String parse = format.substring(begin + ServerThread.sepFormat.length(), end);
                if (parse.startsWith("*")) {
                    System.out.println("mkDir: " + parse);
                    file = new File(location + "\\" + parse.substring(1));
                    if (!file.isDirectory()) {
                        file.mkdir();
                    }
                } else {
                    System.out.println("getting port: " + (Ftp.lastPort) + " " + parse);
                    new Thread(new Ftp(Ftp.lastPort, IP, location + "\\" + parse, true)).start();
                    if (Ftp.lastPort > 20000) {
                        Ftp.lastPort = 8081;
                    }
                    sendMsg("GET " + ServerThread.sepValue + (Ftp.lastPort++) + ServerThread.sepValue + parse + ServerThread.sepValue);
                }
                begin = end;
            }
//            sendMsg("ID= " + pro.data[ProjectDb.ID]);
//            return "ID= " + pro.data[ProjectDb.ID];

        }

        String getProList(String nick) {
            String listStr = ProjectDb.getProjectLists(nick);
//            sendMsg(listStr);
            return listStr;
        }

        void sendMsg(String msg) {
            out.write(msg);
            out.write("\n");
            out.flush();
            System.out.println("Server send: " + msg);
        }

        String ext() {
            listChannels.remove(this);
            frame.reList();
            exit = true;
            return "OK";
        }

        String log(String data) {
            String[] dizi = parseDataToArray(2, data);
            if (User.controlLogin(dizi)) {
                //TODO basarıyla giriş yapabilir
                nick = dizi[0];
                frame.reList();
                return "OK";
            } else {
                //TODO şifre veya parola hatalı
                return "NOT";
            }
        }

        String reg(String data) {  // 0 nick 1 ad 2 soyad 3 sifre
            String[] dizi = parseDataToArray(4, data);
            if (User.add(dizi)) {
                //TODO basarıyla eklendi
            } else {
                //TODO aynı nick de kullanıcı var
            }
            return "OK";
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
    }

    String getDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy*HH:mm:ss");
        Date date = calendar.getTime();
        return df.format(date);
    }

}
