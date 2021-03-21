package de.hechler.bll.data.strategie;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.notification.NotificationInfo;
import de.hechler.bll.data.notification.NotificationScheduler;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.dao.NodeModelDAO;
import de.hechler.bll.persistenz.dao.StrategieDatenDAO;
import de.hechler.bll.persistenz.contract.SkillTreeContract;
import de.hechler.bll.util.HTMLconverter;
import de.hechler.bll.worker.BackgroundWorker;

public class Strategie {
    public static final String KOMMANDO_SETZEVARIABLE = "setzeVariable";
    /** Richtet eine neue tägliche Notifizierung ein, überschreibt bereits vorhandene. */
    public static final String KOMMANDO_SETZE_TAEGLICHE_NOTIFIZIERUNG = "setzteTaeglicheNotifizierung";
    public static final String KOMMANDO_VERWENDEN_AUS = "verwendenAus";
    protected ArrayList<StrategieVerlaufsDaten> strategieVerlaufsDatenListe;
    //Benutzerunabhänige Daten
    private long id;
    private String filenameApp;
    private String filenameNotfication;
    //Benutzerspezifische Daten
    private long userDataId;
    private boolean wirdVerwendet;
    protected boolean notificationActive;
    private Date nextBackgroundTrigger;

    protected NotificationScheduler notificationScheduler;


    public Strategie(long id, String filenameApp, String filenameNotification) {
        this.id = id;
        this.filenameApp = filenameApp;
        this.filenameNotfication = filenameNotification;
        strategieVerlaufsDatenListe = new ArrayList<>();
    }

    public void addVerlauf(StrategieVerlaufsDaten strategieVerlaufsDaten){
        strategieVerlaufsDatenListe.add(strategieVerlaufsDaten);
        save();
    }
    protected void save(){
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        strategieDatenDAO.update(this);
    }
    public ArrayList<StrategieVerlaufsDaten> getStrategieVerlaufsDatenListe() {
        return strategieVerlaufsDatenListe;
    }
    public HashMap<String, String> getPlatzhalterWerte(){
        return new HashMap<>();
    }

    public void executeStrategie(String commando, HashMap<String, String> parameter) {
        switch (commando){
            case KOMMANDO_SETZEVARIABLE:
                setzeVariablen(parameter);
                save();
                break;
            case KOMMANDO_SETZE_TAEGLICHE_NOTIFIZIERUNG:
                String uhrzeitString = parameter.get("uhrzeit");
                Date uhrzeit = HTMLconverter.uhrzeitStringToDate(uhrzeitString);
                notificationScheduler.setzeTaeglicheNotification(uhrzeit);
                if(notificationActive){
                    requestWork(SkillTreeDbHelper.getDbcontext());
                }
                break;
            case KOMMANDO_VERWENDEN_AUS:
                wirdVerwendet = false;
                notificationActive = false;
                String action = StrategieVerlaufsDaten.ACTION_STRATEGIE_VERWENDEN_AUS;
                String info = "Typ: "+getType();
                StrategieVerlaufsDaten svd = new StrategieVerlaufsDaten(action,info);
                strategieVerlaufsDatenListe.add(svd);
                save();
                break;
            default:
                throw new RuntimeException("Kommando nicht bekannt: "+commando);
        }
    }
    protected void setzeVariablen(HashMap<String, String> parameter){
        Log.i("STRATEGIE",parameter.toString());
        for(String variablenName: parameter.keySet()){
            String valueString = parameter.get(variablenName);
            switch (variablenName){
                case "notificationActive":
                    notificationActive = HTMLconverter.stringToBoolean(valueString);
                    break;
                default:
                    throw new RuntimeException("Setze Variable wurde nicht überschrieben "+parameter);
            }
        }
    }
    public void verwenden(Context context){
        if(!kannVerwendetWerden())
            throw new RuntimeException("Bedinung für Verwenden werden nicht erfüllt");
        wirdVerwendet = true;
        strategieVerlaufsDatenListe.add(new StrategieVerlaufsDaten(StrategieVerlaufsDaten.ACTION_STRATEGIE_VERWENDEN_AN,"Typ: "+getType()));
        save();
    }
    public void nichtVerwenden(Context context){
        wirdVerwendet = false;
        strategieVerlaufsDatenListe.add(new StrategieVerlaufsDaten(StrategieVerlaufsDaten.ACTION_STRATEGIE_VERWENDEN_AUS,"Typ: "+getType()));
        save();
    }
    //TODO test entfernen
    public static boolean test = true;
    public synchronized void requestWork(Context context){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();
        if(test){
            Data inputData = new Data.Builder()
                    .putLong(BackgroundWorker.KEY_STRATEGIE_ID,id)
                    .putLong(BackgroundWorker.KEY_STRATEGIEUSERDATA_ID, userDataId)
                    .putLong(BackgroundWorker.KEY_BENUTZER_ID,benutzerManager.getAktuellerBenutzerId()).build();
            WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(BackgroundWorker.class).setInputData(inputData).setInitialDelay(0, TimeUnit.SECONDS).build();
            WorkManager.getInstance(context).enqueue(uploadWorkRequest);
            return;
        }
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        //delay ausrechenen
        nextBackgroundTrigger = notificationScheduler.getNextDate();
        strategieDatenDAO.update(this);
        if(nextBackgroundTrigger == null)
            return;
        Date jetzt = new Date();
        Long miliDely = nextBackgroundTrigger.getTime()-jetzt.getTime();
        Data inputData = new Data.Builder()
                .putLong(BackgroundWorker.KEY_STRATEGIE_ID,id)
                .putLong(BackgroundWorker.KEY_STRATEGIEUSERDATA_ID, userDataId)
                .putLong(BackgroundWorker.KEY_BENUTZER_ID,benutzerManager.getAktuellerBenutzerId()).build();
        WorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(BackgroundWorker.class).setInputData(inputData).setInitialDelay(miliDely, TimeUnit.MILLISECONDS).build();
        WorkManager.getInstance(context).enqueue(uploadWorkRequest);
    }

