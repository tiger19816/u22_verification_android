package v.team.works.u22.hal.u22verification;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MapLayoutSampleActivity extends AppCompatActivity {

    private ListView linearContentsArea;    // 内容エリア]
    private LinearLayout linearLayoutArea;
    private FloatingActionButton fab;
    private final static int DURATION = 400;    // アニメーションにかける時間(ミリ秒)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_layout_sample);

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
