package team.circleofcampus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import team.circleofcampus.R;
import team.circleofcampus.adapter.MyFragmentAdapter;
import team.circleofcampus.fragment.AddFriendsFragment;
import team.circleofcampus.fragment.ContactFragment;
import team.circleofcampus.fragment.UserInfoFragment;
import team.circleofcampus.view.FontTextView;
import team.circleofcampus.view.NoScrollViewPager;

/**
 * 好友列表
 */
public class ContactActivity extends AppCompatActivity {

    MyFragmentAdapter adapter;
   public List<Fragment> data = new ArrayList<>();
    public FontTextView header_left_text;
    public ImageView header_left_image;
    public FontTextView header_title;
    public FontTextView header_right_text;
    public ImageView header_right_image;
    public NoScrollViewPager MyViewPager;
    UserInfoFragment userInfoFragment=new UserInfoFragment();
    public  ContactFragment contactFragment=new ContactFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();
        header_title.setText("好友列表");
        header_right_text.setText("添加好友");
//        header_left_image.setVisibility(View.GONE);



        //返回点击监听
        header_left_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(MyViewPager.getCurrentItem()==1){
                  onBackPressed();
                }else {//好友资料
                    header_title.setText("好友列表");
                    header_right_text.setVisibility(View.VISIBLE);
                    MyViewPager.setCurrentItem(1, true);
                }
            }
        });
        data.add(new AddFriendsFragment());
        data.add(contactFragment);
        data.add(userInfoFragment);
        adapter = new MyFragmentAdapter(getSupportFragmentManager(), data);
        MyViewPager.setAdapter(adapter);
        MyViewPager.setCurrentItem(1);
        //添加好友点击监听
        header_right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                header_title.setText("好友搜索");
                header_left_image.setVisibility(View.VISIBLE);
                header_right_text.setVisibility(View.GONE);
                MyViewPager.setCurrentItem(0, true);
            }
        });

    }

    @Override
    public void onBackPressed() {
     if (MyViewPager.getCurrentItem()==1){
              super.onBackPressed();
        }else {
         header_title.setText("好友列表");
         header_right_text.setVisibility(View.VISIBLE);
         header_left_image.setVisibility(View.GONE);
         MyViewPager.setCurrentItem(1, true);
        }
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
        header_left_text = (FontTextView) findViewById(R.id.header_left_text);
        header_left_image = (ImageView) findViewById(R.id.header_left_image);
        header_title = (FontTextView) findViewById(R.id.header_title);
        header_right_text = (FontTextView) findViewById(R.id.header_right_text);
        header_right_image = (ImageView) findViewById(R.id.header_right_image);
        MyViewPager = (NoScrollViewPager) findViewById(R.id.MyViewPager);
    }
}

