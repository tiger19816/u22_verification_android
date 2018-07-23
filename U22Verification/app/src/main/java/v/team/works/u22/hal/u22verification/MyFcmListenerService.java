package v.team.works.u22.hal.u22verification;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFcmListenerService extends FirebaseMessagingService {

    private final static String TAG = "通知受信";

    @Override
    public void onMessageReceived(RemoteMessage message){
        String msg = message.getNotification().getBody();
        Log.d(TAG, "サーバからの通知:" + msg);
        sendNotification("Verification01からの通知", msg);
    }

    private void sendNotification(String subTitle, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher) // アイコン。
                .setContentTitle("U22夫管理アプリからの通知")  // タイトル。
                .setSubText(subTitle)  // 小タイトル。
                .setContentText(message)    // 表示内容。
//                .setSound(defaultSoundUri)    // 通知音の設定(?)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(message)) // 表示スタイルの設定(?)
//                .setContentIntent(pendingIntent)  // タップした際の遷移先インテントの設定(?)
                ;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // SDKバージョンが26以上の場合、通知チャンネルの設定を行う。
            notificationBuilder.setChannelId("verification_01");
        }


        notificationManager.notify(0 , notificationBuilder.build());    // チャンネルがnull?
    }

}
