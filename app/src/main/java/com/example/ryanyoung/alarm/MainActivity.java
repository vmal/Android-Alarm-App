package com.example.ryanyoung.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private AlarmSet alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //load in alarms from alarm file (if alarm file exists yet)
        alarms = new AlarmSet(this, "allAlarms.txt");

        //for each alarm in alarms create row entry
        for(int i = 0; i < alarms.alarmCount(); i++){
            addAlarmRow(alarms.getAlarm(i),i);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addAlarm:
                Intent intent = new Intent(getBaseContext(), alarmManager.class);
                intent.putExtra("index", "-1");
                startActivityForResult(intent,1);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    //add an alarm row to the table
    public void addAlarmRow(Alarm a,int i){
        TableLayout table = (TableLayout) findViewById(R.id.Created_Alarms);

        //get instance of layout of table row
        TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.alarm_row, null);
        row.setId(i);

        //change color of active days
        ((TextView)row.findViewById(R.id.sun)).setTextColor(a.sunday? Color.parseColor("#00ff00"): Color.parseColor("#888888"));
        ((TextView)row.findViewById(R.id.mon)).setTextColor(a.monday? Color.parseColor("#00ff00"): Color.parseColor("#888888"));
        ((TextView)row.findViewById(R.id.tue)).setTextColor(a.tuesday? Color.parseColor("#00ff00"): Color.parseColor("#888888"));
        ((TextView)row.findViewById(R.id.wed)).setTextColor(a.wednesday? Color.parseColor("#00ff00"): Color.parseColor("#888888"));
        ((TextView)row.findViewById(R.id.thur)).setTextColor(a.thursday? Color.parseColor("#00ff00"): Color.parseColor("#888888"));
        ((TextView)row.findViewById(R.id.fri)).setTextColor(a.friday? Color.parseColor("#00ff00"): Color.parseColor("#888888"));
        ((TextView)row.findViewById(R.id.sat)).setTextColor(a.saturday? Color.parseColor("#00ff00"): Color.parseColor("#888888"));

        CheckBox c = (CheckBox) row.findViewById(R.id.enabled);
        c.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean checked = ((CheckBox) v).isChecked();

                Alarm a = alarms.getAlarm(((TableRow) v.getParent()).getId());
                a.active = checked;

                alarms.updateAlarmFile(MainActivity.this);

                updateAlarmManager();
            }
        });

        //set checkbox if on or off
        if(a.active) {
            c.setChecked(true);
        }
        else{
            c.setChecked(false);
        }


        //setTime
        ((TextView)row.findViewById(R.id.time)).setText("" + a.hour + ":" + a.minute);
        ((TextView)row.findViewById(R.id.am)).setText("" + (a.am? "AM":"PM"));

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = view.getId();
                Intent intent = new Intent(getBaseContext(), alarmManager.class);
                intent.putExtra("index", "" + index);
                startActivityForResult(intent,1);

            }
        });

        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final int alarmIndex = view.getId();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Delete Selected Alarm?")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Alarm a = alarms.getAlarm(alarmIndex);
                                a.active = false;
                                updateAlarmManager();
                                alarms.removeAlarm(alarmIndex);
                                TableLayout table = (TableLayout) findViewById(R.id.Created_Alarms);
                                table.removeAllViews();

                                //for each alarm in alarms create row entry
                                for(int i = 0; i < alarms.alarmCount(); i++){
                                    addAlarmRow(alarms.getAlarm(i),i);
                                }
                                alarms.updateAlarmFile(MainActivity.this);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = builder.create();

                // show it
                alertDialog.show();

                return true;
            }
        });

        updateAlarmManager();

        table.addView(row);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        alarms = new AlarmSet(this, "allAlarms.txt");

        TableLayout table = (TableLayout) findViewById(R.id.Created_Alarms);
        table.removeAllViews();

        //for each alarm in alarms create row entry
        for(int i = 0; i < alarms.alarmCount(); i++){
            addAlarmRow(alarms.getAlarm(i),i);
        }

    }

    private void updateAlarmManager(){
        //Create an offset from the current time in which the alarm will go off.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        //Create a new PendingIntent and add it to the AlarmManager
        for(int i = 0; i < alarms.alarmCount(); i++) {
            Alarm a = alarms.getAlarm(i);

            //pending intent is the intent that will be used when alarm is recieved
            Intent intent = new Intent(MainActivity.this, alarmRecieve.class);
            intent.putExtra("alarmID" , "" + a.ID);
            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,
                   a.ID , intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am =
                    (AlarmManager) getSystemService(Activity.ALARM_SERVICE);

            //if the alarm is active then set alarm with pending intent and next alarm time
            if(a.active){
                //get the time that the alarm should go off
                Calendar calander = Calendar.getInstance();
                calander.setTimeInMillis(System.currentTimeMillis());
                int dayOfWeek = calander.get(Calendar.DAY_OF_WEEK);
                int hourOfDay = calander.get(Calendar.HOUR_OF_DAY);
                int minOfDay = calander.get(Calendar.MINUTE);

                Calendar alarmTime = a.firstTimeAfter(dayOfWeek,hourOfDay,minOfDay);
                am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                        pendingIntent);
            }
            //alarm is not active so cancel it
            else{
                am.cancel(pendingIntent);
            }



        }
    }
}
