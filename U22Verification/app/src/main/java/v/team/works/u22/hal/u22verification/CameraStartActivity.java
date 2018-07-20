package v.team.works.u22.hal.u22verification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 検証4のクラス
 * ボタンクリックでカメラ起動→位置情報埋め込み→ファイル保存→位置情報取得。
 * ※アプリインストールの際にはストレージ、位置情報の権限を許可してください。
 *
 * @author ohs60224
 */
public class CameraStartActivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;

    //緯度
    String latitude;
    //軽度
    String longtude;

    String longitudeRef;
    String latitudeRef;

    //onActivityResultメソッドで受け取るコード
    private static final int CAMERA_REQUEST_CODE = 1;

    //カメラで撮影した画像のURI
    private Uri imageUri;

    //インテント
    Intent _intent;

    //カメラ画像を保存するディレクトリ
    static File mediaStorage;
    static File mediaFile;

    //画像を表示する部分
    ImageView ivDisplay;

    //保存ボタン
    Button btSave;

    Bitmap _bitmap;

    //Exifインスタンス取得
    ExifInterface exif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_start);

        ivDisplay = findViewById(R.id.ivDisplay);

        //位置情報へのアクセス許可の確認
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, 1000);
        }
        else {
            locationStart();

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 50, this);
        }
    }

    public void locationStart() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("debug", "location manager Enabled");
        }
        else {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "not gpsEnable, startActivity");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 50, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 50, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  @NonNull String[]permissions,  @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");

                locationStart();
            }
            else {
                // それでも拒否された時の対応
                Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    /**
     * 緯度経度が更新されたときに動く
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d("地点位置情報", "突入");
        encodeGpsToExifFormat(location);
    }

    /**
     * Exif形式にGPS Locationを変換して返す。
     * longitudeRef => W or E
     * latitudeRef => N or S
     * latitude and longitude => num1/denom1,num2/denom2,num3/denom3
     * ex) 12/1,34/1,56789/1000
     *
     * @param location
     * @return ExifLocation
     */
    public void encodeGpsToExifFormat(Location location) {
        //ExifLocation exifLocation = new ExifLocation();
        // 経度の変換(正->東, 負->西)
        // convertの出力サンプル => 73:9:57.03876
        String[] lonDMS = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS).split(":");
        StringBuilder lon = new StringBuilder();

        // 経度の正負でREFの値を設定（経度からは符号を取り除く）
        if (lonDMS[0].contains("-")) {
            longitudeRef = "W";
        } else {
            longitudeRef = "E";
        }
        lon.append(lonDMS[0].replace("-", ""));
        lon.append("/1,");
        lon.append(lonDMS[1]);
        lon.append("/1,");

        // 秒は小数の桁を数えて精度を求める
        int index = lonDMS[2].indexOf('.');
        if (index == -1) {
            lon.append(lonDMS[2]);
            lon.append("/1");
        } else {
            int digit = lonDMS[2].substring(index + 1).length();
            int second = (int) (Double.parseDouble(lonDMS[2]) * Math.pow(10, digit));
            lon.append(String.valueOf(second));
            lon.append("/1");
            for (int i = 0; i < digit; i++) {
                lon.append("0");
            }
        }
        longtude = lon.toString();

        // 緯度の変換(正->北, 負->南)
        // convertの出力サンプル => 73:9:57.03876
        String[] latDMS = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS).split(":");
        StringBuilder lat = new StringBuilder();

        // 経度の正負でREFの値を設定（経度からは符号を取り除く）
        if (latDMS[0].contains("-")) {
            latitudeRef = "S";
        } else {
            latitudeRef = "N";
        }
        lat.append(latDMS[0].replace("-", ""));
        lat.append("/1,");
        lat.append(latDMS[1]);
        lat.append("/1,");

        // 秒は小数の桁を数えて精度を求める
        index = latDMS[2].indexOf('.');
        if (index == -1) {
            lat.append(latDMS[2]);
            lat.append("/1");
        } else {
            int digit = latDMS[2].substring(index + 1).length();
            int second = (int) (Double.parseDouble(latDMS[2]) * Math.pow(10, digit));
            lat.append(String.valueOf(second));
            lat.append("/1");
            for (int i = 0; i < digit; i++) {
                lat.append("0");
            }
        }
        latitude = lat.toString();
    }

    //Exif情報を書き込むメソッド
    public void exifWrite(File file) {
        try {
            //Exifinterfaceのインスタンス取得
            exif = new ExifInterface(file.toString());

            //位置情報をExifにセット
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitude); //緯度
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, latitudeRef); //緯度の符号（北か南か）
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longtude); //軽度
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, longitudeRef);

            //保存
            exif.saveAttributes();
        }
        catch (Exception e) {
            Log.e("例外", e.toString());
        }
    }

    //ファイル名生成用メソッド
    protected String getFileName(){
        Calendar c = Calendar.getInstance();
        String s = c.get(Calendar.YEAR)
                + "_" + (c.get(Calendar.MONTH)+1)
                + "_" + c.get(Calendar.DAY_OF_MONTH)
                + "_" + c.get(Calendar.HOUR_OF_DAY)
                + "_" + c.get(Calendar.MINUTE)
                + "_" + c.get(Calendar.SECOND)
                + "_" + c.get(Calendar.MILLISECOND)
                + ".jpeg";
        Log.d("名前", s);
        return s;
    }

    //カメラ起動ボタンが押されたとき
    public void onClickCameraStart(View view) {

        //インテントの生成
        _intent = new Intent();

        //インテントのアクションを指定する
        _intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        //カメラへのアクセス許可の確認
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
            }, 1000);
        }
        else {
            //カメラのアクセスの許可を求める
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1000);
            }
            //拒否された場合
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,}, 1000);
            }
            //標準搭載されているカメラアプリのアクティビティを起動する
            startActivityForResult(_intent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == this.CAMERA_REQUEST_CODE) {
            switch(resultCode) {
                case RESULT_OK:    //撮影完了
                    Toast.makeText(CameraStartActivity.this, "保存完了", Toast.LENGTH_SHORT).show();

                    //撮影した画像を取得
                    _bitmap = (Bitmap) data.getExtras().get("data");

                    try {
                        //取得した画像をファイルに保存（※保存完了が反映するまで少し時間がかかります）
                        mediaStorage = Environment.getExternalStorageDirectory();
                        mediaFile = new File(mediaStorage.getAbsolutePath() + "/" + Environment.DIRECTORY_DCIM + "/Camera", getFileName());
                        FileOutputStream outStream = new FileOutputStream(mediaFile);
                        _bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        exifWrite(mediaFile);
                        outStream.close();

                        float[] latLong = new float[2];
                        exif.getLatLong(latLong);
                        String info = String.format("%f,%f", latLong[0],latLong[1]);
                        TextView tvLocation = findViewById(R.id.tvLocation);
                        tvLocation.setText("位置情報：" + info);

                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    ivDisplay.setImageBitmap(_bitmap);
                    break;
                case RESULT_CANCELED:    //撮影が途中で中止
                    Toast.makeText(CameraStartActivity.this, "撮影が中止されました。", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
