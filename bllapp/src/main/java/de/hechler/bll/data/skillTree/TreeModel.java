package de.hechler.bll.data.skillTree;

import java.util.List;
import java.util.Map;

import de.hechler.bll.persistenz.dao.NodeModelDAO;

public class TreeModel {

    private NodeModel root;
    private Map<Long, NodeModel> nodes;

    public List<NodeModel> getChildren(NodeModel parent){
        return parent.getNachfolgerListe();
    }

    public NodeModel getRoot() {
        return root;
    }
    public NodeModel findNodebyID(long id){
        return nodes.get(id);
    }
    public void load(){
        NodeModelDAO nodeModelDAO = NodeModelDAO.getInstance();
        nodes = nodeModelDAO.readAll();
        long rootID = nodeModelDAO.readRootID();
        root = nodes.get(rootID);
    }
}
