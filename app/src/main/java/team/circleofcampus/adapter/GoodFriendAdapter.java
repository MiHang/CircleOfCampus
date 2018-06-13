package team.circleofcampus.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.common.utils.Symbol;
import com.common.view.CircleImageView;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import team.circleofcampus.Interface.OnItemClickListener;
import team.circleofcampus.R;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.model.Contact;
import team.circleofcampus.view.FontTextView;

/**
 * 好友列表适配器
 */
public class GoodFriendAdapter extends SwipeMenuAdapter<GoodFriendAdapter.ViewHolder> {
    private List<Contact> data;
    private Context context;
    OnItemClickListener itemListener;
    HttpHelper http;

    public void setItemListener(OnItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public GoodFriendAdapter(Context context, List<Contact> data) {
        this.context = context;
        this.data = data;
        http=new HttpHelper(context);
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
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

        };
        Glide.with(context)
                .load(http.getPath()+"/res/img/"+data.get(position).getAccount())
                .error(res)
                .into(holder.UserIcon);

    }

    //获取数据的数量
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