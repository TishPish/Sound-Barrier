package headlines.top.tishpish.soundbarrier.Music;

/**
 * Created by tishpish on 10/4/17.
 */


public class Constant
{
    public static final String SHARED_PREF_SIGN_IN_TOKEN = "sign_in_token";
    // links

    public static final String API_GET_MAIN_PAGE_AUDIO_LINK ="http://awaza.net/awaza-rest/audio?columns[audio_release_date][type]=<&columns[audio_release_date][value]=%s&limit=%s&offset=%s&orderby=create_date&ordertype=desc";

    public static final String API_SIGN_UP="http://api.awaza.net/signup";
    public static final String API_SIGN_IN="http://api.awaza.net/signin";
    public static final String API_CREATE_PLAYLIST="http://api.awaza.net/playlist";
    public static final String DEVELOPER_KEY="AIzaSyAnfTkCdezSkC6gf6xYq92LSblu4SBB2Cw";
    public static final String PLAYLIST_URL="http://api.awaza.net/playlist";

    public static final String API_MAIN_PAGE_SLIDER_DATA= "http://api.awaza.net/slider?limit=5&offset=0&columns[slider_page][type]==&columns[slider_page][value]=home&order_by=slider_serial";
    //public static final String API_MAIN_PAGE_TOP_ARTIST= "http://api.awaza.net/popular_artist?orderby=total_popularity&ordertype=desc";
    public static final String API_MAIN_PAGE_TOP_ARTIST= "http://api.awaza.net/artist?limit=10";
    public static final String API_MAIN_PAGE_TOP_ALBUM="http://awaza.net/awaza-rest/album?columns[album_release_date][type]=<&columns[album_release_date][value]=2016-05-06&limit=6&offset=0&orderby=create_date&ordertype=desc";
    public static final String API_MAIN_PAGE_TOP_SONG="http://api.awaza.net/popular_video?orderby=total_popularity&ordertype=desc";
    public static final String API_MAIN_PAGE_VIDEO_DATA="http://awaza.net/awaza-rest/video?columns[video_release_date][type]=<&columns[video_release_date][value]=2016-05-06&limit=6&offset=0&orderby=create_date&ordertype=desc";
    public static final String ADD_SONG_TO_PLAYLIST="http://api.awaza.net/playlist/add_audio";
    public static final String API_USER_INFO= "http://api.awaza.net/user";
    public static final String MY_MUSIC_AUDIO_PLAYLIST="http://api.awaza.net/playlist?limit=10";
    public static final String API_TRENDING_AUDIO="http://api.awaza.net/popular_audio?orderby=total_popularity&ordertype=desc";
    public static final String API_POPULAR_AUDIO="http://api.awaza.net/popular_audio?orderby=total_popularity&ordertype=desc";
    public static final String API_USER_LIKED_AUDIO_LIST = "http://api.awaza.net/user_cms/audio_likes?limit=10&offset=0&orderby=audio_track_name";
    public static final String API_USER_LIKED_VIDEO_LIST = "http://api.awaza.net/user_cms/video_likes?limit=10&offset=0&orderby=video_track_id&orderty[e=asc";
    public static final String API_ARTIST_FOLLOWED_BY_USER="http://api.awaza.net/user_cms/artist_follows";
    public static final String API_ARTIST_INFO="http://api.awaza.net/artist/";  // SUFFIX SHOULD BE ADDED
    public static final String API_USER_AUDIO_HISTORY="http://api.awaza.net/user_cms/audio_play_history";
    public static final String API_USER_VIDEO_HISTORY="http://api.awaza.net/user_cms/video_play_history";
    public static final String API_ALBUM_INFO_BY_ID="http://api.awaza.net/album/"; // SUFFIX SHOULD BE ADDED
    public static final String API_PLAYLIST_BY_ID="http://api.awaza.net/playlist/"; // SUFFIX SHOULD BE ADDED
    public static final String API_AUDIO_INFO_BY_ID="http://api.awaza.net/audio/"; // SUFFIX SHOULD BE ADDED
    public static final String API_VIDEO_INFO_BY_ID="http://www.awaza.net/awaza-rest/video/"; // SUFFIX SHOULD BE ADDED
    public static final String API_FACEBOOK_SIGNUP="http://api.awaza.net/auth/facebook";
    //  public static final String API_ALL_AUDIO_ALBUM = "http://api.awaza.net/album?limit=2&offset=0&orderby=album_id&ordertype=asc&columns[artist_name][type]=like&columns[artist_name][value]=%25min%25";
    public static final String API_ALL_AUDIO_ALBUM = "http://api.awaza.net/album?limit=2&offset=0&orderby=album_id&ordertype=desc&columns[album_audio_count][type]=%3E&columns[album_audio_count][value]=0";
    public static final String API_FOLLOW_ARTIST_BY_ID="http://api.awaza.net/user_cms/artist_follows/"; // SUFFIX SHOULD BE ADDED
    public static final String API_POPULAR_VIDEO = "http://api.awaza.net/popular_video?orderby=total_popularity&ordertype=desc";
    public static final String LIKE_AUDIO_BY_ID= "http://api.awaza.net/user_cms/audio_likes/"; // suffix
    public static final String LIKE_VIDEO_BY_ID= "http://www.awaza.net/awaza-rest/user_cms/video_likes/"; // suffix
    public static  final String API_UPDATE_USER_INFO="http://api.awaza.net/user";

    ///////////////////////////
    public static final String PLAYER_INTENT_FILTER_NAME= "awazamusicplayer";


    // search options

    public static final String API_SEARCH_AUDIO= "http://www.awaza.net:9200/awaza/audio_tracks/_search";



}

