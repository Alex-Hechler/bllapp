package de.hechler.bll.activity.user;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.blox.graphview.sample.R;
import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.strategie.Strategie;

public class UserListAdapter extends ArrayAdapter<Benutzer> {
    private final Activity context;
    private final ArrayList<Benutzer> benutzerListe;

    public UserListAdapter(Activity context, int resource, ArrayList<Benutzer> benutzerListe ) {
        super(context, resource, benutzerListe);
        this.context = context;
        this.benutzerListe = benutzerListe;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_element_user, null,true);

        Benutzer benutzer = benutzerListe.get(position);
        TextView titleText = (TextView) rowView.findViewById(R.id.title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        int id = benutzer.getIconAssetId();
        if(benutzer.getBenutzerID()==0){
            titleText.setGravity(Gravity.CENTER_HORIZONTAL);
            imageView.setImageResource(id);
        }else {
            imageView.setImageResource(BenutzerManager.USER_IMAGES[id]);
        }
        titleText.setText(benutzer.getName());

        return rowView;

    };
    public Benutzer getBenutzerForPostion(int position){
        return benutzerListe.get(position);
    }
}
