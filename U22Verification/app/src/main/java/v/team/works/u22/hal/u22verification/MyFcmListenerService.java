package v.team.works.u22.hal.u22verification;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFcmListenerService extends FirebaseMessagingService {

    private final static String TAG = "通知受信";

    @Override
    public void onMessageReceived(RemoteMessage message){
//        String from = message.getFrom();
//        Map data = message.getData();   // sizeが0になっている。（エラーの要因）
//
//        Log.d(TAG, "from:" + from);
//        Log.d(TAG, "data:" + data.toString());
//
//        String msg = data.get("data").toString();
        String msg = message.getNotification().getBody();
        Log.d(TAG, "サーバからの通知:" + msg);
        sendNotification(msg);
    }

    private void sendNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//        String CHANNEL_ID = "verification_01";
//        CharSequence name = "notify_verification";
//        String Description = "検証01のチャンネル";
//
//        int importance = NotificationManager.IMPORTANCE_HIGH;
//        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
//        notificationChannel.setDescription(Description);
//        notificationChannel.enableLights(true);
//        notificationChannel.setLightColor(android.R.color.holo_red_light);
//        notificationChannel.enableVibration(true);
//        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//        notificationChannel.setShowBadge(false);
//
//        notificationManager.createNotificationChannel(notificationChannel);

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("U22夫管理アプリからの通知")
//                .setSubText("Verification01からの通知")
                .setContentText(message)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                .setContentIntent(pendingIntent)
                ;


        notificationManager.notify(0 , notificationBuilder.build());    // チャンネルがnull?
    }

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            if (/* Check if data needs to be processed by long running job */ true) {
////                scheduleJob();
//            } else {
////                handleNow();
//            }
//
//        }
//
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());    // 受信できている
//        }
//
//    }

}
