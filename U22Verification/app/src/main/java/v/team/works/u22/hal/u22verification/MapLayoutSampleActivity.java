package v.team.works.u22.hal.u22verification;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Map画面のレイアウトサンプルクラス.
 *
 * @author Taiga Hirai
 */
public class MapLayoutSampleActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ListView linearContentsArea;    // 内容エリア]
    private LinearLayout linearLayoutArea;
    private FloatingActionButton fab;
    private final static int DURATION = 400;    // アニメーションにかける時間(ミリ秒)
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layout_sample);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mvMaps);
        mapFragment.getMapAsync(this);

        // 内容エリアの結び付け
        linearContentsArea = findViewById(R.id.lvList);
        // ボタンの結び付け
        fab = findViewById(R.id.fabOpenList);

        linearLayoutArea = findViewById(R.id.llMain);

        // ExpandするViewの元のサイズを保持
        final int originalHeight = linearLayoutArea.getHeight() / 2;

        // 内容エリアを閉じるアニメーション
        ResizeAnimation closeAnimation = new ResizeAnimation(linearContentsArea, -originalHeight, originalHeight);
        closeAnimation.setDuration(DURATION);
        linearContentsArea.startAnimation(closeAnimation);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(34.699886, 135.493033);
        mMap.addMarker(new MarkerOptions().position(sydney).title("HAL大阪"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        // 内容エリアの結び付け
        linearContentsArea = findViewById(R.id.lvList);
        // ボタンの結び付け
        fab = findViewById(R.id.fabOpenList);

        // ExpandするViewの元のサイズを保持
        final int originalHeight = linearLayoutArea.getHeight() / 2;

        // 展開ボタン押下時
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (linearContentsArea.getHeight() > 0) {       // 内容エリアが開いている時

                    // 内容エリアを閉じるアニメーション
                    ResizeAnimation closeAnimation = new ResizeAnimation(linearContentsArea, -originalHeight, originalHeight);
                    closeAnimation.setDuration(DURATION);
                    linearContentsArea.startAnimation(closeAnimation);
                } else {

                    // 内容エリアが閉じている時、内容エリアを開くアニメーション
                    ResizeAnimation openAnimation = new ResizeAnimation(linearContentsArea, originalHeight, 0);
                    openAnimation.setDuration(DURATION);    // アニメーションにかける時間(ミリ秒)
                    linearContentsArea.startAnimation(openAnimation);   // アニメーション開始
                }
            }
        });
    }

    /**
     * フローティングアクションボタンが押された時のイベント処理用メソッド.
     *
     * @param view 画面部品。
     */
    public void onFabOpenListClick(View view) {
    }
}
