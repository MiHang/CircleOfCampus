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
import coc.team.home.background.MyService;
import coc.team.home.R;
import coc.team.home.adapter.MyFragmentAdapter;
import coc.team.home.fragment.AFragment;
import coc.team.home.fragment.BFragment;
import coc.team.home.fragment.CFragment;

/**
 * 主界面
 */
public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    MyFragmentAdapter adapter;
    private ViewPager HomeViewPager;
    List<Fragment> data = new ArrayList<>();
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        MyBroadcastReceiver myBro=new MyBroadcastReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("coc.team.home.activity");
        registerReceiver(myBro,intentFilter);
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("send","jayevip@163.com");
        startService(intent);

        data.add(new AFragment());
        BFragment bFragment=new BFragment();
        bFragment.bind(myBro);
        data.add(bFragment);
        data.add(new CFragment());
        adapter = new MyFragmentAdapter(getSupportFragmentManager(), data);
        HomeViewPager.setAdapter(adapter);


    }

    private void initView() {
        HomeViewPager = (ViewPager) findViewById(R.id.HomeViewPager);
        message = (TextView) findViewById(R.id.message);
        message.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.message:
                HomeViewPager.setCurrentItem(1,true);
                break;
        }
    }


}
