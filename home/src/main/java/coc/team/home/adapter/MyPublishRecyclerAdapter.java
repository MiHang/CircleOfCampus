package coc.team.home.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import coc.team.home.R;

/**
 * 我的发布列表recyclerview适配器
 */
public class MyPublishRecyclerAdapter extends RecyclerView.Adapter<MyPublishRecyclerAdapter.ViewHolder> {

    private ArrayList<String> societyCircles;

    public MyPublishRecyclerAdapter(ArrayList<String> societyCircles) {
        this.societyCircles = societyCircles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_publish_recycler, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return societyCircles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
