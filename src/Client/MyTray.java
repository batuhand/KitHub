/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Rivalzka
 */
public class MyTray {

    static TrayIcon icon;
    final SystemTray tray;
    String path;
    PopupMenu menu;
    public static String locationAddress = System.getProperty("user.dir");    //linux da çalışmıyor // program dosya konumu
    
    public MyTray(String iconPath, String toolTip) throws IOException, AWTException {
        path = iconPath;
        icon = new TrayIcon(ImageIO.read(new File(path)));
        icon.setToolTip(toolTip);        //mouse hover text
        tray = SystemTray.getSystemTray();
        menu = new PopupMenu();
        icon.setPopupMenu(menu);
        tray.add(icon);
    }
    
    
    public void addItem(String value, ActionListener ac) {
        MenuItem item1 = new MenuItem(value);
        item1.addActionListener(ac);
        menu.add(item1);
    }

    public void errorMessage(String head, String error) {
        icon.displayMessage(head, error, TrayIcon.MessageType.ERROR);
    }

    public void warningMessage(String head, String warning) {
        icon.displayMessage(head, warning, TrayIcon.MessageType.WARNING);
    }

    public void infoMessage(String head, String info) {
        icon.displayMessage(head, info, TrayIcon.MessageType.INFO);
    }

    public void noneMessage(String head, String none) {
        icon.displayMessage(head, none, TrayIcon.MessageType.NONE);
    }

    public static void main(String[] args) throws AWTException {
        try {
            MyTray as = new MyTray(MyTray.locationAddress + "\\icon\\letter-k.png", "MyTray");
            ActionListener ac = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    as.tray.remove(as.icon);
                    System.exit(0);
                }
            };
            as.addItem("çıkıs", ac);
            as.noneMessage("Bilgi", "basarılı");
        } catch (IOException ex) {
            Logger.getLogger(MyTray.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
