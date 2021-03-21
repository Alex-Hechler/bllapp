package de.hechler.bll.data.strategie;

import java.util.Date;
import java.util.HashMap;

import de.hechler.bll.data.strategie.verlegen.VerlegenStrategieVerlaufsDaten;
import de.hechler.bll.persistenz.contract.SkillTreeContract;

public class StrategieVerlaufsDaten {
    public static final String ACTION_NUTZUNGSTRATEGIEAPP = "nutzungStrategieApp";
    public static final String ACTION_STRATEGIE_VERWENDEN_AN = "VerwendenAn";
    public static final String ACTION_STRATEGIE_VERWENDEN_AUS = "VerwendenAus";
    protected long id = -1;
    protected Date zeitstempel;
    protected String action;
    protected String info;

    protected StrategieVerlaufsDaten(){

    }

    public StrategieVerlaufsDaten(Date zeitstempel, String action) {
        this.zeitstempel = zeitstempel;
        this.action = action;
    }

    public StrategieVerlaufsDaten( String action, String info) {
        this.zeitstempel = new Date();
        this.action = action;
        this.info = info;
    }

    public StrategieVerlaufsDaten(long id, Date zeitstempel, String action, String info) {
        this.id = id;
        this.zeitstempel = zeitstempel;
        this.action = action;
        this.info = info;
    }

    //Getter
    public long getId() {
        return id;
    }
    public Date getZeitstempel() {
        return zeitstempel;
    }
    public String getAction() {
        return action;
    }
    public String getInfo() {
        return info;
    }
    public String getType(){
        return SkillTreeContract.StrategieDatenEntry.STRATEGIE_DATA_TYPE_STANDARD;
    }

    //Setter
    public void setId(long id) {
        this.id = id;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public void setZeitstempel(Date zeitstempel) {
        this.zeitstempel = zeitstempel;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public static StrategieVerlaufsDaten erstelleVerlaufsDaten(HashMap<String, String> parameter){
        StrategieVerlaufsDaten verlaufsDaten = new StrategieVerlaufsDaten();
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

                default:
                    throw new RuntimeException("Varaibalname unbekannt: "+variablenName);
            }
        }
        return verlaufsDaten;
    }

}
