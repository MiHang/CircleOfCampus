package team.circleofcampus.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import team.circleofcampus.R;
import team.circleofcampus.adapter.GuideViewPagerAdapter;
import team.circleofcampus.util.SharedPreferencesUtil;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.guide_view_pager)
    protected ViewPager guideViewPager;
    @BindView(R.id.item_guide_flag_root)
    protected LinearLayout flagRoot;

    // 存储引导页面
    private ArrayList<View> views = new ArrayList<View>();
    // 存储flag圆点标记
    private ImageView[] flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        boolean isFirstRun = SharedPreferencesUtil.isFirstRun(MainActivity.this);
        if (isFirstRun) {
            initGuide();
        } else {
            int uId = SharedPreferencesUtil.getUID(MainActivity.this);
            long prevLoginTime = SharedPreferencesUtil.getLoginTime(MainActivity.this);

            // id为0或者距上传使用时间大于一周，则重新登陆
            if (uId == 0 || System.currentTimeMillis() - prevLoginTime > 604800000) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    /**
     * 初始化引导页面
     */
    private void initGuide() {
        // 添加引导页
        View guide1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_guide_pager, null);
        ((ImageView)guide1.findViewById(R.id.item_guide_image_view)).setImageResource(R.drawable.guide_1);
        View guide2 = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_guide_pager, null);
        ((ImageView)guide2.findViewById(R.id.item_guide_image_view)).setImageResource(R.drawable.guide_2);
        View guide3 = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_guide_pager_last, null);
        ((ImageView)guide3.findViewById(R.id.item_guide_image_view)).setImageResource(R.drawable.guide_3);
        ((Button)guide3.findViewById(R.id.item_guide_last_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferencesUtil.setFirstRun(MainActivity.this, false);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                finish();
            }
        });

        views.add(guide1);
        views.add(guide2);
        views.add(guide3);

        // 添加页面标记
        flags = new ImageView[views.size()];
        for (int i = 0; i < flags.length; i ++) {
            ImageView imageView = new ImageView(MainActivity.this);
            imageView.setImageResource(R.drawable.icon_dot_normal);
            imageView.setPadding(10,10,10,10);

            //设置imageView在LinearLayout的CENTER_VERTICAL（中间垂直）
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            imageView.setLayoutParams(params);

            flags[i] = imageView;
            flagRoot.addView(flags[i]);
        }
        flags[0].setImageResource(R.drawable.icon_dot_hover);

        GuideViewPagerAdapter guideViewPagerAdapter = new GuideViewPagerAdapter(views);
        guideViewPager.setAdapter(guideViewPagerAdapter);

        guideViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                for (ImageView imageView : flags) {
                    imageView.setImageResource(R.drawable.icon_dot_normal);
                }
                flags[position].setImageResource(R.drawable.icon_dot_hover);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

}
