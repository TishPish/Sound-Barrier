package headlines.top.tishpish.soundbarrier.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import headlines.top.tishpish.soundbarrier.R;

/**
 * Created by tishpish on 10/18/17.
 */

public class Statistics extends Fragment
{



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dummy, container, false);

        ImageView img = (ImageView) v.findViewById(R.id.image);
        img.setImageResource(R.mipmap.stat);


        return v;
    }
}
