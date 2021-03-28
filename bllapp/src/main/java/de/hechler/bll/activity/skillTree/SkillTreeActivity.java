package de.hechler.bll.activity.skillTree;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import de.blox.graphview.Edge;
import de.blox.graphview.Graph;
import de.blox.graphview.GraphAdapter;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;

import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;
import de.hechler.bll.R;
import de.hechler.bll.SkillTreeController;
import de.hechler.bll.activity.list.AppListActivity;
import de.hechler.bll.activity.list.StrategieListActivity;
import de.hechler.bll.activity.user.BenutzerProfilActivity;
import de.hechler.bll.data.Benutzer;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.skillTree.NodeModel;
import de.hechler.bll.data.skillTree.TreeModel;
import de.hechler.bll.persistenz.SkillTreeDbHelper;

public class SkillTreeActivity extends SkillTreeGraphActivity {
    public static SkillTreeActivity instance;
    //TODO: eigene Klasse NodeINfo
    public static class NodeInfo{
        private long id;
        private String name;
        private int color;
        private boolean active;

        public NodeInfo(long id, String name, int color, boolean active) {
            this.id = id;
            this.name = name;
            this.color = color;
            this.active = active;
        }



        public NodeInfo(NodeModel node){
            id = node.getNodeID();
            name = node.getName();
            active = node.isFreigeschaltet();
            color = Color.DKGRAY;
            if(node.isFreigeschaltet()){
                color = Color.GREEN;
            }
            if(node.isBesucht()){
                color = Color.WHITE;
            }
        }
        public boolean isActive() {
            return active;
        }
        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getColor() {
            return color;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    TreeModel treeModel;
    //Benutzer benutzer;
    SkillTreeController skillTreeController;

    @Override
    public Graph createGraph() {
        final Graph graph = new Graph();
        instance = this;
        treeModel = new TreeModel();
        treeModel.load();
        skillTreeController = new SkillTreeController();
        updateGraph(graph);
        //benutzer = BenutzerDAO.getInstance().read();
        //Log.i("SKACTION",benutzer.toString());
        return graph;
    }
    public void updateNodes(Graph graph){
        for(Node n: graph.getNodes()){
            NodeInfo nodeInfo = (NodeInfo) n.getData();
            NodeModel nodeModel = treeModel.findNodebyID(nodeInfo.getId());
            n.setData(new NodeInfo(nodeModel));
        }
    }
    public void updateGraph(Graph graph){
        clearGraph(graph);
        Node rootView = new Node(new NodeInfo(treeModel.getRoot()));
        addChildren(treeModel.getRoot(), rootView, graph);
    }
    private void clearGraph(Graph graph){
        for(Edge e: new ArrayList<>(graph.getEdges())){
            graph.removeEdge(e);
        }
        for(Node n: new ArrayList<>(graph.getNodes())){
            graph.removeNode(n);
        }
    }
    private void addChildren(NodeModel parent, Node parentView, Graph graph){
        for(NodeModel child: parent.getNachfolgerListe()){
            Node childView = new Node(new NodeInfo(child));
            graph.addEdge(parentView, childView);
            addChildren(child, childView, graph);
        }
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    public SkillTreeController getSkillTreeController() {
        return skillTreeController;
    }

    //public Benutzer getBenutzer() {
    //  return benutzer;
    //}

    public static SkillTreeActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SkillTreeDbHelper.setzteDbcontext(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void nodeClicked(long id, GraphAdapter adapter) {
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("NodeId",id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivityForResult(intent,0);
    }
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent intent;
                    switch (item.getItemId()) {
                        case R.id.bottom_navigation_page_nutzerprofil:
                            intent = new Intent(instance, BenutzerProfilActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent,0);
                            return true;
                        case R.id.bottom_navigation_page_skilltree:
                            return true;
                        case R.id.bottom_navigation_page_strategie:
                            intent = new Intent(instance, StrategieListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent,0);
                            return true;
                        case R.id.bottom_navigation_page_apps:
                            intent = new Intent(instance, AppListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(intent,0);
                            return true;
                    }
                    return false;
                }
            };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateNodes(adapter.getGraph());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setLayout(GraphView view) {
        /*FruchtermanReingoldAlgorithm fra = new FruchtermanReingoldAlgorithm(1000);
        fra.setEdgeRenderer(new StraightEdgeRenderer());
        view.setLayout(fra);*/
        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(300)
                .setSubtreeSeparation(300)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
                .build();
        view.setLayout(new BuchheimWalkerAlgorithm(configuration));
    }
    @Override
    protected void setupNavigation(){
        //onClick listener und selectedItem auswählen
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        bottomNavigation.setSelectedItemId(R.id.bottom_navigation_page_skilltree);

        //personalisierte Anzeige des Benutzerprofilitems
        Benutzer aktuellerBenutzer = BenutzerManager.getInstance().getAktuellerBenutzer();
        MenuItem item = bottomNavigation.getMenu().getItem(0);
        item.setTitle(aktuellerBenutzer.getName());
        item.setIcon(BenutzerManager.USER_IMAGES[aktuellerBenutzer.getIconAssetId()]);
    }

}
