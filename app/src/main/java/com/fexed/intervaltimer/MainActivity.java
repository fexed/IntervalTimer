package com.fexed.intervaltimer;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private long millisecondtime, starttime, lastbeep, updatetime, timebuff = 0L;
    private int s, m, millis;
    private Handler updatehandler, beephandler;
    private TextView timetxtv, intervaltxtv;
    private ProgressBar progressLedR, progressLedL;
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
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(getResources().getColor(R.color.colorAccent));

        progressLedR = findViewById(R.id.progressLedR);
        progressLedL = findViewById(R.id.progressLedL);
        timetxtv = findViewById(R.id.timetext);
        intervaltxtv = findViewById(R.id.intervaltext);
        updatehandler = new Handler();
        beephandler = new Handler();
        timetxtv.setText("00:00");
        intervaltxtv.setText(strFromMillis(pref.getLong("interval", 30000)));

        mp = MediaPlayer.create(MainActivity.this, R.raw.beep);

        View.OnClickListener start = view -> {
            starttime = SystemClock.uptimeMillis();
            lastbeep = starttime;
            updatehandler.postDelayed(updaterrunnable, 0);
            beephandler.postDelayed(beeprunnable, pref.getLong("interval", 30000));
            ((View) startfab).setVisibility(View.INVISIBLE);
            ((View) stopfab).setVisibility(View.VISIBLE);
            plusbtn.setVisibility(View.INVISIBLE);
            minusbtn.setVisibility(View.INVISIBLE);
            led.setBackgroundColor(getResources().getColor((R.color.colorPrimaryDark)));
            progressLedR.setProgress(0);
            progressLedL.setProgress(0);
            progressLedR.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            progressLedL.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        };

        View.OnClickListener pause = view -> {
            timebuff += millisecondtime;
            updatehandler.removeCallbacks(updaterrunnable);
            beephandler.removeCallbacks(beeprunnable);
            ((View) startfab).setVisibility(View.VISIBLE);
            ((View) stopfab).setVisibility(View.INVISIBLE);
            plusbtn.setVisibility(View.VISIBLE);
            minusbtn.setVisibility(View.VISIBLE);
            progressLedR.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            progressLedL.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            led.setBackgroundColor(getResources().getColor((R.color.colorAccent)));
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        };

        startfab.setOnClickListener(start);
        stopfab.setOnClickListener(pause);

        plusbtn.setOnClickListener(v -> {
            long interval = pref.getLong("interval", 30000) + 5000;
            pref.edit().putLong("interval", (interval ==  6000 ? 5000 : interval)).apply();
            intervaltxtv.setText(strFromMillis(pref.getLong("interval", 30000)));
        });

        minusbtn.setOnClickListener(v -> {
            long interval = pref.getLong("interval", 30000) - 5000;
            pref.edit().putLong("interval", (interval <  1000 ? 1000 : interval)).apply();
            intervaltxtv.setText(strFromMillis(pref.getLong("interval", 30000)));
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

            float left = (lastbeep + pref.getLong("interval", 30000)) - SystemClock.uptimeMillis();
            float perc = ((left) * 100) / (pref.getLong("interval", 30000));

            progressLedR.setProgress(100 - Math.round(perc));
            progressLedL.setProgress(100 - Math.round(perc));
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
            lastbeep = SystemClock.uptimeMillis();
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
