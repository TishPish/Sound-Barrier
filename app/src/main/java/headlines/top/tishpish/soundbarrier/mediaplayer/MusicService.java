package headlines.top.tishpish.soundbarrier.mediaplayer;

        import android.annotation.SuppressLint;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.app.Service;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.media.AudioManager;
        import android.media.MediaPlayer;
        import android.media.MediaPlayer.OnCompletionListener;
        import android.media.MediaPlayer.OnErrorListener;
        import android.media.MediaPlayer.OnPreparedListener;
        import android.net.Uri;
        import android.net.wifi.WifiManager;
        import android.net.wifi.WifiManager.WifiLock;
        import android.os.Handler;
        import android.os.IBinder;
        import android.os.PowerManager;
        import android.support.v4.app.NotificationCompat;
        import android.util.Log;
        import android.widget.RemoteViews;
        import android.widget.Toast;


        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;

        import headlines.top.tishpish.soundbarrier.Music.AudioFocusHelper;
        import headlines.top.tishpish.soundbarrier.Music.Constant;
        import headlines.top.tishpish.soundbarrier.Music.MediaButtonHelper;
        import headlines.top.tishpish.soundbarrier.Music.MusicFocusable;
        import headlines.top.tishpish.soundbarrier.Music.MusicIntentReceiver;
        import headlines.top.tishpish.soundbarrier.Music.MusicRetriever;
        import headlines.top.tishpish.soundbarrier.Music.PrepareMusicRetrieverTask;
        import headlines.top.tishpish.soundbarrier.R;

