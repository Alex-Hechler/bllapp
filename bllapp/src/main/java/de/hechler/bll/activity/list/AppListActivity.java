package de.hechler.bll.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import de.blox.graphview.sample.R;
import de.hechler.bll.activity.skillTree.SkillTreeActivity;
import de.hechler.bll.activity.strategie.StrategieActivity;
import de.hechler.bll.activity.user.BenutzerProfilActivity;
import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.data.strategie.StrategieVerlaufsDaten;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.dao.StrategieDatenDAO;
import de.hechler.bll.worker.BackgroundWorker;

public class AppListActivity extends AppCompatActivity {
    ListView list;
    TextView textView;
    boolean verwendeteApps = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkillTreeDbHelper.setzteDbcontext(this);
        setContentView(R.layout.activity_list);


        setupToolbar();
        setupNavigation();




        list = findViewById(R.id.strategie_list);
        textView = findViewById(R.id.title);



            erzeugeAppListe();

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
                        intent = new Intent(getBaseContext(), StrategieListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(intent,0);
                        return true;
                    case R.id.bottom_navigation_page_apps:
                        return true;
                }
                return false;
            };
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
    }
    private void setupNavigation(){
        //onClick listener und selectedItem auswählen
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.bottom_navigation_page_apps);


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
                verwendeteApps = true;
                erzeugeAppListe();
                return true;
            case R.id.menu_item_liste_nichtverwendtet:
                verwendeteApps = false;
                erzeugeAppListe();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void erzeugeAppListe(){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();
        if(verwendeteApps) {
            setTitle("Apps");
        }else{
            setTitle("nicht verwendete Apps");
        }
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        ArrayList<Strategie> strategies = strategieDatenDAO.readAppListForBenutzerID(benutzerManager.getAktuellerBenutzerId(), verwendeteApps);

        AppListAdapter adapter = new AppListAdapter(this, R.id.strategie_list, strategies);
        list.setAdapter(adapter);


        list.setOnItemClickListener((parent, view, position, id) -> {
            Strategie s = (Strategie) parent.getItemAtPosition(position);
            s.addVerlauf(new StrategieVerlaufsDaten(StrategieVerlaufsDaten.ACTION_NUTZUNGSTRATEGIEAPP,"Type: "+s.getType()));
            Intent intent = new Intent(view.getContext(), StrategieActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra(BackgroundWorker.KEY_STRATEGIE_ID, s.getId());
            intent.putExtra(BackgroundWorker.KEY_FILENAME, s.getFilenameApp());
            startActivityForResult(intent, 0);
        }
        );
    }

}
