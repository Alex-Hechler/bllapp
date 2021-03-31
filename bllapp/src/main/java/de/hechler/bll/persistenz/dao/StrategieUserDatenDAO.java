package de.hechler.bll.persistenz.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;

import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.notification.NotificationScheduler;
import de.hechler.bll.data.notification.NotificationTime;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.data.strategie.StrategieVerlaufsDaten;
import de.hechler.bll.data.strategie.verlegen.VerlegenStrategie;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.contract.SkillTreeContract;
import de.hechler.bll.persistenz.contract.SkillTreeContract.StrategieUserDatenEntry;
import de.hechler.bll.persistenz.contract.VerlegenStrategieContract.VerlegenStrategieUserDatenEntry;
import de.hechler.bll.util.SQLconverter;


public class StrategieUserDatenDAO {
    SQLiteDatabase db;
    private static StrategieUserDatenDAO instance = null;

    private StrategieUserDatenDAO(SQLiteDatabase db){
        this.db = db;
    }

    public static synchronized StrategieUserDatenDAO getInstance() {
        if(instance == null) {
            SkillTreeDbHelper skillTreeDbHelper = new SkillTreeDbHelper();
            instance = new StrategieUserDatenDAO(skillTreeDbHelper.getWritableDatabase());
        }
        return instance;
    }

