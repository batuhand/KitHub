/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rivalzka
 */
public class FileRW {
    File file;
    String path;
    String mainLocation;
    
    public FileRW(String path) {
        mainLocation=System.getProperty("user.dir");
        this.path=mainLocation+path;
        this.file = new File(this.path);
        controlDir();
        controlFile();
    }
    
    public void controlDir(){
        File file=new File(mainLocation+"\\Data");
        if(!file.isDirectory()){
            file.mkdir();
        }
    }
    public void controlFile(){
        if(!file.isFile()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileRW.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getData() throws FileNotFoundException, IOException {
        String kk = "";
        BufferedReader oku = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
        String str;
        while ((str = oku.readLine()) != null) {
            kk += str;
        }
        oku.close();
        return kk;
    }

    public void setData(String data) throws FileNotFoundException, IOException {
        file.delete();
        FileOutputStream fos = new FileOutputStream(this.path);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        osw.write(data);
        osw.close();
    }
    
    
}
