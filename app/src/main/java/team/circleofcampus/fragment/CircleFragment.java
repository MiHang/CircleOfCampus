package team.circleofcampus.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lcodecore.tkrefreshlayout.IHeaderView;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.youth.banner.Banner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.Interface.FragmentSwitchListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.DetailCircleActivity;
import team.circleofcampus.adapter.CampusCircleListViewAdapter;
import team.circleofcampus.adapter.SocietyCircleListViewAdapter;
import team.circleofcampus.dao.CampusCircleDao;
import team.circleofcampus.dao.SocietyCircleDao;
import team.circleofcampus.http.CampusCircleRequest;
import team.circleofcampus.http.HttpRequest;
import team.circleofcampus.http.ImageRequest;
import team.circleofcampus.http.SocietyCircleRequest;
import team.circleofcampus.pojo.CampusCircle;
import team.circleofcampus.pojo.SocietyCircle;
import team.circleofcampus.service.SingleThreadService;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.util.StorageUtil;
import team.circleofcampus.view.MyListView;

/**
 * 校园圈
 */
public class CircleFragment extends Fragment {

    private View view;
    private List<Integer> images = new ArrayList<>();//声明数组

    @BindView(R.id.home_campus_circle_list_view)
    protected MyListView campusCircleListView;
    @BindView(R.id.home_society_circle_list_view)
    protected MyListView societyCircleListView;
    @BindView(R.id.circle_refresh_layout)
    protected TwinklingRefreshLayout refreshLayout;

    // 单例线程池
    private ExecutorService singleThreadExecutor;
    private ExecutorService downloadImageSingleThreadExecutor;

    private CampusCircleListViewAdapter campusCircleListViewAdapter;
    private SocietyCircleListViewAdapter societyCircleListViewAdapter;
    private List<CampusCircle> campusCircles = new ArrayList<CampusCircle>();
    private List<SocietyCircle> societyCircles = new ArrayList<SocietyCircle>();

    FragmentSwitchListener switchListener;
    public FragmentSwitchListener getSwitchListener() {
        return switchListener;
    }
    public void setSwitchListener(FragmentSwitchListener switchListener) {
        this.switchListener = switchListener;
    }