    private void create(Strategie s){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();
        String type = s.getType();
        Long idBenutzer = benutzerManager.getAktuellerBenutzer().getBenutzerID();

        ContentValues valuesStandard = contentValuesForStrategieUserDaten(s);
        //Statisch deshalb nicht in der contentValue Mehtode
        valuesStandard.put(StrategieUserDatenEntry.COLUMN_NAME_ID_STRATEGIE,s.getId());
        valuesStandard.put(StrategieUserDatenEntry.COLUMN_NAME_ID_BENUTZER, idBenutzer);
        long userDataId = db.insert(StrategieUserDatenEntry.TABLE_NAME,null,valuesStandard);
        s.setUserDataId(userDataId);


        switch (type){
            case SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD:
                break;
            case SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN:
                VerlegenStrategie v = (VerlegenStrategie) s;
                ContentValues values = contentValuesForVerlegenUserDaten(v);
                db.insert(VerlegenStrategieUserDatenEntry.TABLE_NAME,null, values);
                break;
        }
    }
    public void update(Strategie s){
        StrategieVerlaufsDatenDAO strategieVerlaufsDatenDAO = StrategieVerlaufsDatenDAO.getInstance();
        String type = s.getType();

        //Allgemein
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {s.getUserDataId()+""};

        ContentValues valuesStandard = contentValuesForStrategieUserDaten(s);
        db.update(StrategieUserDatenEntry.TABLE_NAME,valuesStandard, selection, selectionArgs);
        //Verlaufsdaten
        //falls die Verlaufsdaten noch nicht in der Tabelle sind werden sie erzeugt
        for(StrategieVerlaufsDaten svd : s.getStrategieVerlaufsDatenListe()){
            if(svd.getId()==-1){
                strategieVerlaufsDatenDAO.create(svd, s.getUserDataId());
            }
        }
        //Typ spezifesch
        switch (type){
            case SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD:
                break;
            case SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN:
                VerlegenStrategie v = (VerlegenStrategie) s;
                ContentValues values = contentValuesForVerlegenUserDaten(v);
                db.update(VerlegenStrategieUserDatenEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
        }
    }

    /**
     * Findet die UserDataId. Wird keine Id gefunden wird null zurückgegeben.
     * @param benutzerId
     * @param strategieId
     * @return
     */
    private Long findUserDataId(long benutzerId, long strategieId){
        Long userDataId = null;
        String[] columns = {BaseColumns._ID};
        String selection = StrategieUserDatenEntry.COLUMN_NAME_ID_BENUTZER +" = ? AND "+StrategieUserDatenEntry.COLUMN_NAME_ID_STRATEGIE +" = ?" ;
        String[] selectionArgs = {benutzerId+"",strategieId+""};

        Cursor cursor = db.query(
                StrategieUserDatenEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        if(cursor.moveToNext()){
            userDataId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        }
        cursor.close();
        return userDataId;
    }

    /**
     * Es wird eine Strategie ohne benutzerspezifische Daten übergeben.
     * Für diese werden dann die Daten des aktuellen Nutzers aus der Datenbank ausgelesen und gesetzt.
     * Ist noch kein Eintrag in der Datenbank vorhanden, wird einer erstellt.
     * @param s
     * @return
     */
    public void readUserDataOhneVerlauf(Strategie s){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();

        Long userDataId = findUserDataId(benutzerManager.getAktuellerBenutzerId(),s.getId());
        if(userDataId!=null){
            s.setUserDataId(userDataId);
        }else{
            create(s);
            s.setNotificationScheduler(readNotifications(s.getUserDataId()));
            return;
        }

        boolean wirdVerwendet = false;
        boolean notificationActive = false;
        Date nextBackgroundTrigger = null;
        String type = s.getType();


        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {userDataId+""};

        Cursor cursor = db.query(
                StrategieUserDatenEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        if(cursor.moveToNext()){
            Integer wirdVerwendetInt = cursor.getInt(cursor.getColumnIndex(StrategieUserDatenEntry.COLUMN_NAME_WIRDVERWENDET));
            wirdVerwendet = SQLconverter.sqlToBoolean(wirdVerwendetInt);
            Integer notificationActiveInt = cursor.getInt(cursor.getColumnIndex(StrategieUserDatenEntry.COLUMN_NAME_NOTIFICATIONACTIVE));
            notificationActive = SQLconverter.sqlToBoolean(notificationActiveInt);
            String nextBackgroundTriggerString = cursor.getString(cursor.getColumnIndex(StrategieUserDatenEntry.COLUMN_NAME_NEXTBACKGROUNDTRIGGER));
            nextBackgroundTrigger = SQLconverter.sqlToDate(nextBackgroundTriggerString);
        }
        cursor.close();

        s.setWirdVerwendet(wirdVerwendet);
        s.setNotificationActive(notificationActive);
        s.setNextBackgroundTrigger(nextBackgroundTrigger);

        s.setNotificationScheduler(readNotifications(userDataId));



        //Typ spezifisch
        switch (type){
            case SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD:
                break;
            case SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN:
                VerlegenStrategie v = (VerlegenStrategie)s;
                cursor = db.query(VerlegenStrategieUserDatenEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                if(!cursor.moveToNext()){
                    throw new RuntimeException("inconsitenct data for type "+type+" and userDataId "+userDataId);
                }
                String gegenstandName = cursor.getString(cursor.getColumnIndex(VerlegenStrategieUserDatenEntry.COLUMN_NAME_GEGENSTANDNAME));
                String aufbewahrungsOrt = cursor.getString(cursor.getColumnIndex(VerlegenStrategieUserDatenEntry.COLUMN_NAME_AUFBEWAHRUNGSORT));
                v.setGegenstandName(gegenstandName);
                v.setAufbewahrungsOrt(aufbewahrungsOrt);
                break;
        }
    }

    /**
     * Erstellt mittels der StrategieUserDatenID einen Notificationschuler.
     * Liest alle entsprechenden Notifications aus der Datenbank und fügt sie hinzu.
     * @param strategieUserDatenId
     * @return ein NotificationScheduler das alle Notifications der Strategie des Users enthält
     */
    public NotificationScheduler readNotifications(long strategieUserDatenId){
        NotificationTimeDAO notificationTimeDAO = NotificationTimeDAO.getInstance();

        NotificationScheduler notificationScheduler = new NotificationScheduler(strategieUserDatenId);

        ArrayList<Long> notificationIDs = notificationTimeDAO.getNotificationIDsbyStrategieUserDatenID(strategieUserDatenId);
        for(long notficationID : notificationIDs){
            NotificationTime notificationTime = notificationTimeDAO.read(notficationID);
            notificationScheduler.addNotfication(notificationTime);
        }
        return notificationScheduler;
    }
    public void readUserDataMitVerlauf(Strategie s){
        StrategieVerlaufsDatenDAO strategieVerlaufsDatenDAO = StrategieVerlaufsDatenDAO.getInstance();
        if(s ==null)
            return;
        readUserDataOhneVerlauf(s);
        ArrayList<Long> verlaufsIds = strategieVerlaufsDatenDAO.getVerlaufsIDbyStrategieID(s.getUserDataId());
        for(long verlaufsID : verlaufsIds){
            StrategieVerlaufsDaten strategieVerlaufsDaten = strategieVerlaufsDatenDAO.read(verlaufsID);
            s.addVerlauf(strategieVerlaufsDaten);
        }
    }

    /**
     * List die Verlaufsdaten von s und fuegt diese hinzu.
     * @param s die strategie
     */
    public void readVerlaufForStrategie(Strategie s){
        StrategieVerlaufsDatenDAO strategieVerlaufsDatenDAO = StrategieVerlaufsDatenDAO.getInstance();

        ArrayList<Long> verlaufsIds = strategieVerlaufsDatenDAO.getVerlaufsIDbyStrategieID(s.getUserDataId());
        for(long verlaufsID : verlaufsIds){
            StrategieVerlaufsDaten strategieVerlaufsDaten = strategieVerlaufsDatenDAO.read(verlaufsID);
            s.addVerlauf(strategieVerlaufsDaten);
        }
    }
    public ArrayList<Long> getListVerwendetForBenutzerID(Long benutzerId){
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        ArrayList<Long> rtn = new ArrayList<>();
        String[] columns = {StrategieUserDatenEntry.COLUMN_NAME_ID_STRATEGIE};
        String selection = StrategieUserDatenEntry.COLUMN_NAME_ID_BENUTZER+" = ? AND "+StrategieUserDatenEntry.COLUMN_NAME_WIRDVERWENDET+" = ?";
        String[] selectionArgs = {benutzerId+"",1+""};

        Cursor cursor = db.query(
                StrategieUserDatenEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            Long strategieID = cursor.getLong(cursor.getColumnIndex(StrategieUserDatenEntry.COLUMN_NAME_ID_STRATEGIE));
            rtn.add(strategieID);
        }
        return rtn;
    }
    public ArrayList<Long> getAllIdsForBenutzer(Long benutzerId){
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        ArrayList<Long> rtn = new ArrayList<>();
        String[] columns = {StrategieUserDatenEntry._ID};
        String selection = StrategieUserDatenEntry.COLUMN_NAME_ID_BENUTZER+" = ?";
        String[] selectionArgs = {benutzerId+""};

        Cursor cursor = db.query(
                StrategieUserDatenEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            Long strategieUserID = cursor.getLong(cursor.getColumnIndex(StrategieUserDatenEntry._ID));
            rtn.add(strategieUserID);
        }
        return rtn;
    }
    public void readVerlaufOfStrategie(Strategie strategie){
        StrategieVerlaufsDatenDAO strategieVerlaufsDatenDAO = StrategieVerlaufsDatenDAO.getInstance();
        ArrayList<Long> verlaufsIds = strategieVerlaufsDatenDAO.getVerlaufsIDbyStrategieID(strategie.getId());
        for(long verlaufsID : verlaufsIds){
            StrategieVerlaufsDaten strategieVerlaufsDaten = strategieVerlaufsDatenDAO.read(verlaufsID);
            strategie.addVerlauf(strategieVerlaufsDaten);
        }
    }
    public void delete(long id){
        StrategieVerlaufsDatenDAO strategieVerlaufsDatenDAO = StrategieVerlaufsDatenDAO.getInstance();
        NotificationTimeDAO notificationTimeDAO = NotificationTimeDAO.getInstance();

        strategieVerlaufsDatenDAO.deleteAllStrategieuserData(id);
        notificationTimeDAO.deleteAllStrategieUserData(id);

        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};
        //Aus allen Tabllen löschen bevor die Muttertabelle gelöscht wird
        //Hier erweitern, wenn neue Typen eingeführt werden
        db.delete(VerlegenStrategieUserDatenEntry.TABLE_NAME,selection,selectionArgs);
        //Muttertabelle löschen
        db.delete(StrategieUserDatenEntry.TABLE_NAME, selection, selectionArgs);


    }
    public void deleteAllBenutzerData(long benutzerId){
        ArrayList<Long> allIds = getAllIdsForBenutzer(benutzerId);
        for(long id : allIds){
            delete(id);
        }
    }



    private ContentValues contentValuesForStrategieUserDaten(Strategie s){
        Integer wirdVerwendet = SQLconverter.booleanToSQL(s.isWirdVerwendet());
        Integer notificationActive = SQLconverter.booleanToSQL(s.isNotificationActive());
        String nextBackgroundTrigger = SQLconverter.dateToSQL(s.getNextBackgroundTrigger());
        ContentValues values = new ContentValues();
        values.put(StrategieUserDatenEntry.COLUMN_NAME_WIRDVERWENDET, wirdVerwendet);
        values.put(StrategieUserDatenEntry.COLUMN_NAME_NOTIFICATIONACTIVE, notificationActive);
        values.put(StrategieUserDatenEntry.COLUMN_NAME_NEXTBACKGROUNDTRIGGER, nextBackgroundTrigger);
        return values;
    }
    private ContentValues contentValuesForVerlegenUserDaten(VerlegenStrategie v){
        ContentValues values = new ContentValues();
        values.put(VerlegenStrategieUserDatenEntry._ID, v.getUserDataId());
        values.put(VerlegenStrategieUserDatenEntry.COLUMN_NAME_GEGENSTANDNAME, v.getGegenstandName());
        values.put(VerlegenStrategieUserDatenEntry.COLUMN_NAME_AUFBEWAHRUNGSORT, v.getAufbewahrungsOrt());
        return values;
    }

}
