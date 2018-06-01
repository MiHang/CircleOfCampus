package team.circleofcampus.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.Interface.FragmentSwitchListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.CampusCircleListViewAdapter;
import team.circleofcampus.util.DensityUtil;
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

    FragmentSwitchListener switchListener;
    public FragmentSwitchListener getSwitchListener() {
        return switchListener;
    }
    public void setSwitchListener(FragmentSwitchListener switchListener) {
        this.switchListener = switchListener;
    }

    @Override
    public void onDestroyView() {
        super .onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);

        // 设置下拉刷新部分的HeadView
        BezierLayout headerView = new BezierLayout(getContext());
        refreshLayout.setHeaderView(headerView);

        // 设置下拉刷新控件相关参数
        refreshLayout.setMaxHeadHeight(100);
        refreshLayout.setHeaderHeight(80);
        refreshLayout.setEnableLoadmore(false);

        // 下拉刷新
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter(){
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                    }
                },3000);
            }
            @Override
            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {}
        });

        // 轮播图部分
        Banner banner = (Banner) view.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new Lunbotu());
        //设置图片集合
        images.add(R.drawable.banner);
        images.add(R.drawable.img_campus_kol);
        images.add(R.drawable.img_54);
        banner.setImages(images);
        banner.setDelayTime(5000);
        banner.start(); //banner设置方法全部调用完毕时最后调用

        ArrayList<String> items = new ArrayList<String>();
        items.add("校园KOL 颜值担当 非你莫属");
        items.add("\"书香成职\"系列活动成果展");
        items.add("\"五四青年节\"文艺汇演");

        // 校园官方公告列表
        campusCircleListView.setAdapter(new CampusCircleListViewAdapter(getContext(), items));
        campusCircleListView.setListViewHeightBasedOnChildren();

        // 社团公告列表
        societyCircleListView.setAdapter(new CampusCircleListViewAdapter(getContext(), items));
        societyCircleListView.setListViewHeightBasedOnChildren();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return view;
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
