package Server;

import DataServer.Message;
import DataServer.ProjectDb;
import DataServer.ProjectsUsers;
import DataServer.Task;
import DataServer.User;

public class Server {
    
    public void readAllDataFromFiles(){
        User.readAllData();
        ProjectDb.readAllData();
        ProjectsUsers.readAllData();
        Task.readAllData();
        Message.readAllData();
    }
    
    public void saveAllDataToFiles(){
        User.saveAllData();
        ProjectDb.saveAllData();
        ProjectsUsers.saveAllData();
        Task.saveAllData();
        Message.saveAllData();
    }
    
    ServerThread server;
    
    public void main(){
        server=new ServerThread(this);
        new Thread(server).start();
        readAllDataFromFiles();
    }
    
    public static void main(String[] args) {
        Server server=new Server();
        server.main();
    }
}
