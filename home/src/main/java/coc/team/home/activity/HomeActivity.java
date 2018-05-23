package coc.team.home.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import coc.team.home.BroadcastReceiver.MyBroadcastReceiver;
import coc.team.home.R;
import coc.team.home.adapter.MyFragmentAdapter;
import coc.team.home.background.MyService;
import coc.team.home.fragment.AFragment;
import coc.team.home.fragment.BFragment;
import coc.team.home.fragment.CFragment;
import coc.team.home.fragment.DFragment;

/**
 * 主界面
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    MyFragmentAdapter adapter;
    private ViewPager HomeViewPager;
    List<Fragment> data = new ArrayList<>();
    private TextView message;
    private TextView circle;
    private TextView publish;
    private TextView mine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        MyBroadcastReceiver myBro = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("coc.team.home.activity");
        registerReceiver(myBro, intentFilter);
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("send", "jayevip@163.com");
        startService(intent);

        data.add(new AFragment());
        BFragment bFragment = new BFragment();
        bFragment.bind(myBro);
        data.add(bFragment);
        data.add(new CFragment());
        data.add(new DFragment());
        adapter = new MyFragmentAdapter(getSupportFragmentManager(), data);
        HomeViewPager.setAdapter(adapter);


    }

    private void initView() {
        HomeViewPager = (ViewPager) findViewById(R.id.HomeViewPager);
        message = (TextView) findViewById(R.id.message);
        message.setOnClickListener(this);

        circle = (TextView) findViewById(R.id.circle);
        circle.setOnClickListener(this);
        publish = (TextView) findViewById(R.id.publish);
        publish.setOnClickListener(this);
        mine = (TextView) findViewById(R.id.mine);
        mine.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circle:
                HomeViewPager.setCurrentItem(0, true);
                break;
            case R.id.message:
                HomeViewPager.setCurrentItem(1, true);
                break;
            case R.id.publish:
                HomeViewPager.setCurrentItem(2, true);
                break;
            case R.id.mine:
                HomeViewPager.setCurrentItem(3, true);
                break;
        }
    }


}
