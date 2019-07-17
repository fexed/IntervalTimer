package com.fexed.intervaltimer;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private long millisecondtime, starttime, updatetime, timebuff = 0L;
    private int s, m, millis;
    private Handler updatehandler, beephandler;
    private TextView timetxtv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        updatehandler = new Handler();
        timetxtv = findViewById(R.id.timetext);

        final FloatingActionButton startfab = findViewById(R.id.startbtn);
        final FloatingActionButton stopfab = findViewById(R.id.stopbtn);
        View.OnClickListener start = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                starttime = SystemClock.uptimeMillis();
                updatehandler.postDelayed(runnable, 0);
                ((View) startfab).setVisibility(View.INVISIBLE);
                ((View) stopfab).setVisibility(View.VISIBLE);
            }
        };
        View.OnClickListener pause = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timebuff += millisecondtime;
                updatehandler.removeCallbacks(runnable);
                ((View) startfab).setVisibility(View.VISIBLE);
                ((View) stopfab).setVisibility(View.INVISIBLE);
            }
        };

        startfab.setOnClickListener(start);
        stopfab.setOnClickListener(pause);
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            millisecondtime = SystemClock.uptimeMillis() - starttime;

            updatetime = timebuff + millisecondtime;

            s = (int) (updatetime / 1000);

            m = s / 60;

            s = s % 60;

            millis = (int) (updatetime % 1000);

            timetxtv.setText("" + m + ":"
                    + String.format("%02d", s));

            updatehandler.postDelayed(this, 0);
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
