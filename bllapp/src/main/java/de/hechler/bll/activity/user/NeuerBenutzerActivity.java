package de.hechler.bll.activity.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;


import de.hechler.bll.R;
import de.hechler.bll.activity.skillTree.SkillTreeActivity;
import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.skillTree.NodeModel;
import de.hechler.bll.persistenz.dao.NodeModelDAO;

public class NeuerBenutzerActivity extends AppCompatActivity {
    int selecedUserImage;
    private static Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neuer_benutzer);
        ImageView profilPicture = findViewById(R.id.img_profilbild);
        selecedUserImage = rand.nextInt(BenutzerManager.USER_IMAGES.length);
        profilPicture.setImageResource(BenutzerManager.USER_IMAGES[selecedUserImage]);


        Button fertig = findViewById(R.id.btn_erstellen_bestaetigen);
        fertig.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BenutzerManager benutzerManager = BenutzerManager.getInstance();
                EditText nameView = findViewById(R.id.benutzername);
                String moeglicherName = nameView.getText().toString();
                if(moeglicherName.isEmpty()){
                    Toast toast = Toast.makeText(getApplicationContext(),"Benutzername darf nicht leer sein",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Benutzer neuerBenutzer = benutzerManager.erstelleNeuenBenutzer(moeglicherName, selecedUserImage);
                if(neuerBenutzer==null) {
                    Toast toast = Toast.makeText(getApplicationContext(),"Benutzername existiert bereits",Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                benutzerManager.setAktuellerBenutzer(neuerBenutzer);
                nutzerDatenInitzalisieren(neuerBenutzer);


                Intent intent = new Intent(v.getContext(), SkillTreeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        FloatingActionButton actionButton = findViewById(R.id.fbtn_profilbild_aendern);
        actionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ImageSelectActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(intent,0);
            }
        });

    }
    private void nutzerDatenInitzalisieren(Benutzer b){
        NodeModelDAO nodeModelDAO = NodeModelDAO.getInstance();
        NodeModel node = nodeModelDAO.readWithoutChildren(nodeModelDAO.readRootID());
        node.setFreigeschaltet(true);
        nodeModelDAO.update(node);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) { // wenn der zurueck button geklickt wurde, dann gibt es kein data intent.
            selecedUserImage = data.getIntExtra("result", selecedUserImage);
            ImageView profilPicture = findViewById(R.id.img_profilbild);
            profilPicture.setImageResource(BenutzerManager.USER_IMAGES[selecedUserImage]);
        }

    }
}