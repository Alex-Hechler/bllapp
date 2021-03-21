package de.hechler.bll.activity.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import de.blox.graphview.sample.R;
import de.hechler.bll.activity.list.AppListActivity;
import de.hechler.bll.activity.skillTree.SkillTreeActivity;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.worker.BackgroundWorker;

public class ImageSelectActivity extends AppCompatActivity {
    BilderGridAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        setupToolbar();

        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a GridLayoutManager with 3 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Verticaal Orientation
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        adapter = new BilderGridAdapter(BenutzerManager.USER_IMAGES,this);
        recyclerView.setAdapter(adapter);
        ViewTreeObserver viewTreeObserver = recyclerView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
               // calculateSize(recyclerView);
               // calculateSize(recyclerView);
            }
        });

    }
    private static final int sColumnWidth = 150; // assume cell width of 120dp
    private void calculateSize(RecyclerView recyclerView) {
        int spanCount = (int) Math.floor(recyclerView.getWidth() / convertDPToPixels(sColumnWidth));
        ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(spanCount);
    }

    private float convertDPToPixels(int dp) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;
        return dp * logicalDensity;
    }
    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Profilbild wählen");
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED,intent);
        finish();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater;
        if (Build.VERSION.SDK_INT > 15)
            inflater = getMenuInflater();
        else
            inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_imagechoose, menu);
        return true;
    }

    public boolean chooseOk(MenuItem item){
        if(adapter.getLastPosition()==-1){
            onSupportNavigateUp();
            return false;
        }
        Intent intent = new Intent();
        intent.putExtra("result",adapter.getLastPosition());
        setResult(Activity.RESULT_OK,intent);
        finish();
        return true;
    }

}