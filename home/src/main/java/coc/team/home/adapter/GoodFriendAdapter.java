package coc.team.home.adapter;

/**
 * 好友列表适配器
 */

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import coc.team.home.OnItemClickListener;
import coc.team.home.R;
import coc.team.home.model.Contact;

public class GoodFriendAdapter extends SwipeMenuAdapter<GoodFriendAdapter.ViewHolder> implements View.OnClickListener{
    private List<Contact> data;
    private Context context;
    OnItemClickListener itemListener;


    public void setItemListener(OnItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public GoodFriendAdapter(Context context, List<Contact> data) {
        this.context = context;
        this.data = data;
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.UserName.setText(data.get(position).getUserName());
        holder.Column.setOnClickListener(this);
        holder.Column.setTag(position);
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onClick(View view) {
        if (itemListener!=null){
            itemListener.onItemClick((int) view.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView UserIcon;
        public TextView UserName;
        public LinearLayout Column;
        public ViewHolder(View rootView) {
            super(rootView);
            this.Column = (LinearLayout) rootView.findViewById(R.id.Column);
            this.UserIcon = (ImageView) rootView.findViewById(R.id.UserIcon);
            this.UserName = (TextView) rootView.findViewById(R.id.UserName);
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