    public boolean checkTrigger(){
        if(test){
            return true;
        }
        if(!notificationActive){
            return false;
        }
        Date jetzt = new Date();
        if(nextBackgroundTrigger==null){
            return false;
        }
        if(jetzt.before(nextBackgroundTrigger)){
            return false;
        }
        return  true;
    }
    public NotificationInfo getNotificationInfo(){
        return new NotificationInfo("EF-App Benachrichtigung","");
    }


    /**
     * Hilft beim Aufbau der Platzhalter.
     * @param normal Geplanter Wert
     * @param wennNull Wert fals der andere Wert null ist
     * @return
     */
    protected String wennNull(String normal, String wennNull){
        if(normal == null){
            return wennNull;
        }
        return normal;
    }
    //Getter
    public long getId() {
        return id;
    }
    public String getFilenameApp() {
        return filenameApp;
    }
    public String getFilenameNotfication() {
        return filenameNotfication;
    }
    public long getUserDataId() {
        return userDataId;
    }
    public boolean isWirdVerwendet() {
        return wirdVerwendet;
    }
    public boolean isNotificationActive() {
        return notificationActive;
    }
    public Date getNextBackgroundTrigger() {
        return nextBackgroundTrigger;
    }


    public boolean isApp(){
        return filenameApp!=null;
    }
    public String getType() {
        return SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD;
    }
    public boolean kannVerwendetWerden(){
        return true;
    }

    //Setter
    public void setId(long id) {
        this.id = id;
    }
    public void setUserDataId(long userDataId) {
        this.userDataId = userDataId;
    }
    public void setWirdVerwendet(boolean wirdVerwendet) {
        this.wirdVerwendet = wirdVerwendet;
    }
    public void setNotificationActive(boolean notificationActive) {
        this.notificationActive = notificationActive;
    }
    public void setNotificationScheduler(NotificationScheduler notificationScheduler) {
        this.notificationScheduler = notificationScheduler;
    }

    public void setNextBackgroundTrigger(Date nextBackgroundTrigger) {
        this.nextBackgroundTrigger = nextBackgroundTrigger;
    }
}
