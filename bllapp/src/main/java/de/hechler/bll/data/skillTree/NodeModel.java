package de.hechler.bll.data.skillTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hechler.bll.persistenz.contract.SkillTreeContract;

public class NodeModel {
    private long nodeID;
    private String name;
    private String filename;
    private List<NodeModel> nachfolgerListe;

    private long nodeUserDataID;
    private boolean freigeschaltet;
    private boolean besucht;



    public NodeModel(long nodeID, String name, String filename){
        this.nodeID = nodeID;
        this.name = name;
        nachfolgerListe = new ArrayList<>();
        this.filename = filename;
    }

    public void addNachfolger(NodeModel nachfolger){
        nachfolgerListe.add(nachfolger);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeModel nodeModel = (NodeModel) o;
        return nodeID == nodeModel.nodeID;
    }

    @Override
    public int hashCode() {
        return (int)(nodeID&0x7FFFFFFF);
    }
    //Setter
    public void setFreigeschaltet(boolean freigeschaltet) {
        this.freigeschaltet = freigeschaltet;
    }
    public void setBesucht(boolean besucht) {
        this.besucht = besucht;
    }
    public void setNodeID(long nodeID) {
        this.nodeID = nodeID;
    }
    public void setNodeUserDataID(long nodeUserDataID) {
        this.nodeUserDataID = nodeUserDataID;
    }

    //Getter
    public long getNodeID() {
        return nodeID;
    }
    public List<NodeModel> getNachfolgerListe() {
        return nachfolgerListe;
    }
    public String getName() {
        return name;
    }
    public String getFilename() {
        return filename;
    }
    public boolean isFreigeschaltet() {
        return freigeschaltet;
    }
    public boolean isBesucht() {
        return besucht;
    }
    public long getNodeUserDataID() {
        return nodeUserDataID;
    }

    public String getType(){
        return SkillTreeContract.NodeModelEntry.NODE_TYPE_LEKTION;
    }
    public HashMap<String, String> getPlatzhalterWerte(){
        return new HashMap<>();


    }
}
