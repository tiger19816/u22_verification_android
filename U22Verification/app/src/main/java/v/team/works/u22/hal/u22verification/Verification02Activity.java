package v.team.works.u22.hal.u22verification;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Verification02Activity extends FragmentActivity implements OnMapReadyCallback {

    private ListView linearContentsArea;    // 内容エリア]
    private LinearLayout linearLayoutArea;
    private FloatingActionButton fab;
    private final static int DURATION = 400;    // アニメーションにかける時間(ミリ秒)
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification02);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mvMaps);
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

        //HAL大阪のマーカー表示
        LatLng HAL_OSAKA = new LatLng(34.699886, 135.493033);
        mMap.addMarker(new MarkerOptions().position(HAL_OSAKA).title("HAL大阪").snippet("HAL大阪のマーカー"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(HAL_OSAKA,15));

        //大阪駅のマーカー表示
        LatLng OSAKA_STATION = new LatLng(34.702485, 135.495951);
        mMap.addMarker(new MarkerOptions().position(OSAKA_STATION).title("大阪駅").snippet("大阪駅のマーカー"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(OSAKA_STATION,15));

        //東梅田駅のマーカー表示
        LatLng EAST_UMEDA_STATION = new LatLng(34.700933, 135.499654);
        mMap.addMarker(new MarkerOptions().position(EAST_UMEDA_STATION).title("東梅田駅").snippet("東梅田駅のマーカー"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EAST_UMEDA_STATION,15));

        //マーカーが押下されたときの処理
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
//                Toast.makeText(getApplicationContext(), marker.getId() + "と" + marker.getTitle(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Verification02Activity.this,Verification02DetailsActivity.class);
                intent.putExtra("markerName", marker.getTitle());
                intent.putExtra("markerPosition", marker.getPosition());
                startActivity(intent);
                return false;
            }
        });
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
