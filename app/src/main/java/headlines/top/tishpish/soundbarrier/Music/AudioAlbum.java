package headlines.top.tishpish.soundbarrier.Music;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import headlines.top.tishpish.soundbarrier.R;
import headlines.top.tishpish.soundbarrier.mediaplayer.MyMediaPlayer;

/**
 * Created by Farhad on 5/9/2016.
 */
public class AudioAlbum extends Fragment
{
    ListView popular_list;
    AlbumAdapter albumListAdapetr;
    List<MusicRetriever.Item> topSongsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v =inflater.inflate(R.layout.audio_menu_album,container,false);
        popular_list = (ListView) v.findViewById(R.id.audio_album_list_id);
        albumListAdapetr = new AlbumAdapter(getActivity(), topSongsList);
        popular_list.setAdapter(albumListAdapetr);
        //prepareTopSongsData();
        prepare();

        popular_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent  player = new Intent(view.getContext(),MyMediaPlayer.class);
                MusicRetriever.Item albumData = topSongsList.get(i);
                //Toast.makeText(context, "Item: " + albumData.getId() + " is selected", Toast.LENGTH_SHORT).show();
                player.putExtra("selected",albumData.getId());
                player.putExtra("parent","trending");  /// to check the origin of the player
                player.putExtra("json","");
                startActivity(player);


            }
        });

        return v;
    }
/*
    public void getTopSongData() throws UnsupportedEncodingException
    {

    }*/

    public void prepare()
    {
        ContentResolver mContentResolver = getActivity().getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //Log.i(TAG, "Querying media...");
        //Log.i(TAG, "URI: " + uri.toString());
        // Perform a query on the content resolver. The URI we're passing specifies that we
        // want to query for all audio media on external storage (e.g. SD card)
        Cursor cur = mContentResolver.query(uri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        //Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));
        if (cur == null) {
            // Query failed...
            //Log.e(TAG, "Failed to retrieve music: cursor is null :-(");
            return;
        }
        if (!cur.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            //Log.e(TAG, "Failed to move cursor to first row (no query results).");
            return;
        }
        //Log.i(TAG, "Listing...");
        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        //Log.i(TAG, "Title column index: " + String.valueOf(titleColumn));
        //Log.i(TAG, "ID column index: " + String.valueOf(titleColumn));
        // add each song to mItems
       do {
            //Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn));
            topSongsList.add(new MusicRetriever.Item(
                    cur.getLong(idColumn),
                    cur.getString(artistColumn),
                    cur.getString(titleColumn),
                    cur.getString(albumColumn),
                    cur.getLong(durationColumn)));
        } while (cur.moveToNext());

        albumListAdapetr.notifyDataSetChanged();
    }



    public class AlbumAdapter extends BaseAdapter
    {
        Activity activity;
        List<MusicRetriever.Item> topSong;

        public AlbumAdapter(Activity activity, List<MusicRetriever.Item> topSong)
        {
            super();
            this.activity = activity;
            this.topSong = topSong;
        }

        @Override
        public int getCount() {
            return topSong.size();
        }

        @Override
        public MusicRetriever.Item getItem(int i) {
            return topSong.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            LayoutInflater inflater=activity.getLayoutInflater();
            view=inflater.inflate(R.layout.audio_menu_album_card,null);

            ImageView topSongsCover = (ImageView)view.findViewById(R.id.image);
            TextView title=(TextView) view.findViewById(R.id.albumname);
            TextView singer=(TextView) view.findViewById(R.id.albumartist);
            TextView totalPlay=(TextView) view.findViewById(R.id.myalbum);
            ImageView options = (ImageView) view.findViewById(R.id.topsongsoptions);

            MusicRetriever.Item data = topSong.get(i);

            title.setText(data.getTitle());
            singer.setText(data.getArtist());
            totalPlay.setText(data.getDuration()+"");
            String url = data.getURI().toString();
            Log.d("url",url);



            /*if (!url.isEmpty()) {
                Picasso.with(activity.getApplicationContext())
                        .load(url)
                        .placeholder(R.drawable.logo)   // optional
                        .error(R.drawable.model)       // optional// optional
                        .into(topSongsCover);
            }*/
            return view;
        }
    }
    
}
