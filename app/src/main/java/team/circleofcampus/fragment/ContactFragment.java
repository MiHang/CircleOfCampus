package team.circleofcampus.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnDismissListener;
import com.common.utils.LanguageUtils;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import team.circleofcampus.Interface.MoreFragmentListener;
import team.circleofcampus.Interface.OnItemClickListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.AddRequestActivity;
import team.circleofcampus.activity.ContactActivity;
import team.circleofcampus.activity.MainActivity;
import team.circleofcampus.adapter.GoodFriendAdapter;
import team.circleofcampus.adapter.GoodFriendItemDecoration;
import team.circleofcampus.adapter.MyIndexAdapter;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.model.Contact;
import team.circleofcampus.model.Letter;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.view.FontTextView;
import team.circleofcampus.view.MyEditText;


/**
 * 联系人界面
 */

public class ContactFragment extends Fragment implements com.bigkoo.alertview.OnItemClickListener, OnDismissListener {


    private MyEditText Search;
    private SwipeMenuRecyclerView rv;
    private ListView IndexList;
    final List<Contact> data = new ArrayList<Contact>();
    LanguageUtils languageUtils = new LanguageUtils();
    private Map<Integer, String> Titles = new HashMap<>();//存放所有key的位置和内容
    List<Letter> d = new ArrayList<>();
    MyIndexAdapter myAdapter;//索引列表适配器
    LinearLayoutManager layoutManager;//布局管理器
    GoodFriendAdapter adapter;
    MoreFragmentListener listener;
    HttpHelper helper;
    private FontTextView num;
    LoadingDialog dialog;
    EditText edit;
    List<Contact> list = new ArrayList<Contact>();
    private AlertView mAlertViewExt;//窗口拓展例子
    GoodFriendItemDecoration itemDecoration ;
    SharedPreferencesUtil sharedPreferencesUtil;
    String Account;

    public void setListener(MoreFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, null);

        initView(view);

        mAlertViewExt = new AlertView("提示", "请填写您想修改的备注",
                "取消", null, new String[]{"完成"}, getContext(), AlertView.Style.Alert, this);
        ViewGroup extView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.alertext_form, null);
        edit = (EditText) extView.findViewById(R.id.edit);
        mAlertViewExt.addExtView(extView);

        //设置布局管理器
        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);
        //设置增加或删除条目的动画
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter = new GoodFriendAdapter(getContext(), data);
        rv.setAdapter(adapter);

        myAdapter = new MyIndexAdapter(getContext(), d);
        IndexList.setAdapter(myAdapter);
        helper = new HttpHelper(getContext());

        Account=sharedPreferencesUtil.getAccount(getContext());
        if (Account!=null){
            dialog = new LoadingDialog(getContext());
            dialog.setLoadingText("加载中")
                    .setSuccessText("加载成功")//显示加载成功时的文字
                    .setFailedText("加载失败")
                    .closeSuccessAnim()
                    .setShowTime(500)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();
            setData(Account);

        }else{
            getActivity().finish();
        }


        return view;
    }
