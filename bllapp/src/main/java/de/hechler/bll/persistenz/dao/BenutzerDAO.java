package de.hechler.bll.persistenz.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.notification.NotificationTime;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.contract.SkillTreeContract.BenutzerEntry;
import de.hechler.bll.util.SQLconverter;

public class BenutzerDAO {
    SQLiteDatabase db;
    private static BenutzerDAO instance = null;

    private BenutzerDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public static synchronized BenutzerDAO getInstance() {
        if(instance == null) {
            SkillTreeDbHelper skillTreeDbHelper = new SkillTreeDbHelper();
            instance = new BenutzerDAO(skillTreeDbHelper.getWritableDatabase());
        }
        return instance;
    }

    public void create(Benutzer b){
        ContentValues values = contentValuesForBenutzer(b);
        long id = db.insert(BenutzerEntry.TABLE_NAME,null, values );
        b.setBenutzerID(id);
    }

    public Benutzer read(long id){
        Benutzer neu = null;
        String name = null;
        Integer iconAssetId = null;
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};

        Cursor cursor = db.query(
                BenutzerEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(BenutzerEntry.COLUMN_NAME_NAME));
            iconAssetId = cursor.getInt(cursor.getColumnIndex(BenutzerEntry.COLUMN_NAME_ICONASSETID));
            neu = new Benutzer(id, name, iconAssetId);
        }
        cursor.close();
        return neu;
    }
    public Benutzer read(){
        Benutzer neu = null;
        String name = null;
        Integer iconAssetId = null;

        Cursor cursor = db.query(
                BenutzerEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(BenutzerEntry._ID));
            name = cursor.getString(cursor.getColumnIndex(BenutzerEntry.COLUMN_NAME_NAME));
            iconAssetId = cursor.getInt(cursor.getColumnIndex(BenutzerEntry.COLUMN_NAME_ICONASSETID));
            neu = new Benutzer(id, name, iconAssetId);
        }
        cursor.close();
        return neu;
    }
    public ArrayList<Long> readAllIDs(){
        ArrayList<Long> rtn = new ArrayList<>();
        String[] columns = {BaseColumns._ID};

        Cursor cursor = db.query(
                BenutzerEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            long benutzerId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            rtn.add(benutzerId);
        }
        cursor.close();
        return rtn;
    }

    public void update(Benutzer b){
        ContentValues values = contentValuesForBenutzer(b);

        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {b.getBenutzerID()+""};

        db.update(BenutzerEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void delete(Benutzer b){
        NodeUserDataDAO nodeUserDataDAO = NodeUserDataDAO.getInstance();
        StrategieUserDatenDAO strategieUserDatenDAO = StrategieUserDatenDAO.getInstance();
        strategieUserDatenDAO.deleteAllBenutzerData(b.getBenutzerID());
        nodeUserDataDAO.deleteAllBenutzerInfo(b.getBenutzerID());

        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {b.getBenutzerID()+""};

        db.delete(BenutzerEntry.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Gibt true zurück, wenn der Name noch nicht existiert.
     * False wenn er bereits existiert
     */
    public boolean isNameFree(String name){
        boolean nameFree = true;
        String selection = BenutzerEntry.COLUMN_NAME_NAME +" = ?";
        String[] selectionArgs = {name+""};

        Cursor cursor = db.query(
                BenutzerEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            nameFree = false;
        }
        cursor.close();
        return nameFree;
    }
    private ContentValues contentValuesForBenutzer(Benutzer b){
        ContentValues values = new ContentValues();
        values.put(BenutzerEntry.COLUMN_NAME_NAME, b.getName());
        values.put(BenutzerEntry.COLUMN_NAME_ICONASSETID, b.getIconAssetId());
        return values;
    }
}
