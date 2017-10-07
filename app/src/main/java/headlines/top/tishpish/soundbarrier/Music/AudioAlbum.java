package headlines.top.tishpish.soundbarrier.Music;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    List<AlbumData> topSongsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v =inflater.inflate(R.layout.audio_menu_album,container,false);
        popular_list = (ListView) v.findViewById(R.id.audio_album_list_id);
        albumListAdapetr = new AlbumAdapter(getActivity(), topSongsList);
        popular_list.setAdapter(albumListAdapetr);
        prepareTopSongsData();

        popular_list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                /*Context context = view.getContext();
                TextView tv = (TextView) view.findViewById(R.id.albumname);
                AlbumData albumData = topSongsList.get(i);
                Toast.makeText(context, "Item: " + albumData.getId() + " is selected", Toast.LENGTH_SHORT).show();*/

                Intent  player = new Intent(view.getContext(),MyMediaPlayer.class);
                //SongList slist = new SongList(""+i,topSongsList.get(i).getSongID(),audio_avatar,id,audio_file,title,artist);
               // String jsonfile = slist.Encode();
                Log.d("json in trending","");
                player.putExtra("parent","trending");  /// to check the origin of the player
                player.putExtra("json","");
                startActivity(player);


            }
        });
        try {
            getTopSongData();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return v;
    }

    public void getTopSongData() throws UnsupportedEncodingException
    {
        /*RequestQueue MyRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        String url= Constant.API_ALL_AUDIO_ALBUM;
        //String url ="http://api.awaza.net/album?limit=2&offset=0&orderby=album_id&ordertype=asc&columns[artist_name][type]=like&columns[artist_name][value]=%min%";
       // final String encodedUrl = String.format(url);
        Log.d("endoded",url);
        StringRequest myReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    Log.d("received",response);
                    JSONObject album = new JSONObject(response);
                    JSONObject album_data = album.getJSONObject("album_data");
                     JSONArray data = album_data.getJSONArray("data");
                    prepareTopSongsData(data);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Log.d("errrrrr",e.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       // Log.d("received","errrrr"+error.toString()+"    "+ url);
                    }
                });
        MyRequestQueue.add(myReq);*/
    }

    public void prepareTopSongsData()  {
        for (int i=0;i<25;i++)
        {
            String singer = "Fahim Arefin";
            String  song = "Jaber mama ganja khay";
            String url = "album_avatar";
            String  info = "31-02-2019";
            String id = "album_id";
            AlbumData TopSong = new AlbumData(song, singer,info, url,id); /// same as the popular video layout
            topSongsList.add(TopSong);
        }
        albumListAdapetr.notifyDataSetChanged();
    }


    public class AlbumData {

        private String title, singer, url, date,id;


        public AlbumData(String title, String singer, String date,  String url,String id) {
            this.title = title;
            this.singer = singer;
            this.date = date;
            this.url = url;
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String name) {
            this.title = name;
        }

        public String getsinger() {
            return singer;
        }

        public void setsinger(String singer) {
            this.singer = singer;
        }

        public String geturl() {
            return url;
        }

        public void seturl(String url) {
            this.singer = singer;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getId() {
            return id;
        }

        public void setId(String date) {
            this.id = date;
        }

    }

    public class AlbumAdapter extends BaseAdapter
    {
        Activity activity;
        List<AlbumData> topSong;

        public AlbumAdapter(Activity activity, List<AlbumData> topSong)
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
        public AlbumData getItem(int i) {
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

            AlbumData data = topSong.get(i);
            title.setText(data.getTitle());
            singer.setText(data.getsinger());
            totalPlay.setText(data.getDate());
            String url = data.geturl();
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
