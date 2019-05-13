
package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rivalzka
 */
   public class Md5ControlUnit {
       
    public static String sep="?!:";
    ArrayList<String> fileFormat = new ArrayList<>();
    public ArrayList<String> newFileFormat = new ArrayList<>();
    public ArrayList<String> deleteFileFormat = new ArrayList<>();
    private String location = "";

    public void control(Md5ControlUnit unit) {
        Md5ControlUnit unitServer = this;
        for (String str : unit.fileFormat) {
            boolean find = false;
            for (String str2 : unitServer.fileFormat) {
                if (str.equals(str2)) {
                    find = true;
                }
            }
            if (!find) {
                newFileFormat.add(str);
            }
        }

        for (String str : unitServer.fileFormat) {
            boolean find = false;
            for (String str2 : unit.fileFormat) {
                if (str.equals(str2)) {
                    find = true;
                }
            }
            if (!find) {
                deleteFileFormat.add(str);
            }
        }
    }

    public void createMd5ListProjects(String location, String projectName) {
        this.location = location;
        File file = new File(location + projectName);
        fileFormat.add("*"+projectName);
        folders(file, projectName);
    }

    private void folders(File folder, String name) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    String name2 = name + "\\" + f.getName();
                    fileFormat.add("*"+name2);
                    folders(f, name2);
                } else {
                    String name2 = name + "\\" + f.getName();
                    try {
                        name2 += sep + getMD5Checksum(location + name2);
                        fileFormat.add(name2);
                    } catch (Exception ex) {
                        Logger.getLogger(Md5ControlUnit.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }
    }

    private byte[] createChecksum(String filename) throws Exception {
        InputStream fis = new FileInputStream(filename);
        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;
        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        return complete.digest();
    }

    private String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public String formatting() {
        String str = Server.ServerThread.sepValue;
        String result = str;
        for (String st : fileFormat) {
            result += st + str;
        }
        return result;
    }

    public void parse(String result) {
        String str = Server.ServerThread.sepValue;
        int begin = 0, end = 0;
        while (true) {
            end = result.indexOf(str, begin + str.length());
            if (end < 0) {
                break;
            }
            String parseStr = result.substring(begin + str.length(), end);
            fileFormat.add(parseStr);
            begin = end;
        }
    }

    public static void main(String[] args) {
//        Md5ControlUnit unit = new Md5ControlUnit();
//        try {
//            String path="E:\\NP\\YazMuhProje\\build.xml";
//            System.out.println(unit.getMD5Checksum(path));
//        }catch (Exception e) {
//            System.out.println("hata");
//        }
    }
}
