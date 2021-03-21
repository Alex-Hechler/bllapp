package de.hechler.bll.persistenz.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;

import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.skillTree.NodeModel;
import de.hechler.bll.data.skillTree.StrategieNodeModel;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.contract.SkillTreeContract;
import de.hechler.bll.persistenz.contract.SkillTreeContract.NodeModelEntry;
import de.hechler.bll.persistenz.contract.SkillTreeContract.NodeUserDataEntry;
import de.hechler.bll.util.SQLconverter;

public class NodeUserDataDAO {
    SQLiteDatabase db;
    private static NodeUserDataDAO instance = null;

    private NodeUserDataDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public static synchronized NodeUserDataDAO getInstance() {
        if(instance == null) {
            SkillTreeDbHelper skillTreeDbHelper = new SkillTreeDbHelper();
            instance = new NodeUserDataDAO(skillTreeDbHelper.getWritableDatabase());
        }
        return instance;
    }

    private void create(NodeModel n){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();
        String type = n.getType();

        ContentValues values = contentValuesForNodeUserData(n);
        values.put(NodeUserDataEntry.COLUMN_NAME_ID_BENUTZER,benutzerManager.getAktuellerBenutzerId());
        values.put(NodeUserDataEntry.COLUMN_NAME_ID_NODEMODEL, n.getNodeID());
        long newRowId = db.insert(NodeUserDataEntry.TABLE_NAME, null,values);
        n.setNodeUserDataID(newRowId);
    }

    public void readUserData(NodeModel n){
        Long nodeUserDataId = getNodeUserDataIdForCurrentUserByNodeId(n.getNodeID());
        //Typ unabhänig
        boolean freigeschaltet = false;
        boolean besucht = false;

        if(nodeUserDataId!=null){
            n.setNodeUserDataID(nodeUserDataId);
        }else{
            create(n);
            return;
        }

        String selection = NodeUserDataEntry._ID + " = ?";
        String[] selectionArgs = {nodeUserDataId + ""};

        Cursor cursor = db.query(
                NodeUserDataEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if (cursor.moveToNext()) {
            int freigeschaltetInt = cursor.getInt(cursor.getColumnIndex(NodeUserDataEntry.COLUMN_NAME_FREIGESCHALTET));
            freigeschaltet = SQLconverter.sqlToBoolean(freigeschaltetInt);
            int besuchtInt = cursor.getInt(cursor.getColumnIndex(NodeUserDataEntry.COLUMN_NAME_BESUCHT));
            besucht = SQLconverter.sqlToBoolean(besuchtInt);
        }
        cursor.close();

        n.setFreigeschaltet(freigeschaltet);
        n.setBesucht(besucht);
    }
    public void update(NodeModel n){
        String type = n.getType();
        //updated values
        ContentValues values = contentValuesForNodeUserData(n);
        // Which row to update, based on the id
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {n.getNodeUserDataID()+"" };

        //update
        db.update(
                NodeUserDataEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    /**
     * Gibt die id der nodeUserData als Long zurück.
     * Wird die nodeUserData nicht gefunden wird null zurück gegeben.
     * @param nodeId
     * @return
     */
    private Long getNodeUserDataIdForCurrentUserByNodeId(long nodeId){
        BenutzerManager benutzerManager = BenutzerManager.getInstance();

        Long nodeUserDataId = null;

        String selection = NodeUserDataEntry.COLUMN_NAME_ID_BENUTZER +" = ? AND "+NodeUserDataEntry.COLUMN_NAME_ID_NODEMODEL +" = ?" ;
        String[] selectionArgs = {benutzerManager.getAktuellerBenutzerId()+"",nodeId+""};

        Cursor cursor = db.query(
                NodeUserDataEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            nodeUserDataId = cursor.getLong(cursor.getColumnIndex(NodeUserDataEntry._ID));
        }
        cursor.close();
        return nodeUserDataId;
    }
    public void delete(NodeModel n){

        // Define 'where' part of query.
        String selection = BaseColumns._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { n.getNodeUserDataID()+"" };
        // Issue SQL statement.

        db.delete(NodeUserDataEntry.TABLE_NAME, selection, selectionArgs);
    }
    public void deleteAllBenutzerInfo(long benutzerId){
        String selection = NodeUserDataEntry.COLUMN_NAME_ID_BENUTZER + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { benutzerId+"" };
        // Issue SQL statement.

        db.delete(NodeUserDataEntry.TABLE_NAME, selection, selectionArgs);
    }

    private ContentValues contentValuesForNodeUserData(NodeModel n){
        ContentValues values = new ContentValues();
        int freigeschaltet = SQLconverter.booleanToSQL(n.isFreigeschaltet());
        int besucht = SQLconverter.booleanToSQL(n.isBesucht());
        values.put(NodeUserDataEntry.COLUMN_NAME_FREIGESCHALTET, freigeschaltet);
        values.put(NodeUserDataEntry.COLUMN_NAME_BESUCHT, besucht);
        return values;
    }
}
