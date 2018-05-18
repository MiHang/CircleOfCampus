package coc.team.home.fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.common.utils.LanguageUtils;
import com.common.utils.TimeUtil;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import coc.team.home.BroadcastReceiver.MyBroadcastReceiver;
import coc.team.home.Interface.MessageListener;
import coc.team.home.activity.ContactActivity;
import coc.team.home.Interface.OnItemClickListener;
import coc.team.home.R;
import coc.team.home.model.UserMsg;
import coc.team.home.adapter.MyMessageAdapter;

/**
 * 消息列表界面
 */

public class BFragment extends Fragment {

    private TextView title;
    private TextView user;
    private SwipeMenuRecyclerView recycler_view;
    MyMessageAdapter adapter;
    List<UserMsg> data=new ArrayList<>();
    TimeUtil timeUtil=new TimeUtil();
    Date date;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    MyBroadcastReceiver BroadcastReceiver;
    public void bind(MyBroadcastReceiver BroadcastReceiver){
        this.BroadcastReceiver=BroadcastReceiver;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, null);
        initView(view);
        title.setText("消息列表");
//        for(int i=0;i<6;i++){
//            UserMsg userMsg=new UserMsg();
//
//            userMsg.setUserName("mingz");
//            data.add(userMsg);
//        }



        if (BroadcastReceiver!=null){//添加广播回调监听，从而更新消息列表
            BroadcastReceiver.setMessageListener(new MessageListener() {
                @Override
                public void sendMessageListener(UserMsg userMsg) {
                    boolean isFlag=true;
                    for(UserMsg msg:data){
                        if (msg.getAccount().equals(userMsg.getAccount())){
                            isFlag=false;
                        }
                    }
                    try {
                        date=sdf.parse(userMsg.getDate());
                        userMsg.setDate(timeUtil.getTimeFormatText(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (isFlag){
                        userMsg.setAmount(1);
                        data.add(userMsg);
                    }else{
                        for (UserMsg u:data){
                            if (u.getAccount().equals(userMsg.getAccount())){
                                u.setMsg(userMsg.getMsg());
                                u.setDate(timeUtil.getTimeFormatText(date));
                                u.setAmount(u.getAmount()+1);
                            }
                        }
                    }


                    Collections.sort(data);
                    adapter.notifyDataSetChanged();

                    Toast.makeText(getContext(), "接收到"+userMsg.getAccount()+":"+userMsg.getMsg(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));// 布局管理器。
        recycler_view.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recycler_view.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
//        mSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。
//        // 添加滚动监听。
//        mSwipeMenuRecyclerView.addOnScrollListener(mOnScrollListener);

        // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
        // 设置菜单创建器。
        recycler_view.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        recycler_view.setSwipeMenuItemClickListener(menuItemClickListener);

        adapter = new MyMessageAdapter(getContext(),data);
        adapter.setOnItemClickListener(onItemClickListener);
        recycler_view.setAdapter(adapter);
        return view;
    }

    private void initView(View view) {
        title = (TextView) view.findViewById(R.id.title);
        user = (TextView) view.findViewById(R.id.user);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
            }
        });
        recycler_view = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);

    }
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            Toast.makeText(getContext(), "我是第" + position + "条。", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(getContext(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(getContext(), "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }

            // TODO 推荐调用Adapter.notifyItemRemoved(position)，也可以Adapter.notifyDataSetChanged();
            if (menuPosition == 1) {// 删除按钮被点击。
                data.remove(adapterPosition);
                adapter.notifyItemRemoved(adapterPosition);
            }
        }
    };

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {

            int width = getResources().getDimensionPixelSize(R.dimen.column_width);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height =getResources().getDimensionPixelSize(R.dimen.column_height);




            SwipeMenuItem closeItem = new SwipeMenuItem(getContext())
                    .setBackgroundDrawable(R.drawable.selector_purple)
                    .setText("置顶") // 文字，还可以设置文字颜色，大小等。。
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。

            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                    .setBackgroundDrawable(R.drawable.selector_red)
                    .setImage(R.mipmap.ic_action_delete)
                    .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。

        }
    };
}
