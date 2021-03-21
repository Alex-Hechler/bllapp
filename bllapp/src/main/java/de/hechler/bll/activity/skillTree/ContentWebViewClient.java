package de.hechler.bll.activity.skillTree;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.HashMap;
import java.util.Set;

import de.hechler.bll.SkillTreeController;
import de.hechler.bll.activity.skillTree.ContentActivity;
import de.hechler.bll.activity.skillTree.SkillTreeActivity;
import de.hechler.bll.data.skillTree.StrategieNodeModel;


public class ContentWebViewClient extends WebViewClient{
    private ContentActivity activity = null;

    public ContentWebViewClient(ContentActivity activity) {
        this.activity = activity;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.i("WEBVIEWCLIENT", "URL="+url);
        SkillTreeController skillTreeController = SkillTreeActivity.instance.getSkillTreeController();
        if(url.startsWith("intern://finished")){
            skillTreeController.besucheVonNode(activity.getNodeModel().getNodeID());
            activity.finish();
            return true;
        }
        if(url.startsWith("intern://benutzer")){
            return true;
        }
        if(url.startsWith("intern://executeStrategie")){
            String commando = getCommandParameter(url);
            HashMap<String, String> parameters = getQueryParameters(url);
            activity.getStategieNodeModel().executeStrategie(commando, parameters);
            return true;
        }
        if(url.startsWith("intern://verwendeStrategie")){
            StrategieNodeModel strategieNodeModel = activity.getStategieNodeModel();
            if(!strategieNodeModel.kannVerwendetWerden()){
                activity.setShowError(true);
                activity.updateView();
                return true;
            }
            strategieNodeModel.verwenden(activity);
            skillTreeController.besucheVonNode(activity.getNodeModel().getNodeID());
            activity.finish();
            return true;
        }
        if(url.startsWith("intern://strategieNichtVerwenden")){
            StrategieNodeModel strategieNodeModel = activity.getStategieNodeModel();
            strategieNodeModel.nichtVerwenden(activity);
            skillTreeController.besucheVonNode(activity.getNodeModel().getNodeID());
            activity.finish();
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
        String command = path.replace("/","");//path starts with /
        return command;
    }


}
