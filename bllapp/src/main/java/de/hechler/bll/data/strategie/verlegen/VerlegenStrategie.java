package de.hechler.bll.data.strategie.verlegen;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hechler.bll.data.notification.NotificationInfo;
import de.hechler.bll.data.notification.NotificationScheduler;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.data.strategie.StrategieVerlaufsDaten;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.contract.SkillTreeContract;
import de.hechler.bll.persistenz.dao.StrategieDatenDAO;
import de.hechler.bll.persistenz.dao.StrategieUserDatenDAO;
import de.hechler.bll.util.HTMLconverter;

public class VerlegenStrategie extends Strategie {
    private static final String KOMMANDO_CREATEVERLAUF_TAEGLICHEABFRAGE = "createVerlaufTaeglicheAbfrage";
    private String gegenstandName;
    private String aufbewahrungsOrt;



    public VerlegenStrategie(long id, String filenameApp, String filenameNotification) {
        super(id,filenameApp, filenameNotification);
    }

    @Override
    public void executeStrategie(String commando, HashMap<String, String> parameter) {
        switch (commando){
            case KOMMANDO_CREATEVERLAUF_TAEGLICHEABFRAGE:
                StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
                parameter.put("action",VerlegenStrategieVerlaufsDaten.ACTOIN_VERLEGEN_TAEGLICHEABFRAGE);
                StrategieVerlaufsDaten strategieVerlaufsDaten = VerlegenStrategieVerlaufsDaten.erstelleVerlaufsDaten(parameter);
                strategieVerlaufsDatenListe.add(strategieVerlaufsDaten);
                strategieDatenDAO.update(this);
                break;
            default:
                super.executeStrategie(commando, parameter);
        }
    }

    @Override
    protected void setzeVariablen(HashMap<String, String> parameter) {
        for(String variablenName: parameter.keySet()){
            String valueString = parameter.get(variablenName);
            switch (variablenName){
                case "gegenstandName":
                    gegenstandName = valueString;
                    break;
                case "aufbewahrungsOrt":
                    aufbewahrungsOrt = valueString;
                    break;
                case "uhrzeit":
                    String uhrzeitString = parameter.get("uhrzeit");
                    Date uhrzeit = HTMLconverter.uhrzeitStringToDate(uhrzeitString);
                    notificationScheduler.setzeTaeglicheNotification(uhrzeit);
                    if(notificationActive){
                        requestWork(SkillTreeDbHelper.getDbcontext());
                    }
                    break;
                default:
                    super.setzeVariablen(parameter);
            }
        }
        save();
    }

    @Override
    public HashMap<String, String> getPlatzhalterWerte() {
        HashMap<String, String> platzhalter = new HashMap<>();
        String uhrzeit = HTMLconverter.dateToUhrzeitString(notificationScheduler.getNextDate());
        platzhalter.put("uhrzeit", wennNull(uhrzeit,""));
        platzhalter.put("gegenstandName", wennNull(gegenstandName,""));
        platzhalter.put("aufbewahrungsOrt", wennNull(aufbewahrungsOrt, ""));
        platzhalter.put("falscherOrtListe", wennNull(HTMLconverter.javaListToUnorderedList(getFalscherOrtListe()),""));
        platzhalter.put("prozessVerlegen",wennNull(HTMLconverter.intsToProgressbar(getTageHintereinanderGefunden(),3,"Tagen"),""));
        platzhalter.putAll(super.getPlatzhalterWerte());
        return platzhalter;
    }

    private int getTageHintereinanderGefunden(){
        return 2;
    }

    /**
     * Verlauf wird hier eingelesen
     * @return
     */
    private ArrayList<String> getFalscherOrtListe(){
        StrategieUserDatenDAO strategieUserDatenDAO = StrategieUserDatenDAO.getInstance();
        strategieUserDatenDAO.readVerlaufForStrategie(this);
        ArrayList<String> falscheOrte = new ArrayList<>();
        for(StrategieVerlaufsDaten svd: strategieVerlaufsDatenListe){
            if(svd instanceof VerlegenStrategieVerlaufsDaten){
                VerlegenStrategieVerlaufsDaten vsvd = (VerlegenStrategieVerlaufsDaten) svd;
                if(vsvd.getFalscherOrt() != null){
                    if(!falscheOrte.contains(vsvd.getFalscherOrt()))
                        falscheOrte.add(vsvd.getFalscherOrt());
                    }
            }
        }
        return falscheOrte;
    }

    @Override
    public void verwenden(Context context) {
        notificationActive = true;
        super.verwenden(context);
        requestWork(context);
    }

    @Override
    public NotificationInfo getNotificationInfo() {
        String title = "Verlegen Strategie";
        String text = "Wo ist "+gegenstandName+"?";
        return new NotificationInfo(title, text);
    }

    //Getter
    @Override
    public String getType(){
        return SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN;
    }
    public String getGegenstandName() {
        return gegenstandName;
    }
    public String getAufbewahrungsOrt() {
        return aufbewahrungsOrt;
    }

    @Override
    public boolean kannVerwendetWerden() {
        if(gegenstandName==null||aufbewahrungsOrt==null)
            return false;
        return super.kannVerwendetWerden();
    }

    //Setter
    public void setGegenstandName(String gegenstandName) {
        this.gegenstandName = gegenstandName;
    }
    public void setAufbewahrungsOrt(String aufbewahrungsOrt) {
        this.aufbewahrungsOrt = aufbewahrungsOrt;
    }

}
