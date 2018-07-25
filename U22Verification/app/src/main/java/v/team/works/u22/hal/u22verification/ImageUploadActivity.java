package v.team.works.u22.hal.u22verification;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ohs60365 on 2018/07/24.
 */

public class ImageUploadActivity extends Activity implements ImageUploadListener{
    final static private String TAG = "HttpPost";
    //データの送信先URLアドレス
    final static private String URL = "http://192.168.42.48:8080/test/ImageUploadServlet";
    private static final int RESULT_PICK_IMAGEFILE = 1000;
    private ImageView imageView;
    private Param param = new Param();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);
        //送信用の画像を選択するギャラリーを開くボタン処理
        findViewById(R.id.imageSelectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                //選択後の処理へ
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });
        //選択している画像をサーバーへの送信を開始するボタン
        findViewById(R.id.UploadButton).setOnClickListener(new UploadButtonListener());
    }

    class Param {
        //選択している画像のアドレス
        public String uri;
        //選択している画像のイメージファイル
        public Bitmap bmp;

        public Param() {
            this.uri = "";
            this.bmp = bmp;
        }
        public void setParam(String uri, Bitmap bmp){
            this.uri = uri;
            this.bmp = bmp;
        }
    }
    //画像選択に画像確認用のImageViewに画像をsetし、グローバルParamにURIとBitmapを格納するメソッド
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    //URIから画像を読み込みBitmapに格納する
                    Bitmap bmp = getBitmapFromUri(uri);
                    imageView.setImageBitmap(bmp);
                    //グローバルParamにも格納して置く
                    param.setParam(uri.toString(),bmp);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    //URIから画像を引っ張ってくるメソッド
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
    //選択している画像をサーバーへ送信するためのクラス
    public class UploadButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            //データの送信先の設定
            HttpPostTask task = new HttpPostTask(URL);
            //グローバルParamからローカルへURIとBitmapを渡す
            Uri uri = Uri.parse(param.uri);
            Bitmap bmp = param.bmp;
            for(int i = 1; i <= 2; i++){
                InputStream is = null;
                //BitmapをByte[]へ変換するためのStream
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try{
                    is = ImageUploadActivity.this.getContentResolver().openInputStream(uri);
                    byte[] buffer = new byte[10240];
                    //BitmapをJPEGへ変換
                    bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
                    baos.flush();
                    //JPEGをbyte[]へ変換
                    byte[] imgByte = baos.toByteArray();
                    //送信データに画像のbyte[]を追加する
                    task.addImage("filename", imgByte);
                }catch (Exception e){
                }finally{
                    try {
                        is.close();
                    } catch (IOException e) {}
                    try {
                        baos.close();
                    } catch (IOException e) {}
                }
            }
            task.setListener(ImageUploadActivity.this);
            //送信実行
            task.execute();
        }
    }
    //送信が成功したときにToastを表示するメソッド
    @Override
    public void postCompletion(byte[] response) {
        Toast.makeText(ImageUploadActivity.this,"データの送信に失敗しました",Toast.LENGTH_SHORT);
        //サーバー側から返された文字列の表示
        Log.e(TAG, new String(response));
    }
    //送信が失敗したときにToastを表示するメソッド
    @Override
    public void postFialure() {
        Toast.makeText(ImageUploadActivity.this,"データの送信に失敗しました",Toast.LENGTH_SHORT);
    }

    //AsyncTask
    public class HttpPostTask extends AsyncTask<Void, Void, byte[]> {
        final static private String BOUNDARY = "MyBoundaryString";
        private ImageUploadListener mListener;
        private String mURL;
        private HashMap<String, byte[]> mImages;

        public HttpPostTask(String url) {
            super();
            mURL = url;
            mListener = null;
            mImages = new HashMap<String, byte[]>();
        }
        /**
         * タスク処理
         */
        @Override
        protected byte[] doInBackground(Void... params) {
            byte[] data = makePostData();
            byte[] result = send(data);
            return result;
        }
        @Override
        protected void onPostExecute(byte[] result) {
            if (mListener != null) {
                if (result != null) {
                    mListener.postCompletion(result);
                } else {
                    mListener.postFialure();
                }
            }
        }

        public void setListener(ImageUploadListener listener) {
            mListener = listener;
        }
        public void addImage(String key, byte[] data) {
            mImages.put(key, data);
        }
        private byte[] send(byte[] data) {
            if (data == null) {
                return null;
            }
            byte[] result = null;
            HttpURLConnection connection = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                URL url = new URL(mURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                // 接続
                connection.connect();
                // 送信
                OutputStream os = connection.getOutputStream();
                os.write(data);
                os.close();
                // レスポンスを取得する
                byte[] buf = new byte[10240];
                int size;
                is = connection.getInputStream();
                while ((size = is.read(buf)) != -1) {
                    baos.write(buf, 0, size);
                }
                result = baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (Exception e) {
                }
                try {
                    connection.disconnect();
                } catch (Exception e) {
                }
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
            return result;
        }
        //送信するPOSTデータを作成するメソッド
        private byte[] makePostData() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {// 画像の設定
                int count = 1;
                for (Map.Entry<String, byte[]> entry : mImages.entrySet()) {
                    String key = entry.getKey();
                    byte[] data = entry.getValue();
                    //FORMでいうname部分
                    String name = "filename";

                    baos.write(("--" + BOUNDARY + "\r\n").getBytes());
                    baos.write(("Content-Disposition: form-data;").getBytes());
                    baos.write(("name=\"" + name + "\";").getBytes());
                    baos.write(("filename=\"" + key + "\"\r\n").getBytes());
                    baos.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());
                    baos.write(data);
                    baos.write(("\r\n").getBytes());
                }

                // 最後にバウンダリを付ける
                baos.write(("--" + BOUNDARY + "--\r\n").getBytes());

                return baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
        }
    }
}