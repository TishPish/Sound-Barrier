package headlines.top.tishpish.soundbarrier.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import headlines.top.tishpish.soundbarrier.Music.SlidingTabLayout;
import headlines.top.tishpish.soundbarrier.Music.ViewPagerAdapter;
import headlines.top.tishpish.soundbarrier.R;

/**
 * Created by tishpish on 10/18/17.
 */

public class HomeFragment extends Fragment
{

    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    int Numboftabs =3;
    String json="";
    CharSequence Titles[]={"Recent","Gallery","All"};
    ViewPager pager;
    ImageView avatar, play,next;
    TextView singer, title;
    String currentId = "-1";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.homefragment, container, false);

        adapter =  new ViewPagerAdapter(getActivity().getSupportFragmentManager(),Titles,Numboftabs);

        pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) v.findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        tabs.setViewPager(pager);


        return v;
    }
}

