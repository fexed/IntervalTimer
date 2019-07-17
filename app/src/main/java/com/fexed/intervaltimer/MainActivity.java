package com.fexed.intervaltimer;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private long millisecondtime, starttime, updatetime, timebuff = 0L;
    private int s, m, millis;
    private Handler updatehandler, beephandler;
    private TextView timetxtv, intervaltxtv;
    private MediaPlayer mp;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref = this.getSharedPreferences("com.fexed.intervaltimer", MODE_PRIVATE);

        final FloatingActionButton startfab = findViewById(R.id.startbtn);
        final FloatingActionButton stopfab = findViewById(R.id.stopbtn);
        final ImageButton plusbtn = findViewById(R.id.plusbtn);
        final ImageButton minusbtn = findViewById(R.id.minusbtn);
        final Button led = findViewById(R.id.led);
        updatehandler = new Handler();
        beephandler = new Handler();
        timetxtv = findViewById(R.id.timetext);
        intervaltxtv = findViewById(R.id.intervaltext);
        intervaltxtv.setText(strFromMillis(pref.getLong("interval", 30000)));

        mp = MediaPlayer.create(MainActivity.this, R.raw.beep);

        View.OnClickListener start = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starttime = SystemClock.uptimeMillis();
                updatehandler.postDelayed(updaterrunnable, 0);
                beephandler.postDelayed(beeprunnable, pref.getLong("interval", 30000));
                ((View) startfab).setVisibility(View.INVISIBLE);
                ((View) stopfab).setVisibility(View.VISIBLE);
                plusbtn.setVisibility(View.INVISIBLE);
                minusbtn.setVisibility(View.INVISIBLE);
                led.setBackgroundColor(getResources().getColor((R.color.colorPrimaryDark)));
            }
        };
        View.OnClickListener pause = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timebuff += millisecondtime;
                updatehandler.removeCallbacks(updaterrunnable);
                beephandler.removeCallbacks(beeprunnable);
                ((View) startfab).setVisibility(View.VISIBLE);
                ((View) stopfab).setVisibility(View.INVISIBLE);
                plusbtn.setVisibility(View.VISIBLE);
                minusbtn.setVisibility(View.VISIBLE);
                led.setBackgroundColor(getResources().getColor((R.color.colorAccent)));
            }
        };

        startfab.setOnClickListener(start);
        stopfab.setOnClickListener(pause);

        plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pref.edit().putLong("interval", pref.getLong("interval", 30000) + 5000).apply();
                intervaltxtv.setText(strFromMillis(pref.getLong("interval", 30000)));
            }
        });
        minusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long interval = pref.getLong("interval", 30000) - 5000;
                pref.edit().putLong("interval", (interval <  5000 ? 5000 : interval)).apply();
                intervaltxtv.setText(strFromMillis(pref.getLong("interval", 30000)));
            }
        });

    }

    public Runnable updaterrunnable = new Runnable() {
        public void run() {
            millisecondtime = SystemClock.uptimeMillis() - starttime;
            updatetime = timebuff + millisecondtime;
            s = (int) (updatetime / 1000);
            m = s / 60;
            s = s % 60;
            millis = (int) (updatetime % 1000);

            timetxtv.setText("" + String.format("%02d", m) + ":"+ String.format("%02d", s));
            updatehandler.postDelayed(this, 0);
        }
    };

    public String strFromMillis(long milliseconds) {
        int seconds, minutes;
        seconds = (int) (milliseconds / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;
        return ("" + String.format("%02d", minutes) + ":"+ String.format("%02d", seconds));
    }

    public Runnable beeprunnable = new Runnable() {
        public void run() {
            mp.start();
            beephandler.postDelayed(this, pref.getLong("interval", 30000));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
