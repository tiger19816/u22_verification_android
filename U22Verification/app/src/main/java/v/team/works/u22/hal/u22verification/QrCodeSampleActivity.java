package v.team.works.u22.hal.u22verification;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AndroidRuntimeException;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;

/**
 * QRコードサンプル画面用アクティビティ。
 * QRコードを利用するには、Gradle Scripts > build.gradleファイル内、dependencies {}の中に、
 * implementation 'com.journeyapps:zxing-android-embedded:3.6.0'
 * を追記することで、オープンソースライブラリがプロジェクトに読み込まれ、QRコードリーダーを含めた一連の処理を記述できる。
 * build.gradleファイルもGit管理されているので、このActivityクラスファイルがあるなら何も行う必要はない。
 * ちなみにQRコードのみならずバーコードも読み込める。
 *
 * @author Taiga Hirai
 */
public class QrCodeSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_sample);
        setTitle(R.string.qr_activity_title);

        //戻るボタンの表示。
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * ボタンが押された時のイベント処理用メソッド.
     *
     * @param view 画面部品。
     */
    public void onButtonClickListener(View view) {
        switch (view.getId()) {
            case R.id.btQrDisplay:  //QRコードを表示するボタンが押された時の処理。
                EditText etQrText = findViewById(R.id.etQrText);
                String qrText = etQrText.getText().toString();

                //QRコード化する文字列が1文字以上であるか確認
                if(0 < qrText.length()) {
                    //QRコード画像の大きさを指定(pixel)
                    int size = 1000;

                    try {
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

                        HashMap hints = new HashMap();

                        //QRコードの文字コードを指定（日本語を扱う場合は"Shift_JIS"。
                        hints.put(EncodeHintType.CHARACTER_SET, "Shift_JIS");

                        //QRコードをBitmapで作成。
                        Bitmap bitmap = barcodeEncoder.encodeBitmap(qrText, BarcodeFormat.QR_CODE, size, size, hints);

                        //作成したQRコードを画面上に配置。
                        ImageView imQrCode = findViewById(R.id.imQrCode);
                        imQrCode.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        throw new AndroidRuntimeException("Barcode Error.", e);
                    }
                }
                break;
            case R.id.btQrReader:   //QRコードリーダを起動するボタンが押された時の処理。
                //QRコードリーダを起動する。
                new IntentIntegrator(QrCodeSampleActivity.this).initiateScan();
                break;
        }
    }

    /**
     * QRコードリーダが終了した時の処理.
     * 何も読み込まれなかった場合もこのメソッドが呼ばれる。
     *
     * @param requestCode 要求コード（特に気にしない）。
     * @param resultCode 操作が成功したRESULT_OKか、バックアウトしたりなど理由で失敗した場合のRESULT_CANCELEDが入っている。
     * @param data 結果のデータを運ぶIntent。
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //resultCodeがRESULT_OKの場合は、処理を行う。
        if(resultCode == RESULT_OK) {
            //読み込まれた文字列をTextViewに表示する。
            TextView tvQrReadText = findViewById(R.id.tvReadText);
            tvQrReadText.setText(result.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
