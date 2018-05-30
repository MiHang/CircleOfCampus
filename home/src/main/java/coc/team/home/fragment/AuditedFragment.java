package coc.team.home.fragment;


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

import coc.team.home.R;
import coc.team.home.adapter.MyPublishRecyclerAdapter;

/**
 * 我的发布页面, 已审核的fragment
 */
public class AuditedFragment extends Fragment {

    private RecyclerView recyclerView;
    private MyPublishRecyclerAdapter myPublishRecyclerAdapter;
    private ArrayList<String> societyCircles = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audited, null);

        // 设置recycler适配器
        recyclerView = view.findViewById(R.id.my_publish_recycler_view);
        for (int i = 0; i < 4; i++) {
            societyCircles.add("Item " + i);
        }
        myPublishRecyclerAdapter = new MyPublishRecyclerAdapter(societyCircles);

        //设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置adapter
        recyclerView.setAdapter(myPublishRecyclerAdapter);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        return view;
    }
}
