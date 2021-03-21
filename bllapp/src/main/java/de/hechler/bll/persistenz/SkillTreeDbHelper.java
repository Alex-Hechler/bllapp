package de.hechler.bll.persistenz;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.strategie.StrategieVerlaufsDaten;
import de.hechler.bll.persistenz.contract.SkillTreeContract;
import de.hechler.bll.persistenz.contract.VerlegenStrategieContract.*;
import de.hechler.bll.persistenz.contract.SkillTreeContract.*;


public class SkillTreeDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 11;
    public static final String DATABASE_NAME = "SkillTree.db";
    private static Context dbcontext;
    //CREATE
    private static final String SQL_CREATE_ENTRIES_NODEMODEL =
            "CREATE TABLE " + NodeModelEntry.TABLE_NAME + " (" +
                    NodeModelEntry._ID + " INTEGER PRIMARY KEY," +
                    NodeModelEntry.COLUMN_NAME_TYPE + " VARCHAR(4),"+
                    NodeModelEntry.COLUMN_NAME_NAME + " TEXT,"+
                    NodeModelEntry.COLUMN_NAME_FILENAME+ " TEXT,"+
                    NodeModelEntry.COLUMN_NAME_PARENTID +" INTEGER,"+
                    "FOREIGN KEY("+ NodeModelEntry.COLUMN_NAME_PARENTID + ") REFERENCES "+ NodeModelEntry.TABLE_NAME+" ("+ BaseColumns._ID+"))";

    private static final String SQL_CREATE_ENTRIES_NODEUSERDATA =
            "CREATE TABLE " + NodeUserDataEntry.TABLE_NAME + " (" +
                    NodeUserDataEntry._ID + " INTEGER PRIMARY KEY," +
                    NodeUserDataEntry.COLUMN_NAME_FREIGESCHALTET + " INTEGER," +
                    NodeUserDataEntry.COLUMN_NAME_BESUCHT + " INTEGER,"+
                    NodeUserDataEntry.COLUMN_NAME_ID_BENUTZER + " TEXT,"+
                    NodeUserDataEntry.COLUMN_NAME_ID_NODEMODEL +" INTEGER,"+
                    "FOREIGN KEY("+NodeUserDataEntry.COLUMN_NAME_ID_BENUTZER+") REFERENCES "+ BenutzerEntry.TABLE_NAME+" ("+ BaseColumns._ID+")," +
                    "FOREIGN KEY("+ NodeUserDataEntry.COLUMN_NAME_ID_NODEMODEL + ") REFERENCES "+ NodeModelEntry.TABLE_NAME+" ("+ BaseColumns._ID+"))";

    private static final String SQL_CREATE_ENTRIES_STRATEGIENODEMODEL =
            "CREATE TABLE " + StrategieNodeModelEntry.TABLE_NAME + " (" +
                    StrategieNodeModelEntry._ID + " INTEGER PRIMARY KEY," +
                    StrategieNodeModelEntry.COLUMN_NAME_ID_STRATEGIEDATEN + " INTEGER," +
                    "FOREIGN KEY("+BaseColumns._ID+") REFERENCES "+NodeModelEntry.TABLE_NAME+" ("+ BaseColumns._ID+")," +
                    "FOREIGN KEY("+StrategieNodeModelEntry.COLUMN_NAME_ID_STRATEGIEDATEN+") REFERENCES "+StrategieDatenEntry.TABLE_NAME+" ("+ BaseColumns._ID+"))";

    private static final String SQL_CREATE_ENTRIES_STRATEGIEDATEN =
            "CREATE TABLE " + StrategieDatenEntry.TABLE_NAME + " (" +
                    StrategieDatenEntry._ID + " INTEGER PRIMARY KEY," +
                    StrategieDatenEntry.COLUMN_NAME_TYPE + " VARCHAR(8)," +
                    StrategieDatenEntry.COLUMN_NAME_FILENAMEAPP + " TEXT,"+
                    StrategieDatenEntry.COLUMN_NAME_FILENAMENOTIFICATOIN + " TEXT)";

    private static final String SQL_CREATE_ENTRIES_STRATEGIEUSERDATEN=
            "CREATE TABLE " + StrategieUserDatenEntry.TABLE_NAME + " (" +
                    StrategieUserDatenEntry._ID + " INTEGER PRIMARY KEY," +
                    StrategieUserDatenEntry.COLUMN_NAME_WIRDVERWENDET + " INTEGER,"+
                    StrategieUserDatenEntry.COLUMN_NAME_NOTIFICATIONACTIVE + " INTEGER,"+
                    StrategieUserDatenEntry.COLUMN_NAME_NEXTBACKGROUNDTRIGGER + " VARCHAR(19),"+
                    StrategieUserDatenEntry.COLUMN_NAME_ID_BENUTZER + " INTEGER,"+
                    StrategieUserDatenEntry.COLUMN_NAME_ID_STRATEGIE + " INTEGER,"+
                    "FOREIGN KEY("+StrategieUserDatenEntry.COLUMN_NAME_ID_BENUTZER+") REFERENCES "+BenutzerEntry.TABLE_NAME+" ("+ BaseColumns._ID+")," +
                    "FOREIGN KEY("+StrategieUserDatenEntry.COLUMN_NAME_ID_STRATEGIE+") REFERENCES "+StrategieDatenEntry.TABLE_NAME+" ("+ BaseColumns._ID+"))";


    private static final String SQL_CREATE_ENTRIES_NOTIFICATIONTIME =
            "CREATE TABLE " + NotificationTimeEntry.TABLE_NAME + " (" +
                    NotificationTimeEntry._ID + " INTEGER PRIMARY KEY," +
                    NotificationTimeEntry.COLUMN_NAME_ERSTENOTIFICATION + " VARCHAR(19)," +
                    NotificationTimeEntry.COLUMN_NAME_WIEDERHOHLUNG+" VARCHAR(6)," +
                    StrategieVerlaufsDatenEntry.COLUMN_NAME_ID_STRATEGIEUSERDATEN + " INTEGER," +
                    "FOREIGN KEY("+BaseColumns._ID+") REFERENCES "+StrategieUserDatenEntry.TABLE_NAME+" ("+ BaseColumns._ID+"))";

    private static final String SQL_CREATE_ENTRIES_STRATEGIEVERLAUFSDATEN =
            "CREATE TABLE " + StrategieVerlaufsDatenEntry.TABLE_NAME + " (" +
                    StrategieVerlaufsDatenEntry._ID + " INTEGER PRIMARY KEY," +
                    StrategieVerlaufsDatenEntry.COLUMN_NAME_TYPE + " VARCHAR(8)," +
                    StrategieVerlaufsDatenEntry.COLUMN_NAME_ZEITSTEMPEL+" VARCHAR(19)," +
                    StrategieVerlaufsDatenEntry.COLUMN_NAME_ACTION + " VARCHAR(15)," +
                    StrategieVerlaufsDatenEntry.COLUMN_NAME_INFO+" TEXT," +
                    StrategieVerlaufsDatenEntry.COLUMN_NAME_ID_STRATEGIEUSERDATEN + " INTEGER," +
                    "FOREIGN KEY("+ StrategieVerlaufsDatenEntry.COLUMN_NAME_ID_STRATEGIEUSERDATEN +") REFERENCES "+StrategieUserDatenEntry.TABLE_NAME+" ("+ BaseColumns._ID+"))";

    private static final String SQL_CREATE_ENTRIES_BENUTZER =
            "CREATE TABLE " + BenutzerEntry.TABLE_NAME + " (" +
                    BenutzerEntry._ID+ " INTEGER PRIMARY KEY," +
                    BenutzerEntry.COLUMN_NAME_NAME+" TEXT," +
                    BenutzerEntry.COLUMN_NAME_ICONASSETID+ " INTEGER)";

    //verlgen Strategie
    private static final String SQL_CREATE_ENTRIES_VERLEGENSTRATEGIEUSERDATEN =
            "CREATE TABLE " + VerlegenStrategieUserDatenEntry.TABLE_NAME + " (" +
                    VerlegenStrategieUserDatenEntry._ID + " INTEGER PRIMARY KEY," +
                    VerlegenStrategieUserDatenEntry.COLUMN_NAME_GEGENSTANDNAME + " TEXT," +
                    VerlegenStrategieUserDatenEntry.COLUMN_NAME_AUFBEWAHRUNGSORT+ " TEXT," +
                    "FOREIGN KEY("+BaseColumns._ID+") REFERENCES "+StrategieUserDatenEntry.TABLE_NAME+" ("+ BaseColumns._ID+"))";

    private static final String SQL_CREATE_ENTRIES_VERLEGENSTRATEGIVERLAUFSEDATEN =
            "CREATE TABLE " + VerlegenStrategieVerlaufsDatenEntry.TABLE_NAME + " (" +
                    VerlegenStrategieVerlaufsDatenEntry._ID + " INTEGER PRIMARY KEY," +
                    VerlegenStrategieVerlaufsDatenEntry.COLUMN_NAME_GEFUNDEN+ " INTEGER," +
                    VerlegenStrategieVerlaufsDatenEntry.COLUMN_NAME_FALSCHERORT+ " INTEGER," +
                    "FOREIGN KEY("+BaseColumns._ID+") REFERENCES "+StrategieVerlaufsDatenEntry.TABLE_NAME+" ("+ BaseColumns._ID+"))";

    //DELETE
    private static final String SQL_DELETE_ENTRIES_NODEMODEL =
            "DROP TABLE IF EXISTS " + NodeModelEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_NODEUSERDATA =
            "DROP TABLE IF EXISTS " + NodeUserDataEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_STRATEGIENODEMODEL =
            "DROP TABLE IF EXISTS " + StrategieNodeModelEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_STRATEGIEUSERDATEN =
            "DROP TABLE IF EXISTS " + StrategieUserDatenEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_VerlegenSTRATEGIEDATEN =
            "DROP TABLE IF EXISTS verlegenStrategieDaten";
    private static final String SQL_DELETE_ENTRIES_STRATEGIEDATEN =
            "DROP TABLE IF EXISTS " + StrategieDatenEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_NOTIFICATIONTIME =
            "DROP TABLE IF EXISTS " + NotificationTimeEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_STRATEGIEVERLAUFSDATEN =
            "DROP TABLE IF EXISTS " + StrategieVerlaufsDatenEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_BENUTZER =
            "DROP TABLE IF EXISTS " + BenutzerEntry.TABLE_NAME;

    //verlegen Strateie
    private static final String SQL_DELETE_ENTRIES_VERLEGENSTRATEGIEUSERDATEN =
            "DROP TABLE IF EXISTS " + VerlegenStrategieUserDatenEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_VERLEGENSTRATEGIEVERLAUFSDATEN =
            "DROP TABLE IF EXISTS " + VerlegenStrategieVerlaufsDatenEntry.TABLE_NAME;

    public SkillTreeDbHelper() {
        super(dbcontext, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_NODEMODEL);
        db.execSQL(SQL_CREATE_ENTRIES_NODEUSERDATA);
        db.execSQL(SQL_CREATE_ENTRIES_STRATEGIENODEMODEL);
        db.execSQL(SQL_CREATE_ENTRIES_STRATEGIEUSERDATEN);
        db.execSQL(SQL_CREATE_ENTRIES_STRATEGIEDATEN);
        db.execSQL(SQL_CREATE_ENTRIES_NOTIFICATIONTIME);
        db.execSQL(SQL_CREATE_ENTRIES_STRATEGIEVERLAUFSDATEN);
        db.execSQL(SQL_CREATE_ENTRIES_BENUTZER);

        db.execSQL(SQL_CREATE_ENTRIES_VERLEGENSTRATEGIEUSERDATEN);
        db.execSQL(SQL_CREATE_ENTRIES_VERLEGENSTRATEGIVERLAUFSEDATEN);
        init(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
        super.onConfigure(db);
    }

    private void init(SQLiteDatabase db){
        long root = insertNode(db,"Basis", "einleitung/einleitung2.html",null);
        long a = insertNode(db,"Arbeitsgedächnis",  "arbeitsgedaechnis/einleitungAG.html", root);
        long b = insertNode( db,"Weg 2",  "basis/test.html", root);
        long datenC = instertStrategieDaten(db,StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN,"arbeitsgedaechnis/verlegen/verlegenApp.html", "arbeitsgedaechnis/verlegen/notification.html");
        long c = insertStrategieNode( db,"Strategie",  "arbeitsgedaechnis/verlegen/verlegen.html", a,datenC);
        long d = insertNode( db,"Weg 2.2", "basis/test.html", b);
        long datenE = instertStrategieDaten(db,StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD, null,null);
        long e = insertStrategieNode( db,"Notizbuch", "arbeitsgedaechnis/notizbuch.html", a, datenE);
        //TODO: User entfernen
        long userId = insertBenutzer(db);
        insertNodeUserData(db, true, false, userId, root);
    }

    private void insertNodeUserData(SQLiteDatabase db, boolean b, boolean b1, long id, long root) {
        ContentValues values = new ContentValues();
        values.put(NodeUserDataEntry.COLUMN_NAME_FREIGESCHALTET, b);
        values.put(NodeUserDataEntry.COLUMN_NAME_BESUCHT, b1);
        values.put(NodeUserDataEntry.COLUMN_NAME_ID_NODEMODEL, root);
        values.put(NodeUserDataEntry.COLUMN_NAME_ID_BENUTZER, id);

        db.insert(NodeUserDataEntry.TABLE_NAME, null, values);
    }

    private long insertNode( SQLiteDatabase db, String name,String type, String filename,Long parentId){
        ContentValues values = new ContentValues();
        values.put(NodeModelEntry.COLUMN_NAME_NAME, name);
        values.put(NodeModelEntry.COLUMN_NAME_TYPE, type);
        values.put(NodeModelEntry.COLUMN_NAME_FILENAME, filename);
        values.put(NodeModelEntry.COLUMN_NAME_PARENTID, parentId);
        long newRowId = db.insertOrThrow(NodeModelEntry.TABLE_NAME, null,values);
        return newRowId;
    }
    private long insertNode( SQLiteDatabase db, String name, String filename,Long parentId){
        return insertNode(db,name,NodeModelEntry.NODE_TYPE_LEKTION,filename,parentId);
    }
    private long insertStrategieNode(SQLiteDatabase db, String name, String filename,Long parentId, long idStrategieDaten){
        long newRowID = insertNode(db,name,NodeModelEntry.NODE_TYPE_STRATEGIE,filename,parentId);
        ContentValues values = new ContentValues();
        values.put(StrategieNodeModelEntry._ID,newRowID);
        values.put(StrategieNodeModelEntry.COLUMN_NAME_ID_STRATEGIEDATEN, idStrategieDaten);
        long straID = db.insertOrThrow(StrategieNodeModelEntry.TABLE_NAME, null, values);
        return newRowID;
    }
    private long instertStrategieDaten(SQLiteDatabase db, String type,  String filenameApp, String filenameNotification){
        ContentValues valuesMinimum = new ContentValues();
        valuesMinimum.put(StrategieDatenEntry.COLUMN_NAME_TYPE, type);
        valuesMinimum.put(StrategieDatenEntry.COLUMN_NAME_FILENAMEAPP, filenameApp);
        valuesMinimum.put(StrategieDatenEntry.COLUMN_NAME_FILENAMENOTIFICATOIN, filenameNotification);
        long newRowId = db.insertOrThrow(StrategieDatenEntry.TABLE_NAME, null, valuesMinimum);

        switch (type){
            case StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD:
                break;
            case StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN:
                break;
            default:
                throw new RuntimeException("Unbekannter strategieDaten type "+type);
        }
        return newRowId;
    }
    private long insertBenutzer(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(BenutzerEntry.COLUMN_NAME_NAME, "test");
        values.put(BenutzerEntry.COLUMN_NAME_ICONASSETID, 0);

        long id = db.insertOrThrow(SkillTreeContract.BenutzerEntry.TABLE_NAME,null, values);
        return id;
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_VERLEGENSTRATEGIEVERLAUFSDATEN);
        db.execSQL(SQL_DELETE_ENTRIES_VERLEGENSTRATEGIEUSERDATEN);
        db.execSQL(SQL_DELETE_ENTRIES_STRATEGIEVERLAUFSDATEN);
        db.execSQL(SQL_DELETE_ENTRIES_NOTIFICATIONTIME);
        db.execSQL(SQL_DELETE_ENTRIES_STRATEGIEUSERDATEN);
        db.execSQL(SQL_DELETE_ENTRIES_NODEUSERDATA);
        db.execSQL(SQL_DELETE_ENTRIES_STRATEGIENODEMODEL);
        db.execSQL(SQL_DELETE_ENTRIES_VerlegenSTRATEGIEDATEN);
        db.execSQL(SQL_DELETE_ENTRIES_STRATEGIEDATEN);
        db.execSQL(SQL_DELETE_ENTRIES_NODEMODEL);
        db.execSQL(SQL_DELETE_ENTRIES_BENUTZER);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


    public static Context getDbcontext() {
        return dbcontext;
    }

    public static void setzteDbcontext(Context dbcontext) {
        if(SkillTreeDbHelper.dbcontext==null)
            SkillTreeDbHelper.dbcontext = dbcontext;
    }
}
