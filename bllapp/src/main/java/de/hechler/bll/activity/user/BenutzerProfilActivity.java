package de.hechler.bll.activity.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hechler.bll.R;
import de.hechler.bll.activity.list.AppListActivity;
import de.hechler.bll.activity.list.StrategieListActivity;
import de.hechler.bll.activity.skillTree.SkillTreeActivity;
import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.persistenz.dao.BenutzerDAO;

public class BenutzerProfilActivity extends AppCompatActivity {
    private EditText benutzername;
    private Button btn_abmelden;
    private Button btn_profil_aendern;
    private FloatingActionButton fbtn_profilbild_aendern;
    private ImageView img_profilbild;
    private static final int REQUESTCODE_PROFILBILD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();
        setContentView(R.layout.activity_benutzer_profil);
        setupToolbar();
        setupNavigation();

        benutzername = findViewById(R.id.benutzername);
        btn_abmelden = findViewById(R.id.btn_abmelden);
        btn_profil_aendern = findViewById(R.id.btn_profil_aendern);
        fbtn_profilbild_aendern = findViewById(R.id.fbtn_profilbild_aendern);
        img_profilbild = findViewById(R.id.img_profilbild);

        img_profilbild.setImageResource(BenutzerManager.USER_IMAGES[aktuellerBenutzer.getIconAssetId()]);
        benutzername.setText(aktuellerBenutzer.getName());
        fbtn_profilbild_aendern.setEnabled(false);

    }
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            item -> {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_page_nutzerprofil:
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
        //onClick listener und selectedItem auswählen
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.bottom_navigation_page_nutzerprofil);


        //personalisierte Anzeige des Benutzerprofilitems
        MenuItem item = bottomNavigation.getMenu().getItem(0);
        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();
        item.setTitle(aktuellerBenutzer.getName());
        item.setIcon(BenutzerManager.USER_IMAGES[aktuellerBenutzer.getIconAssetId()]);
    }
    public void abmelden(View button){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();
        benutzerManager.setAktuellerBenutzer(null);


        Intent intent = new Intent(getBaseContext(), LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
    public void clickedProfilAendernOderFertig(View button){
        if(benutzername.isEnabled()){
            clickedFertig();
        }else {
            clickedProfilAendern();
        }
    }
    private void clickedProfilAendern(){
        benutzername.setEnabled(true);
        btn_abmelden.setEnabled(false);
        btn_abmelden.setVisibility(View.INVISIBLE);
        fbtn_profilbild_aendern.setEnabled(true);
        fbtn_profilbild_aendern.setVisibility(View.VISIBLE);
        btn_profil_aendern.setText("Fertig");
    }

    private void clickedFertig(){
        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();
        BenutzerDAO benutzerDAO = BenutzerDAO.getInstance();
        String neuerName = benutzername.getText().toString();
        //Problem Name Leer
        if(neuerName.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(),"Benutzername darf nicht leer sein",Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //Problem name existiert beriets
        if(!benutzerDAO.isNameFree(neuerName)&&!neuerName.equals(aktuellerBenutzer.getName())) {
            Toast toast = Toast.makeText(getApplicationContext(),"Benutzername existiert bereits",Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        //alles wird umgesetzt kein problem
        aktuellerBenutzer.setName(neuerName);
        benutzerDAO.update(aktuellerBenutzer);
        benutzername.setEnabled(false);
        btn_abmelden.setEnabled(true);
        btn_abmelden.setVisibility(View.VISIBLE);
        fbtn_profilbild_aendern.setEnabled(false);
        fbtn_profilbild_aendern.setVisibility(View.INVISIBLE);
        btn_profil_aendern.setText("Profil ändern");
    }

    public void clickedProfilbildAendern(View view){
        Intent intent = new Intent(getBaseContext(), ImageSelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent,REQUESTCODE_PROFILBILD);
    }
    public void clickedProfilLoeschen(View view){
        ProfilLoeschenDialogFragment profilLoeschenDialogFragment = new ProfilLoeschenDialogFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        profilLoeschenDialogFragment.show(fragmentManager,"profil");
    }
    public static class ProfilLoeschenDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.dialog_profilloeschen)
                    .setPositiveButton(R.string.loeschen, (dialog, id) -> {
                        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();
                        BenutzerDAO benutzerDAO = BenutzerDAO.getInstance();
                        benutzerDAO.delete(aktuellerBenutzer);
                        BenutzerManager benutzerManager = BenutzerManager.getInstance();
                        benutzerManager.setAktuellerBenutzer(null);


                        Intent intent = new Intent(getContext(), LogInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    })
                    .setNegativeButton(R.string.abbrschen, (dialog, id) -> {
                        return;
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUESTCODE_PROFILBILD){
            Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();
            aktuellerBenutzer.setIconAssetId(data.getIntExtra("result", aktuellerBenutzer.getIconAssetId()));

            aktualisieren();
            speichern();
        }
    }

    private void aktualisieren(){
        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();

        img_profilbild.setImageResource(BenutzerManager.USER_IMAGES[aktuellerBenutzer.getIconAssetId()]);
    }
    private void speichern(){
        BenutzerDAO benutzerDAO = BenutzerDAO.getInstance();
        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();

        benutzerDAO.update(aktuellerBenutzer);
    }
    private void profilLoeschen(){
        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();
        BenutzerDAO benutzerDAO = BenutzerDAO.getInstance();
        benutzerDAO.delete(aktuellerBenutzer);
        abmelden(btn_abmelden);
    }
}