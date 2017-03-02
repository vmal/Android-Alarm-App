package com.example.ryanyoung.alarm;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by Ryan Young on 12/7/2016.
 */

public class Alarm {
    public boolean sunday = false;
    public boolean monday = false;
    public boolean tuesday = false;
    public boolean wednesday = false;
    public boolean thursday = false;
    public boolean friday = false;
    public boolean saturday = false;

    public boolean am = false;

    public String hour = "";
    public String minute = "";

    public boolean active = false;

    public String tone = "";

    public int ID = -1;



    public Alarm(){

    }
    /**
     *
     * string formats stored in the file are days;hour;min;am;active;tone;id
     *
     * days is 1 integer value which represents a 7 digit binary string
     * hour is an integer value from 1-12
     * minute is a integer value from 0-59
     * am is an integer value == 1 if time is in am
     * active is an integer value == 1 if active
     * tone is the uri string for the alarm sound
     * id is this alarms id number
     *
     */
    public Alarm(String translate){
        String[] parts = translate.split(";");
        if(parts.length != 7){
            throw new IllegalArgumentException();
        }
        //evaluate the days in all days
        int day = Integer.valueOf(parts[0]);
        if((day & 1) != 0){
            sunday = true;
        }
        if((day & 2) != 0){
            monday = true;
        }
        if((day & 4) != 0){
            tuesday = true;
        }
        if((day & 8) != 0){
            wednesday = true;
        }
        if((day & 16) != 0){
            thursday = true;
        }
        if((day & 32) != 0){
            friday = true;
        }
        if((day & 64) != 0){
            saturday = true;
        }

        //get hour and check for validity
        hour = parts[1];
        int hourCheck = Integer.valueOf(hour);
        if(hourCheck > 12 || hourCheck < 1){
            throw new IllegalArgumentException();
        }

        //get minute and check validity
        minute = parts[2];
        int minuteCheck = Integer.valueOf(minute);
        if(minuteCheck > 59 || minuteCheck < 0){
            throw new IllegalArgumentException();
        }
        else{
            if(minuteCheck < 10 && minute.length() !=2){
                throw new IllegalArgumentException();
            }
        }

        //is time in am or pm?
        int am = Integer.valueOf(parts[3]);
        if(am == 1){
            this.am = true;
        }
        else{
            this.am = false;
        }

        //is alarm active?
        int active = Integer.valueOf(parts[4]);
        if(active == 1){
            this.active = true;
        }
        else{
            this.active = false;
        }

        this.tone = parts[5];


        this.ID = Integer.valueOf(parts[6]);
    }

    /**
     * encode alarm object into a single string
     * @return String of encoded alarm object
     */
    public String encodeAlarm(){
        String encode = "";
        int days = encodeDays(sunday,monday,tuesday,wednesday,thursday,friday,saturday);
        encode += days + ";" + hour + ";" + minute + ";";
        if(am){
            encode += "1";
        }
        else{
            encode += "0";
        }

        encode += ";";

        if(active){
            encode += "1";
        }
        else{
            encode += "0";
        }

        encode += ";" + tone + ";" + ID;

        return encode;
    }

    /**
     * encode the boolean days into an integer value
     * @param sunday
     * @param monday
     * @param tuesday
     * @param wednesday
     * @param thursday
     * @param friday
     * @param saturday
     * @return encoded integer value
     */
    public static int encodeDays(boolean sunday, boolean monday,boolean tuesday,boolean wednesday,
                          boolean thursday,boolean friday,boolean saturday){
        int days = 0;
        if(sunday) days+=1;
        if(monday) days+=2;
        if(tuesday) days+=4;
        if(wednesday) days+=8;
        if(thursday) days+=16;
        if(friday) days+=32;
        if(saturday) days+=64;
        return days;

    }

    public Calendar firstTimeAfter(int day, int hour, int min){

        //adjusted alarmHour
        int alarmHour = Integer.valueOf(this.hour);
        if(!am) alarmHour += 12;
        if(alarmHour == 24){
            alarmHour = 0;
        }
        Date date = new Date();

        //build arrayList of all active times
        ArrayList<Calendar> selectedTimes = new ArrayList<Calendar>();
        if(sunday){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.DAY_OF_WEEK, 1);
            c.set(Calendar.HOUR_OF_DAY, alarmHour);
            c.set(Calendar.MINUTE, Integer.valueOf(this.minute));
            selectedTimes.add(c);
        }
        if(monday){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.DAY_OF_WEEK, 2);
            c.set(Calendar.HOUR_OF_DAY, alarmHour);
            c.set(Calendar.MINUTE, Integer.valueOf(this.minute));
            selectedTimes.add(c);
        }
        if(tuesday){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.DAY_OF_WEEK, 3);
            c.set(Calendar.HOUR_OF_DAY, alarmHour);
            c.set(Calendar.MINUTE, Integer.valueOf(this.minute));
            selectedTimes.add(c);
        }
        if(wednesday){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.DAY_OF_WEEK, 4);
            c.set(Calendar.HOUR_OF_DAY, alarmHour);
            c.set(Calendar.MINUTE, Integer.valueOf(this.minute));
            selectedTimes.add(c);
        }
        if(thursday){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.DAY_OF_WEEK, 5);
            c.set(Calendar.HOUR_OF_DAY, alarmHour);
            c.set(Calendar.MINUTE, Integer.valueOf(this.minute));
            selectedTimes.add(c);
        }
        if(friday){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.DAY_OF_WEEK, 6);
            c.set(Calendar.HOUR_OF_DAY, alarmHour);
            c.set(Calendar.MINUTE, Integer.valueOf(this.minute));
            selectedTimes.add(c);
        }if(saturday){
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.DAY_OF_WEEK, 7);
            c.set(Calendar.HOUR_OF_DAY, alarmHour);
            c.set(Calendar.MINUTE, Integer.valueOf(this.minute));
            selectedTimes.add(c);
        }

        //first time in arrayList is the earliest in the week
        Calendar selectedTime = selectedTimes.get(0);
        boolean TimeAfter = false;

        //loop through all the active times and find the
        //first alarm that comes after provided time
        //if no alarms come after the provided time that means
        //the next alarm is the earliest time next week which is already set
        Iterator<Calendar> it = selectedTimes.iterator();
        while(it.hasNext()){
            Calendar c = it.next();
            int cDay = c.get(Calendar.DAY_OF_WEEK);
            int cHour = c.get(Calendar.HOUR_OF_DAY);
            int cMin = c.get(Calendar.MINUTE);

            if(cDay > day){
                selectedTime = c;
                TimeAfter = true;
                break;
            }
            else if(cDay == day){
                if(cHour > hour){
                    selectedTime = c;
                    TimeAfter = true;
                    break;
                }
                else if(cHour == hour){
                    if(cMin > min){
                        selectedTime = c;
                        TimeAfter = true;
                        break;
                    }
                }
            }

        }
        if(!TimeAfter){
            selectedTime.add(Calendar.HOUR,24*7);
        }

        return selectedTime;

    }


}
