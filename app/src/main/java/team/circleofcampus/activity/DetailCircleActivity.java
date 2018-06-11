package team.circleofcampus.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;

/**
 * 校园官方公告和社团公告的详情页
 */
public class DetailCircleActivity extends AppCompatActivity {

    @BindView(R.id.header_title)
    protected TextView headerTitle;
    @BindView(R.id.detail_circle_icon)
    protected ImageView circleIcon;
    @BindView(R.id.detail_circle_name)
    protected TextView circleName;
    @BindView(R.id.detail_circle_publish_time)
    protected TextView circlePublishTime;
    @BindView(R.id.detail_circle_title)
    protected TextView circleTitle;
    @BindView(R.id.detail_circle_content)
    protected TextView circleContent;
    @BindView(R.id.detail_circle_image_view)
    protected ImageView circleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_circle);
        ButterKnife.bind(this);
        headerTitle.setText("公告详情");

        Glide.with(DetailCircleActivity.this)
                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1528728960240&di=933bab110301326556e2345d5f057753" +
                        "&imgtype=0&src=http%3A%2F%2Fa.hiphotos.baidu.com%2Fimage%2Fpic%2Fitem%2F0df3d7ca7bcb0a46aa1f61a36763f6246b60af6f.jpg")
                .placeholder(R.drawable.img_loading_failed)
                .error(R.drawable.img_loading_failed)
                .into(circleImage);
    }

    /**
     * 标题栏返回按钮图标点击事件
     * @param view
     */
    @OnClick(R.id.header_left_image)
    public void onClickBackIcon(View view) {
        onBackPressed();
    }

}
