package team.circleofcampus.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.BroadcastReceiver.MyBroadcastReceiver;
import team.circleofcampus.Interface.FragmentSwitchListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.MyFragmentAdapter;
import team.circleofcampus.background.MyService;
import team.circleofcampus.fragment.CampusCircleFragment;
import team.circleofcampus.fragment.CircleFragment;
import team.circleofcampus.fragment.MessageFragment;
import team.circleofcampus.fragment.MyPublishFragment;
import team.circleofcampus.fragment.MineFragment;
import team.circleofcampus.fragment.QRFragment;
import team.circleofcampus.fragment.SocietyCircleFragment;
import team.circleofcampus.model.SocietyCircle;
import team.circleofcampus.view.NoScrollViewPager;


/**
 * 主界面
 */
public class HomeActivity extends AppCompatActivity {

    MyFragmentAdapter adapter;
    @BindView(R.id.HomeViewPager)
    protected NoScrollViewPager HomeViewPager;
    @BindView(R.id.message)
    protected ImageView message;
    @BindView(R.id.circle)
    protected ImageView circle;
    @BindView(R.id.publish)
    protected ImageView publish;
    @BindView(R.id.mine)
    protected ImageView mine;
    @BindView(R.id.header_right_text)
    protected TextView headerRightText;
    @BindView(R.id.header_right_image)
    protected ImageView headerRightImage;
    @BindView(R.id.header_left_text)
    protected TextView headerLeftText;
    @BindView(R.id.header_left_image)
    protected ImageView headerLeftImage;
    @BindView(R.id.header_title)
    protected TextView title;

    private boolean isEditMine = false; // 我的页面是否处于编辑状态
    private String userName;
    private MineFragment mineFragment; // 我的页面

    List<Fragment> data = new ArrayList<>();
    private int selectedPageId = 0;

