package de.hechler.bll.persistenz.contract;

import android.provider.BaseColumns;

public final class SkillTreeContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private SkillTreeContract() {}

    /* Inner class that defines the table contents */
    public static class NodeModelEntry implements BaseColumns {
        public static final String TABLE_NAME = "nodeModel";
        public static final String COLUMN_NAME_TYPE = "type"; //lekt, stra
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_FILENAME = "filename";
        public static final String COLUMN_NAME_PARENTID = "parentID";
        public static final String NODE_TYPE_LEKTION = "LEKT";
        public static final String NODE_TYPE_STRATEGIE = "STRA";
    }
    public static class NodeUserDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "nodeUserData";
        public static final String COLUMN_NAME_FREIGESCHALTET = "freigeschaltet";
        public static final String COLUMN_NAME_BESUCHT = "besucht";
        public static final String COLUMN_NAME_ID_NODEMODEL = "idNodeModel";
        public static final String COLUMN_NAME_ID_BENUTZER = "idBenutzer";
    }
    public static class StrategieNodeModelEntry implements BaseColumns {
        public static final String TABLE_NAME = "strategieNodeModel";
        public static final String COLUMN_NAME_ID_STRATEGIEDATEN = "idStrategieDaten";
    }
    public static class StrategieDatenEntry implements BaseColumns {
        public static final String TABLE_NAME = "strategieDaten";
        public static final String COLUMN_NAME_TYPE = "type"; //standard, verlegen
        public static final String COLUMN_NAME_FILENAMEAPP = "filenameApp";
        public static final String COLUMN_NAME_FILENAMENOTIFICATOIN = "filenameNotification";
        public static final String STRATEGIE_DATA_TYPE_STANDARD = "STANDARD";
        public static final String STRATEGIE_DATA_TYPE_VERLEGEN = "VERLEGEN";
    }
    public static class StrategieUserDatenEntry implements BaseColumns {
        public static final String TABLE_NAME = "strategieUserDaten";
        public static final String COLUMN_NAME_WIRDVERWENDET = "wirdVerwendet";
        public static final String COLUMN_NAME_NOTIFICATIONACTIVE = "notificationActive";
        public static final String COLUMN_NAME_NEXTBACKGROUNDTRIGGER = "nextBackgroundTrigger";
        public static final String COLUMN_NAME_ID_STRATEGIE = "idStrategie";
        public static final String COLUMN_NAME_ID_BENUTZER = "idBenutzer";
    }
    public static class NotificationTimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "notificationTime";
        public static final String COLUMN_NAME_ERSTENOTIFICATION = "ersteNotification";
        public static final String COLUMN_NAME_WIEDERHOHLUNG = "wiederhohlung";
        public static final String COLUMN_NAME_ID_STRATEGIEUSERDATEN = "idStrategieUserDaten";
    }
    public static class StrategieVerlaufsDatenEntry implements BaseColumns {
        public static final String TABLE_NAME = "strategieVerlaufsDaten";
        public static final String COLUMN_NAME_TYPE = "type";//standart, verlegen
        public static final String COLUMN_NAME_ZEITSTEMPEL = "zeitstempel";
        public static final String COLUMN_NAME_ACTION = "act";
        public static final String COLUMN_NAME_INFO = "info";
        public static final String COLUMN_NAME_ID_STRATEGIEUSERDATEN = "idStrategieUserDaten";
        public static final String VERLAUF_DATA_TYPE_STANDARD = "STANDARD";
        public static final String VERLAUF_DATA_TYPE_VERLEGEN = "VERLEGEN";
    }
    public static class BenutzerEntry implements BaseColumns {
        public static final String TABLE_NAME = "benutzer";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_ICONASSETID = "iconAssetId";
    }

}
