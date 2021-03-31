package de.hechler.bll.persistenz.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.HashMap;

import de.hechler.bll.data.skillTree.StrategieNodeModel;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.util.SQLconverter;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.contract.SkillTreeContract.StrategieNodeModelEntry;
import de.hechler.bll.persistenz.contract.SkillTreeContract.NodeModelEntry;

import de.hechler.bll.data.skillTree.NodeModel;

public class NodeModelDAO {
    SQLiteDatabase db;
    private static NodeModelDAO instance = null;

    private NodeModelDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public static synchronized NodeModelDAO getInstance() {
        if(instance == null) {
            SkillTreeDbHelper skillTreeDbHelper = new SkillTreeDbHelper();
            instance = new NodeModelDAO(skillTreeDbHelper.getWritableDatabase());
        }
        return instance;
    }

    public void create(NodeModel n){
        String type = n.getType();

        ContentValues valuesMinimum = contentValuesForNodeModel(n);
        long newRowId = db.insert(NodeModelEntry.TABLE_NAME, null,valuesMinimum);
        n.setNodeID(newRowId);

        switch (type){
            case NodeModelEntry.NODE_TYPE_LEKTION:
                break;
            case NodeModelEntry.NODE_TYPE_STRATEGIE:
                StrategieNodeModel s = (StrategieNodeModel) n;
                StrategieDatenDAO.getInstance().create(s.getStrategie());
                ContentValues values = contentValuesForStrategieNodeModel(s);
                db.insert(StrategieNodeModelEntry.TABLE_NAME,null, values);
                break;
            default:
                throw new RuntimeException("unbekannter Node type"+type);
        }
    }

