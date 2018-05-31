package team.circleofcampus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private TextView more_campus_notice;
    private TextView more_corporation_notice;

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
        images.add(R.drawable.lunbo01);
        images.add(R.drawable.lunbo02);
        images.add(R.drawable.lunbo04);
        banner.setImages(images);

        //banner设置方法全部调用完毕时最后调用
        banner.start();

        ArrayList<String> items = new ArrayList<String>();
        for (int i = 1; i < 4; i ++) {
            items.add("Item " + i);
        }

        campusCircleListView = (MyListView) view.findViewById(R.id.home_campus_circle_list_view);
        campusCircleListView.setAdapter(new CampusCircleListViewAdapter(getContext(), items));
        campusCircleListView.setListViewHeightBasedOnChildren();



        more_campus_notice = view.findViewById(R.id.home_campus_notice);
        more_campus_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),NoticeActivity.class);
                startActivity(intent);
            }
        });
        more_corporation_notice = view.findViewById(R.id.home_campus_corporation_notice);
        more_corporation_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),CorporationActivity.class);
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