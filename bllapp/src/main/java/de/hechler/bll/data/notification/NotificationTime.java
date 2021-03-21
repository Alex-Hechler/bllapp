package de.hechler.bll.data.notification;

import java.util.Calendar;
import java.util.Date;

public class NotificationTime {
    private long id;
    private Date ersteNotification;
    private WiederhohlungsZeitraum wiederhohlung;

    public NotificationTime(Date ersteNotification, WiederhohlungsZeitraum wiederhohlung) {
        this.ersteNotification = ersteNotification;
        this.wiederhohlung = wiederhohlung;
    }
    public NotificationTime(long id, Date ersteNotification, WiederhohlungsZeitraum wiederhohlung) {
        this.id = id;
        this.ersteNotification = ersteNotification;
        this.wiederhohlung = wiederhohlung;
    }
    //Getter
    public Date getErsteNotification() {
        return ersteNotification;
    }
    public WiederhohlungsZeitraum getWiederhohlung() {
        return wiederhohlung;
    }
    public Long getId() {
        return id;
    }

    //Setter

    public void setId(Long id) {
        this.id = id;
    }
    public void setErsteNotification(Date ersteNotification) {
        this.ersteNotification = ersteNotification;
    }
    public void setWiederhohlung(WiederhohlungsZeitraum wiederhohlung) {
        this.wiederhohlung = wiederhohlung;
    }

    /**
     * Gibt den Zeitpunkt der nächsten Notfication als Date zurück.
     * @return Date
     */
    public Date getNextDate(){
        Calendar notification = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        notification.setTime(ersteNotification);
        //erste Notification war schon
        if(!notification.after(now)){
            if(wiederhohlung==WiederhohlungsZeitraum.EINMAL){
                return null;
            }
            if(wiederhohlung==WiederhohlungsZeitraum.TAG){
                notification.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH));
                if(!notification.after(now)){
                    notification.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
            if(wiederhohlung==WiederhohlungsZeitraum.WOECHE){
                notification.set(Calendar.YEAR,now.get(Calendar.YEAR));
                notification.set(Calendar.WEEK_OF_YEAR, now.get(Calendar.WEEK_OF_YEAR));
                if(!notification.after(now)){
                    notification.add(Calendar.WEEK_OF_YEAR, 1);
                }
            }
        }
        return notification.getTime();
    }
    public boolean isPartOfNotification(Date date){
        Calendar dateC = Calendar.getInstance();
        dateC.setTime(date);
        Calendar noti = Calendar.getInstance();
        noti.setTime(ersteNotification);
        switch (wiederhohlung){
            case EINMAL:
                if(noti.get(Calendar.YEAR) == dateC.get(Calendar.YEAR)
                        && noti.get(Calendar.DAY_OF_YEAR)==dateC.get(Calendar.DAY_OF_YEAR)
                        && noti.get(Calendar.HOUR_OF_DAY)==dateC.get(Calendar.HOUR_OF_DAY)
                        && noti.get(Calendar.MINUTE)==dateC.get(Calendar.MINUTE)) {
                    return true;
                }
                return false;
            case TAG:
                if(noti.get(Calendar.HOUR_OF_DAY)==dateC.get(Calendar.HOUR_OF_DAY)&&noti.get(Calendar.MINUTE)==dateC.get(Calendar.MINUTE)){
                    return true;
                }
                return  false;
            case WOECHE:
                if(noti.get(Calendar.DAY_OF_WEEK)!=dateC.get(Calendar.DAY_OF_WEEK)) {
                    return false;
                }
                if(noti.get(Calendar.HOUR_OF_DAY)==dateC.get(Calendar.HOUR_OF_DAY)&&noti.get(Calendar.MINUTE)==dateC.get(Calendar.MINUTE)){
                    return true;
                }
                return  false;
        }
        return false;
    }
}
