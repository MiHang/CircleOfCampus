package coc.team.home.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import coc.team.home.R;

public class NoticeActivity extends AppCompatActivity {

    private ImageView notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        notice = findViewById(R.id.home_campus_notice_1);
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoticeActivity.this,AFragment.class);
                startActivity(intent);
            }
        });
    }
}
