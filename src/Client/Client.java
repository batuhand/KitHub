package Client;

import DataServer.Message;
import DataServer.ProjectDb;
import DataServer.Task;
import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    public static MyTray myTray;
    public static ClientFrame frame;
    public static ConnectionToServer cs;
    public static String nick = "";

    public static void createTray(String toolkit) throws IOException, AWTException {
        myTray = new MyTray(MyTray.locationAddress + "\\icon\\letter-k.png", "Kithup: " + toolkit);

        ActionListener ac3 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO :
                if (frame == null) {
                    frame = new ClientFrame();
                }
                frame.setVisible(true);
            }
        };
        myTray.addItem("ARAYÜZÜ GÖSTER ", ac3);

        myTray.menu.addSeparator();
        ActionListener ac = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signOut();
            }
        };
        myTray.addItem("PROJEDEN ÇIK", ac);
        myTray.noneMessage("Bilgi", "basarılı");
    }

    public static boolean createConnection(String ip) {
        try {
            cs = new ConnectionToServer(ip);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static void signOut() {
        try {
            Notify.exit=true;
            saveAllDataToFiles();
            ProjectDb.list = new ArrayList<>();
            Client.cs.exit();
            myTray.tray.remove(myTray.icon);
            frame.setVisible(false);
            new LoginPage();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void readAllDataFromFiles() {
        Task.readAllData();
        Message.readAllData();
    }

    public static void saveAllDataToFiles() {
        Task.saveAllData();
        Message.saveAllData();
    }

    public void main() {
        new LoginPage(this);
    }

    public static void main(String[] args) {
        readAllDataFromFiles();
        Client client = new Client();
        client.main();
    }
}
