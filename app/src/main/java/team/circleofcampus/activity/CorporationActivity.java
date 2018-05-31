package team.circleofcampus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import team.circleofcampus.R;


public class CorporationActivity extends AppCompatActivity {

    private ImageView backIcon;
    private TextView headerTitle;
    private ImageView headerSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corporation);

        headerTitle = (TextView) findViewById(R.id.header_title);
        headerTitle.setText("社团公告");

        headerSearch = (ImageView) findViewById(R.id.header_right_image);
        headerSearch.setImageResource(R.drawable.search);

        backIcon = findViewById(R.id.header_left_image);
        backIcon.setVisibility(View.VISIBLE);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
