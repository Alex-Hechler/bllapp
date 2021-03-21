package de.hechler.bll;

import de.hechler.bll.activity.skillTree.SkillTreeActivity;
import de.hechler.bll.data.skillTree.NodeModel;
import de.hechler.bll.data.skillTree.TreeModel;
import de.hechler.bll.persistenz.dao.NodeModelDAO;

public class SkillTreeController {

    public SkillTreeController() {

    }

    public void besucheVonNode(long id){
        TreeModel treeModel = SkillTreeActivity.instance.getTreeModel();
        NodeModel n = treeModel.findNodebyID(id);
        n.setBesucht(true);
        NodeModelDAO.getInstance().update(n);
        for(NodeModel child: treeModel.getChildren(n)){
            child.setFreigeschaltet(true);
            NodeModelDAO.getInstance().update(child);
        }
    }


}
