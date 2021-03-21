package de.hechler.bll.activity.list;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import java.util.ArrayList;

import de.blox.graphview.sample.R;
import de.hechler.bll.data.strategie.Strategie;

public class AppListAdapter extends ArrayAdapter<Strategie> {
    private final Activity context;
    private final ArrayList<Strategie> verwendeteteStrategien;

    public AppListAdapter(Activity context, int resource, ArrayList<Strategie> verwendeteteStrategien ) {
        super(context, resource, verwendeteteStrategien);
        this.context = context;
        this.verwendeteteStrategien = verwendeteteStrategien;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_element_app, null,true);

        Strategie strategie = verwendeteteStrategien.get(position);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView warningText = (TextView) rowView.findViewById(R.id.warning);

        //TODO: überarbeiten
        titleText.setText(strategie.getNotificationInfo().getTitle());
        int color =  ContextCompat.getColor(context,R.color.colorPrimaryDark);
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(color));
        imageView.setImageResource(R.drawable.ic_list_strategie_apps);
        warningText.setText("!");
        //TODO: Warunung eigenschaft
        warningText.setVisibility(View.INVISIBLE);
        if(strategie.isNotificationActive())
            warningText.setVisibility(View.VISIBLE);

        return rowView;

    };
    public Strategie getStrategieForPosition(int position){
        return verwendeteteStrategien.get(position);
    }
}
