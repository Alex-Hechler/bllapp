package de.hechler.bll.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


import de.hechler.bll.R;
import de.hechler.bll.activity.skillTree.ContentActivity;
import de.hechler.bll.activity.skillTree.SkillTreeActivity;
import de.hechler.bll.activity.strategie.StrategieActivity;
import de.hechler.bll.activity.user.BenutzerProfilActivity;
import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.skillTree.NodeModel;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.data.strategie.StrategieVerlaufsDaten;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.dao.NodeModelDAO;
import de.hechler.bll.persistenz.dao.StrategieDatenDAO;
import de.hechler.bll.worker.BackgroundWorker;

public class StrategieListActivity extends AppCompatActivity {
    ListView list;
    TextView textView;
    boolean verwendeteStrategien = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkillTreeDbHelper.setzteDbcontext(this);
        setContentView(R.layout.activity_list);


        setupToolbar();
        setupNavigation();




        list = findViewById(R.id.strategie_list);
        textView = findViewById(R.id.title);


        erzeugeStrategieListe();

    }
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_page_nutzerprofil:
                        intent = new Intent(getBaseContext(), BenutzerProfilActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent,0);
                        return true;
                    case R.id.bottom_navigation_page_skilltree:
                        intent = new Intent(getBaseContext(), SkillTreeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent,0);
                        return true;
                    case R.id.bottom_navigation_page_strategie:
                        return true;
                    case R.id.bottom_navigation_page_apps:
                        intent = new Intent(getBaseContext(), AppListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent,0);
                        return true;
                }
                return false;
            };
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
    }
    private void setupNavigation(){
        //onClickListener und selected Item auswählen
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.bottom_navigation_page_strategie);

        //personalisierte Anzeige des Benutzerprofilitems
        MenuItem item = bottomNavigation.getMenu().getItem(0);
        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();
        item.setTitle(aktuellerBenutzer.getName());
        item.setIcon(BenutzerManager.USER_IMAGES[aktuellerBenutzer.getIconAssetId()]);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_item_liste_strategie:
                verwendeteStrategien = true;
                erzeugeStrategieListe();
                return true;
            case R.id.menu_item_liste_nichtverwendtet:
                verwendeteStrategien = false;
                erzeugeStrategieListe();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void erzeugeStrategieListe(){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();
        if(verwendeteStrategien) {
            setTitle("Strategien");
        }else{
            setTitle("nicht verwendete Strategien");
        }
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        ArrayList<Strategie> strategies = strategieDatenDAO.readStrategieListForBenutzerID(benutzerManager.getAktuellerBenutzerId(),verwendeteStrategien);


        StrategieListAdapter adapter = new StrategieListAdapter(this, R.id.strategie_list, strategies);
        list.setAdapter(adapter);


        list.setOnItemClickListener((parent, view, position, id) -> {
            NodeModelDAO nodeModelDAO = NodeModelDAO.getInstance();
            Strategie s = (Strategie) parent.getItemAtPosition(position);
            NodeModel nodeModel = nodeModelDAO.readNodeForStrategie(s.getId());
            Intent intent = new Intent(view.getContext(), ContentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("NodeId", nodeModel.getNodeID());
            startActivityForResult(intent, 4);
        }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==4){
            erzeugeStrategieListe();
        }
    }
}
