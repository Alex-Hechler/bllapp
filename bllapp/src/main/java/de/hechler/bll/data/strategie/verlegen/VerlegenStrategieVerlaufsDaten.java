package de.hechler.bll.data.strategie.verlegen;

import java.util.Date;
import java.util.HashMap;

import de.hechler.bll.data.strategie.StrategieVerlaufsDaten;
import de.hechler.bll.persistenz.contract.SkillTreeContract;
import de.hechler.bll.util.HTMLconverter;

public class VerlegenStrategieVerlaufsDaten extends StrategieVerlaufsDaten {
    public static final String ACTOIN_VERLEGEN_TAEGLICHEABFRAGE = "verlegenVerlaufsDatenTaeglicheAbfrage";
    private boolean gefunden;
    private String falscherOrt;

    private VerlegenStrategieVerlaufsDaten(){
        super();
    }

    public VerlegenStrategieVerlaufsDaten(Date zeitstempel, String action) {
        super(zeitstempel, action);
    }

    public VerlegenStrategieVerlaufsDaten(long id, Date zeitstempel, String action, String info, boolean gefunden, String falscherOrt) {
        super(id, zeitstempel, action, info);
        this.gefunden = gefunden;
        this.falscherOrt = falscherOrt;
    }

    public static VerlegenStrategieVerlaufsDaten erstelleVerlaufsDaten(HashMap<String, String> parameter){
        VerlegenStrategieVerlaufsDaten verlaufsDaten = new VerlegenStrategieVerlaufsDaten();
        verlaufsDaten.zeitstempel = new Date();
        for(String variablenName: parameter.keySet()){
            String valueString = parameter.get(variablenName);
            switch (variablenName){
                case "action":
                    verlaufsDaten.action = valueString;
                    break;
                case "info":
                    verlaufsDaten.info = valueString;
                    break;
                case "gefunden":
                    verlaufsDaten.gefunden = HTMLconverter.stringToBoolean(valueString);
                    break;
                case "falscherOrt":
                    verlaufsDaten.falscherOrt = valueString;
                    break;
                default:
                    throw new RuntimeException("Varaibalname unbekannt: "+variablenName);
            }
        }
        return verlaufsDaten;
    }



    //Getter
    @Override
    public String getType(){
        return SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_VERLEGEN;
    }




    public boolean isGefunden() {
        return gefunden;
    }
    public String getFalscherOrt() {
        return falscherOrt;
    }

    //Setter
    public void setGefunden(boolean gefunden) {
        this.gefunden = gefunden;
    }
    public void setFalscherOrt(String falscherOrt) {
        this.falscherOrt = falscherOrt;
    }

}