    /**
     * Liest alle für die Klasse NodeModel benötigeten Daten, außer der Kinder.
     * Das bedeutet es werden auch die UserDaten für den angemeldeten Benutzer ausgelesen.
     * @param id
     * @return
     */
    public NodeModel readWithoutChildren(long id){
        NodeUserDataDAO nodeUserDataDAO = NodeUserDataDAO.getInstance();

        NodeModel neu = null;
        String type = null;
        String name = null;
        String filename = null;
        //Minimum part
        String selection = BaseColumns._ID +" = ?";
        String[] selectionArgs = {id+""};

        Cursor cursor = db.query(
                NodeModelEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            type = cursor.getString(cursor.getColumnIndex(NodeModelEntry.COLUMN_NAME_TYPE));
            name = cursor.getString(cursor.getColumnIndex(NodeModelEntry.COLUMN_NAME_NAME));
            filename = cursor.getString(cursor.getColumnIndex(NodeModelEntry.COLUMN_NAME_FILENAME));
        }
        cursor.close();
        //node nicht gefunden return null
        if(type==null){
            return null;
        }
        //spezifisch
        switch (type){
            case NodeModelEntry.NODE_TYPE_LEKTION:
                neu = new NodeModel(id,name,filename);
                break;
            case NodeModelEntry.NODE_TYPE_STRATEGIE:
                StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
                cursor = db.query(StrategieNodeModelEntry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                if(!cursor.moveToNext()){
                    throw new RuntimeException("inconsitenct data for type "+type+" and id "+id);
                }
                long strategieDatenID = cursor.getLong(cursor.getColumnIndex(StrategieNodeModelEntry.COLUMN_NAME_ID_STRATEGIEDATEN));
                Strategie strategie = strategieDatenDAO.read(strategieDatenID);
                neu = new StrategieNodeModel(id,name,filename, strategie);
                break;
            default:
                throw new RuntimeException("unbekannter Node type "+type);
        }
        nodeUserDataDAO.readUserData(neu);
        return neu;
    }
    public Long readRootID(){
        String selection = NodeModelEntry.COLUMN_NAME_PARENTID +" IS NULL";
        String[] columns = {BaseColumns._ID};
        Long id = null;

        Cursor cursor = db.query(
                NodeModelEntry.TABLE_NAME,
                columns,
                selection,
                null,
                null,
                null,
                null
        );
        if(cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(NodeModelEntry._ID));
        }
        cursor.close();
        return id;
    }
    public NodeModel readNodeForStrategie(long strategieId){
        String selection = StrategieNodeModelEntry.COLUMN_NAME_ID_STRATEGIEDATEN +" = ?";
        String[] selectionArgs = {strategieId+""};
        String[] columns = {BaseColumns._ID};
        Long id = null;

        Cursor cursor = db.query(
                StrategieNodeModelEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
        }
        cursor.close();
        return readWithoutChildren(id);
    }
    private Long readParentID(Long childId){
        String selection = NodeModelEntry._ID +" = ?";
        String[] selectionArgs = {childId+""};
        String[] columns = {NodeModelEntry.COLUMN_NAME_PARENTID};
        Long id = null;

        Cursor cursor = db.query(
                NodeModelEntry.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(cursor.moveToNext()){
            if(!cursor.isNull(cursor.getColumnIndex(NodeModelEntry.COLUMN_NAME_PARENTID)))
                id = cursor.getLong(cursor.getColumnIndex(NodeModelEntry.COLUMN_NAME_PARENTID));
        }
        cursor.close();;
        return id;
    }

    /**
     * Speichert alle daten des Nodemodels, also auch die UserDaten.
     * @param n
     */
    public void update(NodeModel n){
        NodeUserDataDAO nodeUserDataDAO = NodeUserDataDAO.getInstance();
        String type = n.getType();
        //updated values
        ContentValues valuesMinimum = contentValuesForNodeModel(n);
        // Which row to update, based on the id
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = { n.getNodeID()+"" };

        //update
        db.update(
                NodeModelEntry.TABLE_NAME,
                valuesMinimum,
                selection,
                selectionArgs);
        switch (type){
            case NodeModelEntry.NODE_TYPE_LEKTION:
                break;
            case NodeModelEntry.NODE_TYPE_STRATEGIE:
                StrategieNodeModel s = (StrategieNodeModel) n;
                ContentValues values = contentValuesForStrategieNodeModel(s);
                db.update(StrategieNodeModelEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new RuntimeException("unbekannter Node type "+type);
        }
        nodeUserDataDAO.update(n);
    }
    public void delete(NodeModel n){
        String type = n.getType();
        // Define 'where' part of query.
        String selection = BaseColumns._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { n.getNodeID()+"" };
        // Issue SQL statement.


        switch (type){
            case NodeModelEntry.NODE_TYPE_LEKTION:
                break;
            case NodeModelEntry.NODE_TYPE_STRATEGIE:
                StrategieNodeModel s = (StrategieNodeModel) n;
                long strategieDatenID = s.getStrategie().getId();
                StrategieDatenDAO.getInstance().delete(strategieDatenID);
                db.delete(StrategieNodeModelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new RuntimeException("unbekannter Node type "+type);
        }
        db.delete(NodeModelEntry.TABLE_NAME, selection, selectionArgs);
    }

    public ArrayList<Long> readAllIDs(){
        ArrayList<Long> iDs = new ArrayList<>();
        Cursor cursor = db.query(
                NodeModelEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        while(cursor.moveToNext()){
            Long id = cursor.getLong(cursor.getColumnIndex(NodeModelEntry._ID));
            iDs.add(id);
        }
        cursor.close();
        return iDs;
    }
    public String getFilenameForStrategieID(long strategieID){
        String selection = StrategieNodeModelEntry.COLUMN_NAME_ID_STRATEGIEDATEN +" = ?";
        String[] selectionArgs = {strategieID+""};
        String sql =
                  "SELECT " + NodeModelEntry.COLUMN_NAME_FILENAME
                + " FROM " + NodeModelEntry.TABLE_NAME
                + " INNER JOIN " + StrategieNodeModelEntry.TABLE_NAME
                +   " USING("+BaseColumns._ID+")"
                + " WHERE " + StrategieNodeModelEntry.TABLE_NAME+"."+StrategieNodeModelEntry.COLUMN_NAME_ID_STRATEGIEDATEN + " = ?";
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        String filename = null;
        if(cursor.moveToNext()){
            filename = cursor.getString(cursor.getColumnIndex(NodeModelEntry.COLUMN_NAME_FILENAME));
        }
        return filename;
    }
    /**
     * Erzeugt eine HasMap mit der Id und dem NodeModel.
     * Die NodeModels sind vollständig inklusive Liste der Kinder.
     *
     * @return nodeModels
     */
    public HashMap<Long, NodeModel> readAll(){
        HashMap<Long, NodeModel> nodeModels = new HashMap<>();
        ArrayList<Long> iDs = readAllIDs();
        for(long id: iDs){
            NodeModel node = readWithoutChildren(id);
            nodeModels.put(id,node);
        }
        for(NodeModel child: nodeModels.values()){
            Long parentID = readParentID(child.getNodeID());
            if(parentID == null){
                continue;
            }
            NodeModel parent = nodeModels.get(parentID);
            parent.addNachfolger(child);
        }
        return nodeModels;
    }

    private ContentValues contentValuesForNodeModel(NodeModel n){
        ContentValues values = new ContentValues();
        values.put(NodeModelEntry.COLUMN_NAME_TYPE, n.getType());
        values.put(NodeModelEntry.COLUMN_NAME_NAME, n.getName());
        values.put(NodeModelEntry.COLUMN_NAME_FILENAME, n.getFilename());
        return values;
    }
    private ContentValues contentValuesForStrategieNodeModel(StrategieNodeModel s){
        long idStrategieDaten = s.getStrategie().getId();

        ContentValues values = new ContentValues();
        values.put(StrategieNodeModelEntry._ID, s.getNodeID());
        values.put(StrategieNodeModelEntry.COLUMN_NAME_ID_STRATEGIEDATEN, idStrategieDaten);
        return values;
    }
}
