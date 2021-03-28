package de.hechler.bll.activity.strategie;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


import de.hechler.bll.R;
import de.hechler.bll.activity.list.AppListActivity;
import de.hechler.bll.activity.list.StrategieListActivity;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.dao.StrategieDatenDAO;
import de.hechler.bll.util.HTMLconverter;
import de.hechler.bll.worker.BackgroundWorker;

public class StrategieActivity extends AppCompatActivity {
    private Strategie strategie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkillTreeDbHelper.setzteDbcontext(this);
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        BenutzerManager benutzerManager = BenutzerManager.getInstance();

        setContentView(R.layout.activity_strategy);

        WebView webView = (WebView) findViewById(R.id.webViewStrategie);
        long id = getIntent().getLongExtra(BackgroundWorker.KEY_STRATEGIE_ID, -1);
        long idBenutzer = getIntent().getLongExtra(BackgroundWorker.KEY_BENUTZER_ID, -1);
        String filename = getIntent().getStringExtra(BackgroundWorker.KEY_FILENAME);

        if(idBenutzer!=-1) {
            benutzerManager.setAktuellerBenutzer(benutzerManager.getBenutzerByID(idBenutzer));
        }
        strategie = strategieDatenDAO.read(id);

        setupToolbar();
        setupWebView(webView, filename);
    }
    public void changeView(String url){
        WebView webView = (WebView) findViewById(R.id.webViewStrategie);
        setupWebView(webView, url);
    }
    private void setupWebView(WebView webView, String url) {
        webView.requestFocus();
        webView.setWebViewClient(new StrategieWebViewClient(this));
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

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarStrategie);
        toolbar.setTitle(strategie.getNotificationInfo().getTitle());
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(strategie.isApp()) {
            getMenuInflater().inflate(R.menu.menu_app, menu);
            MenuItem item = menu.findItem(R.id.menu_app_item_stumm);
            item.setChecked(strategie.isNotificationActive());
        }else{
            getMenuInflater().inflate(R.menu.menu_strategie, menu);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_item_verwenden_aus:
                strategie.executeStrategie(Strategie.KOMMANDO_VERWENDEN_AUS,new HashMap<>());
                return true;
            case R.id.menu_app_item_stumm:
                HashMap<String, String> hm = new HashMap<>();
                item.setChecked(!item.isChecked());
                hm.put("notificationActive", HTMLconverter.booleanToString(item.isChecked()));
                strategie.executeStrategie(Strategie.KOMMANDO_SETZEVARIABLE, hm);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent;
        if(!strategie.isApp()){
            intent = new Intent(this, StrategieListActivity.class);
            startActivity(intent);
            return super.getParentActivityIntent();
        }
        intent = new Intent(this, AppListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if(getIntent().hasExtra(BackgroundWorker.KEY_ISNOTIFICATION)){
            intent.putExtra(BackgroundWorker.KEY_ISNOTIFICATION, true);
        }
        startActivity(intent);
        return super.getParentActivityIntent();
    }

    @Override
    public boolean onNavigateUp() {
        Intent intent = new Intent(this, AppListActivity.class);
        startActivity(intent);
        this.finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AppListActivity.class);
        startActivityForResult(intent, 0);
    }

    private String replacePlatzhalter(String resources) {
        resources = ersetzteBenutzerPlatzhalter(resources);
        resources = ersetzeStrategiePlatzhalter(resources);
        return resources;
    }
    private String ersetzteBenutzerPlatzhalter(String resources) {
        return resources;
    }
    private String ersetzeStrategiePlatzhalter(String resources) {
        HashMap<String, String> platzhalterMap = strategie.getPlatzhalterWerte();
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

    public Strategie getStrategie() {
        return strategie;
    }
}
