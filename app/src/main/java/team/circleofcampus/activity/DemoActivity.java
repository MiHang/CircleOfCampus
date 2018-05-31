package team.circleofcampus.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import team.circleofcampus.BroadcastReceiver.MyBroadcastReceiver;
import team.circleofcampus.R;
import team.circleofcampus.background.MyService;

public class DemoActivity extends AppCompatActivity {

    private Button start_service;
    private Button stop_service;
    private Button bind_service;
    private Button unbind_service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        initView();
    }

    private void initView() {
        start_service = (Button) findViewById(R.id.start_service);
        stop_service = (Button) findViewById(R.id.stop_service);
        bind_service = (Button) findViewById(R.id.bind_service);
        unbind_service = (Button) findViewById(R.id.unbind_service);

        start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyBroadcastReceiver myBro=new MyBroadcastReceiver();

                IntentFilter intentFilter=new IntentFilter();
                intentFilter.addAction("coc.team.home.activity");
                registerReceiver(myBro,intentFilter);
                Intent intent=new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("send","123");
                startService(intent);
            }
        });

    }


}
