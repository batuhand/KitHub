/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataServer;

import java.util.ArrayList;

/**
 *
 * @author Rivalzka
 */
public class TimeToLast {
    public static final String TYPE_PROJECT="pro";
    public static final String TYPE_TASK="tsk";
    public static final String TYPE_MSG="msg";
    public static ArrayList<TimeToLast> list =new ArrayList<>();
    
    public static final int TYPE=0;
    public static final int PROJE=1;
    public static final int DATE=2;
    
    public String data[]=new String[3];
    
    public static void addProject(String name,String date){
        TimeToLast tt=new TimeToLast();
        tt.data[TYPE]=TYPE_PROJECT;
        tt.data[PROJE]=name;
        tt.data[DATE]=date;
        list.add(tt);
    }
    
    public static void addTask(String name,String date){
        TimeToLast tt=new TimeToLast();
        tt.data[TYPE]=TYPE_TASK;
        tt.data[PROJE]=name;
        tt.data[DATE]=date;
        list.add(tt);
    }
    
    public static void addMsg(String name,String date){
        TimeToLast tt=new TimeToLast();
        tt.data[TYPE]=TYPE_MSG;
        tt.data[PROJE]=name;
        tt.data[DATE]=date;
        list.add(tt);
    }
    public static TimeToLast getProject(String name){
        for(TimeToLast tt:list){
            if(tt.data[TYPE].equals(TYPE_PROJECT) && tt.data[PROJE].equals(name)){
                return tt;
            }
        }
        return null;
    }
    
    public static TimeToLast getTask(String name){
        for(TimeToLast tt:list){
            if(tt.data[TYPE].equals(TYPE_TASK) && tt.data[PROJE].equals(name)){
                return tt;
            }
        }
        return null;
    }
    
    public static TimeToLast getMsg(String name){
        for(TimeToLast tt:list){
            if(tt.data[TYPE].equals(TYPE_MSG) && tt.data[PROJE].equals(name)){
                return tt;
            }
        }
        return null;
    }
}
