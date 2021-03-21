package de.hechler.bll.data.notification;

import android.content.Context;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.hechler.bll.persistenz.dao.NotificationTimeDAO;
import de.hechler.bll.worker.BackgroundWorker;

public class NotificationScheduler {
    private ArrayList<NotificationTime> notificationSchedule;
    private long strategieUserDatenId;

    public NotificationScheduler(long strategieUserDatenId) {
        notificationSchedule = new ArrayList<>();
        this.strategieUserDatenId = strategieUserDatenId;
    }


    /**
     * Erzeugt eine neue tägliche Notification oder
     * ersetzt eine bereits vorhandene.
     * @param ersteNotification
     */
    public void setzeTaeglicheNotification(Date ersteNotification){
        NotificationTimeDAO notificationTimeDAO = NotificationTimeDAO.getInstance();
        for(NotificationTime notification:notificationSchedule){
            if(notification.getWiederhohlung()== WiederhohlungsZeitraum.TAG){
                notification.setErsteNotification(ersteNotification);
                notificationTimeDAO.update(notification);

                return;
            }
            //TODO: doppelte request problem
            if(ersteNotification.before(getNextDate())){
                //request Kontext porblem
            }
        }
        NotificationTime neu = new NotificationTime(ersteNotification, WiederhohlungsZeitraum.TAG);
        notificationTimeDAO.create(neu, strategieUserDatenId);
        notificationSchedule.add(neu);
    }
    public void addNotfication(NotificationTime n){
        notificationSchedule.add(n);
    }
    public boolean istNotficationTime(Date date){
        Calendar dateC = Calendar.getInstance();
        dateC.setTime(date);
        for(NotificationTime n : notificationSchedule){
            if(n.isPartOfNotification(date)){
                continue;
            }
            return false;
        }
        return true;
    }
    public Date getNextDate(){
        long currentTime = new Date().getTime();
        long differenzMin = Long.MAX_VALUE;
        Date min = null;
        for(NotificationTime n : notificationSchedule){
            Date nextDate = n.getNextDate();
            if(nextDate==null){
                continue;
            }
            long differenzJetzt = nextDate.getTime() - currentTime;
            if(differenzJetzt<differenzMin){
                min = nextDate;
                differenzMin = differenzJetzt;
            }
        }
        return min;
    }

    private void removeNotfication(){

    }



}
