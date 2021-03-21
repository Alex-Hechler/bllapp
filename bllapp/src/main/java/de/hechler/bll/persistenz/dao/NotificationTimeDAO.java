package de.hechler.bll.persistenz.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;

import de.hechler.bll.data.notification.NotificationTime;
import de.hechler.bll.data.notification.WiederhohlungsZeitraum;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.util.SQLconverter;
import de.hechler.bll.persistenz.contract.SkillTreeContract.NotificationTimeEntry;

public class NotificationTimeDAO {
    SQLiteDatabase db;
    private static NotificationTimeDAO instance = null;

    private NotificationTimeDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public static synchronized NotificationTimeDAO getInstance() {
        if(instance == null) {
            SkillTreeDbHelper skillTreeDbHelper = new SkillTreeDbHelper();
            instance = new NotificationTimeDAO(skillTreeDbHelper.getWritableDatabase());
        }
        return instance;
    }

    public void create(NotificationTime t, long idUserStrategieDaten){
        ContentValues values = contentValuesForNotificationTime(t);
        values.put(NotificationTimeEntry.COLUMN_NAME_ID_STRATEGIEUSERDATEN, idUserStrategieDaten);
        long id = db.insertOrThrow(NotificationTimeEntry.TABLE_NAME,null,values);
        t.setId(id);
    }




    public NotificationTime read(long id){
        NotificationTime neu = null;
        Date ersteNotification = null;
        WiederhohlungsZeitraum wiederhohlung = null;
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};

        Cursor cursor = db.query(
                NotificationTimeEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            String ersteNotificationString = cursor.getString(cursor.getColumnIndex(NotificationTimeEntry.COLUMN_NAME_ERSTENOTIFICATION));
            ersteNotification = SQLconverter.sqlToDate(ersteNotificationString);
            String wiederholungString = cursor.getString(cursor.getColumnIndex(NotificationTimeEntry.COLUMN_NAME_WIEDERHOHLUNG));
            wiederhohlung = WiederhohlungsZeitraum.valueOf(wiederholungString);
            neu = new NotificationTime(id, ersteNotification, wiederhohlung);
        }
        cursor.close();
        return neu;
    }

    public ArrayList<Long> getNotificationIDsbyStrategieUserDatenID(long strategieUserDatenID){
        ArrayList<Long> rtn = new ArrayList<>();
        String[] columns = {BaseColumns._ID};
        String selection = NotificationTimeEntry.COLUMN_NAME_ID_STRATEGIEUSERDATEN +" = ?";
        String[] selectionArgs = {strategieUserDatenID+""};

        Cursor cursor = db.query(
                NotificationTimeEntry.TABLE_NAME,
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

    public void update(NotificationTime t){
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {t.getId()+""};

        ContentValues values = contentValuesForNotificationTime(t);
        db.update(NotificationTimeEntry.TABLE_NAME,values,selection,selectionArgs);
    }
    public void delete(long id){
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};

        db.delete(NotificationTimeEntry.TABLE_NAME,selection,selectionArgs);
    }
    public void deleteAllStrategieUserData(long strategieUserDataId){
        ArrayList<Long> allIds = getNotificationIDsbyStrategieUserDatenID(strategieUserDataId);
        for(long id : allIds){
            delete(id);
        }
    }

    private ContentValues contentValuesForNotificationTime(NotificationTime t){
        ContentValues values = new ContentValues();
        values.put(NotificationTimeEntry.COLUMN_NAME_ERSTENOTIFICATION,SQLconverter.dateToSQL(t.getErsteNotification()));
        values.put(NotificationTimeEntry.COLUMN_NAME_WIEDERHOHLUNG, t.getWiederhohlung().toString());
        return values;
    }
}