    private int userId = 0; // 用户ID
    private boolean isRefreshing = false; // 当前是否正在刷新
    private boolean isResume = true; // 当前Fragment是否可见
    private boolean isShowHint = true;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001: {
                    if (isResume && isShowHint) {
                        Toast.makeText(getContext(), "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    }
                    closeRefreshAnimation();
                } break;
                case 0x0002: { // 校园圈与社团圈数据加载完毕
                    campusCircleListViewAdapter.notifyDataSetChanged();
                    societyCircleListViewAdapter.notifyDataSetChanged();
                    if (isRefreshing) {
                        isRefreshing = false;
                        refreshLayout.finishRefreshing();
                    }
                } break;
                case 0x0003: { // 关闭刷新动画
                    closeRefreshAnimation();
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);

        // 设置下拉刷新部分的HeadView
        SinaRefreshView sinaRefreshView = new SinaRefreshView(getContext());
        sinaRefreshView.setBackgroundResource(R.drawable.bg);
        sinaRefreshView.setTextColor(Color.WHITE);
        sinaRefreshView.setArrowResource(R.drawable.ico_arrow_down);
        ImageView loadView = sinaRefreshView.findViewById(com.lcodecore.tkrefreshlayout.R.id.iv_loading);
        loadView.setImageResource(R.drawable.anim_loading);
        refreshLayout.setHeaderView(sinaRefreshView);

        // 设置下拉刷新控件相关参数
        refreshLayout.setMaxHeadHeight(100);
        refreshLayout.setHeaderHeight(80);
        refreshLayout.setEnableLoadmore(false);

        // 下拉刷新
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                Log.e("tag", "CircleFragment refresh data...");
                isRefreshing = true;
                loadData();
            }
            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {}
        });

        // 轮播图部分
        Banner banner = (Banner) view.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new Lunbotu());
        // 设置图片集合
        images.add(R.drawable.banner);
        images.add(R.drawable.banner1);
        images.add(R.drawable.banner2);
        banner.setImages(images);
        banner.setDelayTime(5000);
        banner.start(); //banner设置方法全部调用完毕时最后调用

        // 加载本地数据
        try {
            CampusCircleDao campusCircleDao = new CampusCircleDao(getContext());
            SocietyCircleDao societyCircleDao = new SocietyCircleDao(getContext());
            List<CampusCircle> campusCircleList = campusCircleDao.queryDataTopNum(3);
            List<SocietyCircle> societyCircleList = societyCircleDao.queryDataTopNum(3);
            if (campusCircleList != null && campusCircleList.size() > 0) {
                campusCircles = campusCircleList;
            }
            if (societyCircleList != null && societyCircleList.size() > 0) {
                societyCircles = societyCircleList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 校园官方公告列表
        campusCircleListViewAdapter = new CampusCircleListViewAdapter(getContext(), campusCircles);
        campusCircleListView.setAdapter(campusCircleListViewAdapter);
        campusCircleListView.setListViewHeightBasedOnChildren();
        campusCircleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("tag", "campus circle id = " + id);
                Intent intent = new Intent(getContext(), DetailCircleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", (int)id);
                bundle.putBoolean("isCampusCircle", true);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });

        // 社团公告列表
        societyCircleListViewAdapter = new SocietyCircleListViewAdapter(getContext(), societyCircles);
        societyCircleListView.setAdapter(societyCircleListViewAdapter);
        societyCircleListView.setListViewHeightBasedOnChildren();
        societyCircleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("tag", "society circle id = " + id);
                Intent intent = new Intent(getContext(), DetailCircleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", (int)id);
                bundle.putBoolean("isCampusCircle", false);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return view;
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

    @Override
    public void onDestroyView() {
        super .onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    /**
     * 关闭刷新动画
     */
    public void closeRefreshAnimation() {
        if (isRefreshing && refreshLayout != null) {
            isRefreshing = false;
            isShowHint = false;
            refreshLayout.finishRefreshing();
        }
    }

    /**
     * 数据加载
     */
    public void loadData() {
        Log.e("tag", "CircleFragment reload data...");
        isShowHint = true;
        // 图片下载单例线程池
        downloadImageSingleThreadExecutor = SingleThreadService.newSingleThreadExecutor();
        if (singleThreadExecutor == null) { // 数据加载单例线程池
            singleThreadExecutor = SingleThreadService.getSingleThreadPool();
        }
        singleThreadExecutor.execute(loadingTop3CampusCircle()); // 加载3条校园圈数据
        singleThreadExecutor.execute(loadingTop3SocietyCircle()); // 加载3条社团圈数据
    }

    /**
     * 数据加载,同时设置用户ID
     */
    public void loadData(int userId) {
        this.userId = userId; // 用户ID
        loadData();
    }

    /**
     * 加载3条社团圈数据
     * @return
     */
    private Runnable loadingTop3SocietyCircle() {
        return new Runnable() {
            @Override
            public void run() {
                int size = SharedPreferencesUtil.getSocietyCircleCount(getContext());
                String result = SocietyCircleRequest.getSocietyCircle(userId, 0, (size >= 3 ? 3 : size));
                if (null != result) {
                    try {
                        JSONArray jsonArr = new JSONArray(result);
                        Gson gson = new Gson();
                        JSONObject json = new JSONObject(jsonArr.getString(0));
                        if (!json.has("result") && jsonArr.length() > 0) {

                            // 清除本地相关缓存数据，重新加载
                            SocietyCircleDao societyCircleDao = new SocietyCircleDao(getContext());
                            if(societyCircleDao.deleteForAllData() == 1) { // 清空本地数据库数据
                                Log.e("tag", "清空本地数据库社团公告表数据");
                            }

                            // 将数据保存到本地数据库
                            for (int i =0; i < jsonArr.length(); i ++) {
                                SocietyCircle societyCircle = gson.fromJson(jsonArr.getString(i), SocietyCircle.class);
                                societyCircleDao.insertData(societyCircle);
                                Log.e("tag", "society circle write local sqlite ：" +
                                        "id = " + societyCircle.getId() + "; title = " + societyCircle.getTitle());

                                // 获取要下载的图片的URL
                                String str = societyCircle.getImagesUrl();
                                if (str != null && !str.equals("")) {
                                    JSONArray jsonArray = new JSONArray(str);
                                    for (int j = 0; j < jsonArray.length(); j ++) {
                                        JSONObject jsonObject = new JSONObject(jsonArray.getString(j));
                                        // 下载校园圈图片
                                        downloadImageSingleThreadExecutor.execute(ImageRequest.downloadImage(jsonObject.getString("url")));
                                    }
                                }

                                societyCircles.add(i, societyCircle);
                                if (i+1 < societyCircles.size()) {
                                    societyCircles.remove(i+1);
                                }
                            }
                            downloadImageSingleThreadExecutor.shutdown();
                            while (true) {
                                if(downloadImageSingleThreadExecutor.isTerminated()){
                                    Log.e("tag", "校园圈与社团圈数据加载完毕");
                                    handler.sendEmptyMessage(0x0002);
                                    break;
                                }
                                Thread.sleep(1000);
                            }
                        } else {
                            handler.sendEmptyMessage(0x0003);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0001);
                }
            }
        };
    }

    /**
     * 加载3条校园圈数据
     * @return
     */
    private Runnable loadingTop3CampusCircle() {
        return new Runnable() {
            @Override
            public void run() {
                int size = SharedPreferencesUtil.getCampusCircleCount(getContext());
                String result = CampusCircleRequest.getCampusCircle(userId, 0, (size >= 3 ? 3 : size));
                if (null != result) {
                    try {
                        JSONArray jsonArr = new JSONArray(result);
                        Gson gson = new Gson();
                        JSONObject json = new JSONObject(jsonArr.getString(0));
                        if (!json.has("result") && jsonArr.length() > 0) {

                            // 清除本地相关缓存数据，重新加载
                            CampusCircleDao campusCircleDao = new CampusCircleDao(getContext());
                            if(campusCircleDao.deleteForAllData() == 1) { // 清空本地数据库数据
                                Log.e("tag", "清空本地数据库校园公告表数据");
                            }

                            // 将数据保存到本地数据库
                            for (int i =0; i < jsonArr.length(); i ++) {
                                CampusCircle campusCircle = gson.fromJson(jsonArr.getString(i), CampusCircle.class);

                                campusCircleDao.insertData(campusCircle);
                                Log.e("tag", "campus circle write local sqlite ：" +
                                        "id = " + campusCircle.getId() + "; title = " + campusCircle.getTitle());

                                // 获取要下载的图片的URL
                                String str = campusCircle.getImagesUrl();
                                if (str != null && !str.equals("")) {
                                    JSONArray jsonArray = new JSONArray(str);
                                    for (int j = 0; j < jsonArray.length(); j ++) {
                                        JSONObject jsonObject = new JSONObject(jsonArray.getString(j));
                                        // 下载校园圈图片
                                        downloadImageSingleThreadExecutor.execute(ImageRequest.downloadImage(jsonObject.getString("url")));
                                    }
                                }

                                campusCircles.add(i, campusCircle);
                                if (i+1 < campusCircles.size()) {
                                    campusCircles.remove(i+1);
                                }
                            }
                        } else {
                            handler.sendEmptyMessage(0x0003);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0001);
                }
            }
        };
    }

    /**
     * 更多校园官方公告点击事件
     * @param view
     */
    @OnClick(R.id.home_campus_notice_more)
    protected void onClickMoreCampusNotice(final View view) {
        if (switchListener!=null){
            switchListener.displayThisFragment(5, true);
        }
    }

    /**
     * 更多社团公告点击事件
     * @param view
     */
    @OnClick(R.id.home_corporation_notice_more)
    protected void onClickMoreSocietyNotice(final View view) {
        if (switchListener!=null){
            switchListener.displayThisFragment(6, true);
        }
    }

}
