package com.example.ryanyoung.alarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class alarmManager extends AppCompatActivity {

    int index;
    AlarmSet alarms;
    String toneuri;

    @Override
    /**
     * set up activity
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.managerToolBar);
        setSupportActionBar(toolbar);


        index = Integer.valueOf(getIntent().getStringExtra("index"));

        //load in alarms from alarm file (if alarm file exists yet)
        alarms = new AlarmSet(this, "allAlarms.txt");
        if(index >= 0){
            loadInValues();
        }

        setUpButtons();
    }

    @Override
    /**
     * action bar inflation
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.change_alarm_menu, menu);
        return true;
    }

    @Override
    /**
     * action bar button handlers
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //no submission go back
            case R.id.no_change:
                finish();
                return true;
            //add or change alarm
            case R.id.alarm_change:
                //tone uri must be set
                if(toneuri == null){
                    Context context = getApplicationContext();
                    CharSequence text = "No Tone Selected";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return true;
                }
                //get selected days
                boolean sunday = !findViewById(R.id.SundayToggle).getTag().equals("cell");
                boolean monday = !findViewById(R.id.MondayToggle).getTag().equals("cell");
                boolean tuesday = !findViewById(R.id.TuesdayToggle).getTag().equals("cell");
                boolean wednesday = !findViewById(R.id.WednesdayToggle).getTag().equals("cell");
                boolean thursday = !findViewById(R.id.ThursdayToggle).getTag().equals("cell");
                boolean friday = !findViewById(R.id.FridayToggle).getTag().equals("cell");
                boolean saturday = !findViewById(R.id.SaturdayToggle).getTag().equals("cell");

                //get alarm information
                int days = Alarm.encodeDays(sunday,monday,tuesday,wednesday,thursday,friday,saturday);
                int hour = Integer.valueOf(((TextView) findViewById(R.id.Hour)).getText().toString());
                int min = Integer.valueOf(((TextView) findViewById(R.id.Minute)).getText().toString());
                boolean am = ((Button) findViewById(R.id.AM_PM)).getText().equals("AM");

                //1 or more days must be set
                if(days == 0){
                    Context context = getApplicationContext();
                    CharSequence text = "No Days Selected";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    return true;
                }

                //update or add new alarm
                if(index >=0) {
                    alarms.changeAlarm(index,hour,min,days,am,true,toneuri);
                }
                else{
                    alarms.addAlarm(hour,min,days,am,true,toneuri);
                }

                //update alarms file
                alarms.updateAlarmFile(this);

                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * load in alarm information to activity to display currently selected alarm
     *
     */
    private void loadInValues(){
        //illegal state treat as if a new alarm is being added
        if(alarms == null || (alarms.alarmCount() < index && index >=0)){
            index = -1;
            return;
        }
        Alarm a = alarms.getAlarm(index);

        //set toggle of active days
        if(a.sunday) {
            Button b = (Button) findViewById(R.id.SundayToggle);
            b.setBackgroundResource(R.drawable.active_cell);
            b.setTag("active cell");
        }
        if(a.monday) {
            Button b = (Button) findViewById(R.id.MondayToggle);
            b.setBackgroundResource(R.drawable.active_cell);
            b.setTag("active cell");
        }
        if(a.tuesday){
            Button b = (Button) findViewById(R.id.TuesdayToggle);
            b.setBackgroundResource(R.drawable.active_cell);
            b.setTag("active cell");
        }
        if(a.wednesday){
            Button b = (Button) findViewById(R.id.WednesdayToggle);
            b.setBackgroundResource(R.drawable.active_cell);
            b.setTag("active cell");
        }
        if(a.thursday){
            Button b = (Button) findViewById(R.id.ThursdayToggle);
            b.setBackgroundResource(R.drawable.active_cell);
            b.setTag("active cell");
        }
        if(a.friday){
            Button b = (Button) findViewById(R.id.FridayToggle);
            b.setBackgroundResource(R.drawable.active_cell);
            b.setTag("active cell");
        }
        if(a.saturday) {
            Button b = (Button) findViewById(R.id.SaturdayToggle);
            b.setBackgroundResource(R.drawable.active_cell);
            b.setTag("active cell");
        }

        //set hour
        ((TextView) findViewById(R.id.Hour)).setText(a.hour);

        //set minute
        ((TextView) findViewById(R.id.Minute)).setText(a.minute);

        //set AM/PM
        if(a.am) ((Button) findViewById(R.id.AM_PM)).setText("AM");
        else ((Button) findViewById(R.id.AM_PM)).setText("PM");

        //set Selected Alarm Tone
        Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(a.tone));
        toneuri = a.tone;
        ((TextView) findViewById(R.id.SelectedTone)).setText(r.getTitle(this));


    }


    /**
     * set up all but listeners
     */
    private void setUpButtons(){

        //new tone file to be selected
        RelativeLayout toneButton =(RelativeLayout)findViewById(R.id.SelectedToneButton);
        toneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                selectTone();
            }
        });


        //increment hour up
        Button upHour = (Button) findViewById(R.id.upHour);
        upHour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                incrementHour();
            }
        });

        //decrement hour value
        Button downHour = (Button) findViewById(R.id.downHour);
        downHour.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                decrementHour();
            }
        });

        //increment minute
        Button upMinute = (Button) findViewById(R.id.upMinute);
        upMinute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                incrementMinute();
            }
        });

        //decrement minute
        Button downMinute = (Button) findViewById(R.id.downMinute);
        downMinute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                decrementMinute();
            }
        });

        //change AM_PM state
        Button AM_PM = (Button) findViewById(R.id.AM_PM);
        AM_PM.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                flipAM_PM();
            }
        });

    }

    /**
     * increment the hour text up updating necessary fields
     */
    private void incrementHour(){

        TextView hour = (TextView) findViewById(R.id.Hour);
        int hourAmnt = Integer.valueOf(hour.getText().toString());
        hourAmnt += 1;
        if(hourAmnt == 12){
            flipAM_PM();
        }
        else if(hourAmnt > 12){
            hourAmnt = 1;
        }
        hour.setText("" + hourAmnt);
    }

    /**
     * increment minute field up
     */
    private void incrementMinute(){
        TextView minute = (TextView) findViewById(R.id.Minute);
        int minuteAmnt = Integer.valueOf(minute.getText().toString());
        String newMinute = "";
        minuteAmnt+=1;
        if(minuteAmnt == 60){
            newMinute = "00";
        }
        else if(minuteAmnt < 10){
            newMinute = "0" + minuteAmnt;
        }
        else{
            newMinute = "" + minuteAmnt;
        }

        minute.setText(newMinute);
    }

    /**
     * decrement hour text and update necessary field
     */
    private void decrementHour(){
        TextView hour = (TextView) findViewById(R.id.Hour);
        int hourAmnt = Integer.valueOf(hour.getText().toString());


        if(hourAmnt == 12){
            flipAM_PM();
        }
        hourAmnt -= 1;
        if(hourAmnt == 0){
            hourAmnt = 12;
        }
        hour.setText("" + hourAmnt);
    }

    /**
     * decrement minute text down
     */
    private void decrementMinute(){
        TextView minute = (TextView) findViewById(R.id.Minute);
        int minuteAmnt = Integer.valueOf(minute.getText().toString());
        String newMinute = "";
        minuteAmnt-=1;
        if(minuteAmnt == -1){
            newMinute = "59";
        }
        else if(minuteAmnt < 10){
            newMinute = "0" + minuteAmnt;
        }
        else{
            newMinute = "" + minuteAmnt;
        }

        minute.setText(newMinute);
    }

    /**
     * flip AM and PM in AM_PM button
     */
    private void flipAM_PM(){
        Button AM_PM = (Button) findViewById(R.id.AM_PM);
        String a_p = AM_PM.getText().toString();
        if(a_p.equals("AM")){
            AM_PM.setText("PM");
        }
        else{
            AM_PM.setText("AM");
        }
    }

    /**
     * open up tone selection menu where a new alarm tone can be selected
     */
    private void selectTone(){
        //open list of ringtones that this alarm could use
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, 5);
    }

    @Override
    /**
     * return from tone selection and update the selected tone information
     */
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                Ringtone r = RingtoneManager.getRingtone(this, uri);
                toneuri = uri.toString();

                ((TextView) findViewById(R.id.SelectedTone)).setText(r.getTitle(this));
            }
        }
    }

    /**
     * toggle the display of the provided view such that it has an active or inactive cell
     * @param v
     */
    public void ToggleCell(View v) {
        if(v.getTag().equals("cell")) {
            v.setBackgroundResource(R.drawable.active_cell);
            v.setTag("active cell");
        }
        else {
            v.setBackgroundResource(R.drawable.cell);
            v.setTag("cell");
        }
    }
}
