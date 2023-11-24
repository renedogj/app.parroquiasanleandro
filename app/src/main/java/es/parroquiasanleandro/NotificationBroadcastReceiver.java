package es.parroquiasanleandro;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Objects;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    //private final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String idCanal = intent.getStringExtra("idCanal");

        if (Objects.equals(idCanal, NotificacionSL.CANAL_GENERAL)) {
            NotificacionSL notificacionSL = NotificacionSL.crearNotificacionSLRecordatorio();
            Notification notification = notificacionSL.crearNotification(context);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(notificacionSL.id, notification);

            //NotificacionSL.programarNotificacion(context, NotificacionSL.CANAL_GENERAL, NotificacionSL.MILLIS_3_DIAS);
        }
        //createSimpleNotification(context);
    }

    private void createSimpleNotification(Context context) {
        /*Intent intent = new Intent(context, ActivityNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);*/

        /*Notification notification = new NotificationCompat.Builder(context, "myChannel")
                .setSmallIcon(android.R.drawable.ic_delete)
                .setContentTitle("My title")
                .setContentText("Esto es un ejemplo")
                .setStyle(
                        new NotificationCompat.BigTextStyle()
                                .bigText("Lorem Ipsum es simplemente el texto de relleno de las imprentas y archivos de texto. Lorem Ipsum ha sido el texto de relleno estándar de las industrias desde el año 1500, cuando un impresor (N. del T. persona que se dedica a la imprenta) desconocido usó una galería de textos y los mezcló de tal manera que logró hacer un libro de textos especimen. No sólo sobrevivió 500 años, sino que tambien ingresó como texto de relleno en documentos electrónicos, quedando esencialmente igual al original. Fue popularizado en los 60s con la creación de las hojas \"Letraset\", las cuales contenian pasajes de Lorem Ipsum, y más recientemente con software de autoedición, como por ejemplo Aldus PageMaker, el cual incluye versiones de Lorem Ipsum.")
                )
                .setAutoCancel(true)
                .setLights(Color.MAGENTA, 1000, 1000)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();*/

        //NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //manager.notify(NOTIFICATION_ID, notification);
    }
}
