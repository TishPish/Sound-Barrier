package headlines.top.tishpish.soundbarrier.Database;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
/**
 * Created by farhad on 5/8/16.
 */
public class AppSharedPreference
{
    private static AppSharedPreference mAppSharedPreference;

    private SharedPreferences mSharedPreferences;


    private Editor mEditor;
    private static Context mContext;

    /*
     * Implementing Singleton DP
     */
    private AppSharedPreference() {
        mSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public static AppSharedPreference getInstance(Context context) {
        mContext = context;
        if (mAppSharedPreference == null)
            mAppSharedPreference = new AppSharedPreference();
        return mAppSharedPreference;
    }


    public void updateTime(long s)
    {
        //long x = s/60000;
        mEditor.putLong("time",s);
        mEditor.commit();
    }

    public Long GetTime()
    {
        return (mSharedPreferences.getLong("time",0));
    }

    public void updateVisit()
    {
        long x = GetVisit()+1;
        mEditor.putLong("visit",x);
        mEditor.commit();
    }

    public Long GetVisit()
    {
        return (mSharedPreferences.getLong("visit",0));
    }


}