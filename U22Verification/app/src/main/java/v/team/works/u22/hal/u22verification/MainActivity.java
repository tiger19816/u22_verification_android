package v.team.works.u22.hal.u22verification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * ボタンが押された時のイベント処理用メソッド.
     * Intentをnewする時のの第二引数を各検証画面に変更する。
     *
     * @param view 画面部品。
     */
    public void onButtonClickListener(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.btVerification01:
                intent = new Intent(MainActivity.this, MainActivity.class);
                break;
            case R.id.btVerification02:
                intent = new Intent(MainActivity.this, Verification02Activity.class);
                break;
            case R.id.btVerification03:
                intent = new Intent(MainActivity.this, MainActivity.class);
                break;
            case R.id.btVerification04:
                intent = new Intent(MainActivity.this, MainActivity.class);
                break;
            case R.id.btVerification05:
                intent = new Intent(MainActivity.this, MainActivity.class);
                break;
            case R.id.btVerification06:
                intent = new Intent(MainActivity.this, MainActivity.class);
                break;
            case R.id.btVerification07:
                intent = new Intent(MainActivity.this, MapLayoutSampleActivity.class);
                break;
            case R.id.btSample01:
                intent = new Intent(MainActivity.this, TabLayoutSampleActivity.class);
                break;
            case R.id.btSample02:
                intent = new Intent(MainActivity.this, QrCodeSampleActivity.class);
                break;
        }
        startActivity(intent);
    }
}
