package coc.team.home.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
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
public class HomeActivity extends AppCompatActivity {

    MyFragmentAdapter adapter;
    private ViewPager HomeViewPager;
    List<Fragment> data = new ArrayList<>();
    private TextView message;
    private TextView circle;
    private TextView publish;
    private TextView mine;
    private TextView user;
    private TextView title;
    int[] Titles = {
            R.string.circle,
            R.string.message,
            R.string.publish,
            R.string.mine

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        title.setText(Titles[0]);
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
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(Titles[1]);
                HomeViewPager.setCurrentItem(1, true);
            }
        });

        circle = (TextView) findViewById(R.id.circle);
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(Titles[0]);
                HomeViewPager.setCurrentItem(0, true);
            }
        });
        publish = (TextView) findViewById(R.id.publish);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(Titles[2]);
                HomeViewPager.setCurrentItem(2, true);
            }
        });
        mine = (TextView) findViewById(R.id.mine);
        mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText(Titles[3]);
                HomeViewPager.setCurrentItem(3, true);
            }
        });
        user = (TextView) findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });
        title = (TextView) findViewById(R.id.title);

    }



}
