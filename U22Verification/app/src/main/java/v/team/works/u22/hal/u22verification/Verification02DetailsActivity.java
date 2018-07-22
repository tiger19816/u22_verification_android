package v.team.works.u22.hal.u22verification;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class Verification02DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification02_details);

        Intent intent = getIntent();
        String markerName = intent.getStringExtra("markerName");
        String markerPosition = intent.getStringExtra("markerPosition");
        TextView tvMarkerName = findViewById(R.id.tvMarkerName);
        TextView tvMarkerPosition = findViewById(R.id.tvMarkerPosition);
        tvMarkerName.setText(markerName);
    }
}
