package headlines.top.tishpish.soundbarrier.SoundAnalysis;

/**
 * Created by tishpish on 10/9/17.
 */

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import headlines.top.tishpish.soundbarrier.R;


public class Analyzer extends Activity
{

    private static String volumeVisual = "";

    //private static final String TAG = "bluetooth1";
    private PowerManager.WakeLock wl;
    ValueLineSeries series;
    private Handler handler;
    int index=0;
    private SoundMeter mSensor;
    private TextView volumeLevel, status;
    ValueLineChart mCubicValueLineChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getBaseContext(), "Loading...", Toast.LENGTH_LONG).show();
        setContentView(R.layout.analyzer);

        // Wakelock
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");

        TextView volumeLevel = (TextView) findViewById(R.id.volumeLevel);
        TextView volumeBars = (TextView) findViewById(R.id.volumeBars);
        mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        /*series.addPoint(new ValueLinePoint("Jan", 2.4f));
        series.addPoint(new ValueLinePoint("Feb", 3.4f));
        series.addPoint(new ValueLinePoint("Mar", .4f));
        series.addPoint(new ValueLinePoint("Apr", 1.2f));
        series.addPoint(new ValueLinePoint("Mai", 2.6f));
        series.addPoint(new ValueLinePoint("Jun", 1.0f));
        series.addPoint(new ValueLinePoint("Jul", 3.5f));
        series.addPoint(new ValueLinePoint("Aug", 2.4f));
        series.addPoint(new ValueLinePoint("Sep", 2.4f));
        series.addPoint(new ValueLinePoint("Oct", 3.4f));
        series.addPoint(new ValueLinePoint("Nov", .4f));
        series.addPoint(new ValueLinePoint("Dec", 1.3f));

        List<ValueLinePoint> vcs = series.getSeries();
        if (vcs.size()>50)
            vcs.remove(0);*/

        mCubicValueLineChart.addSeries(series);



        mSensor = new SoundMeter();

        try
        {
            mSensor.start();
            Toast.makeText(getBaseContext(), "Sound sensor initiated.", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        handler = new Handler();

        final Runnable r = new Runnable() {

            public void run() {
                //mSensor.start();
                Log.d("Amplify","HERE");
                Toast.makeText(getBaseContext(), "Working!", Toast.LENGTH_LONG).show();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        double volume = mSensor.getTheAmplitude();
                        //int volumeToSend = (int) volume;
                        updateTextView(R.id.volumeLevel, "Volume: " + volume+"  EMA: "+mSensor.getAmplitudeEMA());

                        volumeVisual = "||";

                        updateTextView(R.id.volumeBars, "Volume: " + String.valueOf(volumeVisual));

                        series.addPoint(new ValueLinePoint(""+(index++), (float) volume));

                        List<ValueLinePoint> vcs = series.getSeries();
                        if (vcs.size()>20)
                        {
                            vcs.remove(0);
                            series.setSeries(vcs);
                        }

                        mCubicValueLineChart.addSeries(series);


                        handler.postDelayed(this, 250); // amount of delay between every cycle of volume level detection + sending the data  out
                    }
                });
            }
        };

        handler.postDelayed(r, 50);

    }


    @Override
    public void onResume() {
        super.onResume();
        wl.acquire();

        //updateTextView(R.id.status, "On resume, need to initiate sound sensor.");
        // Sound based code
        try {
            mSensor.start();
            Toast.makeText(getBaseContext(), "Sound sensor initiated.", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            Toast.makeText(getBaseContext(), "On resume, sound sensor messed up...", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed()
    {
        stop();
        super.onBackPressed();

    }

    @Override
    public void onPause() {

        //updateTextView(R.id.status, "Paused.");
        super.onPause();
        wl.release(); // Wakelock

    }


    public void updateTextView(int text_id, String toThis)
    {

        TextView val = (TextView) findViewById(text_id);
        val.setText(toThis);

        return;
    }

    private void start() throws IllegalStateException, IOException {
        mSensor.start();
    }

    private void stop() {
        mSensor.stop();
    }

    private void sleep() {
        mSensor.stop();
    }

}