package team.circleofcampus.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.common.model.UserMsg;
import com.common.utils.TimeUtil;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import team.circleofcampus.Interface.OnItemClickListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.ChatActivity;
import team.circleofcampus.adapter.MyMessageAdapter;
import team.circleofcampus.dao.UserMsg_Dao;

/**
 * 消息列表界面
 */
public class MessageFragment extends Fragment {

    private View view;
    private SwipeMenuRecyclerView recycler_view;
    MyMessageAdapter adapter;
    List<UserMsg> data = new ArrayList<UserMsg>();
    UserMsg_Dao dao = null;
    TimeUtil timeUtil = new TimeUtil();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_msg, null);

        recycler_view = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));// 布局管理器。
        recycler_view.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recycler_view.setSwipeMenuCreator(swipeMenuCreator);

        // 设置菜单Item点击监听。
        recycler_view.setSwipeMenuItemClickListener(menuItemClickListener);

        adapter = new MyMessageAdapter(getContext(), data);
        if (adapter!=null){
            adapter.setOnItemClickListener(onItemClickListener);
            recycler_view.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {

                    Intent intent=new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("receive",data.get(position).getAccount());
                    intent.putExtra("nickName",data.get(position).getUserName());
                    startActivity(intent);
                    data.get(position).setVisible(false);
                    if (dao!=null){
                        List<UserMsg> m=dao.queryMsgBySearch(data.get(0).getAccount()); // 判断是否已有记录,有则更新
                        if (m!=null&&m.size()>0){
                            UserMsg user=m.get(0);
                            user.setMsg(user.getMsg());
                            user.setAmount(0);
                            dao.update(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMsgList(getContext());
    }

    @Override
    public void onDestroyView() {
        super .onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    /**
     * 更新消息预览列表
     * @param context
     */
    public void updateMsgList(Context context) {
        try {
            dao = new UserMsg_Dao(context);
            List<UserMsg> list = dao.getAllMsg();
            if (list != null && list.size() > 0){
                data.clear();
                for (UserMsg userMsg : list) {
                    Date date = sdf.parse(userMsg.getDate());
                    userMsg.setDate(timeUtil.getTimeFormatText(date));
                    if (userMsg.getAmount() == 0) {
                        userMsg.setVisible(false);
                    }
                    data.add(userMsg);
                }
            }

            // 排序&&刷新适配器
            Collections.sort(data);
            if (adapter!=null){
                adapter.notifyDataSetChanged();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            // 打开自定义的Activity
            Intent intentNotifi = new Intent(getActivity(), ChatActivity.class);
            getActivity().startActivity(intentNotifi);
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
                    .setBackgroundDrawable(R.drawable.zd_selector)
                    .setText("置顶") // 文字，还可以设置文字颜色，大小等。。
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。

            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                    .setBackgroundDrawable(R.drawable.delete_selector)
                    .setImage(R.mipmap.ic_action_delete)
                    .setText("删除") // 文字，还可以设置文字颜色，大小等。。
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
        }
    };
}
