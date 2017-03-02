package com.example.ryanyoung.alarm;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by Ryan Young on 12/7/2016.
 */

public class AlarmSet {

    private ArrayList<Alarm> alarms = new ArrayList<Alarm>();
    String alarmFile;


    public AlarmSet(){

    }
    /**
     * construct alarm set based on managing file
     * which should contain multiple encoded alarm strings
     * @param context
     * @param fileName
     */
    public AlarmSet(Context context, String fileName){
        try {
            FileInputStream is = context.openFileInput(fileName);

            Scanner scan = new Scanner(is);
            while (scan.hasNextLine()) {
                alarms.add(new Alarm(scan.nextLine()));
            }
            scan.close();
        }
        catch (IOException e){
            System.out.println(fileName + " does not exist yet");
        }

        alarmFile = fileName;

    }

    /**
     * add new alarm based on paramater
     * @param hour
     * @param min
     * @param days encoded integer for days
     * @param am
     * @param active
     */
    public void addAlarm(int hour, int min, int days, boolean am, boolean active, String tone){
        int newID = 1;
        Iterator<Alarm> it = alarms.iterator();
        while(it.hasNext()){
            if(newID < it.next().ID){
                break;
            }
            else{
                newID++;
            }
        }

        String encode = "" + days + ";" + hour + ";" +
                (min<10? "0" + min: min) + ";" + (am? 1:0)
                + ";" + (active? 1:0) + ";" + tone + ";" + newID;
        alarms.add(0,new Alarm(encode));
    }

    /**
     * update alarm entry with new data
     * @param index
     * @param hour
     * @param min
     * @param days
     * @param am
     * @param active
     * @param tone
     */
    public void changeAlarm(int index, int hour, int min, int days, boolean am, boolean active, String tone){
        int id = alarms.get(index).ID;
        String encode = "" + days + ";" + hour + ";" +
                (min<10? "0" + min: min) + ";" + (am? 1:0)
                + ";" + (active? 1:0) + ";" + tone + ";" + id;
        alarms.set(index, new Alarm(encode));
    }

    /**
     * get alarm at index
     * @param index
     * @return alarm at index
     */
    public Alarm getAlarm(int index){
        return alarms.get(index);
    }

    /**
     * remove alarm at index be sure to update alarmManager prior to calling this
     * @param index
     */
    public void removeAlarm(int index){ alarms.remove(index); }

    /**
     *
     * @return number of alarms stored
     */
    public int alarmCount(){
        return alarms.size();
    }

    /**
     * update file that alarms are managed from
     * @param context
     */
    public void updateAlarmFile(Context context){
        try {
            FileOutputStream os = context.openFileOutput(alarmFile, Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(os);
            Iterator<Alarm> iter = alarms.iterator();
            while (iter.hasNext()) {
                pw.println(iter.next().encodeAlarm());
            }
            pw.close();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
