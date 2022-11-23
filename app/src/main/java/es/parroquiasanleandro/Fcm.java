package es.parroquiasanleandro;

/*public class Fcm extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.e("tag","Mensaje recibido de" + from);
        if(remoteMessage.getNotification() != null){
            Log.e("tag","el titulo es "+ remoteMessage.getNotification().getTitle());
            Log.e("tag","el titulo es "+ remoteMessage.getNotification().getBody());
        }

        if(remoteMessage.getData().size() > 0){
            Log.e("ta","el titulo es: "+ remoteMessage.getData().get("titulo"));
            Log.e("ta","el detalle es: "+ remoteMessage.getData().get("detalle"));
            Log.e("ta","el color es: "+ remoteMessage.getData().get("color"));


        }
    }

    public static void guardarToken(FirebaseUser user, Context context){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(context,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String token = task.getResult();
                        FirebaseDatabase.getInstance().getReference("Usuarios")
                                .child(user.getUid()).child("Dispositivos").push().setValue(token);
                    }
                });
    }
}*/
