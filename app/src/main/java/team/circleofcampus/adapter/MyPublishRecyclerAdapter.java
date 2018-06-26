package team.circleofcampus.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import team.circleofcampus.Interface.OnItemClickListener;
import team.circleofcampus.R;
import team.circleofcampus.pojo.MyPublishSocietyCircle;
import team.circleofcampus.util.AsyncTaskImageLoad;

/**
 * 我的发布列表recyclerview适配器
 */
public class MyPublishRecyclerAdapter extends RecyclerView.Adapter<MyPublishRecyclerAdapter.ViewHolder> {

    private List<MyPublishSocietyCircle> items;
    private OnItemClickListener onItemClickListener;

    public MyPublishRecyclerAdapter(List<MyPublishSocietyCircle> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_publish_recycler, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // 给 cover 设置一个tag, 通过 tag 来防止图片错位
        holder.cover.setTag("tag" + position);
        holder.cover.setImageResource(R.drawable.img_loading_failed);
        // 异步加载公告封面
        LoadImage(holder.cover, items.get(position).getImagesUrl(), "tag" + position);
        // 设置标题
        holder.title.setText(items.get(position).getTitle());
        // 设置申请时间
        holder.publishTime.setText("申请时间：" + items.get(position).getPublishTime());
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(items.get(position).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (items != null? items.size() : 0);
    }

    /**
     * 异步加载图片资源
     * @param imageView - 图片控件
     * @param imagesPath - 图片路径
     */
    private void LoadImage(ImageView imageView, String imagesPath, String tag) {
        AsyncTaskImageLoad async = new AsyncTaskImageLoad(imageView);
        async.execute(imagesPath, tag);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        ImageView cover;
        TextView title;
        TextView publishTime;
        public ViewHolder(View itemView) {
            super(itemView);
            root = (LinearLayout) itemView.findViewById(R.id.item_my_publish_root);
            cover = (ImageView) itemView.findViewById(R.id.item_my_publish_cover);
            title = (TextView) itemView.findViewById(R.id.item_my_publish_title);
            publishTime = (TextView) itemView.findViewById(R.id.item_my_publish_publish_time);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
