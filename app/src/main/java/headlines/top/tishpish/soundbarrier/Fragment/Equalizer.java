package headlines.top.tishpish.soundbarrier.Fragment;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.util.ArrayList;
import java.util.List;

import ademar.phasedseekbar.PhasedInteractionListener;
import ademar.phasedseekbar.PhasedListener;
import ademar.phasedseekbar.PhasedSeekBar;
import ademar.phasedseekbar.SimplePhasedAdapter;
import headlines.top.tishpish.soundbarrier.Database.AppSharedPreference;
import headlines.top.tishpish.soundbarrier.Music.SlidingTabLayout;
import headlines.top.tishpish.soundbarrier.Music.ViewPagerAdapter;
import headlines.top.tishpish.soundbarrier.R;

/**
 * Created by tishpish on 10/18/17.
 */

public class Equalizer extends Fragment
{

    protected PhasedSeekBar  psbNoImages;
    protected PhasedSeekBar  psbNoImages1;
    protected PhasedSeekBar  psbNoImages2;
    protected PhasedSeekBar  psbNoImages3;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dummy, container, false);
        psbNoImages = (PhasedSeekBar) v.findViewById(R.id.psb_no_images);
        psbNoImages1 = (PhasedSeekBar) v.findViewById(R.id.psb_no_images1);
        psbNoImages2 = (PhasedSeekBar) v.findViewById(R.id.psb_no_images2);
        psbNoImages3 = (PhasedSeekBar) v.findViewById(R.id.psb_no_images3);

        final Resources resources = getResources();
        psbNoImages.setAdapter(new SimplePhasedAdapter(resources, new int[]
                {
                        R.drawable.no_image
                }));
        psbNoImages1.setAdapter(new SimplePhasedAdapter(resources, new int[]
                {
                        R.drawable.no_image
                }));
        psbNoImages2.setAdapter(new SimplePhasedAdapter(resources, new int[]
                {
                        R.drawable.no_image
                }));
        psbNoImages3.setAdapter(new SimplePhasedAdapter(resources, new int[]
                {
                        R.drawable.no_image
                }));

        psbNoImages.setPosition(0);
        psbNoImages1.setPosition(2);
        psbNoImages2.setPosition(3);
        psbNoImages3.setPosition(4);

       /* psbNoImages.setListener(new PhasedListener() {
            @Override
            public void onPositionSelected(int i)
            {
                Log.d("val",i+"");
                psbNoImages.setPosition(i);
            }
        });

        psbNoImages.setInteractionListener(new PhasedInteractionListener() {
            @Override
            public void onInteracted(int x, int y, int position, MotionEvent motionEvent) {
                Log.d("PSB", String.format("onInteracted %d %d %d %d", x, y, position, motionEvent.getAction()));
                //psbNoImages.setY(y);
            }
        });*/



        return v;
    }
}

