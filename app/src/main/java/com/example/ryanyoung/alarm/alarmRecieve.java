package com.example.ryanyoung.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class alarmRecieve extends AppCompatActivity {

    AlarmSet alarms;
    Alarm activeAlarm;
    int id;
    MediaPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_recieve);

        alarms = new AlarmSet(this, "allAlarms.txt");
        id = Integer.valueOf(getIntent().getStringExtra("alarmID"));


        //find the alarm that triggered this alert and update alarmmanager for this
        //alarms next trigger time
        for(int i = 0; i < alarms.alarmCount(); i++) {

            Alarm a = alarms.getAlarm(i);
            //alarm that triggered alert found

            if(a.ID == id) {
                activeAlarm = a;
                //set up intent for next trigger
                Intent intent = new Intent(alarmRecieve.this, alarmRecieve.class);
                intent.putExtra("alarmID", "" + a.ID);
                PendingIntent pendingIntent = PendingIntent.getActivity(alarmRecieve.this,
                        id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am =
                        (AlarmManager) getSystemService(Activity.ALARM_SERVICE);

                //get the time that the alarm should go off next
                Calendar calander = Calendar.getInstance();
                calander.setTimeInMillis(System.currentTimeMillis());
                int dayOfWeek = calander.get(Calendar.DAY_OF_WEEK);
                int hourOfDay = calander.get(Calendar.HOUR_OF_DAY);
                int minOfDay = calander.get(Calendar.MINUTE);

                Calendar alarmTime = a.firstTimeAfter(dayOfWeek, hourOfDay, minOfDay);
                am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(),
                        pendingIntent);

                break;

            }
        }

        Button b = (Button) findViewById(R.id.silenceAlarm);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.stop();
                finish();
            }
        });


        Uri alarmTone = Uri.parse(activeAlarm.tone);
        player = MediaPlayer.create(this, alarmTone);
        player.setLooping(true);
        player.start();


    }
}
