package team.circleofcampus.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.common.model.UserMsg;
import com.common.utils.Symbol;
import com.common.utils.TimeUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.Interface.MessageListener;
import team.circleofcampus.Interface.OfflineListener;
import team.circleofcampus.dao.Data_Dao;
import team.circleofcampus.dao.UserDao;
import team.circleofcampus.pojo.User;
import team.circleofcampus.Interface.FragmentSwitchListener;
import team.circleofcampus.Interface.NetworkStateChangeListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.MyFragmentPagerAdapter;
import team.circleofcampus.http.CampusCircleRequest;
import team.circleofcampus.http.SocietyCircleRequest;
import team.circleofcampus.receiver.NetworkConnectChangedReceiver;
import team.circleofcampus.fragment.CampusCircleFragment;
import team.circleofcampus.fragment.CircleFragment;
import team.circleofcampus.fragment.MessageFragment;
import team.circleofcampus.fragment.MyPublishFragment;
import team.circleofcampus.fragment.MineFragment;
import team.circleofcampus.fragment.QRFragment;
import team.circleofcampus.fragment.SocietyCircleFragment;
import team.circleofcampus.http.SocietyAuthorityRequest;
import team.circleofcampus.service.MyService;
import team.circleofcampus.service.SingleThreadService;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.view.NoPreloadViewPager;

/**
 * 主界面
 */
public class HomeActivity extends AppCompatActivity {

    MyFragmentPagerAdapter adapter;
    @BindView(R.id.HomeViewPager)
    protected NoPreloadViewPager HomeViewPager;
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
    CircleFragment circleFragment = new CircleFragment();
    MyPublishFragment publishFragment=new MyPublishFragment();
    MineFragment mineFragment = new MineFragment();
    QRFragment qrFragment = new QRFragment();
    List<Fragment> data = new ArrayList<>();
    private int selectedPageId = 0;

    String account;
    WebSocketClient myClient;
    MyService myService;

    List<UserMsg> UserMsg_data=new ArrayList<>();
    MessageFragment bFragment = new MessageFragment();
    TimeUtil timeUtil=new TimeUtil();
    Date date;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            MyService.MsgBinder myBinder = (MyService.MsgBinder) binder;
            myClient = myBinder.getService().getMyClient();
            myService = myBinder.getService();
            myBinder.getService().setMessageListener(new MessageListener() {
                @Override
                public void update(final String account, boolean isUpdate) {
                    HomeViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            MessageFragment messageFragment = ((MessageFragment)data.get(1));
                            if (messageFragment != null) {
                                try {
                                    messageFragment.updateMsgList(HomeActivity.this,account);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    Log.e("tag", "------ 新消息------");
                }
            });
            Log.e("tag", "------ HomeActivity onServiceConnected ------");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("tag", "------ HomeActivity onServiceDisconnected ------");
        }
    };

    // 单例线程池
    private ExecutorService singleThreadExecutor;
    // 网络连接状态改变广播
    private NetworkConnectChangedReceiver networkConnectChangedReceiver = new NetworkConnectChangedReceiver();

    // 当前activity可见
    private boolean isResume = true;
    private boolean isShowHint = true;
    private boolean isDisconnectNetwork = false; // 是否断开网络连接

