package headlines.top.tishpish.soundbarrier.mediaplayer;

/**
 * Created by Tiash on 5/18/2016.
 */
import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import headlines.top.tishpish.soundbarrier.Music.Constant;
import headlines.top.tishpish.soundbarrier.R;

/**
 * Main activity: shows media player buttons. This activity shows the media player buttons and
 * lets the user click them. No media handling is done here -- everything is done by passing
 * Intents to our {@link MusicService}.
 * */
public class MyMediaPlayer extends Activity implements OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback,SeekBar.OnSeekBarChangeListener{
    /**
     * The URL we suggest as default when adding by URL. This is just so that the user doesn't
     * have to find an URL to test this sample.
     */

    View DialogView;
    WindowManager windowManager;


    int likeButtonStatus =-1;  // -1 is for un initialized, 1 is true, 0 for false
    final String SUGGESTED_URL = "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg";
    ImageView mPlayButton,mNextButton,mPreviousButton,cover,likebutton,settings;
    String json,parent;
    List<String> songURLList = new ArrayList<String>();
    int songPosition_in_the_list =0;
    TextView title,artist,playcount,likecount;
    SeekBar seekBar;
    boolean seekBarisPressed= false;
    TextView totalTime, currentTime;
    int totalDuration=0;
    String currentId = "-1";
    final private int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=  124;
    MyBroadcastReceiver myBroadcast;
    Intent intent;
    IntentFilter intentFilter;
    String audio_file[], audio_id[],audio_title[],audio_artist[],audio_avatar[];
    @Override
    public void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        intent = new Intent(this, MusicService.class);
        intentFilter = new IntentFilter(Constant.PLAYER_INTENT_FILTER_NAME);

        setContentView(R.layout.audio_music_player);
        mPlayButton = (ImageView) findViewById(R.id.play);
        mPlayButton.setOnClickListener(this);

        mNextButton = (ImageView) findViewById(R.id.next);
        mNextButton.setOnClickListener(this);

        mPreviousButton = (ImageView) findViewById(R.id.previous);
        mPreviousButton.setOnClickListener(this);

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        totalTime = (TextView) findViewById(R.id.totalTime);
        currentTime = (TextView) findViewById(R.id.currentTime);

        cover = (ImageView) findViewById(R.id.coverImage);
        title = (TextView) findViewById(R.id.songTitle);
        artist = (TextView) findViewById(R.id.artist);

        likecount = (TextView) findViewById(R.id.likecount);
        playcount = (TextView) findViewById(R.id.playcount);




        if (savedInstanceState == null)
        {
            Bundle extras = getIntent().getExtras();
            if (extras!=null)
            {

                JSONObject job;
                json = extras.getString("json");
                parent = extras.getString("parent");
                try {
                    job = new JSONObject(json);
                    songPosition_in_the_list = job.getInt("now_playing");
                    JSONArray jay = job.getJSONArray("song_data");
                    //audio_file = itItAllValue(jay);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (isMyServiceRunning(MusicService.class))
                {
                    if (!parent.equals("player")) {
                        Toast.makeText(getApplicationContext(), "stopping previous service", Toast.LENGTH_SHORT).show();
                        stopService(intent);
                        prepareList(audio_file);
                        getPermission();
                    }
                    else
                    {
                        prepareList(audio_file);
                    }
                }
                else
                {
                    prepareList(audio_file);
                    getPermission();
                }
            }
        }
    }





    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void onClick(View target) {
        // Send the correct intent to the MusicService, according to the button that was clicked
        if (target == mPlayButton)
        {
            Intent play = new Intent(getApplicationContext(), MusicService.class);
            play.setAction("com.example.android.musicplayer.action.TOGGLE_PLAYBACK");
            startService(play);
            /*
            Log.d("dekhi","dekhtesi");
            resetUI();
            getPermission();*/
        }
        else if (target == mNextButton)
        {
            resetUI();
            playNextSong();
        }
        else if (target == mPreviousButton)
        {

            resetUI();

            Intent play = new Intent(this, MusicService.class);
            play.setAction("com.example.android.musicplayer.action.PREVIOUS");
            startService(play);
        }

    }

    public void resetUI()
    {
        seekBar.setProgress(0);
        currentTime.setText("00:00");
        totalTime.setText("00:00");
    }

    protected Map<String, String> getDummyParams() {
        Map<String, String> dataForParams = new HashMap<String, String>();
        return dataForParams;
    }




