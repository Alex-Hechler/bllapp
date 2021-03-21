package de.hechler.bll.persistenz.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.data.strategie.verlegen.VerlegenStrategie;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.contract.SkillTreeContract.StrategieDatenEntry;


public class StrategieDatenDAO {
    SQLiteDatabase db;
    private static StrategieDatenDAO instance = null;

    private StrategieDatenDAO(SQLiteDatabase db){
        this.db = db;
    }

    public static synchronized StrategieDatenDAO getInstance() {
        if(instance == null) {
            SkillTreeDbHelper skillTreeDbHelper = new SkillTreeDbHelper();
            instance = new StrategieDatenDAO(skillTreeDbHelper.getWritableDatabase());
        }
        return instance;
    }
    //unnötig
    public void create(Strategie s){
        String type = s.getType();

        ContentValues valuesStandard = contentValuesForStrategieDaten(s);
        long id = db.insert(StrategieDatenEntry.TABLE_NAME,null,valuesStandard);
        s.setId(id);


        switch (type){
            case StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD:
                break;
            case StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN:
                break;
        }
    }

    /**
     * Speichert die Klasse Strategie, also auch die UserDaten.
     * @param s
     */
    public void update(Strategie s){
        StrategieUserDatenDAO strategieUserDatenDAO = StrategieUserDatenDAO.getInstance();
        String type = s.getType();

        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {s.getId()+""};

        ContentValues valuesStandard = contentValuesForStrategieDaten(s);
        db.update(StrategieDatenEntry.TABLE_NAME,valuesStandard, selection, selectionArgs);


        switch (type){
            case StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD:
                break;
            case StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN:
                break;
        }
        //UserDaten
        strategieUserDatenDAO.update(s);
    }

    /**
     * Liest alle für die Klasse Stategie benötigten Daten aus, also auch die UserDaten.
     * @param id
     * @return
     */
    public Strategie read(long id){
        StrategieUserDatenDAO strategieUserDatenDAO = StrategieUserDatenDAO.getInstance();
        Strategie neu = null;
        String type = null;
        String filenameApp = null;
        String filenameNotification = null;
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};

        Cursor cursor = db.query(
                StrategieDatenEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            type = cursor.getString(cursor.getColumnIndex(StrategieDatenEntry.COLUMN_NAME_TYPE));
            filenameApp = cursor.getString(cursor.getColumnIndex(StrategieDatenEntry.COLUMN_NAME_FILENAMEAPP));
            filenameNotification = cursor.getString(cursor.getColumnIndex(StrategieDatenEntry.COLUMN_NAME_FILENAMENOTIFICATOIN));
        }
        cursor.close();
        if(type==null){
            return null;
        }
        switch (type){
            case StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD:
                neu = new Strategie(id, filenameApp, filenameNotification);
                break;
            case StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN:
                neu = new VerlegenStrategie(id, filenameApp, filenameNotification);
                break;
        }
        strategieUserDatenDAO.readUserDataOhneVerlauf(neu);
        return neu;
    }

    public void delete(long id){
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};
        //TODO: Referenzens
        db.delete(StrategieDatenEntry.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Gibt die verwendeten/nicht verwendeten App-Strategien zurück.
     * @param benutzerId
     * @param verwendet
     * @return
     */
    public ArrayList<Strategie> readAppListForBenutzerID(Long benutzerId, boolean verwendet){
        StrategieUserDatenDAO strategieUserDatenDAO = StrategieUserDatenDAO.getInstance();
        ArrayList<Strategie> rtn = new ArrayList<>();
        ArrayList<Long> ids;
        ArrayList<Long> idsVerwendet = strategieUserDatenDAO.getListVerwendetForBenutzerID(benutzerId);
        ArrayList<Long> idsApps = readListAppIDs();
        if(verwendet) {
            idsVerwendet.retainAll(idsApps);
            ids = idsVerwendet;
        }else {
            idsApps.removeAll(idsVerwendet);
            ids = idsApps;
        }

        for(long id : ids){
            Strategie s = read(id);
            rtn.add(s);
        }
        return rtn;
    }

    /**
     * Gibt die verwendeten/nicht verwendeten App-Strategien zurück.
     * @param benutzerId
     * @param verwendet
     * @return
     */
    public ArrayList<Strategie> readStrategieListForBenutzerID(Long benutzerId, boolean verwendet){
        StrategieUserDatenDAO strategieUserDatenDAO = StrategieUserDatenDAO.getInstance();
        ArrayList<Strategie> rtn = new ArrayList<>();
        ArrayList<Long> ids;
        ArrayList<Long> idsVerwendet = strategieUserDatenDAO.getListVerwendetForBenutzerID(benutzerId);
        ArrayList<Long> idsStrategien = readListStrategieIDs();
        if(verwendet) {
            idsVerwendet.retainAll(idsStrategien);
            ids = idsVerwendet;
        }else {
            idsStrategien.removeAll(idsVerwendet);
            ids = idsStrategien;
        }

        for(long id : ids){
            Strategie s = read(id);
            rtn.add(s);
        }
        return rtn;
    }
    public ArrayList<Long> readListAppIDs(){
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        ArrayList<Long> rtn = new ArrayList<>();
        String[] columns = {StrategieDatenEntry._ID};
        String selection = StrategieDatenEntry.COLUMN_NAME_FILENAMEAPP+" IS NOT NULL";
        String[] selectionArgs = {};

        Cursor cursor = db.query(
                StrategieDatenEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            Long strategieID = cursor.getLong(cursor.getColumnIndex(StrategieDatenEntry._ID));
            rtn.add(strategieID);
        }
        return rtn;
    }
    public ArrayList<Long> readListStrategieIDs(){
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        ArrayList<Long> rtn = new ArrayList<>();
        String[] columns = {StrategieDatenEntry._ID};
        String selection = StrategieDatenEntry.COLUMN_NAME_FILENAMEAPP+" IS NULL";
        String[] selectionArgs = {};

        Cursor cursor = db.query(
                StrategieDatenEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            Long strategieID = cursor.getLong(cursor.getColumnIndex(StrategieDatenEntry._ID));
            rtn.add(strategieID);
        }
        return rtn;
    }

    private ContentValues contentValuesForStrategieDaten(Strategie s){
        ContentValues values = new ContentValues();
        values.put(StrategieDatenEntry.COLUMN_NAME_TYPE,s.getType());
        values.put(StrategieDatenEntry.COLUMN_NAME_FILENAMEAPP, s.getFilenameApp());
        values.put(StrategieDatenEntry.COLUMN_NAME_FILENAMENOTIFICATOIN, s.getFilenameNotfication());
        return values;
    }


}
