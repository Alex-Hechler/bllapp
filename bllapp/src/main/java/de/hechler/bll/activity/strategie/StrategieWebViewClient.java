package de.hechler.bll.activity.strategie;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import de.hechler.bll.activity.strategie.StrategieActivity;
import de.hechler.bll.data.strategie.StrategieVerlaufsDaten;
import de.hechler.bll.data.strategie.verlegen.VerlegenStrategieVerlaufsDaten;
import de.hechler.bll.worker.BackgroundWorker;

public class StrategieWebViewClient extends WebViewClient {
    private StrategieActivity activity;
    private StrategieVerlaufsDaten verlaufsDaten = null;

    public StrategieWebViewClient(StrategieActivity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("STRATEGIEWEBVIEWCLIENT", "URL= "+url);
        Uri uri = Uri.parse(url);
        String schema = uri.getScheme();
        String host = uri.getHost();
        if(schema.equals("intern")){
            if(host.startsWith("executeStrategie")){
                String commando = getCommandParameter(url);
                HashMap<String, String> parameters = getQueryParameters(url);
                activity.getStrategie().executeStrategie(commando, parameters);
            }
            if(host.startsWith("benutzer")){
            }
            if(host.startsWith("loadHTML")){
                String commando = getCommandParameter(url);
                activity.changeView(commando);
                return true;
            }
            if(host.contains("Finish")){
                activity.finish();
            }

            return true;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        activity.startActivity(intent);
        return true;
    }


    private String getQueryParameter(String url, String parameterName){
        Uri uri = Uri.parse(url);
        Set<String> parameterNames = uri.getQueryParameterNames();
        if(!parameterNames.contains(parameterName)){
            return null;
        }
        return uri.getQueryParameter(parameterName);
    }
    private HashMap<String, String> getQueryParameters(String url){
        Uri uri = Uri.parse(url);
        HashMap<String, String> parameters = new HashMap<>();
        for(String quereyParameterName:uri.getQueryParameterNames()) {
            parameters.put(quereyParameterName, uri.getQueryParameter(quereyParameterName));
        }
        return parameters;
    }
    private String getCommandParameter(String url){
        Uri uri = Uri.parse(url);
        String path = uri.getPath();
        String command = path.replaceFirst("/","");//path starts with /
        return command;
    }


}
