package headlines.top.tishpish.soundbarrier.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.ValueLineSeries;

import headlines.top.tishpish.soundbarrier.Database.AppSharedPreference;
import headlines.top.tishpish.soundbarrier.R;

/**
 * Created by tishpish on 10/18/17.
 */

public class Statistics extends Fragment
{

    ValueLineSeries series;
    ValueLineChart mCubicValueLineChart;
    TextView visits,dailyuse;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.equalizer, container, false);

        BarChart barChart = (BarChart) v.findViewById(R.id.chart);
        visits = (TextView) v.findViewById(R.id.visit);
        dailyuse = (TextView) v.findViewById(R.id.dailyavg);

        AppSharedPreference asp = AppSharedPreference.getInstance(getContext());
        Toast.makeText(getContext(),"Second: "+(asp.GetTime()/1000)+" Minute: "+(asp.GetTime()/60000),Toast.LENGTH_LONG).show();
        float data = asp.GetTime()/60000;

        barChart.addBar(new BarModel("Oct 28",50.3f,0xFF225556));
        barChart.addBar(new BarModel("Oct 29",21.3f,0xFF323456));
        barChart.addBar(new BarModel("Oct 30",41.3f,0xFF423456));
        barChart.addBar(new BarModel("Oct 31",38.3f,0xFF523456));
        barChart.addBar(new BarModel("Nov 1",29.3f,0xFF623256));
        barChart.addBar(new BarModel("Nov 2",data,0xFF113156));
        barChart.startAnimation();

        dailyuse.setText("Average Daily Use Time : "+(50+21+41+38+29+data)/6 + " Minutes");
        visits.setText("Total Visits:  "+ asp.GetVisit());

        return v;
    }
}

