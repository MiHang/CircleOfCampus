package team.circleofcampus.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;
import team.circleofcampus.activity.ApplySocietyAuthorityActivity;
import team.circleofcampus.activity.LoginActivity;
import team.circleofcampus.activity.RegisterActivity;
import team.circleofcampus.adapter.MyFragmentPagerAdapter;
import team.circleofcampus.http.LoginRequest;
import team.circleofcampus.http.SocietyAuthorityRequest;
import team.circleofcampus.util.DensityUtil;
import team.circleofcampus.util.FontUtil;
import team.circleofcampus.util.SharedPreferencesUtil;

/**
 * 我发布的页面, 此fragment的写法是Fragment防止自动清理 (ViewPager滑动时，滑出屏幕后被清理)
 */
public class MyPublishFragment extends Fragment {

    private View view;

    @BindView(R.id.unauthorized_root)
    protected LinearLayout unauthorizedRoot;
    @BindView(R.id.authorized_root)
    protected LinearLayout authorizedRoot;

    @BindView(R.id.my_publish_tab_layout)
    protected TabLayout tabLayout;
    @BindView(R.id.my_publish_view_pager)
    protected ViewPager viewPager;
    private MyFragmentPagerAdapter myPublishFragmentAdapter;

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private boolean isSkipAuthority = false; // 是否已跳转到权限申请页面

    /**
     * 用户ID
     */
    private int userId = 0;
    private boolean isAuthority = false;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001 : {
                    Toast.makeText(getContext(), "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                } break;
                case 0x0002 : { // 已授权
                    SharedPreferencesUtil.setAuthorized(getContext(), true);
                    unauthorizedRoot.setVisibility(View.GONE);
                    authorizedRoot.setVisibility(View.VISIBLE);
                    initView();
                } break;
                case 0x0003 : { // 未授权
                    SharedPreferencesUtil.setAuthorized(getContext(), false);
                    unauthorizedRoot.setVisibility(View.VISIBLE);
                    authorizedRoot.setVisibility(View.GONE);
                } break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super .onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isSkipAuthority) {
            Log.e("tag", "onResume()....., 从权限申请页面返回");
            isSkipAuthority = false;
            loadingSocietyAuthority();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_publish, null);
        ButterKnife.bind(this, view);

        // 获取用户ID
        userId = SharedPreferencesUtil.getUID(getContext());

        // 社团圈发布权限管理
        isAuthority = SharedPreferencesUtil.isAuthorized(getContext());
        if (isAuthority) {
            handler.sendEmptyMessage(0x0002);
        } else {
            handler.sendEmptyMessage(0x0003);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (isAuthority != SharedPreferencesUtil.isAuthorized(getContext())) {
            if (SharedPreferencesUtil.isAuthorized(getContext())) {
                handler.sendEmptyMessage(0x0002);
            } else {
                handler.sendEmptyMessage(0x0003);
            }
        }

        return view;
    }

    private void initView() {

        // 添加导航栏列表数据
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        // 设置viewpager适配器
        fragments.add(new AuditedFragment());
        fragments.add(new UnauditedFragment());
        myPublishFragmentAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragments);
        viewPager.setAdapter(myPublishFragmentAdapter);

        // 设置TabLayout和ViewPager的联动，该方法必须在设置tab标题之前，否则Tab标题会被清除
        tabLayout.setupWithViewPager(viewPager);

        // 设置tab显示的文本
        tabLayout.getTabAt(0).setText("已审核");
        tabLayout.getTabAt(1).setText("未审核");

        // 设置Tab文字字体
        changeTabsFont();

        // 设置下划线左右边距
        setTabLine(tabLayout, 63,63);

        // 为tab添加垂直分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.shape_tab_layout_divider));
        linearLayout.setDividerPadding(DensityUtil.dpToPx(getContext(), 10f));

        // 加载我发布过的社团圈信息
        loadingMyPublishSocietyCircle();
    }

    /**
     * 加载我发布过的社团圈信息
     */
    private void loadingMyPublishSocietyCircle() {
        // 联网线程
        new Thread(){
            @Override
            public void run() {
                String result = SocietyAuthorityRequest.hasSocietyAuthority(userId);
                if (null != result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        result = json.getString("result");
                        if ("authority".equals(result)) { // 已授权
                            handler.sendEmptyMessage(0x0002);
                        } else if ("unauthority".equals(result)) { // 未授权
                            handler.sendEmptyMessage(0x0003);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0001);
                }
            }
        }.start();
    }

    /**
     * 加载网络数据 - 社团发布权限
     */
    private void loadingSocietyAuthority() {
        // 联网线程
        new Thread(){
            @Override
            public void run() {
                String result = SocietyAuthorityRequest.hasSocietyAuthority(userId);
                if (null != result) {
                    try {
                        JSONObject json = new JSONObject(result);
                        result = json.getString("result");
                        if ("authority".equals(result)) { // 已授权
                            handler.sendEmptyMessage(0x0002);
                        } else if ("unauthority".equals(result)) { // 未授权
                            handler.sendEmptyMessage(0x0003);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.sendEmptyMessage(0x0001);
                }
            }
        }.start();
    }

    /**
     * 未授权时点击权限申请按钮
     */
    @OnClick(R.id.unauthorized_applay_btn)
    public void onClickApplayBtn() {
        isSkipAuthority = true;
        Intent intent = new Intent(getContext(), ApplySocietyAuthorityActivity.class);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    /**
     * 设置TabLayout的字体
     */
    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(FontUtil.setFont(getContext()));
                }
            }
        }
    }

    /**
     * 设置下划线左右边距
     * @param tab
     * @param left
     * @param right
     */
    public void setTabLine(TabLayout tab, int left, int right){
        try {
            Class<?> tablayout = tab.getClass();
            Field tabStrip = tablayout.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
            LinearLayout ll_tab= (LinearLayout) tabStrip.get(tabLayout);
            for (int i = 0; i < ll_tab.getChildCount(); i++) {
                View child = ll_tab.getChildAt(i);
                child.setPadding(0,0,0,0);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT,1);

                //修改两个tab的间距
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginStart(DensityUtil.dpToPx(getContext(), left));
                    params.setMarginEnd(DensityUtil.dpToPx(getContext(), right));
                }
                child.setLayoutParams(params);
                child.invalidate();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
