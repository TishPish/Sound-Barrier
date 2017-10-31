package headlines.top.tishpish.soundbarrier;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.io.IOException;
import java.util.List;

import headlines.top.tishpish.soundbarrier.Database.AppSharedPreference;
import headlines.top.tishpish.soundbarrier.Fragment.Equalizer;
import headlines.top.tishpish.soundbarrier.Fragment.HomeFragment;
import headlines.top.tishpish.soundbarrier.Fragment.Statistics;
import headlines.top.tishpish.soundbarrier.Music.AudioMenu;
import headlines.top.tishpish.soundbarrier.Music.Constant;
import headlines.top.tishpish.soundbarrier.Music.SlidingTabLayout;
import headlines.top.tishpish.soundbarrier.Music.ViewPagerAdapter;
import headlines.top.tishpish.soundbarrier.SoundAnalysis.Analyzer;
import headlines.top.tishpish.soundbarrier.SoundAnalysis.SoundMeter;
import headlines.top.tishpish.soundbarrier.mediaplayer.MusicService;
import headlines.top.tishpish.soundbarrier.mediaplayer.MyMediaPlayer;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    //ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    int Numboftabs =3;
    String json="";
    CharSequence Titles[]={"Recent","Gallery","All"};
    ViewPager pager;
    ImageView avatar, play,next;
    TextView singer, title;
    String currentId = "-1";
    MyBroadcastReceiver myBroadcast;
    Intent intent;
    IntentFilter intentFilter;
    LinearLayout player;
    Button setmoder;
    ToggleButton toggle;
    ValueLineChart mCubicValueLineChart;
    private PowerManager.WakeLock wl;
    ValueLineSeries series;
    private Handler handler;
    private SoundMeter mSensor;
    int index = 0;
    boolean player_state;
    double avgVolume=0;
    Runnable r;
    boolean use = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        intent = new Intent(this, MusicService.class);
        intentFilter = new IntentFilter(Constant.PLAYER_INTENT_FILTER_NAME);
        setContentView(R.layout.activity_home);
        //getSupportActionBar().setTitle("Sound Barrier");

        AppSharedPreference adp = AppSharedPreference.getInstance(getApplicationContext());
        adp.updateVisit();

        /*
    compile 'com.opalox.rangebarvertical:rangebarvertical:1.1'*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sound Barrier");

        title = (TextView) findViewById(R.id.song_name);
        singer = (TextView) findViewById(R.id.song_artist);
        avatar = (ImageView) findViewById(R.id.avatar);
        play = (ImageView) findViewById(R.id.play);
        next = (ImageView) findViewById(R.id.next);
        player = (LinearLayout) findViewById(R.id.player);
        player.setVisibility(View.GONE);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");

        mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);
        mCubicValueLineChart.setVisibility(View.GONE);


        series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);

        mCubicValueLineChart.addSeries(series);

        mSensor = new SoundMeter();



        toggle = (ToggleButton) findViewById(R.id.toggle);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
               // Toast.makeText(getApplicationContext(),isChecked? "checked":"not checked",Toast.LENGTH_SHORT).show();
                if (isChecked)
                {
                    AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager.isWiredHeadsetOn())
                    {
                        mCubicValueLineChart.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "headphone on, init barrier", Toast.LENGTH_SHORT).show();
                        use = true;
                        runBarrier();

                    }
                    else
                    {
                        toggle.setChecked(false);
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Home.this);
                        builder1.setMessage("Please connect earphone and try again");
                        builder1.setCancelable(true);
                        builder1.setTitle("Failed to start");
                        builder1.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }

                }
                else
                {
                    mSensor.stop();
                    use = false;
                    handler.removeCallbacks(r);
                    r = null;
                    mCubicValueLineChart.setVisibility(View.GONE);

                }

            }
        });





        player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent  player = new Intent(getApplicationContext(),MyMediaPlayer.class);
                player.putExtra("json",json);
                player.putExtra("parent","player");
                startActivity(player);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent play = new Intent(getApplicationContext(), MusicService.class);
                play.setAction("com.example.android.musicplayer.action.NEXT");
                startService(play);*/
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent play = new Intent(getApplicationContext(), MusicService.class);
                play.setAction("com.example.android.musicplayer.action.TOGGLE_PLAYBACK");
                startService(play);
            }
        });

        /////////////////////////////////////////////////////////////////
