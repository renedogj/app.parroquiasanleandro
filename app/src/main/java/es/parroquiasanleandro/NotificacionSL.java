package es.parroquiasanleandro;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import es.parroquiasanleandro.activitys.ActivityNavigation;

public class NotificacionSL {

    //IDs Canales notificaciones
    public static final String CANAL_GENERAL = "General";
    public static final String CANAL_AVISOS = "Avisos";

    //Int millisegundos de notificación
    public static final int MILLIS_10_DIAS = 864000000;
    public static final int MILLIS_3_DIAS = 259200000;

    public int id;
    public String idCanal;
    public String titulo;
    public String texto;
    public String textoGrande;
    public Class<?> activity =  ActivityNavigation.class;
//    public PendingIntent pendingIntent;
    //public String icono;


    public NotificacionSL() {
    }

    public NotificacionSL(int id, String idCanal, String titulo, String texto, Class<?> activity, String textoGrande) {
        this.id = id;
        this.idCanal = idCanal;
        this.titulo = titulo;
        this.texto = texto;
        this.activity = activity;
        this.textoGrande = textoGrande;
    }

    public NotificacionSL(int id, String idCanal, String titulo, String texto, Class<?> activity) {
        this.id = id;
        this.idCanal = idCanal;
        this.titulo = titulo;
        this.texto = texto;
        this.activity = activity;
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void programarNotificacion(Context context, String idCanal, long notificarEn) {
        programarNotificacion(context, idCanal, notificarEn, false);
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void programarNotificacion(Context context, String idCanal, long notificarEn, boolean isRepeting) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.putExtra("idCanal", idCanal);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + notificarEn, notificarEn, pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), notificarEn, pendingIntent);
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + notificarEn, pendingIntent);

        if(isRepeting){
            notificarEn += (long)(Math.random() * 7200000 + 600000);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + notificarEn,notificarEn, pendingIntent);
        }else{
//            int randomNum = (int)(Math.random() * 3600000 + 300000);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + notificarEn, pendingIntent);
        }
    }

    public Notification crearNotification(Context context) {
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(context, idCanal)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_logoparroquia_dark)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textoGrande))
                //.setLights(Color.MAGENTA, 1000, 1000)x
                //.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                //.setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
    }

    public static void crearCanal(Context context, String idCanal) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(idCanal, idCanal, NotificationManager.IMPORTANCE_HIGH);
            canal.enableLights(true);
            canal.enableVibration(true);
            canal.setLightColor(Color.RED);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);
        }
    }

    public static NotificacionSL intentInfoToNotificacionSL(Intent intent) {
        NotificacionSL notification = new NotificacionSL();
        notification.id = intent.getIntExtra("id", 0);
        notification.idCanal = intent.getStringExtra("idCanal");
        notification.titulo = intent.getStringExtra("titulo");
        notification.texto = intent.getStringExtra("texto");
        notification.textoGrande = intent.getStringExtra("textoGrande");
        return notification;
    }

    public static NotificacionSL crearNotificacionSLRecordatorio (){
        return new NotificacionSL(10102300, CANAL_GENERAL, "¡Llevamos mucho sin verte!", "Puede que tengas nuevos avisos", ActivityNavigation.class);
    }
}
