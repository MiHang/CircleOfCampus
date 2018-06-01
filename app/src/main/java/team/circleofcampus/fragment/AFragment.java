package team.circleofcampus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import team.circleofcampus.R;
import team.circleofcampus.activity.CorporationActivity;
import team.circleofcampus.activity.NoticeActivity;
import team.circleofcampus.adapter.CampusCircleListViewAdapter;
import team.circleofcampus.view.MyListView;

/**
 * Created by 惠普 on 2018-05-11.
 */
public class AFragment extends Fragment {

    private View view;
    private List<Integer> images = new ArrayList<>();//声明数组
    private MyListView campusCircleListView;
    private MyListView societyCircleListView;
    private LinearLayout more_campus_notice;
    private LinearLayout more_corporation_notice;

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

        Banner banner = (Banner) view.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new Lunbotu());
        //设置图片集合
        images.add(R.drawable.banner);
        images.add(R.drawable.img_campus_kol);
        images.add(R.drawable.img_54);
        banner.setImages(images);

        banner.setDelayTime(5000);

        //banner设置方法全部调用完毕时最后调用
        banner.start();

        ArrayList<String> items = new ArrayList<String>();
        items.add("校园KOL 颜值担当 非你莫属");
        items.add("\"书香成职\"系列活动成果展");
        items.add("\"五四青年节\"文艺汇演");

        // 校园官方公告列表
        campusCircleListView = (MyListView) view.findViewById(R.id.home_campus_circle_list_view);
        campusCircleListView.setAdapter(new CampusCircleListViewAdapter(getContext(), items));
        campusCircleListView.setListViewHeightBasedOnChildren();

        // 社团公告列表
        societyCircleListView = (MyListView) view.findViewById(R.id.home_society_circle_list_view);
        societyCircleListView.setAdapter(new CampusCircleListViewAdapter(getContext(), items));
        societyCircleListView.setListViewHeightBasedOnChildren();

        // 校园官方公告 更多
        more_campus_notice = (LinearLayout) view.findViewById(R.id.home_campus_notice_more);
        more_campus_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NoticeActivity.class);
                startActivity(intent);
            }
        });

        // 社团公告 更多
        more_corporation_notice = (LinearLayout)view.findViewById(R.id.home_corporation_notice_more);
        more_corporation_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CorporationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return view;
    }

}
