package de.hechler.bll.data;

import java.util.ArrayList;


import de.hechler.bll.R;
import de.hechler.bll.persistenz.dao.BenutzerDAO;

public class BenutzerManager {
    private static BenutzerManager instance;
    private Benutzer aktuellerBenutzer;
    public final static int[] USER_IMAGES = {R.drawable.schokolade, R.drawable.donut,
            R.drawable.dog1,R.drawable.dog2,R.drawable.dog3,R.drawable.dog4,R.drawable.dog5,R.drawable.dog6,
            R.drawable.cat, R.drawable.cat2, R.drawable.cat3, R.drawable.catwithlion,
            R.drawable.donkey};



    private BenutzerManager(){
        aktuellerBenutzer = null;
    }
    public static BenutzerManager getInstance(){
        if(instance==null){
            instance = new BenutzerManager();
        }
        return instance;
    }

    public Benutzer getAktuellerBenutzer() {
        return aktuellerBenutzer;
    }

    public Long getAktuellerBenutzerId(){
        return aktuellerBenutzer.getBenutzerID();
    }

    public void setAktuellerBenutzer(Benutzer aktuellerBenutzer) {
        this.aktuellerBenutzer = aktuellerBenutzer;
    }

    public ArrayList<Benutzer> getAlleBenutzer(){
        BenutzerDAO benutzerDAO = BenutzerDAO.getInstance();
        ArrayList<Benutzer> benutzerListe = new ArrayList<>();
        ArrayList<Long> idsBenutzer = benutzerDAO.readAllIDs();
        for(Long id : idsBenutzer){
            Benutzer benutzer = benutzerDAO.read(id);
            benutzerListe.add(benutzer);
        }
        return benutzerListe;
    }
    public Benutzer getBenutzerByID(long id){
        BenutzerDAO benutzerDAO = BenutzerDAO.getInstance();
        Benutzer benutzer = benutzerDAO.read(id);
        return benutzer;
    }
    public Benutzer erstelleNeuenBenutzer(String name, int imageID){
        BenutzerDAO benutzerDAO = BenutzerDAO.getInstance();
        Benutzer neuerBenutzer = null;
        if(benutzerDAO.isNameFree(name)){
            neuerBenutzer = new Benutzer(name, imageID);
            benutzerDAO.create(neuerBenutzer);
        }
        return neuerBenutzer;
    }
}
