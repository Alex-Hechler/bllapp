package de.hechler.bll.activity.list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


import de.hechler.bll.R;
import de.hechler.bll.data.strategie.Strategie;

public class NotificationListAdapter extends ArrayAdapter<Strategie> {
    private final Activity context;
    private final ArrayList<Strategie> verwendeteNotificationStrategien;

    public NotificationListAdapter(Activity context, int resource, ArrayList<Strategie> verwendeteNotificationStrategien ) {
        super(context, resource, verwendeteNotificationStrategien);
        this.context = context;
        this.verwendeteNotificationStrategien = verwendeteNotificationStrategien;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_element_app, null,true);

        Strategie strategie = verwendeteNotificationStrategien.get(position);

        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView warningText = (TextView) rowView.findViewById(R.id.warning);

        //TODO: überarbeiten
        titleText.setText(strategie.getNotificationInfo().getTitle());
        imageView.setImageResource(R.drawable.circle);
        warningText.setText("!");
        //TODO: Warunung eigenschaft
        warningText.setVisibility(View.INVISIBLE);

        return rowView;

    };

}