    @Override
    public void onBackPressed() {
        if (selectedPageId == 4) {
            headerSelect(3);
            HomeViewPager.setCurrentItem(3,true);
        } else if (selectedPageId == 5 || selectedPageId == 6) {
            headerSelect(0);
            HomeViewPager.setCurrentItem(0,false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this); // 绑定Activity
        headerSelect(0);

        // 开启后台服务
        MyBroadcastReceiver myBro = new MyBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("coc.team.home.activity");
        registerReceiver(myBro, intentFilter);
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("send", "jayevip@163.com");
        startService(intent);

        // 校园圈
        CircleFragment circleFragment = new CircleFragment();
        circleFragment.setSwitchListener(new FragmentSwitchListener() {
            @Override
            public void displayThisFragment(boolean display) {}
            @Override
            public void displayThisFragment(int currentId, boolean display) {
                headerSelect(currentId);
                HomeViewPager.setCurrentItem(currentId,false);
            }
        });
        data.add(circleFragment);

        // 消息
        MessageFragment bFragment = new MessageFragment();
        bFragment.bind(myBro);
        data.add(bFragment);

        // 我的发布
        data.add(new MyPublishFragment());

        // 我的
        mineFragment = new MineFragment();
        mineFragment.setSwitchListener(new FragmentSwitchListener() {
            @Override
            public void displayThisFragment(boolean display) {
                headerSelect(4);
                HomeViewPager.setCurrentItem(4,true);
            }
            @Override
            public void displayThisFragment(int currentId, boolean display) {}
        });
        data.add(mineFragment);

        // 我的二维码
        QRFragment qrFragment = new QRFragment();
        qrFragment.setAccount("assaas");
        data.add(qrFragment);

        // 更多校园官方公告
        CampusCircleFragment campusCircleFragment = new CampusCircleFragment();
        data.add(campusCircleFragment);
        // 更多社团公告
        SocietyCircleFragment societyCircleFragment = new SocietyCircleFragment();
        data.add(societyCircleFragment);

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
                headerRightImage.setImageResource(R.drawable.icon_search_white);
                headerRightImage.setVisibility(View.VISIBLE);
                setHomeBottomNavNormal();
                circle.setImageResource(R.drawable.icon_home_campus_hover);
                isEditMine = false;
                if (null != mineFragment) {
                    mineFragment.getUserName().setText(userName);
                    mineFragment.getUserName().setEnabled(false);
                    mineFragment.getGenderRadioGroup().setVisibility(View.GONE);
                    mineFragment.getSex().setVisibility(View.VISIBLE);
                }
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
                isEditMine = false;
                if (null != mineFragment) {
                    mineFragment.getUserName().setText(userName);
                    mineFragment.getUserName().setEnabled(false);
                    mineFragment.getGenderRadioGroup().setVisibility(View.GONE);
                    mineFragment.getSex().setVisibility(View.VISIBLE);
                }
            };break;
            case 2: {
                title.setText("我的发布");
                headerLeftText.setText("");
                headerLeftImage.setVisibility(View.GONE);
                headerRightText.setText("");
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                publish.setImageResource(R.drawable.icon_home_my_publish_hover);
                isEditMine = false;
                if (null != mineFragment) {
                    mineFragment.getUserName().setText(userName);
                    mineFragment.getUserName().setEnabled(false);
                    mineFragment.getGenderRadioGroup().setVisibility(View.GONE);
                    mineFragment.getSex().setVisibility(View.VISIBLE);
                }
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
                headerRightText.setText("");
                headerLeftImage.setVisibility(View.VISIBLE);
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                mine.setImageResource(R.drawable.icon_home_my_hover);
                isEditMine = false;
                if (null != mineFragment) {
                    mineFragment.getUserName().setText(userName);
                    mineFragment.getUserName().setEnabled(false);
                    mineFragment.getGenderRadioGroup().setVisibility(View.GONE);
                    mineFragment.getSex().setVisibility(View.VISIBLE);
                }
            };break;
            case 5: {
                title.setText("校园官方公告");
                headerLeftText.setText("");
                headerRightText.setText("");
                headerLeftImage.setVisibility(View.VISIBLE);
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                circle.setImageResource(R.drawable.icon_home_campus_hover);
                isEditMine = false;
                if (null != mineFragment) {
                    mineFragment.getUserName().setText(userName);
                    mineFragment.getUserName().setEnabled(false);
                    mineFragment.getGenderRadioGroup().setVisibility(View.GONE);
                    mineFragment.getSex().setVisibility(View.VISIBLE);
                }
            };break;
            case 6: {
                title.setText("社团公告");
                headerLeftText.setText("");
                headerRightText.setText("");
                headerLeftImage.setVisibility(View.VISIBLE);
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                circle.setImageResource(R.drawable.icon_home_campus_hover);
                isEditMine = false;
                if (null != mineFragment) {
                    mineFragment.getUserName().setText(userName);
                    mineFragment.getUserName().setEnabled(false);
                    mineFragment.getGenderRadioGroup().setVisibility(View.GONE);
                    mineFragment.getSex().setVisibility(View.VISIBLE);
                }
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

    /**
     * 标题栏右侧文字点击事件
     * @param view
     */
    @OnClick(R.id.header_right_text)
    protected void onClickRightText(final View view) {
        if (selectedPageId == 1) { // 当前页面为消息页
            Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        } else if (selectedPageId == 3) { // 我的页面
            if (!isEditMine) {
                isEditMine = true;
                headerRightText.setText("保存");
                userName = mineFragment.getUserName().getText().toString();
                mineFragment.getUserName().setEnabled(true);
                mineFragment.getSex().setVisibility(View.GONE);
                mineFragment.getGenderRadioGroup().setVisibility(View.VISIBLE);
                userName = mineFragment.getUserName().getText().toString();
            } else {
                isEditMine = false;
                headerRightText.setText("编辑");
                userName = mineFragment.getUserName().getText().toString();
                if (mineFragment.getMaleRb().isChecked()) {
                    mineFragment.getSex().setText("男");
                } else if (mineFragment.getFemaleRb().isChecked()) {
                    mineFragment.getSex().setText("女");
                }
                mineFragment.getUserName().setEnabled(false);
                mineFragment.getGenderRadioGroup().setVisibility(View.GONE);
                mineFragment.getSex().setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 标题栏返回图标点击事件
     * @param view
     */
    @OnClick(R.id.header_left_image)
    protected void onClickBackIcon(final View view) {
        if (selectedPageId == 4) { // 我的二维码返回到我的页面
            headerSelect(3);
            HomeViewPager.setCurrentItem(3,true);
        } else if (selectedPageId == 5 || selectedPageId == 6) { // 校园官方公告和社团公告返回到校园圈页面
            headerSelect(0);
            HomeViewPager.setCurrentItem(0,true);
        }
    }

    /**
     * 底部导航栏点击事件
     * @param view
     */
    @OnClick({R.id.circle, R.id.message, R.id.publish, R.id.mine})
    protected void onClickNav(final View view) {

        // 缩放动画 - 将选中状态的图标缩小1/2（中心缩放）
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f,
                view.getWidth() / 2, view.getHeight() / 2);
        scaleAnimation.setDuration(100);
        view.startAnimation(scaleAnimation);

        switch (view.getId()) {
            case R.id.circle:{
                setHomeBottomNavNormal();
                circle.setImageResource(R.drawable.icon_home_campus_hover);
                HomeViewPager.setCurrentItem(0, true);
            };break;
            case R.id.message:{
                setHomeBottomNavNormal();
                message.setImageResource(R.drawable.icon_home_msg_hover);
                HomeViewPager.setCurrentItem(1, true);
            };break;
            case R.id.publish:{
                setHomeBottomNavNormal();
                publish.setImageResource(R.drawable.icon_home_my_publish_normal);
                HomeViewPager.setCurrentItem(2, true);
            };break;
            case R.id.mine:{
                setHomeBottomNavNormal();
                mine.setImageResource(R.drawable.icon_home_my_hover);
                HomeViewPager.setCurrentItem(3, true);
            };break;
        }
    }

}
