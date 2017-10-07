package headlines.top.tishpish.soundbarrier.Music;

import java.util.concurrent.Executor;

import javax.sql.RowSet;

/**
 * Created by Tiash on 5/12/2016.
 */


import android.os.AsyncTask;
/**
 * Asynchronous task that prepares a MusicRetriever. This asynchronous task essentially calls
 * {@link MusicRetriever#prepare()} on a {@link MusicRetriever}, which may take some time to
 * run. Upon finishing, it notifies the indicated {@MusicRetrieverPreparedListener}.
 */
public class PrepareMusicRetrieverTask extends AsyncTask<Void, Void, Void> {
    MusicRetriever mRetriever;
    MusicRetrieverPreparedListener mListener;
    public PrepareMusicRetrieverTask(MusicRetriever retriever,
                                     MusicRetrieverPreparedListener listener) {
        mRetriever = retriever;
        mListener = listener;
    }
    @Override
    protected Void doInBackground(Void... arg0) {
        mRetriever.prepare();
        return null;
    }
    @Override
    protected void onPostExecute(Void result) {
        mListener.onMusicRetrieverPrepared();
    }
    public interface MusicRetrieverPreparedListener {
        public void onMusicRetrieverPrepared();
    }
}