    private void playNextSong()
    {

        seekBar.setProgress(0);
        Intent play = new Intent(this, MusicService.class);
        play.setAction("com.example.android.musicplayer.action.NEXT");
        startService(play);
    }
    public void getAudioData(String date,String limit,String offset )
    {
       /* RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = String.format(Constant.API_GET_MAIN_PAGE_AUDIO_LINK ,date, limit , offset);
        StringRequest myReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject audio = new JSONObject(response);
                    JSONObject audio_data = audio.getJSONObject("audio_data");
                    JSONArray data = audio_data.getJSONArray("data");
                    prepareAudioData(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        MyRequestQueue.add(myReq);*/
    }
    public void prepareAudioData(JSONArray audioData) throws JSONException
    {
       // Toast.makeText(getApplicationContext(), "Server call", Toast.LENGTH_LONG).show();
    }

    public void getPermission()
    {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        {
            if (songURLList.size()>0) {

              //  AppSharedPreference asp = AppSharedPreference.getInstance(getApplicationContext());
              //  asp.setCurrentMusic(audio_avatar[songPosition_in_the_list], audio_title[songPosition_in_the_list],audio_artist[songPosition_in_the_list],audio_id[songPosition_in_the_list]);
                Intent play = new Intent(this, MusicService.class);
                play.putExtra("json",json);
                play.putExtra("parent",parent);
                play.setAction("com.example.android.musicplayer.action.URL");
                startService(play);
            }
        } else {


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }

    }

    @Override
    public void onPause()
    {
        unregisterReceiver(myBroadcast);
        super.onPause();
    }

    @Override
    public void onResume()
    {
        myBroadcast = new MyBroadcastReceiver();

        registerReceiver(myBroadcast,intentFilter);
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                startService(new Intent(MusicService.ACTION_TOGGLE_PLAYBACK));
                return true;
            case KeyEvent.KEYCODE_BACK:

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        seekBarisPressed = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        int pos = progressToTimer(seekBar.getProgress(), totalDuration);
        Log.d("seek",pos+"  ");
        Intent play = new Intent(  this, MusicService.class );
        seekBarisPressed = false;
        play.setAction("com.example.android.musicplayer.action.SEEK");
        play.putExtra("position",pos);
        startService(play);
    }


    public class MyBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String command = intent.getStringExtra("Command");
            if (command.equals("SendNextURL"))
            {
                playNextSong();
                return;
            }
            if (!seekBarisPressed)
            {
                int x = intent.getIntExtra("time", 0);
                seekBar.setProgress(x);
                int current = intent.getIntExtra("currentTime", 0);
                int total = intent.getIntExtra("totalDuration", 1);
                totalTime.setText(getTimeString(total));
                currentTime.setText(getTimeString(current));
                totalDuration = total;
                boolean state = intent.getBooleanExtra("player_state", false);
                if (state == false)
                    mPlayButton.setImageDrawable(getResources().getDrawable(R.mipmap.audioplay));
                else
                    mPlayButton.setImageDrawable(getResources().getDrawable(R.mipmap.audiopause));
                if (!currentId.equals(intent.getStringExtra("player_id"))) {

                    currentId = intent.getStringExtra("player_id");
                    String audio_avatar = intent.getStringExtra("player_avatar");


                    title.setText(intent.getStringExtra("player_title"));
                    artist.setText(intent.getStringExtra("player_artist"));
                }
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            Log.v("we are fine","Permission: "+permissions[0]+ "was "+grantResults[0]);
       //     Toast.makeText(getApplicationContext(), "on Request playing", Toast.LENGTH_LONG).show();
            Intent play = new Intent(this, MusicService.class);
            play.setAction("com.example.android.musicplayer.action.URL");
            startService(play);
        }
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        //int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf

                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }

    public void prepareList(String id[])
    {
        for (int i=0;i<20;i++)
        {
            songURLList.add("http://www.vorbis.com/music/Epoq-Lepidoptera.ogg");
            /*if (id[i].length()>30)
            {
                songURLList.add( id[i]);
                Log.d("myyurl", id[i]);
            }
            else
            {
                songURLList.add("http://api.awaza.net/music_files/" + id[i]);
                Log.d("myyurl", "http://api.awaza.net/music_files/" + id[i]);
            }*/
        }

    }


}