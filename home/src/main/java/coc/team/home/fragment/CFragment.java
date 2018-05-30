package coc.team.home.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;

import coc.team.home.R;
import coc.team.home.adapter.MyPublishFragmentAdapter;
import coc.team.home.util.DensityUtil;

/**
 * 我发布的页面
 */
public class CFragment  extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyPublishFragmentAdapter myPublishFragmentAdapter;

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publish, null);

        initView(view);

        // 添加导航栏列表数据
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());

        // 设置viewpager适配器
        fragments.add(new AuditedFragment());
        fragments.add(new UnauditedFragment());
        myPublishFragmentAdapter = new MyPublishFragmentAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(myPublishFragmentAdapter);

        // 设置TabLayout和ViewPager的联动，该方法必须在设置tab标题之前，否则Tab标题会被清除
        tabLayout.setupWithViewPager(viewPager);

        // 设置tab显示的文本
        tabLayout.getTabAt(0).setText("已审核");
        tabLayout.getTabAt(1).setText("未审核");

        // 设置下划线左右边距
        setTabLine(tabLayout, 63,63);

        // 为tab添加垂直分割线
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.shape_tab_layout_divider));
        linearLayout.setDividerPadding(DensityUtil.dpToPx(getContext(), 10f));

        return view;
    }

    /**
     * 初始化控件
     * @param view
     */
    private void initView(View view) {
        tabLayout = (TabLayout) view.findViewById(R.id.my_publish_tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.my_publish_view_pager);
    }

    /**
     * 设置下划线左右边距
     * @param tab
     * @param left
     * @param right
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
                params.setMarginStart(DensityUtil.dpToPx(getContext(), left));
                params.setMarginEnd(DensityUtil.dpToPx(getContext(), right));
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
