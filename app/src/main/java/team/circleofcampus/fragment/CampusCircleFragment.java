package team.circleofcampus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import team.circleofcampus.R;
import team.circleofcampus.activity.DetailCircleActivity;
import team.circleofcampus.adapter.MoreCircleListAdapter;
import team.circleofcampus.dao.CampusCircleDao;
import team.circleofcampus.http.CampusCircleRequest;
import team.circleofcampus.model.Circle;
import team.circleofcampus.service.SingleThreadService;
import team.circleofcampus.util.DateUtil;
import team.circleofcampus.util.SharedPreferencesUtil;

public class CampusCircleFragment extends Fragment {

    private View view;
    @BindView(R.id.more_campus_circle_list)
    protected ListView listView;
    private MoreCircleListAdapter moreCircleListAdapter;
    private ArrayList<Circle> circles = new ArrayList<Circle>();

    private int userId = 0; // 用户ID
    private LoadingDialog loadingDialog;

    // 单例线程池
    private ExecutorService singleThreadExecutor;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001:{ // 加载失败
                    if (loadingDialog != null) loadingDialog.loadFailed();
                } break;
                case 0x0002:{ // 加载成功
                    if (loadingDialog != null) loadingDialog.loadSuccess();
                    moreCircleListAdapter.notifyDataSetChanged();
                } break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_more_circle, null);
        ButterKnife.bind(this, view);

        // 用户ID
        userId = SharedPreferencesUtil.getUID(getContext());

        moreCircleListAdapter = new MoreCircleListAdapter(getContext(), circles);
        listView.setAdapter(moreCircleListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (id != -1) {
                    Intent intent = new Intent(getContext(), DetailCircleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", (int)id);
                    bundle.putBoolean("isCampusCircle", true);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (circles != null) {
            circles.clear();
            moreCircleListAdapter.notifyDataSetChanged();
        }
        loadData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) loadingDialog.close();
    }

    /**
     * 数据加载
     */
    public void loadData() {
        Log.e("tag", "CampusCircleFragment load data...");
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.setLoadingText("加载中")
                .setSuccessText("加载成功")
                .setFailedText("加载失败")
                .closeSuccessAnim()
                .setShowTime(1000)
                .setInterceptBack(false)
                .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                .show();
        if (singleThreadExecutor == null) { // 数据加载单例线程池
            singleThreadExecutor = SingleThreadService.getSingleThreadPool();
        }
        singleThreadExecutor.execute(loadingCampusCircle()); // 加载校园公告数据
    }

    /**
     * 加载校园公告数据
     * @return
     */
    private Runnable loadingCampusCircle() {
        return new Runnable() {
            @Override
            public void run() {
                int size = SharedPreferencesUtil.getCampusCircleCount(getContext());
                String result = CampusCircleRequest.getCampusCircle(userId, 0, size);
                if (null != result) {
                    try {
                        JSONArray jsonArr = new JSONArray(result);
                        JSONObject json = new JSONObject(jsonArr.getString(0));
                        if (!json.has("result") && jsonArr.length() > 0) {
                            for (int i =0; i < jsonArr.length(); i ++) {
                                json = new JSONObject(jsonArr.getString(i));
                                Circle circle = new Circle();
                                circle.setId(json.getInt("id"));
                                circle.setTitle(json.getString("title"));
                                circle.setImagesUrl(json.getString("imagesUrl"));
                                circle.setPublishTime(json.getString("publishTime"));
                                circle.setActivityTime(json.getString("activityTime"));
                                circle.setVenue(json.getString("venue"));
                                circles.add(circle);
                            }

                            // 获取当前时间
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String nowDate = sdf.format(new Date());
                            String[] timeArr = nowDate.split(" ");

                            // 添加时间戳
//                            for (int i = 0; i < circles.size() - 1; i++) {
//                                if (i == 0 && circles.get(i).getId() != -1) { // 第一条公告
//                                    String[] timeTempArr = circles.get(i).getPublishTime().split(" ");
//                                    if (timeArr[0].equals(timeTempArr[0])) { // 今天发布的公告
//                                        String[] temp = timeArr[1].split(":");
//                                        Circle circle = new Circle();
//                                        circle.setId(-1);
//                                        circle.setPublishTime(timeTempArr[1]);
//                                        circles.add(i, circle);
//                                    } else {
//                                        Circle circle = new Circle();
//                                        circle.setId(-1);
//                                        circle.setPublishTime(timeArr[0]);
//                                        circles.add(i, circle);
//                                    }
//                                }
//                            }
                            handler.sendEmptyMessage(0x0002);
                        } else {
                            handler.sendEmptyMessage(0x0001);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0001);
                }
            }
        };
    }

}
