package team.circleofcampus.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.common.view.CircleImageView;

import java.util.List;

import team.circleofcampus.Interface.OnItemClickListener;
import team.circleofcampus.R;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.model.Contact;
import team.circleofcampus.view.FontTextView;

/**
 * Created by 惠普 on 2018-06-03.
 */

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.ViewHolder> {

    private List<Contact> data;
    OnItemClickListener itemListener;
    HttpHelper http;
    Context context;


    public void setItemListener(OnItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public FriendSearchAdapter(Context context, List<Contact> data) {
        this.context = context;
        this.data = data;
        http=new HttpHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       ViewHolder holder = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.UserName.setText(data.get(position).getUserName());
        holder.Column.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemListener!=null){
                    itemListener.onItemClick((int) view.getTag());
                }
            }
        });
        holder.Column.setTag(position);
        //加载头像 查询服务器是否有头像图片，若无则按性别加载
        int res=R.drawable.woman;

        if (data.get(position).getSex()==null||data.get(position).getSex().equals("male")) {
            res=R.drawable.man;

        }
        Toast.makeText(context, ""+data.get(position).getSex(), Toast.LENGTH_SHORT).show();
        Glide.with(context)
                .load(http.getPath()+"/res/img/"+data.get(position).getAccount())
                .crossFade()
                .error(res)
                .into(holder.UserIcon);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView UserIcon;
        public FontTextView UserName;
        public LinearLayout Column;
        public ViewHolder(View rootView) {
            super(rootView);
            this.Column = (LinearLayout) rootView.findViewById(R.id.Column);
            this.UserIcon = (CircleImageView) rootView.findViewById(R.id.UserIcon);
            this.UserName = (FontTextView) rootView.findViewById(R.id.UserName);
            if(isAndroid5()){
                this.Column.setBackgroundResource(R.drawable.contact_pressed);//添加点击效果
            }
        }

    }
    /**
     * 判断android SDK 版本是否大于等于5.0
     * @return
     */
    public static boolean isAndroid5() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
