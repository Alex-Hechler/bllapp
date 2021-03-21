package de.hechler.bll.persistenz.contract;

import android.provider.BaseColumns;

public class VerlegenStrategieContract {
    private VerlegenStrategieContract(){};

    public static class VerlegenStrategieUserDatenEntry implements BaseColumns {
        public static final String TABLE_NAME = "verlegenStrategieUserDaten";
        public static final String COLUMN_NAME_GEGENSTANDNAME = "gegenstandName";
        public static final String COLUMN_NAME_AUFBEWAHRUNGSORT = "aufbewahrungsOrt";
    }
    public static class VerlegenStrategieVerlaufsDatenEntry implements BaseColumns {
        public static final String TABLE_NAME = "verlegenStrategieVerlaufsDaten";
        public static final String COLUMN_NAME_GEFUNDEN = "gefunden";
        public static final String COLUMN_NAME_FALSCHERORT = "falscherOrt";
    }
}
