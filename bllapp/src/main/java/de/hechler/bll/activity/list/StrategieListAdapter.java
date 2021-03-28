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
import de.hechler.bll.data.skillTree.NodeModel;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.persistenz.dao.NodeModelDAO;

public class StrategieListAdapter extends ArrayAdapter<Strategie> {
    private final Activity context;
    private final ArrayList<Strategie> verwendeteteStrategien;

    public StrategieListAdapter(Activity context, int resource, ArrayList<Strategie> verwendeteteStrategien ) {
        super(context, resource, verwendeteteStrategien);
        this.context = context;
        this.verwendeteteStrategien = verwendeteteStrategien;
    }

    public View getView(int position, View view, ViewGroup parent) {
        NodeModelDAO nodeModelDAO = NodeModelDAO.getInstance();

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_element_strategie, null,true);

        Strategie strategie = verwendeteteStrategien.get(position);
        NodeModel nodeModel = nodeModelDAO.readNodeForStrategie(strategie.getId());
        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        //TODO: überarbeiten
        titleText.setText(nodeModel.getName());
        imageView.setImageResource(R.drawable.ic_strategie_notizbuch);

        return rowView;

    };
    public Strategie getStrategieForPosition(int position){
        return verwendeteteStrategien.get(position);
    }
}
