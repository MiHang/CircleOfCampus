package coc.team.home.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import coc.team.home.Interface.OnItemClickListener;
import coc.team.home.NoScrollViewPager;
import coc.team.home.R;
import coc.team.home.adapter.MyFragmentAdapter;
import coc.team.home.fragment.ContactFragment;
import coc.team.home.fragment.UserInfoFragment;

/**
 * 好友列表
 */
public class ContactActivity extends AppCompatActivity {

    MyFragmentAdapter adapter;
    List<Fragment> data = new ArrayList<>();
    private TextView header_left_text;
    private ImageView header_left_image;
    private TextView header_title;
    private TextView header_right_text;
    private ImageView header_right_image;
    private NoScrollViewPager MyViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();
        header_title.setText("好友列表");
        header_right_text.setText("添加好友");
        header_left_image.setVisibility(View.GONE);
        ContactFragment contactFragment=new ContactFragment();
        contactFragment.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                header_title.setText("好友资料");
                header_right_text.setVisibility(View.GONE);
                MyViewPager.setCurrentItem(1, true);
                header_left_image.setVisibility(View.GONE);
                //延时2毫秒刷新
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        header_left_image.setVisibility(View.VISIBLE);
                    }
                }, 200);

            }
        });
        header_left_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                header_title.setText("好友列表");
                header_right_text.setVisibility(View.VISIBLE);
                header_left_image.setVisibility(View.VISIBLE);
                MyViewPager.setCurrentItem(0, true);
            }
        });
        data.add(contactFragment);
        data.add(new UserInfoFragment());
        adapter = new MyFragmentAdapter(getSupportFragmentManager(), data);
        MyViewPager.setAdapter(adapter);
        header_right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ContactActivity.this, "点击", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 隐藏与显示控件 —标题栏右侧"好友"
     *
     * @param position
     */
    public void displayUserButton(int position) {
        if (position == 1) {
            header_right_text.setVisibility(View.VISIBLE);
        } else {
            header_right_text.setVisibility(View.GONE);
        }
    }


    private void initView() {
        header_left_text = (TextView) findViewById(R.id.header_left_text);
        header_left_image = (ImageView) findViewById(R.id.header_left_image);
        header_title = (TextView) findViewById(R.id.header_title);
        header_right_text = (TextView) findViewById(R.id.header_right_text);
        header_right_image = (ImageView) findViewById(R.id.header_right_image);
        MyViewPager = (NoScrollViewPager) findViewById(R.id.MyViewPager);
    }
}

