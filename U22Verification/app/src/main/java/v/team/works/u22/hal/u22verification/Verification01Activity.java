package v.team.works.u22.hal.u22verification;

import android.annotation.TargetApi;
import android.app.Activity;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
    /**
     * トークンを代入するフィールド。
     */
    private String _token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_01_verification);

        Log.i("アクティビティ","アクティビティ01起動");
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
        HttpResponsAsync hra = new HttpResponsAsync(this);
        hra.execute(this._token);
//        hra.doInBackground(this._token);
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

    public class HttpResponsAsync extends AsyncTask<String, Void, Void> {

        private Activity _activity;

        /**
         * コンストラクタ。
         *
         * @param activity 遷移元アクティビティ。
         */
        public HttpResponsAsync(Activity activity) {

            this._activity = activity;

        }

        /**
         * サーブレットへパラメータを送信するメソッド。
         *
         * @param token ①発行されたトークン
         * @return null
         */
        @Override
        protected Void doInBackground(String... token) {
            HttpURLConnection cnct = null;
            URL url = null;
            OutputStream opStream = null;
            String urlStr = "http://10.0.2.2:8080/U22Verification/Verification01Servlet";   // 接続先URL
//            String urlStr = "http://0.0.0.0/hello/hello"; // コネクションエラーがちゃんと出る。

            String postData = "token=" + token[0];  // POSTで送りたいデータ

            Log.d("URL", "アクセス先: " + urlStr + "?" + postData);

            try {
                url = new URL(urlStr);  // URLの作成
                cnct = (HttpURLConnection) url.openConnection(); // 接続用HttpURLConnectionオブジェクト作成
                cnct.setRequestMethod("POST");  // リクエストメソッドの設定
                cnct.setInstanceFollowRedirects(false); // リダイレクトの可否
                cnct.setDoOutput(true); // データ書き込みの可否
                cnct.setReadTimeout(10000); // タイムアウトまでの時間
                cnct.setConnectTimeout(20000);
//                cnct.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                cnct.setUseCaches(false);

                cnct.connect(); // 接続

//                JSONObject objToken = new JSONObject();
//                objToken.put("token", token[0]);


                try {
                    opStream = cnct.getOutputStream();

                    // 送信する値をByteデータに変換する（UTF-8）
                    opStream.write(postData.getBytes("UTF-8"));
//                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(opStream, "UTF-8"));
//                    writer.write(String.valueOf(objToken));
//                    Log.d("URL", "送信パラメーター: " + writer.toString());
//                    writer.flush();
//                    writer.close();
                    opStream.flush();

                    switch (cnct.getResponseCode()) {
                        case HttpURLConnection.HTTP_OK :
                            Log.d("URL", "コネクション状況: 成功");
                            break;
                        case HttpURLConnection.HTTP_INTERNAL_ERROR:
                            Log.e("URL", "エラー内容: 500 内部サーバーエラー");
                            break;
                        default:
                            Log.e("URL", "コネクションレスポンスコード: " + cnct.getResponseCode());
                            break;
                    }
                }
                catch (Exception e) {
                    Log.e("URL", "POST送信エラー: " + e.toString());
                }
                finally {

                    if(opStream != null) {
                        try {
                            opStream.close();
                        }
                        catch (Exception e) {
                            Log.e("URL", "OutputStream解放失敗: " + e.toString());
                        }
                    }
                }

            } catch (Exception e) {
                Log.e("URL", "コネクションエラー: " + e.toString());
            } finally {
                if (cnct != null) {
                    cnct.disconnect();
                }
            }

            return null;
        }

        /**
         * サーブレットへアクセスした後に実行されるメソッド。
         */
        @Override
        public void onPostExecute(Void param) {
            Log.d("HTTP", "onPostExecute: 通過");
        }

    }
}

// Logs
//
//        07-26 04:33:47.016 13174-13209/v.team.works.u22.hal.u22verification W/OkHttpClient: A connection to http://10.0.2.2:8080/ was leaked. Did you forget to close a response body?
//        07-26 04:33:47.032 13174-13289/v.team.works.u22.hal.u22verification W/FirebaseMessaging: Error while parsing timestamp in GCM event
//        java.lang.NumberFormatException: s == null
//        at java.lang.Integer.parseInt(Integer.java:570)
//        at java.lang.Integer.parseInt(Integer.java:643)
//        at com.google.firebase.messaging.zzb.zzc(Unknown Source:81)
//        at com.google.firebase.messaging.zzb.zze(Unknown Source:2)
//        at com.google.firebase.messaging.FirebaseMessagingService.zzd(Unknown Source:280)
//        at com.google.firebase.iid.zzg.run(Unknown Source:26)
//        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1162)
//        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:636)
//        at com.google.android.gms.common.util.concurrent.zza.run(Unknown Source:7)
//        at java.lang.Thread.run(Thread.java:764)
//        07-26 04:33:47.035 13174-13283/v.team.works.u22.hal.u22verification D/FA: Event not sent since app measurement is disabled
//        07-26 04:33:47.037 13174-13289/v.team.works.u22.hal.u22verification W/FirebaseMessaging: Error while parsing timestamp in GCM event
//        java.lang.NumberFormatException: s == null
//        at java.lang.Integer.parseInt(Integer.java:570)
//        at java.lang.Integer.parseInt(Integer.java:643)
//        at com.google.firebase.messaging.zzb.zzc(Unknown Source:81)
//        at com.google.firebase.messaging.zzb.zzh(Unknown Source:2)
//        at com.google.firebase.messaging.FirebaseMessagingService.zzd(Unknown Source:321)
//        at com.google.firebase.iid.zzg.run(Unknown Source:26)
//        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1162)
//        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:636)
//        at com.google.android.gms.common.util.concurrent.zza.run(Unknown Source:7)
//        at java.lang.Thread.run(Thread.java:764)
//        07-26 04:33:47.035 13174-13283/v.team.works.u22.hal.u22verification D/FA: Event not sent since app measurement is disabled
