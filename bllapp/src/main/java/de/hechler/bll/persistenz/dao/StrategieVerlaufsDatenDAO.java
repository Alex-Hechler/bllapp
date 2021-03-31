package de.hechler.bll.persistenz.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;

import de.hechler.bll.data.strategie.StrategieVerlaufsDaten;
import de.hechler.bll.data.strategie.verlegen.VerlegenStrategieVerlaufsDaten;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.contract.SkillTreeContract.StrategieVerlaufsDatenEntry;
import de.hechler.bll.persistenz.contract.VerlegenStrategieContract.VerlegenStrategieVerlaufsDatenEntry;
import de.hechler.bll.util.SQLconverter;

public class StrategieVerlaufsDatenDAO {
    SQLiteDatabase db;
    private static StrategieVerlaufsDatenDAO instance = null;

    private StrategieVerlaufsDatenDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public static synchronized StrategieVerlaufsDatenDAO getInstance() {
        if(instance == null) {
            SkillTreeDbHelper skillTreeDbHelper = new SkillTreeDbHelper();
            instance = new StrategieVerlaufsDatenDAO(skillTreeDbHelper.getWritableDatabase());
        }
        return instance;
    }
    public void create(StrategieVerlaufsDaten s,long idStrategieUserDaten){
        String type = s.getType();

        ContentValues valuesStandard = contentValuesForStrategieVerlaufsDaten(s, idStrategieUserDaten);
        long id = db.insertOrThrow(StrategieVerlaufsDatenEntry.TABLE_NAME,null,valuesStandard);
        s.setId(id);


        switch (type){
            case StrategieVerlaufsDatenEntry.VERLAUF_DATA_TYPE_STANDARD:
                break;
            case StrategieVerlaufsDatenEntry.VERLAUF_DATA_TYPE_VERLEGEN:
                VerlegenStrategieVerlaufsDaten v = (VerlegenStrategieVerlaufsDaten) s;
                ContentValues values = contentValuesForVerlegen(v);
                db.insert(VerlegenStrategieVerlaufsDatenEntry.TABLE_NAME,null, values);
                break;
        }
    }
    public StrategieVerlaufsDaten read(long id){
        StrategieVerlaufsDaten neu = null;
        String type = null;
        Date zeitstempel = null;
        String action = null;
        String info = null;
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};

        Cursor cursor = db.query(
                StrategieVerlaufsDatenEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            type = cursor.getString(cursor.getColumnIndex(StrategieVerlaufsDatenEntry.COLUMN_NAME_TYPE));
            String zeitstempelString = cursor.getString(cursor.getColumnIndex(StrategieVerlaufsDatenEntry.COLUMN_NAME_ZEITSTEMPEL));
            zeitstempel = SQLconverter.sqlToDate(zeitstempelString);
            action = cursor.getString(cursor.getColumnIndex(StrategieVerlaufsDatenEntry.COLUMN_NAME_ACTION));
            info = cursor.getString(cursor.getColumnIndex(StrategieVerlaufsDatenEntry.COLUMN_NAME_INFO));
        }
        cursor.close();
        if(type==null){
            return null;
        }
        switch (type){
            case StrategieVerlaufsDatenEntry.VERLAUF_DATA_TYPE_STANDARD:
                neu = new StrategieVerlaufsDaten(id,zeitstempel,action, info);
                break;
            case StrategieVerlaufsDatenEntry.VERLAUF_DATA_TYPE_VERLEGEN:
                cursor = db.query(VerlegenStrategieVerlaufsDatenEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                if(!cursor.moveToNext()){
                    throw new RuntimeException("inconsitenct data for type "+type+" and id "+id);
                }
                int gefundenInt= cursor.getInt(cursor.getColumnIndex(VerlegenStrategieVerlaufsDatenEntry.COLUMN_NAME_GEFUNDEN));
                boolean gefunden = SQLconverter.sqlToBoolean(gefundenInt);
                String falscherOrt = cursor.getString(cursor.getColumnIndex(VerlegenStrategieVerlaufsDatenEntry.COLUMN_NAME_FALSCHERORT));
                neu = new VerlegenStrategieVerlaufsDaten(id,zeitstempel,action,info, gefunden,falscherOrt);
                break;
        }
        return neu;
    }
    public ArrayList<Long> getVerlaufsIDbyStrategieID(long strategieUserDatenId){
        ArrayList<Long> rtn = new ArrayList<>();
        String selection = StrategieVerlaufsDatenEntry.COLUMN_NAME_ID_STRATEGIEUSERDATEN +" = ?";
        String[] selectionArgs = {strategieUserDatenId+""};
        String[] columns = {BaseColumns._ID};

        Cursor cursor = db.query(
                StrategieVerlaufsDatenEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            long verlaufsId = cursor.getLong(0);
            rtn.add(verlaufsId);
        }
        cursor.close();
        return rtn;
    }
    public void delete(long id){
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};

        //Aus allen Tabllen löschen bevor die Muttertabelle gelöscht wird
        //Hier erweitern, wenn neue Typen eingeführt werden
        db.delete(VerlegenStrategieVerlaufsDatenEntry.TABLE_NAME,selection,selectionArgs);
        //Muttertabelle löschen
        db.delete(StrategieVerlaufsDatenEntry.TABLE_NAME, selection, selectionArgs);


    }
    public void deleteAllStrategieuserData(long strategieUserDataId){
        ArrayList<Long> allIds = getVerlaufsIDbyStrategieID(strategieUserDataId);
        for(long id : allIds){
            delete(id);
        }
    }
    private ContentValues contentValuesForStrategieVerlaufsDaten(StrategieVerlaufsDaten s,long idStrategieUserDaten){
        ContentValues values = new ContentValues();
        values.put(StrategieVerlaufsDatenEntry.COLUMN_NAME_TYPE,s.getType());
        values.put(StrategieVerlaufsDatenEntry.COLUMN_NAME_ZEITSTEMPEL, SQLconverter.dateToSQL(s.getZeitstempel()));
        values.put(StrategieVerlaufsDatenEntry.COLUMN_NAME_ACTION, s.getAction());
        values.put(StrategieVerlaufsDatenEntry.COLUMN_NAME_INFO, s.getInfo());
        values.put(StrategieVerlaufsDatenEntry.COLUMN_NAME_ID_STRATEGIEUSERDATEN, idStrategieUserDaten);
        return values;
    }
    private ContentValues contentValuesForVerlegen(VerlegenStrategieVerlaufsDaten v){
        ContentValues values = new ContentValues();
        values.put(VerlegenStrategieVerlaufsDatenEntry._ID, v.getId());
        values.put(VerlegenStrategieVerlaufsDatenEntry.COLUMN_NAME_GEFUNDEN, v.isGefunden());
        values.put(VerlegenStrategieVerlaufsDatenEntry.COLUMN_NAME_FALSCHERORT, v.getFalscherOrt());
        return values;
    }

}
