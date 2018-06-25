package team.circleofcampus.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import team.circleofcampus.R;
import team.circleofcampus.activity.HomeActivity;
import team.circleofcampus.adapter.MyPublishRecyclerAdapter;
import team.circleofcampus.dao.CampusCircleDao;
import team.circleofcampus.dao.MyPublishSocietyCircleDao;
import team.circleofcampus.dao.SocietyCircleDao;
import team.circleofcampus.http.ImageRequest;
import team.circleofcampus.http.SocietyCircleRequest;
import team.circleofcampus.pojo.CampusCircle;
import team.circleofcampus.pojo.MyPublishSocietyCircle;
import team.circleofcampus.pojo.SocietyCircle;
import team.circleofcampus.service.SingleThreadService;
import team.circleofcampus.util.SharedPreferencesUtil;

/**
 * 我的发布页面, 已审核的fragment
 */
public class AuditedFragment extends Fragment {

    private View view;
    @BindView(R.id.my_publish_recycler_view)
    protected RecyclerView recyclerView;
    private MyPublishRecyclerAdapter myPublishRecyclerAdapter;
    private List<MyPublishSocietyCircle> myPublishSocietyCircles = new ArrayList<MyPublishSocietyCircle>();

    // 单例线程池
    private ExecutorService singleThreadExecutor;
    private ExecutorService downloadImageSingleThreadExecutor;

    private LoadingDialog loadingDialog; // 加载框

    private int userId = 0; // 用户ID
    private boolean isResume = true; // 当前Fragment是否可见
    private boolean isLoaded = false; // 数据是否加载完成

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001 : {
                    if (isResume) {
                        Toast.makeText(getContext(), "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    }
                } break;
                case 0x0002 : { // 数据加载完成
                    myPublishRecyclerAdapter.notifyDataSetChanged();
                    isLoaded = true;
                    if (isLoaded && loadingDialog != null) {
                        loadingDialog.loadSuccess();
                    }
                } break;
                case 0x0003 : { // 数据加载失败
                    isLoaded = true;
                    if (isLoaded && loadingDialog != null) {
                        loadingDialog.loadFailed();
                    }
                } break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_audited, null);
        ButterKnife.bind(this, view);

        // 获取用户ID
        userId = SharedPreferencesUtil.getUID(getContext());

        // 加载本地数据
        try {
            MyPublishSocietyCircleDao myPublishSocietyCircleDao = new MyPublishSocietyCircleDao(getContext());
            List<MyPublishSocietyCircle> myPublishSocietyCircleList = myPublishSocietyCircleDao.queryData();

            if (myPublishSocietyCircleList != null && myPublishSocietyCircleList.size() > 0) {
                myPublishSocietyCircles = myPublishSocietyCircleList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 设置recycler适配器
        myPublishRecyclerAdapter = new MyPublishRecyclerAdapter(myPublishSocietyCircles);
        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置adapter
        recyclerView.setAdapter(myPublishRecyclerAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        loadData(); // 加载数据
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isPublishedNewCircle = SharedPreferencesUtil.isPublishedNewCircle(getContext());
        if (isPublishedNewCircle) {
            isLoaded = false;
            SharedPreferencesUtil.setPublishedNewCircle(getContext(), false);
            loadData(); // 加载数据
        } else if (!isLoaded) { // 显示加载对话框
            loadingDialog = new LoadingDialog(getContext());
            loadingDialog.setLoadingText("数据加载中")
                    .setSuccessText("加载成功")
                    .setFailedText("加载失败")
                    .closeSuccessAnim()
                    .closeFailedAnim()
                    .setShowTime(1000)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null) {
            loadingDialog.close();
        }
    }

    /**
     * 加载数据
     */
    public void loadData() {
        Log.e("tag", "AuditedFragment reload data...");
        isLoaded = false;
        // 图片下载单例线程池
        downloadImageSingleThreadExecutor = SingleThreadService.newSingleThreadExecutor();
        if (singleThreadExecutor == null) { // 数据加载单例线程池
            singleThreadExecutor = SingleThreadService.getSingleThreadPool();
        }
        singleThreadExecutor.execute(loadingMyPublishSocietyCircle()); // 加载此用户发布的已通过审核的社团公告
    }

    /**
     * 数据加载,并设置id
     */
    public void loadData(int userId) {
        this.userId = userId;
        loadData();
    }

    /**
     * 加载我发布过的社团圈信息
     */
    private Runnable loadingMyPublishSocietyCircle() {
        return new Runnable() {
            @Override
            public void run() {
                int size = SharedPreferencesUtil.getMyPublishSocietyCircleCount(getContext());
                String result = SocietyCircleRequest.getMyPublishSocietyCircle(userId, 0, size);
                if (null != result) {
                    try {
                        JSONArray jsonArr = new JSONArray(result);
                        Gson gson = new Gson();
                        JSONObject json = new JSONObject(jsonArr.getString(0));
                        if (!json.has("result") && jsonArr.length() > 0) {

                            // 清除本地相关缓存数据，重新加载
                            MyPublishSocietyCircleDao myPublishSocietyCircleDao = new MyPublishSocietyCircleDao(getContext());
                            myPublishSocietyCircleDao.deleteForAllData(); // 清空本地数据库数据

                            // 将数据保存到本地数据库
                            for (int i =0; i < jsonArr.length(); i ++) {
                                MyPublishSocietyCircle myPublishSocietyCircle = gson.fromJson(
                                        jsonArr.getString(i), MyPublishSocietyCircle.class);
                                if (myPublishSocietyCircleDao.queryDataById(myPublishSocietyCircle.getId()) == null) {
                                    myPublishSocietyCircleDao.insertData(myPublishSocietyCircle);
                                    Log.e("tag", "my publish society circle write local sqlite ：" +
                                            "id = " + myPublishSocietyCircle.getId() + "; title = " + myPublishSocietyCircle.getTitle());

                                    // 获取要下载的图片的URL
                                    String str = myPublishSocietyCircle.getImagesUrl();
                                    if (str != null && !str.equals("")) {
                                        JSONArray jsonArray = new JSONArray(str);
                                        for (int j = 0; j < jsonArray.length(); j ++) {
                                            JSONObject jsonObject = new JSONObject(jsonArray.getString(j));
                                            // 下载校园圈图片
                                            downloadImageSingleThreadExecutor.execute(ImageRequest.downloadImage(jsonObject.getString("url")));
                                        }
                                    }
                                }
                                myPublishSocietyCircles.add(i, myPublishSocietyCircle);
                                if (i+1 < myPublishSocietyCircles.size()) {
                                    myPublishSocietyCircles.remove(i+1);
                                }
                            }
                            downloadImageSingleThreadExecutor.shutdown();
                            while (true) {
                                if(downloadImageSingleThreadExecutor.isTerminated()){
                                    Log.e("tag", "我发布的社团公告数据加载完毕");
                                    handler.sendEmptyMessage(0x0002);
                                    break;
                                }
                                Thread.sleep(1000);
                            }
                        } else if (json.getString("result").equals("error")) {
                            handler.sendEmptyMessage(0x0003); // 加载失败
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

}
