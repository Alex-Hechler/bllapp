package de.hechler.bll.worker;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import de.hechler.bll.R;
import de.hechler.bll.activity.strategie.StrategieActivity;
import de.hechler.bll.data.BenutzerManager;
import de.hechler.bll.data.notification.NotificationInfo;
import de.hechler.bll.data.strategie.Strategie;
import de.hechler.bll.persistenz.SkillTreeDbHelper;
import de.hechler.bll.persistenz.dao.BenutzerDAO;
import de.hechler.bll.persistenz.dao.StrategieDatenDAO;

public class BackgroundWorker extends Worker {
    private static final String CHANNEL_ID = "SKILLTREECHANEL";
    public static final String KEY_STRATEGIE_ID = "strategieID";
    public static final String KEY_BENUTZER_ID = "benutzerID";
    public static final String KEY_STRATEGIEUSERDATA_ID = "strategieUserDataID";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_ISNOTIFICATION = "isNotification";

    public BackgroundWorker(Context context, WorkerParameters params){
        super(context, params);
    }

    @NonNull
    @Override
    public synchronized Result doWork() {
        SkillTreeDbHelper.setzteDbcontext(getApplicationContext());
        long idBenutzer = getInputData().getLong(KEY_BENUTZER_ID,-1);
        long idStrategie = getInputData().getLong(KEY_STRATEGIE_ID,-1);
        long idStrategieUserData = getInputData().getLong(KEY_STRATEGIEUSERDATA_ID,-1);
        int idInt = (int)(idStrategieUserData&0xFFFFFFFF);
        Log.i("BACKGROUND","SkillTreecalled Benutzer: "+idBenutzer+" Strategie: "+idStrategie);
        StrategieDatenDAO strategieDatenDAO = StrategieDatenDAO.getInstance();
        BenutzerManager benutzerManager = BenutzerManager.getInstance();
        benutzerManager.setAktuellerBenutzer(benutzerManager.getBenutzerByID(idBenutzer));

        Strategie strategie = strategieDatenDAO.read(idStrategie);
        if(strategie==null) {
            Log.w("BACKGROUND", "Strategie mit der id nicht gefunden " + idStrategie);
            return Result.success();
        }
        Log.i("BACKGROUND", "Strategie mit der id geladen " + idStrategie);
        if(Strategie.test){

        }else {
            if (!strategie.checkTrigger()) {
                Log.i("BACKGROUND", "skipped");
                return Result.success();
            }
            strategie.requestWork(getApplicationContext());
        }
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(getApplicationContext(), StrategieActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(KEY_STRATEGIE_ID, idStrategie);
        intent.putExtra(KEY_BENUTZER_ID, idBenutzer);
        intent.putExtra(KEY_FILENAME, strategie.getFilenameNotfication());
        intent.putExtra(KEY_ISNOTIFICATION,true);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationInfo info = strategie.getNotificationInfo();

        createNotificationChannel();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_arrow_back)
                .setContentTitle(info.getTitle())
                .setContentText(info.getText())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

        notificationManager.notify(idInt, builder.build());


        return Result.success();
    }



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "skillTreeChanel";
            String description = "skillTreeVerwenden Strategie";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