    /**
     * 用户ID
     */
    private int userId = 0;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001 : {
                    if (isResume && isShowHint) {
                        Toast.makeText(HomeActivity.this, "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                    }
                    if ((CircleFragment)data.get(0) != null) { // 如果有刷新动画，则关闭
                        isShowHint = false;
                        ((CircleFragment)data.get(0)).closeRefreshAnimation();
                    }
                } break;
                case 0x0002 : { // 社团权限信息
                    if ((boolean)msg.obj) { // 已授权
                        SharedPreferencesUtil.setAuthorized(HomeActivity.this, true);
                    } else { // 未授权
                        SharedPreferencesUtil.setAuthorized(HomeActivity.this, false);
                    }
                } break;
                case 0x0003 : { // 获取校园圈的数量
                    SharedPreferencesUtil.setCampusCircleCount(HomeActivity.this, msg.arg1);
                    Log.e("tag", "campus circle count = " + msg.arg1);
                } break;
                case 0x0004 : { // 获取校园圈的数量
                    SharedPreferencesUtil.setSocietyCircleCount(HomeActivity.this, msg.arg1);
                    Log.e("tag", "society circle count = " + msg.arg1);
                } break;
                case 0x0005 : { // 获取我发布的社团圈的数量
                    SharedPreferencesUtil.setMyPublishSocietyCircleCount(HomeActivity.this, msg.arg1);
                    Log.e("tag", "my publish society circle count = " + msg.arg1);
                    loadData();
                } break;
                case 0x0006 : { // 网络重新连接
                    if (selectedPageId == 0) {
                        isShowHint = true;
                        singleThreadExecutor.execute(loadingSocietyAuthority()); // 加载社团发布权限
                        singleThreadExecutor.execute(loadingCampusCircleCount()); // 获取校园圈的数量
                        singleThreadExecutor.execute(loadingSocietyCircleCount()); // 获取社团圈的数量
                        singleThreadExecutor.execute(loadingMyPublishSocietyCircleCount()); // 获取我发布的社团圈的数量
                    } else {
                        loadData();
                    }
                } break;
            }
        }
    };

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
        ButterKnife.bind(this);

        account = SharedPreferencesUtil.getAccount(this);

        // 获取用户ID
        userId = SharedPreferencesUtil.getUID(HomeActivity.this);
        // 记录本次登陆时间
        SharedPreferencesUtil.setLoginTime(HomeActivity.this, System.currentTimeMillis());

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintResource(R.drawable.bg);
        headerSelect(0);

        // 校园圈
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

        data.add(bFragment);

        // 我的发布
        data.add(publishFragment);

        // 我的
        mineFragment.setSwitchListener(new FragmentSwitchListener() {
            @Override
            public void displayThisFragment(boolean display) {
                headerSelect(4);
                HomeViewPager.setCurrentItem(4,true);
            }
            @Override
            public void displayThisFragment(int currentId, boolean display) {}
        });
        mineFragment.setListener(new OfflineListener() {//下线监听
            @Override
            public void Offline(boolean isFlag) {
                if (myClient != null) {
                    if (myClient.getConnection().isOpen()) {
                        JSONObject js = new JSONObject();
                        try {
                            js.put("Account", account);
                            js.put("Request", "Offline");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        myClient.send(js.toString());
                    }
                }
            }
        });
        data.add(mineFragment);

        // 我的二维码
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("Account",account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        qrFragment.setAccount(jsonObject.toString());
        data.add(qrFragment);

        // 更多校园官方公告
        CampusCircleFragment campusCircleFragment = new CampusCircleFragment();
        data.add(campusCircleFragment);

        // 更多社团公告
        SocietyCircleFragment societyCircleFragment = new SocietyCircleFragment();
        data.add(societyCircleFragment);

        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), data);
        HomeViewPager.setAdapter(adapter);

        HomeViewPager.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {
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

        // 获取单例线程池
        singleThreadExecutor = SingleThreadService.getSingleThreadPool();

        // 注册网络连接状态改变广播
        IntentFilter netWorkIntentFilter = new IntentFilter();
        netWorkIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkConnectChangedReceiver, netWorkIntentFilter);
        networkConnectChangedReceiver.setNetworkStateChangeListener(new NetworkStateChangeListener() {
            @Override
            public void networkAvailable(int type) {
                // 数据加载， 网络重新连接上可再次加载
                Log.e("tag", "HomeActivity reload data...");
                SharedPreferencesUtil.setNetworkAvailable(HomeActivity.this, true);
                if (myService != null && isDisconnectNetwork) {
                    myService.getCon(account);
                }
                isDisconnectNetwork = false;
                handler.sendEmptyMessage(0x0006);
            }
            @Override
            public void networkUnavailable() {
                SharedPreferencesUtil.setNetworkAvailable(HomeActivity.this, false);
                isDisconnectNetwork = true;
                Toast.makeText(HomeActivity.this, "糟糕，网络开小差了(;′⌒`)", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = new Intent(this, MyService.class);
        SharedPreferences preferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("send", account);
        editor.commit();
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;

        // 更新用户数据后返回我的信息页面更新用户数据
        boolean isUserInfoUpdate = SharedPreferencesUtil.isUserInfoUpdate(HomeActivity.this);
        boolean isNetworkAvailable = SharedPreferencesUtil.isNetworkAvailable(HomeActivity.this);
        if (isUserInfoUpdate && selectedPageId==3 && isNetworkAvailable) {
            SharedPreferencesUtil.setUserInfoUpdate(HomeActivity.this, false);
            loadData();
        }

        Intent intent = getIntent();
        int selectedPageId = intent.getIntExtra("selectedPageId", -1);
        if (selectedPageId != -1) {
            HomeViewPager.setCurrentItem(selectedPageId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (networkConnectChangedReceiver != null) {
            Log.e("tag", "HomeActivity unregister networkConnectChangedReceiver...");
            unregisterReceiver(networkConnectChangedReceiver);
        }
        if (singleThreadExecutor != null) {
            singleThreadExecutor.shutdownNow();
            SingleThreadService.destroySingleThreadPool();
        }

        unbindService(conn);
    }

    /**
     * 重新加载页面数据
     */
    private void loadData() {
        switch (selectedPageId) {
            case 0 : { // 加载首页相关数据
                ((CircleFragment)data.get(0)).loadData(userId);
            } break;
            case 2 : { // 加载我的发布页相关数据
                ((MyPublishFragment)data.get(2)).loadData();
            } break;
            case 3 : { // 加载我的页面相关数据
                ((MineFragment)data.get(3)).loadData();
            } break;
        }
    }

    /**
     * 获取我发布的社团圈的数量
     */
    private Runnable loadingMyPublishSocietyCircleCount() {
        return new Runnable() {
            @Override
            public void run() {
                String result = SocietyCircleRequest.getMyPublishSocietyCircleSize(userId);
                if (null != result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("size")) { // 查询成功
                            int size = json.getInt("size");
                            Message msg = new Message();
                            msg.what = 0x0005;
                            msg.arg1 = size;
                            handler.sendMessage(msg);
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

    /**
     * 获取社团圈的数量
     */
    private Runnable loadingSocietyCircleCount() {
        return new Runnable() {
            @Override
            public void run() {
                String result = SocietyCircleRequest.getSocietyCircleSize(userId);
                if (null != result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("size")) { // 查询成功
                            int size = json.getInt("size");
                            Message msg = new Message();
                            msg.what = 0x0004;
                            msg.arg1 = size;
                            handler.sendMessage(msg);
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

    /**
     * 获取校园圈的数量
     */
    private Runnable loadingCampusCircleCount() {
        return new Runnable() {
            @Override
            public void run() {
                String result = CampusCircleRequest.getCampusCircleSize(userId);
                if (null != result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("size")) { // 查询成功
                            int size = json.getInt("size");
                            Message msg = new Message();
                            msg.what = 0x0003;
                            msg.arg1 = size;
                            handler.sendMessage(msg);
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

    /**
     * 加载网络数据 - 社团发布权限
     */
    private Runnable loadingSocietyAuthority() {
        return new Runnable(){
            @Override
            public void run() {
                String result = SocietyAuthorityRequest.hasSocietyAuthority(userId);
                if (null != result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        result = json.getString("result");

                        Message msg = new Message();
                        if ("authority".equals(result)) { // 已授权
                            msg.obj = true;
                        } else if ("unauthority".equals(result)) { // 未授权
                            msg.obj = false;
                        }
                        msg.what = 0x0002;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0001);
                }
            }
        };
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
                headerRightText.setText("");
                headerLeftImage.setVisibility(View.VISIBLE);
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                mine.setImageResource(R.drawable.icon_home_my_hover);
            };break;
            case 5: {
                title.setText("校园官方公告");
                headerLeftText.setText("");
                headerRightText.setText("");
                headerLeftImage.setVisibility(View.VISIBLE);
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                circle.setImageResource(R.drawable.icon_home_campus_hover);
            };break;
            case 6: {
                title.setText("社团公告");
                headerLeftText.setText("");
                headerRightText.setText("");
                headerLeftImage.setVisibility(View.VISIBLE);
                headerRightImage.setVisibility(View.GONE);
                setHomeBottomNavNormal();
                circle.setImageResource(R.drawable.icon_home_campus_hover);
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
     * 标题栏左侧文字点击事件
     */
    @OnClick(R.id.header_left_text)
    public void onClickLeftText() {
        if (selectedPageId == 0) { // 首页
            if(SharedPreferencesUtil.isAuthorized(HomeActivity.this)) { // 已授权发布社团圈信息
                Intent intent = new Intent(HomeActivity.this, PublishActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            } else {
                Toast.makeText(HomeActivity.this, "您还没有发布社团信息的权限，请先申请授权",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 标题栏右侧文字点击事件
     * @param view
     */
    @OnClick(R.id.header_right_text)
    protected void onClickRightText(final View view) {
        if (selectedPageId == 1) { // 当前页面为消息页
            if (account!=null){
                Intent intent = new Intent(HomeActivity.this, ContactActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
            }else{
                Toast.makeText(this, "请登录", Toast.LENGTH_SHORT).show();
            }
        } else if (selectedPageId == 3) { // 我的页面 - 编辑
            try {
                UserDao userDao = new UserDao(HomeActivity.this);
                User user = userDao.queryData(SharedPreferencesUtil.getUID(HomeActivity.this));
                if (user != null) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("uId",user.getuId());
                    jsonObject.put("email",user.getEmail());
                    jsonObject.put("userName",user.getUserName());
                    jsonObject.put("gender",user.getGender());
                    jsonObject.put("campusName",user.getCampusName());
                    jsonObject.put("facultyName",user.getFacultyName());

                    Intent intent = new Intent(HomeActivity.this, AlterUserInfoActivity.class);
                    intent.putExtra("param", jsonObject.toString());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
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
