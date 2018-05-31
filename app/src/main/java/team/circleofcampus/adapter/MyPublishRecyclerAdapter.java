package team.circleofcampus.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import team.circleofcampus.R;
import team.circleofcampus.model.SocietyCircle;


/**
 * 我的发布列表recyclerview适配器
 */
public class MyPublishRecyclerAdapter extends RecyclerView.Adapter<MyPublishRecyclerAdapter.ViewHolder> {

    private ArrayList<SocietyCircle> societyCircles;

    public MyPublishRecyclerAdapter(ArrayList<SocietyCircle> societyCircles) {
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
        holder.title.setText(societyCircles.get(position).getTitle());
        holder.publishTime.setText("申请时间：" + societyCircles.get(position).getPublishTime());
    }

    @Override
    public int getItemCount() {
        return societyCircles.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cover;
        TextView title;
        TextView publishTime;
        public ViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.item_my_publish_cover);
            title = (TextView) itemView.findViewById(R.id.item_my_publish_title);
            publishTime = (TextView) itemView.findViewById(R.id.item_my_publish_publish_time);
        }
    }
}
