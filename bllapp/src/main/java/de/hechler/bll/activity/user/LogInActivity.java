package de.hechler.bll.activity.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

import de.blox.graphview.sample.R;
import de.hechler.bll.activity.skillTree.SkillTreeActivity;
import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.persistenz.SkillTreeDbHelper;

public class LogInActivity extends AppCompatActivity {
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        SkillTreeDbHelper.setzteDbcontext(this);

        list = findViewById(R.id.user_list);
        erzeugeNutzerListe();
        setupToolbar();


    }
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        setTitle("Benutzerwahl");
    }
    private void erzeugeNutzerListe(){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();
        ArrayList<Benutzer> benutzerArrayList = benutzerManager.getAlleBenutzer();
        benutzerArrayList.add(new Benutzer(0,"neuen Benutzer hinzufügen",R.drawable.ic_baseline_add_24));
        UserListAdapter adapter = new UserListAdapter(this, R.id.user_list, benutzerArrayList);
        list.setAdapter(adapter);


        list.setOnItemClickListener((parent, view, position, id) -> {
                    //code specific to first list item
                    Benutzer b = (Benutzer) parent.getItemAtPosition(position);
                    if(b.getBenutzerID()==0){
                        starteNeuerBenutzerActivity();
                        return;
                    }
                    Log.i("LOGIN", "Benutzer geclickt "+b.getName());
                    benutzerManager.setAktuellerBenutzer(b);
                    Intent intent = new Intent(view.getContext(), SkillTreeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                }
        );
    }
    private void starteNeuerBenutzerActivity(){
        Intent intent = new Intent(getApplicationContext(), NeuerBenutzerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}