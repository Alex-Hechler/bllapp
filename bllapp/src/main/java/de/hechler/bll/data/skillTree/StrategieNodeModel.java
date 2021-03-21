package de.hechler.bll.data.skillTree;

import android.content.Context;

import java.util.HashMap;

import de.hechler.bll.data.skillTree.NodeModel;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.persistenz.contract.SkillTreeContract;

public class StrategieNodeModel extends NodeModel {
    private Strategie strategie;

    public StrategieNodeModel(long nodeID, String name,  String filename, Strategie strategie) {
        super(nodeID, name, filename);
        this.strategie = strategie;
    }

    @Override
    public HashMap<String, String> getPlatzhalterWerte() {
        return strategie.getPlatzhalterWerte();
    }


    public void executeStrategie(String commando, HashMap<String, String> parameter) {
        strategie.executeStrategie(commando, parameter);
    }
    public void verwenden(Context context){
        strategie.verwenden(context);
    }
    public void nichtVerwenden(Context context){
        strategie.nichtVerwenden(context);
    }


    public boolean kannVerwendetWerden() {
        return strategie.kannVerwendetWerden();
    }


    //Getter
    public String getType(){
        return SkillTreeContract.NodeModelEntry.NODE_TYPE_STRATEGIE;
    }
    public Strategie getStrategie() {
        return strategie;
    }
}
