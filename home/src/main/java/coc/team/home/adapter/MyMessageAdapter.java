package coc.team.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import coc.team.home.OnItemClickListener;
import coc.team.home.R;
import coc.team.home.model.UserMsg;

/**
 * 消息列表适配器
 */

public class MyMessageAdapter extends SwipeMenuAdapter<MyMessageAdapter.ViewHolder> implements View.OnClickListener {

    Context context;
    List<UserMsg> data;
    OnItemClickListener mOnItemClickListener;

    public MyMessageAdapter(Context context, List<UserMsg> data) {
        this.context = context;
        this.data = data;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(data.get(position).getUserName());
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
        public ImageView icon;
        public TextView name;
        public TextView msg;
        public TextView Num;
        public TextView time;

        public ViewHolder(View rootView) {
            super(rootView);
            this.icon = (ImageView) rootView.findViewById(R.id.icon);
            this.name = (TextView) rootView.findViewById(R.id.name);
            this.msg = (TextView) rootView.findViewById(R.id.msg);
            this.Num = (TextView) rootView.findViewById(R.id.num);
            this.time = (TextView) rootView.findViewById(R.id.time);
        }

    }
}

