package team.circleofcampus.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import team.circleofcampus.BroadcastReceiver.MyBroadcastReceiver;
import team.circleofcampus.Interface.FragmentSwitchListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.MyFragmentAdapter;
import team.circleofcampus.background.MyService;
import team.circleofcampus.fragment.AFragment;
import team.circleofcampus.fragment.BFragment;
import team.circleofcampus.fragment.CFragment;
import team.circleofcampus.fragment.MineFragment;
import team.circleofcampus.fragment.QRFragment;
import team.circleofcampus.view.NoScrollViewPager;


/**
 * 主界面
 */
public class HomeActivity extends AppCompatActivity {

    MyFragmentAdapter adapter;
    private NoScrollViewPager HomeViewPager;
    List<Fragment> data = new ArrayList<>();
    private ImageView message;
    private ImageView circle;
    private ImageView publish;
    private ImageView mine;
    private TextView headerRightText;
    private ImageView headerRightImage;
    private TextView headerLeftText;
    private ImageView headerLeftImage;
    private TextView title;

    private int selectedPageId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        headerSelect(0);

//        //开启后台服务
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
        MineFragment mineFragment=new MineFragment();
        mineFragment.setSwitchListener(new FragmentSwitchListener() {
            @Override
            public void displayThisFragment(boolean display) {
                headerSelect(4);
                HomeViewPager.setCurrentItem(4,true);
            }
        });
        data.add(mineFragment);
        QRFragment qrFragment = new QRFragment();
        qrFragment.setAccount("assaas");

        data.add(qrFragment);
        adapter = new MyFragmentAdapter(getSupportFragmentManager(), data);
        HomeViewPager.setAdapter(adapter);
        HomeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                selectedPageId = position;
                headerSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /**
     * 标题栏控件显示设置
     * @param position
     */
    public void headerSelect(int position){
        switch (position) {
            case 0: {
                title.setText("校园圈");
                headerLeftText.setText("发表");
                headerLeftImage.setVisibility(View.GONE);
                headerRightText.setText("");
                headerRightImage.setImageResource(R.drawable.search);
                headerRightImage.setVisibility(View.VISIBLE);
                setHomeBottomNavNormal();
                circle.setImageResource(R.drawable.icon_home_campus_hover);
            };break;
            case 1: {
                title.setText("消息");
                headerLeftText.setText("");
                headerLeftImage.setVisibility(View.GONE);
                headerRightText.setText("好友");
                headerRightText.setVisibility(View.VISIBLE);
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                message.setImageResource(R.drawable.icon_home_msg_hover);
            };break;
            case 2: {
                title.setText("我的发布");
                headerLeftText.setText("");
                headerLeftImage.setVisibility(View.GONE);
                headerRightText.setText("");
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                publish.setImageResource(R.drawable.icon_home_my_publish_hover);
            };break;
            case 3: {
                title.setText("我的");
                headerLeftText.setText("");
                headerLeftImage.setVisibility(View.GONE);
                headerRightText.setText("编辑");
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                mine.setImageResource(R.drawable.icon_home_my_hover);
            };break;
            case 4: {
                title.setText("我的二维码");
                headerLeftText.setText("");
                headerLeftImage.setVisibility(View.VISIBLE);
                headerRightText.setVisibility(View.GONE);
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                mine.setImageResource(R.drawable.icon_home_my_hover);
            };break;
        }
    }

    /**
     * 设置所有的底部导航栏的选中状态为normal
     */
    private void setHomeBottomNavNormal() {
        message.setImageResource(R.drawable.icon_home_msg_normal);
        circle.setImageResource(R.drawable.icon_home_campus_normal);
        publish.setImageResource(R.drawable.icon_home_my_publish_normal);
        mine.setImageResource(R.drawable.icon_home_my_normal);
    }

    private void initView() {

        HomeViewPager = (NoScrollViewPager) findViewById(R.id.HomeViewPager);
        message = (ImageView) findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHomeBottomNavNormal();
                message.setImageResource(R.drawable.icon_home_msg_hover);
                HomeViewPager.setCurrentItem(1, true);
            }
        });

        circle = (ImageView) findViewById(R.id.circle);
        circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHomeBottomNavNormal();
                circle.setImageResource(R.drawable.icon_home_campus_hover);
                HomeViewPager.setCurrentItem(0, true);
            }
        });
        publish = (ImageView) findViewById(R.id.publish);
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHomeBottomNavNormal();
                publish.setImageResource(R.drawable.icon_home_my_publish_normal);
                HomeViewPager.setCurrentItem(2, true);
            }
        });
        mine = (ImageView) findViewById(R.id.mine);
        mine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHomeBottomNavNormal();
                mine.setImageResource(R.drawable.icon_home_my_hover);
                HomeViewPager.setCurrentItem(3, true);
            }
        });

        headerRightText = (TextView) findViewById(R.id.header_right_text);
        headerRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPageId == 1) { // 当前页面为消息页
                    Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                }
            }
        });
        headerRightImage = (ImageView) findViewById(R.id.header_right_image);
        headerLeftImage = (ImageView) findViewById(R.id.header_left_image);
        headerLeftText = (TextView) findViewById(R.id.header_left_text);
        title = (TextView) findViewById(R.id.header_title);
        headerLeftImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headerSelect(3);
                HomeViewPager.setCurrentItem(3,true);
            }
        });
    }

}
