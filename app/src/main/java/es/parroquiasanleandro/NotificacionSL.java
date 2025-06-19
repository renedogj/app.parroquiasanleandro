package es.parroquiasanleandro;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

import es.parroquiasanleandro.activitys.ActivityNavigation;

public class NotificacionSL {
    public static final String NOTIFICATION = "Notification";
    public static final String NOTIFICATION_STATUS = "Notification_status";

//    public static final boolean STATUS_ACTIVADA = true;
//    public static final boolean STATUS_DESACTIVADA = false;
    public static final String STATUS_ACTIVADAS = "Activadas";
    public static final String STATUS_DESACTIVADAS = "Desactivadas";

    public static final String NOTI_RECORDATORIOS = "notificacionRecordatorios";
    public static final String NOTI_RECORDATORIOS_VIBR = "vibrRecordatorios";


    //IDs Canales notificaciones
    public static final String CANAL_GENERAL = "General";
    public static final String CANAL_AVISOS = "Avisos";

    //Int millisegundos de notificación
    public static final long MILLIS_10_DIAS = 864000000L;
    public static final long MILLIS_5_DIAS = 432000000L;
    public static final long MILLIS_3_DIAS = 259200000L;
    public static final long MILLIS_1_DIA = 86400000L;
    public static final long MILLIS_1_HORA = 3600000L;
    public static final long MILLIS_1_MIN = 60000L;

    public int id;
    public String idCanal;
    public String titulo;
    public String texto;
    public String textoGrande;
    public boolean vibr;
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

    public NotificacionSL(int id, String idCanal, String titulo, String texto, boolean vibr, Class<?> activity) {
        this.id = id;
        this.idCanal = idCanal;
        this.titulo = titulo;
        this.texto = texto;
        this.vibr = vibr;
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

//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + notificarEn, notificarEn, pendingIntent);
//        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + notificarEn, pendingIntent);

        if(isRepeting){
//            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, Calendar.getInstance().getTimeInMillis() + notificarEn, notificarEn, pendingIntent);
            notificarEn += (long)(Math.random() * 7200000 + 600000);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + notificarEn, notificarEn, pendingIntent);
        }else{
            notificarEn += (int)(Math.random() * 3600000 + 300000);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + notificarEn, pendingIntent);
        }
    }

    public Notification crearNotification(Context context) {
        Intent intent = new Intent(context, activity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, idCanal)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSmallIcon(R.drawable.ic_logoparroquia_dark)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(textoGrande))
                //.setLights(Color.MAGENTA, 1000, 1000)
                //.setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if(this.vibr){
            notificationBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }

        return notificationBuilder.build();
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

    public static NotificacionSL crearNotificacionSLRecordatorio (Context context){
        return new NotificacionSL(
                1,
                CANAL_GENERAL,
                "¡Llevamos mucho sin verte!",
                "Puede que tengas nuevos avisos",
                getBooleanInfoNotification(context, NOTI_RECORDATORIOS_VIBR),
                ActivityNavigation.class
        );
    }

    public static String changeAllNotificationsStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION, Context.MODE_PRIVATE);
        String notificationStatus = sharedPreferences.getString(NOTIFICATION_STATUS, "");

        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(notificationStatus.equals(STATUS_ACTIVADAS)) {
            notificationStatus = STATUS_DESACTIVADAS;
        }else if(notificationStatus.equals(STATUS_DESACTIVADAS)){
            notificationStatus = STATUS_ACTIVADAS;
        }else{
            notificationStatus = STATUS_ACTIVADAS;
        }
        editor.putString(NOTIFICATION_STATUS, notificationStatus);
        editor.apply();
        return notificationStatus;
    }

//    public static String getNotificationStatus(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION, Context.MODE_PRIVATE);
//        return sharedPreferences.getString(, STATUS_ACTIVADAS);
//    }

//    public static String get
    public static String getStringInfoNotification(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static boolean getBooleanInfoNotification(Context context, String key){
        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    public static void changeInfoNotification(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(NOTIFICATION_STATUS, !sharedPreferences.getBoolean(key, true));
        editor.apply();
    }

    public static void changeInfoNotification(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(NOTIFICATION, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
