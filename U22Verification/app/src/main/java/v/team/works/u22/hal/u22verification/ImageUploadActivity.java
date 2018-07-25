package v.team.works.u22.hal.u22verification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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
    final static private String ServerUrl = "http://192.168.42.8:8080/test/ImageUploadServlet";
    final static private String TAG = "HttpPost";
    private static final int RESULT_PICK_IMAGEFILE = 1000;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.image_view);

        findViewById(R.id.imageSelectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });
    }

    class Param {
        public String uri;
        public Bitmap bmp;

        public Param(String uri, Bitmap bmp) {
            this.uri = uri;
            this.bmp = bmp;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    Bitmap bmp = getBitmapFromUri(uri);
                    imageView.setImageBitmap(bmp);

                    HttpPostTask task = new HttpPostTask(ServerUrl);
                    //画像を追加
                    AssetManager manager = getAssets();

                    for(int i = 1; i <= 2; i++){
                        InputStream is = null;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        try{
                            is = this.getContentResolver().openInputStream(uri);
                            int len;
                            byte[] buffer = new byte[10240];


                            bmp.compress(Bitmap.CompressFormat.JPEG,100,baos);
                            baos.flush();
                            byte[] imgByte = baos.toByteArray();

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
                    task.setListener(this);
                    task.execute();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void postCompletion(byte[] response) {
        Toast.makeText(this,"画像の送信に成功しました",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void postFialure() {
        Toast.makeText(this,"画像の送信に失敗しました",Toast.LENGTH_SHORT).show();
    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

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

        private byte[] makePostData() {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {

                // 画像の設定
                int count = 1;
                for (Map.Entry<String, byte[]> entry : mImages.entrySet()) {
                    String key = entry.getKey();
                    byte[] data = entry.getValue();
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

