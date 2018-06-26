package team.circleofcampus.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;
import team.circleofcampus.http.CampusCircleRequest;
import team.circleofcampus.http.HttpRequest;
import team.circleofcampus.http.SocietyCircleRequest;
import team.circleofcampus.model.CircleDetail;
import team.circleofcampus.service.SingleThreadService;
import team.circleofcampus.util.StorageUtil;

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

    private int id = 0;
    private boolean isCampusCircle;
    private boolean isResume = true; // 当前Activity是否可见

    private CircleDetail circleDetail; // 数据

    // 单例线程池
    private ExecutorService singleThreadExecutor;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001 : {
                    if (isResume) {
                        Toast.makeText(DetailCircleActivity.this, "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    }
                } break;
                case 0x0002 : {
                    if (isResume) {
                        Toast.makeText(DetailCircleActivity.this, "出错了(;′⌒`)", Toast.LENGTH_SHORT).show();
                    }
                } break;
                case 0x0003 : { // 加载成功
                    update();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_circle);
        ButterKnife.bind(this);
        headerTitle.setText("公告详情");

        singleThreadExecutor = SingleThreadService.getSingleThreadPool();

        // 获取参数
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        isCampusCircle = bundle.getBoolean("isCampusCircle");
        if (id != 0) {
            singleThreadExecutor.execute(loadingCircleDetail());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
    }

    /**
     * 更新界面
     */
    private void update() {
        if (circleDetail != null) {
            // 加载相关图片
            loadImage(circleIcon, HttpRequest.URL + circleDetail.getPublisherIco());
            try {
                JSONArray jsonArray = new JSONArray(circleDetail.getImagesUrl());
                if (jsonArray.length() > 0) {
                    JSONObject json = new JSONObject(jsonArray.getString(0));
                    loadImage(circleImage, HttpRequest.URL + json.getString("url"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // 相关数据
            circleName.setText(circleDetail.getPublisher());
            circlePublishTime.setText(circleDetail.getPublishTime());
            circleTitle.setText(circleDetail.getTitle());
            circleContent.setText(circleDetail.getContent());
        }
    }

    /**
     * 异步加载图片
     * @param imageView
     * @param url
     */
    private void loadImage(final ImageView imageView, final String url) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(DetailCircleActivity.this)
                                .load(url)
                                .placeholder(R.drawable.img_loading_failed)
                                .error(R.drawable.img_loading_failed)
                                .into(imageView);
                    }
                });
            }
        });
    }

    /**
     * 加载公告详情数据
     * @return
     */
    private Runnable loadingCircleDetail() {
        return new Runnable() {
            @Override
            public void run() {
                String result;
                if (isCampusCircle) {
                    result = CampusCircleRequest.getCampusCircleDetail(id);
                } else {
                    result = SocietyCircleRequest.getSocietyCircleDetail(id);
                }
                try {
                    if (null != result) {
                        JSONObject json = new JSONObject(result);
                        if (!json.has("result")) {
                            circleDetail = new Gson().fromJson(json.toString(), CircleDetail.class);
                            handler.sendEmptyMessage(0x0003);
                        } else { // 出错了
                            handler.sendEmptyMessage(0x0002);
                        }
                    } else {
                        handler.sendEmptyMessage(0x0001);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
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