/*
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(pager);  */




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.nav_camera);
    }

    void runBarrier()
    {
        try
        {
            mSensor.start();
            Toast.makeText(getBaseContext(), "Sound sensor initiated.", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (r==null)
        {

            handler = new Handler();
            r = new Runnable() {

                public void run()
                {


                    if (use)
                    {



                        Log.d("Amplify","HERE");
                        Toast.makeText(getBaseContext(), "Working!", Toast.LENGTH_LONG).show();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                avgVolume =0;
                                double volume = mSensor.getTheAmplitude();


                                series.addPoint(new ValueLinePoint(""+(index++), (float) volume));

                                List<ValueLinePoint> vcs = series.getSeries();
                                if (vcs.size()>50)
                                {
                                    vcs.remove(0);
                                    series.setSeries(vcs);
                                }

                                for (int i=vcs.size()-1;i>0 && i>= vcs.size()-5;i--)
                                {
                                    ValueLinePoint ok = vcs.get(i);
                                    avgVolume+=ok.getValue();
                                }
                                avgVolume/=5;
                                Log.d("Avg volume: ",avgVolume+" ");
                                if (avgVolume!=1)
                                {
                                    AppSharedPreference asp = AppSharedPreference.getInstance(getBaseContext());
                                    asp.updateTime(asp.GetTime()+50);
                                }
                                if (avgVolume>1900 && player_state)
                                {
                                    Intent play = new Intent(getApplicationContext(), MusicService.class);
                                    play.setAction("com.example.android.musicplayer.action.TOGGLE_PLAYBACK");
                                    startService(play);


                                }



                                mCubicValueLineChart.addSeries(series);


                                handler.postDelayed(this, 50); // amount of delay between every cycle of volume level detection + sending the data  out
                            }
                        });

                    }

                }
            };

        }

        handler.postDelayed(r, 20);
    }

    @Override
    public void onBackPressed()
    {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            stop();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);
        return true;


    }

    private void displaySelectedScreen(int itemId)
    {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId)
        {
            case R.id.nav_camera:
                fragment = new HomeFragment();
                break;

            case R.id.nav_gallery:
                fragment = new Equalizer();
                break;
            case R.id.nav_slideshow:
                fragment = new Statistics();
                break;
        }

        if (fragment != null)
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment,"MAP_Fragment");
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

    private void start() throws IllegalStateException, IOException {
    mSensor.start();
}

    private void stop() {
        mSensor.stop();
    }

    private void sleep() {
        mSensor.stop();
    }


    public class MyBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //Log.d("receiver","music state");
            String command = intent.getStringExtra("Command");
            int x = intent.getIntExtra("time", 0);

            player.setVisibility(View.VISIBLE);
            String player_avatar = intent.getStringExtra("player_avatar");
            String player_title = intent.getStringExtra("player_title");
            String player_artist = intent.getStringExtra("player_artist");
            json = intent.getStringExtra("json");
            player_state = intent.getBooleanExtra("player_state",true);

            if (player_state)
                play.setImageDrawable(getResources().getDrawable(R.mipmap.audiopause));
            else
                play.setImageDrawable(getResources().getDrawable(R.mipmap.audioplay));

            singer.setText(player_artist);
            title.setText(player_title);

            /*if (!currentId.equals(intent.getStringExtra("player_id"))) {
                if (!player_avatar.isEmpty()) {
                    Picasso.with(getApplicationContext())
                            .load(player_avatar)
                            .placeholder(R.drawable.logo)   // optional
                            .error(R.drawable.model)       // optional// optional
                            .into(avatar);
                }

                //      Toast.makeText(getApplicationContext(),"playing: "+ player_avatar, Toast.LENGTH_SHORT).show();
                currentId = intent.getStringExtra("player_id");
                AppSharedPreference asp = AppSharedPreference.getInstance(getApplicationContext());
                asp.setCurrentMusic_id(currentId);
            }*/

        }

    }
}
