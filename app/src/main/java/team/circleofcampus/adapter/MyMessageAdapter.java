package team.circleofcampus.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import team.circleofcampus.Interface.OnItemClickListener;
import team.circleofcampus.R;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.model.UserMsg;
import team.circleofcampus.view.CircleImageView;


/**
 * 消息列表适配器
 */

public class MyMessageAdapter extends SwipeMenuAdapter<MyMessageAdapter.ViewHolder> implements View.OnClickListener {

    Context context;
    List<UserMsg> data;
    OnItemClickListener mOnItemClickListener;

    int scale=120;
    HttpHelper http;

    public MyMessageAdapter(Context context, List<UserMsg> data) {
        this.context = context;
        this.data = data;
        http=new HttpHelper(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(context).inflate(R.layout.msg_item, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.e("tag",data.get(position).getAccount()+data.get(position).getUserName()+data.get(position).getSex());
        if (holder.icon.getDrawable() == null) {
            //加载头像 查询服务器是否有头像图片，若无则按性别加载
            Glide.with(context)
                    .load(http.getPath()+"/res/img/"+data.get(position).getUserName())
                    .override(scale,scale)
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if(data.get(position).getSex().equals("male")){
                                Glide.with(context)
                                        .load(R.drawable.man)
                                        .override(scale,scale)
                                        .crossFade()
                                        .into(holder.icon);
                            }else{
                                Glide.with(context)
                                        .load(R.drawable.woman)
                                        .override(scale,scale)
                                        .crossFade()
                                        .into(holder.icon);
                            }

                            return false;
                        }
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.icon);
        }

        holder.name.setText(data.get(position).getUserName());
        holder.msg.setText(data.get(position).getMsg());
        holder.Amount.setText(data.get(position).getAmount()+"条消息");
        holder.time.setText(data.get(position).getDate());
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick((Integer) view.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView icon;
        public TextView name;
        public TextView msg;
        public TextView Amount;
        public TextView time;

        public ViewHolder(View rootView) {
            super(rootView);
            this.icon = (CircleImageView) rootView.findViewById(R.id.icon);
            this.name = (TextView) rootView.findViewById(R.id.name);
            this.msg = (TextView) rootView.findViewById(R.id.msg);
            this.Amount = (TextView) rootView.findViewById(R.id.amount);
            this.time = (TextView) rootView.findViewById(R.id.time);
        }
    }
}

