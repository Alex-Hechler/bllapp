package de.hechler.bll.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SQLconverter {
    /**
     * Erstellt aus einem Date einen String mit dem Muster: yyyy-MM-dd HH:mm:ss.
     * Zum schreiben von Dates in die Datenbank.
     * Gibt null zurück, wenn das Date null ist.
     * @param date
     * @return
     */
    public static String dateToSQL(Date date){
        if(date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    /**
     * Erstellt aus einem String mit dem Muster: yyyy-MM-dd HH:mm:ss ein Date.
     * Zum Auslesen der in der Datenbank gespeicherten Dates.
     * Gibt null zurück, wenn der String null ist.
     * @param sql
     * @return
     */
    public static Date sqlToDate(String sql) {
        if(sql==null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return dateFormat.parse(sql);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gibt basierend auf einem Boolean eine Integer zurück(0=false, 1=true)
     * Gibt null zurück wenn der Boolean null ist.
     * @param b
     * @return
     */
    public static Integer booleanToSQL(Boolean b){
        if(b == null)
            return null;
        if(b)
            return 1;
        return 0;
    }

    /**
     * Gibt basierend auf einem Integer einen Boolean zurück(zurück(0=false, 1=true)
     * Gibt null zurück wenn der Integer null ist.
     * @param i
     * @return
     */
    public static Boolean sqlToBoolean(Integer i){
        if(i == null)
            return null;
        if(i==0)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }
}
