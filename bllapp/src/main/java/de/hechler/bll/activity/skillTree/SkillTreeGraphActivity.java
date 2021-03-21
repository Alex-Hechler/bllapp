package de.hechler.bll.activity.skillTree;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.blox.graphview.Graph;
import de.blox.graphview.GraphAdapter;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.sample.R;
import de.hechler.bll.activity.skillTree.SkillTreeActivity.NodeInfo;

public abstract class SkillTreeGraphActivity extends AppCompatActivity {
    private Node currentNode;
    protected GraphView graphView;
    protected GraphAdapter adapter;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_tree_graph);
        setTitle("Skill Tree");

        final Graph graph = createGraph();
        setupToolbar();
        setupNavigation();
        setupAdapter(graph);
    }

    protected void setupNavigation(){
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupAdapter(Graph graph) {
        graphView = findViewById(R.id.graph);
        setLayout(graphView);
        adapter = new GraphAdapter<GraphView.ViewHolder>(graph) {

            @Override
            public int getCount() {
                return graph.getNodeCount();
            }

            @Override
            public Object getItem(int position) {
                return graph.getNodeAtPosition(position);
            }

            @Override
            public boolean isEmpty() {
                return graph.hasNodes();
            }

            @NonNull
            @Override
            public GraphView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node, parent, false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    float scale = getResources().getDisplayMetrics().density;
                    view.setElevation(2f * scale +0.5f);
                }else{
                    graphView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
                return new SimpleViewHolder(view);
            }

            @Override
            public void onBindViewHolder(GraphView.ViewHolder viewHolder, Object data, int position) {
                NodeInfo nodeInfo = (NodeInfo) ((Node) data).getData();
                ((SimpleViewHolder) viewHolder).textView.setText(nodeInfo.getName());
                ((SimpleViewHolder) viewHolder).linearLayout.setBackgroundColor(nodeInfo.getColor());
            }

            class SimpleViewHolder extends GraphView.ViewHolder {
                TextView textView;
                LinearLayout linearLayout;

                SimpleViewHolder(View itemView) {
                    super(itemView);
                    textView = itemView.findViewById(R.id.textView);
                    linearLayout = itemView.findViewById(R.id.nodeBackground);
                }
            }
        };
        graphView.setAdapter(adapter);
        graphView.setOnItemClickListener((parent, view, position, id) -> {
            currentNode = (Node) adapter.getItem(position);
            NodeInfo nodeInfo = (NodeInfo) currentNode.getData();
            if(!nodeInfo.isActive()){
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float scale = getResources().getDisplayMetrics().density;
                view.setElevation(2f * scale +0.5f);
            }
            long nodeId =nodeInfo.getId();
            //Snackbar.make(graphView, "Clicked on " + nodeId, Snackbar.LENGTH_SHORT).show();
            nodeClicked(nodeId, adapter);
        });
    }
    protected abstract void nodeClicked(long id, GraphAdapter adapter);

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public abstract Graph createGraph();

    public abstract void setLayout(GraphView view);
}
