package de.hechler.bll.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class HTMLconverter {

    private HTMLconverter(){
    }

    /**
     * Erstellt aus einem String mit dem Format: HH:mm ein Date.
     * Das Date besitzt das jetzige Datum und die gestetzte Uhrzeit.
     * Dabei wird auf die Milisekunde genau auf den beginn der Minute gesetzt.
     * Ist der String null wird null zurückgeben;
     * @param uhrzeitString
     * @return
     */
    public static Date uhrzeitStringToDate(String uhrzeitString){
        if(uhrzeitString==null){
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date uhrzeit;
        try {
            uhrzeit = simpleDateFormat.parse(uhrzeitString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar uhrzeitCalendar = Calendar.getInstance();
        uhrzeitCalendar.set(Calendar.HOUR_OF_DAY,uhrzeit.getHours());
        uhrzeitCalendar.set(Calendar.MINUTE, uhrzeit.getMinutes());
        uhrzeitCalendar.set(Calendar.SECOND, 0);
        uhrzeitCalendar.set(Calendar.MILLISECOND, 0);
        return uhrzeitCalendar.getTime();
    }

    /**
     * Erstellt aus einem Date einen String mit dem Format: HH:mm.
     * Ist das Date null wird null zurückgegeben.
     * @param date
     * @return
     */
    public static String dateToUhrzeitString(Date date){
        String uhrzeit = null;
        if(date!=null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            uhrzeit = simpleDateFormat.format(date);
        }
        return  uhrzeit;
    }

    /**
     * Erstellt aus einem Date einen String mit dem Format: HH:mm.
     * Ist das Date null wird null zurückgegeben.
     * @param date
     * @return
     */
    public static String dateToString(Date date){
        String uhrzeit = null;
        if(date!=null){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            uhrzeit = simpleDateFormat.format(date);
        }
        return  uhrzeit;
    }

    /**
     * Erstellt aus einem String('true' oder 'false') einen boolean.
     * @param booleanString
     * @return
     */
    public static boolean stringToBoolean(String booleanString){
        if(booleanString.equals("true")){
            return true;
        }
        if(booleanString.equals("false")){
            return false;
        }
        throw new RuntimeException("cannot convert "+booleanString+" to boolean");
    }
    public static String booleanToString(boolean bolean){
        if(bolean){
            return "true";
        }
        return "false";
    }
    public static String javaListToUnorderedList(List<String> list){
        if(list.isEmpty()){
            return null;
        }
        String htmlList = "<ul>";
        for(String s:list){
            htmlList+= "<li>"+s+"</li>";
        }
        htmlList+="</ul>";
        return htmlList;
    }

    public static String intsToProgressbar(int wert, int max,String beschreibung){
        String progressAnfang = "<progress value=\""+wert+"\" max=\""+max+"\">";
        String progressMitte = wert+" von "+max+" "+beschreibung;
        String progressEnde = "</progress>";
        return progressAnfang+progressMitte+progressEnde;
    }

}
