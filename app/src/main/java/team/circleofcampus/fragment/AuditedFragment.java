package team.circleofcampus.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import team.circleofcampus.R;
import team.circleofcampus.adapter.MyPublishRecyclerAdapter;
import team.circleofcampus.model.SocietyCircle;


/**
 * 我的发布页面, 已审核的fragment
 */
public class AuditedFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private MyPublishRecyclerAdapter myPublishRecyclerAdapter;
    private ArrayList<SocietyCircle> societyCircles = new ArrayList<SocietyCircle>();

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

        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_audited, null);

        loadListData(); // 加载列表数据

        // 设置recycler适配器
        recyclerView = view.findViewById(R.id.my_publish_recycler_view);
        myPublishRecyclerAdapter = new MyPublishRecyclerAdapter(societyCircles);

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置adapter
        recyclerView.setAdapter(myPublishRecyclerAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return view;
    }

    /**
     * 加载列表数据
     */
    private void loadListData() {

        SocietyCircle societyCircle1 = new SocietyCircle();
        societyCircle1.setTitle("羽毛球社团招新");
        societyCircle1.setPublishTime("2018-05-30");
        societyCircles.add(societyCircle1);

        SocietyCircle societyCircle2 = new SocietyCircle();
        societyCircle2.setTitle("激情绽放\"羽\"你共享 第九届校园羽毛球比赛");
        societyCircle2.setPublishTime("2018-06-02");
        societyCircles.add(societyCircle2);

        SocietyCircle societyCircle3 = new SocietyCircle();
        societyCircle3.setTitle("羽毛球社团周年庆");
        societyCircle3.setPublishTime("2018-06-03");
        societyCircles.add(societyCircle3);
    }
}
