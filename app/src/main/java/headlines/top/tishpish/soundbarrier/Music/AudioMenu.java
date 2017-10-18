package headlines.top.tishpish.soundbarrier.Music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import headlines.top.tishpish.soundbarrier.R;
import headlines.top.tishpish.soundbarrier.mediaplayer.MusicService;
import headlines.top.tishpish.soundbarrier.mediaplayer.MyMediaPlayer;

/**
 * Created by Farhad on 5/9/2016.
 */
public class AudioMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    int Numboftabs =3;
    String json="";
    CharSequence Titles[]={"Trending","Popular","Albums"};
    ViewPager pager;
    ImageView avatar, play,next;
    TextView singer, title;
    String currentId = "-1";
    MyBroadcastReceiver myBroadcast;
    Intent intent;
    IntentFilter intentFilter;
    LinearLayout player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        intent = new Intent(this, MusicService.class);
        intentFilter = new IntentFilter(Constant.PLAYER_INTENT_FILTER_NAME);
        setContentView(R.layout.audio_activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        title = (TextView) findViewById(R.id.song_name);
        singer = (TextView) findViewById(R.id.song_artist);
        avatar = (ImageView) findViewById(R.id.avatar);
        play = (ImageView) findViewById(R.id.play);
        next = (ImageView) findViewById(R.id.next);
        player = (LinearLayout) findViewById(R.id.player);
        player.setVisibility(View.GONE);
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
                Intent play = new Intent(getApplicationContext(), MusicService.class);
                play.setAction("com.example.android.musicplayer.action.NEXT");
                startService(play);
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

        tabs.setViewPager(pager);



        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/

       /* NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

       // inItNavHeader(navigationView);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


    public class MyBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String command = intent.getStringExtra("Command");
            int x = intent.getIntExtra("time", 0);

                player.setVisibility(View.VISIBLE);
                String player_avatar = intent.getStringExtra("player_avatar");
                String player_title = intent.getStringExtra("player_title");
                String player_artist = intent.getStringExtra("player_artist");
                json = intent.getStringExtra("json");
                boolean player_state = intent.getBooleanExtra("player_state",true);

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

/*
 some decllarations:

    currently playing audio will be updated in the shared preference.

 */