public class MusicService extends Service implements OnCompletionListener, OnPreparedListener,
        OnErrorListener, MusicFocusable,
        PrepareMusicRetrieverTask.MusicRetrieverPreparedListener {

    private final Handler handler = new Handler();
    Intent intent;
    Intent receivedIntent;
    String json;
    private String urlToPlay="";
    // The tag we put on debug messages
    final static String TAG = "MusicPlayer";
    // These are the Intent actions that we are prepared to handle. Notice that the fact these
    // constants exist in our class is a mere convenience: what really defines the actions our
    // service can handle are the <action> tags in the <intent-filters> tag for our service in
    // AndroidManifest.xml.
    public static final String ACTION_TOGGLE_PLAYBACK = "com.example.android.musicplayer.action.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "com.example.android.musicplayer.action.PLAY";
    public static final String ACTION_PAUSE = "com.example.android.musicplayer.action.PAUSE";
    public static final String ACTION_NEXT = "com.example.android.musicplayer.action.NEXT";
    public static final String ACTION_PREVIOUS = "com.example.android.musicplayer.action.PREVIOUS";
    public static final String ACTION_URL = "com.example.android.musicplayer.action.URL";
    public static final String ACTION_SEEK = "com.example.android.musicplayer.action.SEEK";
    public static final String BROADCAST_ACTION = "com.websmithing.broadcasttest.displayevent";
    // The volume we set the media player to when we lose audio focus, but are allowed to reduce
    // the volume instead of stopping playback.
    public static final float DUCK_VOLUME = 0.1f;
    // our media player
    MediaPlayer mPlayer = null;
    // our AudioFocusHelper object, if it's available (it's available on SDK level >= 8)
    // If not available, this will be null. Always check for null before using!
    AudioFocusHelper mAudioFocusHelper = null;
    // indicates the state our service:
    enum State {
        Retrieving, // the MediaRetriever is retrieving music
        Stopped,    // media player is stopped and not prepared to play
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!). (but the media player may actually be
        Paused      // playback paused (media player ready!)
    };
    State mState = State.Retrieving;
    // if in Retrieving mode, this flag indicates whether we should start playing immediately
    // when we are ready or not.
    boolean mStartPlayingAfterRetrieve = false;
    // if mStartPlayingAfterRetrieve is true, this variable indicates the URL that we should
    // start playing when we are ready. If null, we should play a random song from the device
    Uri mWhatToPlayAfterRetrieve = null;
    enum PauseReason {
        UserRequest,  // paused by user request
        FocusLoss,    // paused because of audio focus loss
    };
    // why did we pause? (only relevant if mState == State.Paused)
    PauseReason mPauseReason = PauseReason.UserRequest;
    // do we have audio focus?
    enum AudioFocus {
        NoFocusNoDuck,    // we don't have audio focus, and can't duck
        NoFocusCanDuck,   // we don't have focus, but can play at a low volume ("ducking")
        Focused           // we have full audio focus
    }
    AudioFocus mAudioFocus = AudioFocus.NoFocusNoDuck;
    // title of the song we are currently playing
    String mSongTitle = "";
    // whether the song we are playing is streaming from the network
    boolean mIsStreaming = false;
    // Wifi lock that we hold when streaming files from the internet, in order to prevent the
    // device from shutting off the Wifi radio
    WifiLock mWifiLock;
    // The ID we use for the notification (the onscreen alert that appears at the notification
    // area at the top of the screen as an icon -- and as text as well if the user expands the
    // notification area).
    final int NOTIFICATION_ID = 1;
    // Our instance of our MusicRetriever, which handles scanning for media and
    // providing titles and URIs as we need.
    MusicRetriever mRetriever;
    // our RemoteControlClient object, which will use remote control APIs available in
    // SDK level >= 14, if they're available.
    /// RemoteControlClientCompat mRemoteControlClientCompat;
    // Dummy album art we will pass to the remote control (if the APIs are available).
    //  Bitmap mDummyAlbumArt;
    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;
    AudioManager mAudioManager;
    NotificationManager mNotificationManager;
    Notification.Builder mNotificationBuilder = null;
    JSONObject job;

    String audio_file[], audio_id[],audio_title[],audio_artist[],audio_avatar[];

    int songPosition_in_the_list =0;
    String parent;
    /**
     * Makes sure the media player exists and has been reset. This will create the media player
     * if needed, or reset the existing media player if one already exists.
     */
    void createMediaPlayerIfNeeded() {
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
            // Make sure the media player will acquire a wake-lock while playing. If we don't do
            // that, the CPU might go to sleep while the song is playing, causing playback to stop.
            //
            // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
            // permission in AndroidManifest.xml.
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            // we want the media player to notify us when it's ready preparing, and when it's done
            // playing:
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
        }
        else
            mPlayer.reset();
    }
    @SuppressLint("WifiManagerLeak")
    @Override
    public void onCreate() {
        Log.i(TAG, "debug: Creating service");


        intent = new Intent(BROADCAST_ACTION);

        // Create the Wifi lock (this does not acquire the lock, this just creates it)
        mWifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        // Create the retriever and start an asynchronous task that will prepare it.


        /////////////////////////////////////////////////////////////// this section reads from sd card


        mRetriever = new MusicRetriever(getContentResolver());
        (new PrepareMusicRetrieverTask(mRetriever,this)).execute();


        //////////////////////////////////////////////////////////////////

        // create the Audio Focus Helper, if the Audio Focus feature is available (SDK 8 or above)
        if (android.os.Build.VERSION.SDK_INT >= 8)
            mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(), this);
        else
            mAudioFocus = AudioFocus.Focused; // no focus feature, so we always "have" audio focus
        //mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.model);
        mMediaButtonReceiverComponent = new ComponentName(this, MusicIntentReceiver.class);
    }
    /**
     * Called when we receive an Intent. When we receive an intent sent to us via startService(),
     * this is the method that gets called. So here we react appropriately depending on the
     * Intent's action, which specifies what is being requested of us.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        receivedIntent = intent;

        //urlToPlay = receivedIntent.getStringExtra("urlToPlay");


        if (action.equals(ACTION_TOGGLE_PLAYBACK)) processTogglePlaybackRequest();
        else if (action.equals(ACTION_PLAY)) processPlayRequest();
        else if (action.equals(ACTION_PAUSE)) processPauseRequest();
        else if (action.equals(ACTION_URL))
        {
            json = receivedIntent.getStringExtra("json");
            parent = receivedIntent.getStringExtra("parent");
//            Log.d("dekhi",json);
            preparePlaylist();
            processAddRequest(intent);
        }
        else if (action.equals(ACTION_SEEK)) seekToPosition(intent);
        else if (action.equals(ACTION_NEXT)) processNextSong(intent);
        else if (action.equals(ACTION_PREVIOUS)) processPreviousSong(intent);
        return START_NOT_STICKY;

    }

    private void preparePlaylist()
    {
        try {
            //job = new JSONObject(json);
            songPosition_in_the_list = 0;
            //JSONArray jay = job.getJSONArray("song_data");
            audio_file = itItAllValue();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String[] itItAllValue() throws JSONException {
        String url[] = new String[20];
        audio_id =new String[20];
        audio_title =new String[20];
        audio_artist =new String[20];
        audio_avatar=new String[20];
        for (int i=0;i<20;i++)
        {
            url[i]= "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg";
            audio_id[i]= "id";
            audio_artist[i]="Fahim Arefin";
            audio_title[i]="Jaber mia ganja khay";
            audio_avatar[i]="avatar";
        }
        //url = fixAllUrl(url);
        urlToPlay = url[0];
        //Log.d("url yo pl", urlToPlay);
        return url;
    }


    private void processNextSong(Intent intent)
    {
        songPosition_in_the_list++;
        if (songPosition_in_the_list<audio_file.length)
        {
            String url = audio_file[songPosition_in_the_list];
            if (url.contains(".mp3"))
                playNextSong(audio_file[songPosition_in_the_list]);
            else {
                Toast.makeText(getApplicationContext(), "there was an error. moving to next", Toast.LENGTH_LONG).show();
                processNextSong(intent);
            }
        }
        else
        {
            songPosition_in_the_list--;
            String url = audio_file[songPosition_in_the_list];
            if (url.contains(".mp3"))
                playNextSong(audio_file[songPosition_in_the_list]);

        }
    }

    private void processPreviousSong(Intent intent)
    {
        songPosition_in_the_list--;
        if (songPosition_in_the_list<0)
            songPosition_in_the_list++;
        playNextSong(audio_file[songPosition_in_the_list]);
    }

    void processTogglePlaybackRequest() {
        if (mState == State.Paused || mState == State.Stopped) {
            processPlayRequest();
        } else {
            processPauseRequest();
        }
    }
    void processPlayRequest()
    {
        // Toast.makeText(MusicService.this, "inside player", Toast.LENGTH_SHORT).show();
        if (mState == State.Retrieving) {
            // If we are still retrieving media, just set the flag to start playing when we're
            // ready
            mWhatToPlayAfterRetrieve = null; // play a random song
            mStartPlayingAfterRetrieve = true;
            return;
        }
        tryToGetAudioFocus();
        // actually play the song
        if (mState == State.Stopped) {
            // If we're stopped, just go ahead to the next song and start playing
            playNextSong(urlToPlay);
        }
        else if (mState == State.Paused) {
            mState = State.Playing;
            setUpAsForeground(mSongTitle);
            configAndStartMediaPlayer();
        }
        // Tell any remote controls that our playback state is 'playing'.
      /*  if (mRemoteControlClientCompat != null) {
            mRemoteControlClientCompat
                    .setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
        }*/
    }


    private Runnable sendUpdatesToUI = new Runnable()
    {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 1000); // 10 seconds
        }
    };

    private void DisplayLoggingInfo() {
        //  Log.d(TAG, "entered DisplayLoggingInfo");

        if (mState == State.Paused || mState == State.Playing)
        {
            if (songPosition_in_the_list<audio_file.length)
            {
                int pos = mPlayer.getCurrentPosition();
                int duration = mPlayer.getDuration();
                int percent = (pos * 100) / duration;

                if (mPlayer.isPlaying())
                    intent.putExtra("player_state", true);
                else
                    intent.putExtra("player_state", false);

          /*  AppSharedPreference asp = AppSharedPreference.getInstance(getApplicationContext());
            String player_avatar = asp.getCurrentMusic_avatar();
            String player_title= asp.getCurrentMusic_title();
            String player_artist=asp.getCurrentMusic_artist();
            String player_id= asp.getCurrentMusic_id();*/

                intent.putExtra("parent", parent);
                intent.putExtra("json", json);
                intent.putExtra("Command", "noCommand");
                intent.putExtra("time", percent);
                intent.putExtra("currentTime", pos);
                intent.putExtra("totalDuration", duration);
                intent.putExtra("player_avatar", audio_avatar[songPosition_in_the_list]);
                intent.putExtra("player_title", audio_title[songPosition_in_the_list]);
                intent.putExtra("player_artist", audio_artist[songPosition_in_the_list]);
                intent.putExtra("player_id", audio_id[songPosition_in_the_list]);
                intent.putExtra("currentPosition", "" + songPosition_in_the_list);
                intent.setAction(Constant.PLAYER_INTENT_FILTER_NAME);
                sendBroadcast(intent);
            }
        }
    }

    void seekToPosition(Intent intent)
    {
        if (mState == State.Playing || mState ==State.Paused)
        {
            int pos = intent.getIntExtra("position",0);
            mPlayer.seekTo(pos);
        }
    }
    void processPauseRequest() {

        Log.d("inside","pause requ");
        if (mState == State.Retrieving) {
            // If we are still retrieving media, clear the flag that indicates we should start
            // playing when we're ready
            mStartPlayingAfterRetrieve = false;
            return;
        }
        if (mState == State.Playing) {
            // Pause media player and cancel the 'foreground service' state.
            mState = State.Paused;
            mPlayer.pause();
            relaxResources(false); // while paused, we always retain the MediaPlayer
            // do not give up audio focus
        }
        // Tell any remote controls that our playback state is 'paused'.
     /*   if (mRemoteControlClientCompat != null)
        {
            mRemoteControlClientCompat
                    .setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
        }*/
    }
    void processRewindRequest() {
        if (mState == State.Playing || mState == State.Paused)
            mPlayer.seekTo(0);
    }
    void processSkipRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            tryToGetAudioFocus();
            playNextSong(null);
        }
    }
    void processStopRequest() {
        processStopRequest(false);
    }
    void processStopRequest(boolean force) {
        if (mState == State.Playing || mState == State.Paused || force) {
            mState = State.Stopped;
            // let go of all resources...
            relaxResources(true);
            giveUpAudioFocus();
            // Tell any remote controls that our playback state is 'paused'.
       /*     if (mRemoteControlClientCompat != null) {
                mRemoteControlClientCompat
                        .setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
            }*/
            // service is no longer necessary. Will be started again if needed.
            stopSelf();
        }
    }

    void relaxResources(boolean releaseMediaPlayer) {
        // stop being a foreground service
        stopForeground(true);
        // stop and release the Media Player, if it's available
        if (releaseMediaPlayer && mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
        // we can also release the Wifi lock, if we're holding it
        if (mWifiLock.isHeld()) mWifiLock.release();
    }
    void giveUpAudioFocus() {
        if (mAudioFocus == AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.abandonFocus())
            mAudioFocus = AudioFocus.NoFocusNoDuck;
    }
    /**
     * Reconfigures MediaPlayer according to audio focus settings and starts/restarts it. This
     * method starts/restarts the MediaPlayer respecting the current audio focus state. So if
     * we have focus, it will play normally; if we don't have focus, it will either leave the
     * MediaPlayer paused or set it to a low volume, depending on what is allowed by the
     * current focus settings. This method assumes mPlayer != null, so if you are calling it,
     * you have to do so from a context where you are sure this is the case.
     */
    void configAndStartMediaPlayer() {
        if (mAudioFocus == AudioFocus.NoFocusNoDuck) {
            // If we don't have audio focus and can't duck, we have to pause, even if mState
            // is State.Playing. But we stay in the Playing state so that we know we have to resume
            // playback once we get the focus back.
            if (mPlayer.isPlaying()) mPlayer.pause();
            return;
        }
        else if (mAudioFocus == AudioFocus.NoFocusCanDuck)
            mPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME);  // we'll be relatively quiet
        else
            mPlayer.setVolume(1.0f, 1.0f); // we can be loud
        if (!mPlayer.isPlaying()) mPlayer.start();
    }
    void processAddRequest(Intent intent)
    {
        // Log.d("dekhi ki",urlToPlay);
        if (mState == State.Retrieving)
        {

            mWhatToPlayAfterRetrieve = Uri.parse(urlToPlay);
            mStartPlayingAfterRetrieve = true;
            // Log.d("url to play vbvbv",urlToPlay);
        }
        else if (mState == State.Playing || mState == State.Paused ) {

            processTogglePlaybackRequest();

        }
        else
        {
            Log.i(TAG, "Playing from URL/path: " + "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg");
            tryToGetAudioFocus();
            playNextSong("http://www.vorbis.com/music/Epoq-Lepidoptera.ogg");
        }
    }
    void tryToGetAudioFocus() {
        if (mAudioFocus != AudioFocus.Focused && mAudioFocusHelper != null
                && mAudioFocusHelper.requestFocus())
            mAudioFocus = AudioFocus.Focused;
    }
    /**
     * Starts playing the next song. If manualUrl is null, the next song will be randomly selected
     * from our Media Retriever (that is, it will be a random song in the user's device). If
     * manualUrl is non-null, then it specifies the URL or path to the song that will be played
     * next.
     */
    void playNextSong(String manualUrl) {
        mState = State.Stopped;
        relaxResources(false); // release everything except MediaPlayer
        try {
            MusicRetriever.Item playingItem = null;
            if (manualUrl != null) {
                // set the source of the media player to a manual URL or path
                createMediaPlayerIfNeeded();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(manualUrl);
                mIsStreaming = manualUrl.startsWith("http:") || manualUrl.startsWith("https:");
                playingItem = new MusicRetriever.Item(0, null, manualUrl, null, 0);
            }
            else
            {


            }
            mSongTitle = playingItem.getTitle();
            mState = State.Preparing;
            setUpAsForeground(mSongTitle + " (loading)");
            // Use the media button APIs (if available) to register ourselves for media button
            // events
            MediaButtonHelper.registerMediaButtonEventReceiverCompat(
                    mAudioManager, mMediaButtonReceiverComponent);
            // Use the remote control APIs (if available) to set the playback state
           /* if (mRemoteControlClientCompat == null) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                intent.setComponent(mMediaButtonReceiverComponent);
                mRemoteControlClientCompat = new RemoteControlClientCompat(
                        PendingIntent.getBroadcast(this /*context*///,
            //0 /*requestCode, ignored*/, intent /*intent*/,// 0 /*flags*/ /*));
           /*     RemoteControlHelper.registerRemoteControlClient(mAudioManager,
                        mRemoteControlClientCompat);
            }
            mRemoteControlClientCompat.setPlaybackState(
                    RemoteControlClient.PLAYSTATE_PLAYING);
            mRemoteControlClientCompat.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP);
            // Update the remote controls
            mRemoteControlClientCompat.editMetadata(true)
                    .putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, playingItem.getArtist())
                    .putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, playingItem.getAlbum())
                    .putString(MediaMetadataRetriever.METADATA_KEY_TITLE, playingItem.getTitle())
                    .putLong(MediaMetadataRetriever.METADATA_KEY_DURATION,
                            playingItem.getDuration())
                    // TODO: fetch real item artwork
                    .putBitmap(
                            RemoteControlClientCompat.MetadataEditorCompat.METADATA_KEY_ARTWORK,
                            mDummyAlbumArt)
                    .apply();*/
            // starts preparing the media player in the background. When it's done, it will call
            // our OnPreparedListener (that is, the onPrepared() method on this class, since we set
            // the listener to 'this').
            //
            // Until the media player is prepared, we *cannot* call start() on it!
            mPlayer.prepareAsync();
            // If we are streaming from the internet, we want to hold a Wifi lock, which prevents
            // the Wifi radio from going to sleep while the song is playing. If, on the other hand,
            // we are *not* streaming, we want to release the lock if we were holding it before.
            if (mIsStreaming) mWifiLock.acquire();
            else if (mWifiLock.isHeld()) mWifiLock.release();
        }
        catch (IOException ex) {
            Log.e("MusicService", "IOException playing next song: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    /** Called when media player is done playing current song. */
    public void onCompletion(MediaPlayer player)
    {
        songPosition_in_the_list++;
        if (songPosition_in_the_list>=audio_file.length)
            songPosition_in_the_list--;
        playNextSong(audio_file[songPosition_in_the_list]);
        /*
        intent.putExtra("Command","SendNextURL");
        intent.setAction(Constant.PLAYER_INTENT_FILTER_NAME);
        sendBroadcast(intent);*/
        //playNextSong("http://searchgurbani.com/audio/sggs/1.mp3");
    }
    /** Called when media player is done preparing. */
    public void onPrepared(MediaPlayer player) {
        // The media player is done preparing. That means we can start playing!
        mState = State.Playing;
        updateNotification(mSongTitle + " (playing)");
        configAndStartMediaPlayer();
        handler.post(sendUpdatesToUI);

    }
    /** Updates the notification. */
    void updateNotification(String text)
    {

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);

        ///// when next is pressed
        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);
        views.setOnClickPendingIntent(R.id.next, pnextIntent);
        ////// when previous is pressed

        Intent prevIntent = new Intent(this, MusicService.class);
        prevIntent.setAction(ACTION_PREVIOUS);
        PendingIntent pprevIntent = PendingIntent.getService(this, 0, prevIntent, 0);
        views.setOnClickPendingIntent(R.id.previous, pprevIntent);
        ////// when play is pressed
        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(ACTION_TOGGLE_PLAYBACK);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        views.setOnClickPendingIntent(R.id.play, pplayIntent);
        if (mState == State.Playing)
        {
            views.setImageViewResource(R.id.play,R.mipmap.audiopause);
        }
        else
        {
            views.setImageViewResource(R.id.play,R.mipmap.audioplay);
        }

        //////////////////////////////////////////////////////////////
        views.setTextViewText(R.id.title, "gaan chole na?");

        //views.setImageViewUri(R.id.notif_id,Uri.parse(audio_avatar[songPosition_in_the_list]));

        Intent notificationIntent = new Intent(this, MyMediaPlayer.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon

                .setSmallIcon(R.drawable.model)
                // Set Ticker Message
                .setTicker(text)
                // Set PendingIntent into Notification
                .setContentIntent(pendingIntent)
                // Set RemoteViews into Notification
                .setContent(views);


        final Notification notification = builder.build();
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = views;
        }
       /* if (audio_avatar[songPosition_in_the_list].length()>7) {
            Picasso.with(getApplicationContext())
                    .load(audio_avatar[songPosition_in_the_list])
                    .into(views, R.id.notif_id, NOTIFICATION_ID, notification);
            NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            startForeground(NOTIFICATION_ID, notification);
        }*/

    }
    /**
     * Configures service as a foreground service. A foreground service is a service that's doing
     * something the user is actively aware of (such as playing music), and must appear to the
     * user as a notification. That's why we create the notification here.
     */
    void setUpAsForeground(String text)

    {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);
        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.setAction(ACTION_NEXT);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);
        views.setOnClickPendingIntent(R.id.next, pnextIntent);
        views.setTextViewText(R.id.title, "hello tishpish");

        ////// when previous is pressed

        Intent prevIntent = new Intent(this, MusicService.class);
        prevIntent.setAction(ACTION_PREVIOUS);
        PendingIntent pprevIntent = PendingIntent.getService(this, 0, prevIntent, 0);
        views.setOnClickPendingIntent(R.id.previous, pprevIntent);

        ////// when play is pressed
        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.setAction(ACTION_TOGGLE_PLAYBACK);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        views.setOnClickPendingIntent(R.id.play, pplayIntent);
        if (mState == State.Playing)
        {
            views.setImageViewResource(R.id.play,R.mipmap.audiopause);
        }
        else
        {
            views.setImageViewResource(R.id.play,R.mipmap.audioplay);
        }


        //views.setImageViewUri(R.id.notif_id,Uri.parse(audio_avatar[songPosition_in_the_list]));

        Intent notificationIntent = new Intent(this, MyMediaPlayer.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.model)
                // Set Ticker Message
                .setTicker(text)
                // Set PendingIntent into Notification
                .setContentIntent(pendingIntent)
                // Set RemoteViews into Notification
                .setContent(views);

        final Notification notification = builder.build();
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification.bigContentView = views;
        }

       /* if (audio_avatar[songPosition_in_the_list].length()>7)
        {
            Picasso.with(getApplicationContext())
                    .load(audio_avatar[songPosition_in_the_list])
                    .into(views, R.id.notif_id, NOTIFICATION_ID, notification);
            NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            startForeground(NOTIFICATION_ID, notification);
        }*/

    }
    /**
     * Called when there's an error playing media. When this happens, the media player goes to
     * the Error state. We warn the user about the error and reset the media player.
     */
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        Toast.makeText(getApplicationContext(), "Media player error! Try pressing play again.",
                Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Error: what=" + String.valueOf(what) + ", extra=" + String.valueOf(extra));
        mState = State.Stopped;
        relaxResources(true);
        giveUpAudioFocus();
        createMediaPlayerIfNeeded();
        mState = State.Playing;
        processNextSong(intent);
        return true; // true indicates we handled the error
    }
    public void onGainedAudioFocus() {
        //  Toast.makeText(getApplicationContext(), "gained audio focus.", Toast.LENGTH_SHORT).show();
        mAudioFocus = AudioFocus.Focused;
        // restart media player with new focus settings
        if (mState == State.Playing)
            configAndStartMediaPlayer();
    }
    public void onLostAudioFocus(boolean canDuck) {
        // Toast.makeText(getApplicationContext(), "lost audio focus." + (canDuck ? "can duck" :"no duck"), Toast.LENGTH_SHORT).show();
        mAudioFocus = canDuck ? AudioFocus.NoFocusCanDuck : AudioFocus.NoFocusNoDuck;
        // start/restart/pause media player with new focus settings
        if (mPlayer != null && mPlayer.isPlaying())
            configAndStartMediaPlayer();
    }
    public void onMusicRetrieverPrepared()
    {
        // Done retrieving!
        mState = State.Stopped;
        // If the flag indicates we should start playing after retrieving, let's do that now.
        if (mStartPlayingAfterRetrieve)
        {
            tryToGetAudioFocus();
            playNextSong(mWhatToPlayAfterRetrieve == null ? null : mWhatToPlayAfterRetrieve.toString());
        }
    }
    @Override
    public void onDestroy() {
        // Service is being killed, so make sure we release our resources

        handler.removeCallbacks(sendUpdatesToUI);
        mState = State.Stopped;
        relaxResources(true);
        giveUpAudioFocus();
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}