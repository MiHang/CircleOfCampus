package coc.team.home.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import coc.team.home.R;

public class CorporationActivity extends AppCompatActivity {

    private ImageView corporation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporation);

        corporation = findViewById(R.id.home_corporation_notice_1);
        corporation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CorporationActivity.this,AFragment.class);
                startActivity(intent);
            }
        });
    }
}
