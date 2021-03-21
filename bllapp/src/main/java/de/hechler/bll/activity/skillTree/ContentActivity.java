
package de.hechler.bll.activity.skillTree;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import de.blox.graphview.sample.R;
import de.hechler.bll.data.skillTree.NodeModel;
import de.hechler.bll.data.skillTree.StrategieNodeModel;
import de.hechler.bll.worker.BackgroundWorker;

public class ContentActivity extends AppCompatActivity {
    private NodeModel nodeModel;
    private boolean showError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        SkillTreeActivity parent = SkillTreeActivity.instance;
        WebView webView = (WebView) findViewById(R.id.webViewContent);
        long id = getIntent().getLongExtra("NodeId", 0);
        nodeModel = parent.getTreeModel().findNodebyID(id);

        setupToolbar();
        setupWebView(webView, nodeModel.getFilename());
    }

    public void updateView() {
        WebView webView = (WebView) findViewById(R.id.webViewContent);
        setupWebView(webView, nodeModel.getFilename());
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarContent);
        toolbar.setTitle(nodeModel.getName());
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupWebView(WebView webView, String url) {
        webView.requestFocus();
        webView.setWebViewClient(new ContentWebViewClient(this));
        webView.getSettings().setLightTouchEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(false);
        webView.setSoundEffectsEnabled(true);
        webView.loadDataWithBaseURL(null, readResource(url),
                "text/html", "UTF-8", null);
    }



    private String readResource(String filename) {
        try {
            InputStream in = getAssets().open(filename);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] block = new byte[1024];
            int count = in.read(block);
            while (count > 0) {
                buffer.write(block, 0, count);
                count = in.read(block);
            }
            in.close();
            String resource = buffer.toString("UTF-8");
            resource = replacePlatzhalter(resource);
            return resource;
        } catch (IOException e) {
            e.printStackTrace();
            return "file not found" + filename;
        }
    }

    private String replacePlatzhalter(String resources) {
        resources = ersetzteBenutzerPlatzhalter(resources);
        resources = ersetzeNodeModelPlatzhalter(resources); //enthält Strategie Platzhalter
        resources = ersetzeAnzeigePlatzhalter(resources);
        return resources;
    }

    private String ersetzteBenutzerPlatzhalter(String resources) {
        return resources;
    }

    private String ersetzeAnzeigePlatzhalter(String resources) {
        String ersetzen = "none";
        if (showError) {
            ersetzen = "inline";
        }
        resources = ersetzeTextPlatzhalter(resources, "verlegen_fehler", ersetzen);
        return resources;
    }

    private String ersetzeNodeModelPlatzhalter(String resources) {
        HashMap<String, String> platzhalterMap = nodeModel.getPlatzhalterWerte();
        for (String platzhalter : platzhalterMap.keySet()) {
             resources = ersetzeTextPlatzhalter(resources, platzhalter, platzhalterMap.get(platzhalter));
        }
        return resources;
    }


    /**
     * @param resources
     * @param platzhalterName ohne $
     * @param text
     * @return
     */
    private String ersetzeTextPlatzhalter(String resources, String platzhalterName, String text) {
        resources = resources.replace("$" + platzhalterName, text);
        return resources;
    }

    public NodeModel getNodeModel() {
        return nodeModel;
    }
    public boolean isStrategy(){
        return nodeModel instanceof StrategieNodeModel;
    }
    public StrategieNodeModel getStategieNodeModel(){
        if(!isStrategy())
            return null;
        return (StrategieNodeModel)nodeModel;
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = new Intent(this, SkillTreeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(getIntent().hasExtra(BackgroundWorker.KEY_ISNOTIFICATION)){
            intent.putExtra(BackgroundWorker.KEY_ISNOTIFICATION, true);
        }
        startActivity(intent);
        return intent;
    }

    public void setShowError(boolean showError) {
        this.showError = showError;
    }

    private String wennNull(String normal, String wennNull) {
        if (normal == null) {
            return wennNull;
        }
        return normal;
    }
}