package v.team.works.u22.hal.u22verification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * 追加インポート。
 */
import android.util.*;
import android.view.View;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * サーバからプッシュ通知を受信するクラス。
 *
 *
 * ソース記述前の手順。
 *
 * project.properties
 *
 * ローカルホスト:10.0.2.2
 * @author Yuki Yoshida
 */
public class Verification01Activity extends AppCompatActivity {

    private String _token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_01_verification);

        Log.d("アクティビティ","アクティビティ01起動");
        // 起動アクティビティで実行しなければならないメソッド by Yuki Yoshida.
        MyInstanceIDListenerService service = new MyInstanceIDListenerService();
        service.onTokenRefresh();
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        this._token = FirebaseInstanceId.getInstance().getToken();

        String channelId = "verification_01";
            String channelName = "notify_verification";
            String channelDescription = "検証01のチャンネル";

        // 通知チャンネルの作成 by Yuki Yoshida.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
//                    channelName, NotificationManager.IMPORTANCE_LOW));
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelManager ncm = new NotificationChannelManager();
            ncm.create(
                getApplicationContext(),
                channelId,
                channelName,
                channelDescription);
        }
    }

    /**
     * 編集中。サーバへトークンを送信するメソッド。
     *
     * @param view 画面部品。
     */
    public void onSendClick(View view) {

//        HttpURLConnection cnct = null;
//        URL url;
//        String urlSt = "http://10.0.2.2:8080/U22Verification/Verification01Servlet?token=" + this._token;
//
//        try {
//            // URLの作成
//            url = new URL(urlSt);
//            // 接続用HttpURLConnectionオブジェクト作成
//            cnct = (HttpURLConnection)url.openConnection();
//            // リクエストメソッドの設定
//            cnct.setRequestMethod("GET");
//            // リダイレクトを自動で許可しない設定
//            cnct.setInstanceFollowRedirects(false);
//            // URL接続からデータを読み取る場合はtrue
//            cnct.setDoInput(false);
//            // URL接続にデータを書き込む場合はtrue
//            cnct.setDoOutput(false);
//
//            // 接続
//            cnct.connect(); // ①
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            cnct.disconnect();
//        }

//        HttpResponsAsync cnct = new HttpResponsAsync();
//        cnct.doInBackground(this._token);

    }


    public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

        private final String TAG = "インスタンスID";

        /**
         * トークンを更新するメソッド。
         */
        @Override
        public void onTokenRefresh() {
            // インスタンスIDの更新。
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            Log.d(TAG, "生成されたトークン:" + refreshedToken);

            // サーバーにトークンを送信。
            sendRegistrationToServer(refreshedToken);
        }

        /**
         * トークンをサーバへ送信するメソッド。
         *
         * @param token 新規生成されたトークン。
         */
        private void sendRegistrationToServer(String token) {
            // TODO: Implement this method to send token to your app server.
        }
    }

    public class NotificationChannelManager {

        /**
         * 通知チャンネルを設定するメソッド。Android O以降で必須。
         *
         * @param context アプリケーションコンテキスト。
         * @param channelId チャンネルID。
         * @param channelTitle チャンネル名。
         * @param channelDescription チャンネルの説明。
         */
        @TargetApi(Build.VERSION_CODES.O)
        public void create(Context context, String channelId, String channelTitle, String channelDescription) {
            String title = channelTitle;
            String description = channelDescription;

            NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

//    public class HttpResponsAsync extends AsyncTask<Void, Void, String> {
//
//        @Override
//        protected String doInBackground(String... token) {
//            HttpURLConnection con = null;
//            URL url = null;
//            String urlSt = "http://10.0.2.2:8080/U22Verification/Verification01Servlet?token=" + token;
//
//            try {
//                // URLの作成
//                url = new URL(urlSt);
//                // 接続用HttpURLConnectionオブジェクト作成
//                con = (HttpURLConnection)url.openConnection();
//                // リクエストメソッドの設定
//                con.setRequestMethod("POST");
//                // リダイレクトを自動で許可しない設定
//                con.setInstanceFollowRedirects(false);
//                // URL接続からデータを読み取る場合はtrue
//                con.setDoInput(false);
//                // URL接続にデータを書き込む場合はtrue
//                con.setDoOutput(false);
//
//                con.connect(); // 接続
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//    }
}
