package coc.team.home.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import coc.team.home.BroadcastReceiver.MyBroadcastReceiver;
import coc.team.home.background.MyService;
import coc.team.home.R;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {

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

        start_service.setOnClickListener(this);
        stop_service.setOnClickListener(this);
        bind_service.setOnClickListener(this);
        unbind_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                MyBroadcastReceiver myBro=new MyBroadcastReceiver();

                IntentFilter intentFilter=new IntentFilter();
                intentFilter.addAction("coc.team.home.activity");
                registerReceiver(myBro,intentFilter);
                Intent intent=new Intent(this,MyService.class);
                intent.putExtra("send","123");
                startService(intent);

                break;
            case R.id.stop_service:

                break;
            case R.id.bind_service:

                break;
            case R.id.unbind_service:

                break;
        }
    }

}