public void setData(final String id){
    AsyncTask.execute(new Runnable() {
        @Override
        public void run() {

            final String s = helper.queryFriendInfo(id);
            rv.post(new Runnable() {
                @Override
                public void run() {

                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        int index=0;
                        if (!s.equals("")) {

                            num.setText("共" + jsonObject.getString("result") + "位联系人");
                            JSONArray jsonArray = new JSONArray(jsonObject.getString("Info"));
                            index= Integer.parseInt(jsonObject.getString("result"));
                            if (index ==0){
                                num.setText("暂无联系人");
                                dialog.loadFailed();
                                return;
                            }

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject js = new JSONObject(jsonArray.get(i).toString());
                                Contact contact = new Contact();
                                contact.setUserName(js.getString("nickName"));
                                contact.setSex(js.getString("sex"));

                                contact.setAccount(js.getString("account"));
                                list.add(contact);
                            }

                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getUserName() != null) {
                                    String lastName = list.get(i).getUserName().toUpperCase().charAt(0) + "";
                                    if (languageUtils.isChinese(list.get(i).getUserName())) {
                                        lastName = languageUtils.toEnglish(list.get(i).getUserName().charAt(0));
                                    }

                                    Contact contact = new Contact();
                                    contact.setLastName(lastName);
                                    contact.setSex(list.get(i).getSex());

                                    contact.setUserIcon(list.get(i).getUserIcon());
                                    contact.setUserName(list.get(i).getUserName());
                                    contact.setAccount(list.get(i).getAccount());
                                    data.add(contact);
                                }
                            }
                            Collections.sort(data);//按字母顺序排序
                            adapter.notifyDataSetChanged();

                            //标题
                            Titles.put(0, data.get(0).getLastName());
                            for (int i = 1; i < data.size(); i++) {
                                if (!data.get(i).getLastName().equals(data.get(i - 1).getLastName())) {
                                    Titles.put(i, data.get(i).getLastName());
                                }

                            }

                            itemDecoration = new GoodFriendItemDecoration(getContext());
                            itemDecoration.setTitles(Titles);
                            rv.addItemDecoration(itemDecoration);


                            rv.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
                            rv.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

                            // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
                            // 设置菜单创建器。
                            rv.setSwipeMenuCreator(swipeMenuCreator);
                            // 设置菜单Item点击监听。
                            rv.setSwipeMenuItemClickListener(menuItemClickListener);


                            setSideBar();


                            dialog.loadSuccess();
                        } else {
                            num.setText("暂无联系人");
                            dialog.loadFailed();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            });
        }
    });
    adapter.setItemListener(new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            if (listener != null) {
                String[] str={
                        data.get(position).getAccount(),
                        data.get(position).getUserName()
                };
                listener.setValueExtra(2,str);
            }

        }
    });

    //滑动监听
    rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            int x1 = layoutManager.findFirstVisibleItemPosition();//可见范围内的第一项的位置
            int x2 = layoutManager.findLastVisibleItemPosition();//可见范围内的最后一项的位置
            int itemCount = layoutManager.getItemCount();//recyclerview中的item的所有的数目


            //修改选中状态
            for (Letter l : d) {
                if (l.isHover() == true) {
                    l.setHover(false);
                    break;
                }
            }
            for (Letter l : d) {
                if (l.getLetter().toUpperCase().equals(data.get(x1).getLastName())) {
                    l.setHover(!l.isHover());

                    break;
                }
            }

            //延时2毫秒刷新
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    myAdapter.notifyDataSetChanged();
                }
            }, 200);


        }
    });

    IndexList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            d.get(i).setHover(!d.get(i).isHover());
            int index = 0;
            for (int x = 0; x < d.size(); x++) {
                if (d.get(x).isHover() == true) {
                    index++;
                    if (x != i) {
                        d.get(x).setHover(!d.get(x).isHover());
                    }

                }
            }
            if (index > 0) {
                myAdapter.notifyDataSetChanged();//更新索引位置
            }
            //跳转到指定位置
            for (Map.Entry<Integer, String> entry : Titles.entrySet()) {
                if (d.get(i).getLetter().equals(entry.getValue())) {
                    rv.scrollToPosition(entry.getKey());
                    break;
                }
            }

        }
    });
}

    private void initView(View view) {
        Search = (MyEditText) view.findViewById(R.id.Search);
        rv = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        IndexList = (ListView) view.findViewById(R.id.IndexList);
        num = (FontTextView) view.findViewById(R.id.num);

    }

    Closeable closeable;
    String friend;

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public void setCloseable(Closeable closeable) {
        this.closeable = closeable;
    }

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
            mAlertViewExt.show();
            setCloseable(closeable);
            setFriend(data.get(adapterPosition).getAccount());

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
            int height = getResources().getDimensionPixelSize(R.dimen.contact_column_height);


            SwipeMenuItem closeItem = new SwipeMenuItem(getContext())
                    .setBackgroundDrawable(R.drawable.zd_selector)
                    .setText("备注") // 文字，还可以设置文字颜色，大小等。。
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。

        }
    };

    /**
     * 设置索引条
     */
    public void setSideBar() {
        Letter letter = new Letter();
        letter.setHover(true);
        letter.setLetter("#");
        d.add(letter);
        for (Map.Entry<Integer, String> entry : Titles.entrySet()) {
            Letter l = new Letter();
            l.setLetter(entry.getValue());
            d.add(l);
        }
        myAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(Object o, final int position) {

        //判断是否是拓展窗口View，而且点击的是非取消按钮
        if (o == mAlertViewExt && position != AlertView.CANCELPOSITION) {
            final String note = edit.getText().toString();
            if (note.isEmpty()) {
                Toast.makeText(getContext(), "您未填写备注", Toast.LENGTH_SHORT).show();

            } else {
                final ProgressDialog dialog = new ProgressDialog(getContext());
                dialog.setTitle("提示");
                dialog.setMessage("提交申请中");
                dialog.show();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        final String s = helper.updateFriendNote(Account,getFriend(),note);
                        edit.post(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (!s.equals("")) {
                                          if (jsonObject.getString("result").equals("success")){

                                              data.get(position).setUserName(note);
                                              adapter.notifyDataSetChanged();
                                              Titles.clear();
                                              Titles.put(0, data.get(0).getLastName());
                                              for (int i = 1; i < data.size(); i++) {
                                                  if (!data.get(i).getLastName().equals(data.get(i - 1).getLastName())) {
                                                      Titles.put(i, data.get(i).getLastName());
                                                  }

                                              }
                                              rv.invalidateItemDecorations();
                                              d.clear();
                                              setSideBar();
                                              Toast.makeText(getContext(), "修改成功", Toast.LENGTH_LONG).show();

                                          }else{
                                              Toast.makeText(getContext(), "修改失败", Toast.LENGTH_LONG).show();
                                          }
                                    } else {
                                        Toast.makeText(getContext(), "修改失败", Toast.LENGTH_LONG).show();
                                    }

                                    dialog.dismiss();
                                    closeable.smoothCloseMenu();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                        });
                    }
                });



            }
        }
    }

    @Override
    public void onDismiss(Object o) {
    }


}
