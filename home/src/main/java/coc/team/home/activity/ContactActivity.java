package coc.team.home.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.common.utils.LanguageUtils;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import coc.team.home.Interface.OnItemClickListener;
import coc.team.home.R;
import coc.team.home.model.Contact;
import coc.team.home.model.Letter;
import coc.team.home.common.MyEditText;
import coc.team.home.common.MyIndexAdapter;
import coc.team.home.adapter.GoodFriendItemDecoration;
import coc.team.home.adapter.GoodFriendAdapter;

public class ContactActivity extends AppCompatActivity {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();

        //设置布局管理器
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);


        //设置增加或删除条目的动画
        rv.setItemAnimator(new DefaultItemAnimator());

        List<Contact> list = new ArrayList<Contact>();

        Contact contact1 = new Contact();
        contact1.setUserName("啊爬子");
        contact1.setAccount("1");
        list.add(contact1);
        Contact contact2 = new Contact();
        contact2.setUserName("王贤");
        contact2.setAccount("2");
        list.add(contact2);
        Contact contact4 = new Contact();
        contact4.setUserName("林成功");
        contact4.setAccount("111");
        list.add(contact4);
        Contact contact5 = new Contact();
        contact5.setUserName("流");
        contact5.setAccount("1211");
        list.add(contact5);
        Contact contact6 = new Contact();
        contact6.setUserName("宁");
        contact6.setAccount("111111");
        list.add(contact6);

        Contact contact7 = new Contact();
        contact7.setUserName("成员");
        contact7.setAccount("123");
        list.add(contact7);

        Contact contact8 = new Contact();
        contact8.setUserName("比员");
        contact8.setAccount("123");
        list.add(contact8);

        Contact contact11 = new Contact();
        contact11.setUserName("第员");
        contact11.setAccount("123");
        list.add(contact11);
        Contact contact12 = new Contact();
        contact12.setUserName("额员");
        contact12.setAccount("123");
        list.add(contact12);

        Contact contact22 = new Contact();
        contact22.setUserName("飞员");
        contact22.setAccount("123");
        list.add(contact22);
        Contact contact32 = new Contact();
        contact32.setUserName("个员");
        contact32.setAccount("123");
        list.add(contact32);
        Contact contact42 = new Contact();
        contact42.setUserName("b员");
        contact42.setAccount("123");
        list.add(contact42);

        Contact contact9 = new Contact();
        contact9.setUserName("次员");
        contact1.setAccount("123");
        list.add(contact9);



        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserName() != null) {
                String lastName = list.get(i).getUserName().toUpperCase().charAt(0) + "";
                if (languageUtils.isChinese(list.get(i).getUserName())) {
                    lastName = languageUtils.toEnglish(list.get(i).getUserName().charAt(0));
                }

                Contact contact = new Contact();
                contact.setLastName(lastName);

                contact.setUserIcon(list.get(i).getUserIcon());
                contact.setUserName(list.get(i).getUserName());
                contact.setAccount(list.get(i).getAccount());
                data.add(contact);
            }
        }
        Collections.sort(data);//按字母顺序排序

        adapter = new GoodFriendAdapter(this, data);

        //标题
        Titles.put(0, data.get(0).getLastName());
        for (int i = 1; i < data.size(); i++) {
            if (!data.get(i).getLastName().equals(data.get(i - 1).getLastName())) {
                Titles.put(i, data.get(i).getLastName());
            }

        }


        GoodFriendItemDecoration itemDecoration = new GoodFriendItemDecoration(getApplicationContext());
        itemDecoration.setTitles(Titles);
        rv.addItemDecoration(itemDecoration);



        rv.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        rv.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        // 为SwipeRecyclerView的Item创建菜单就两句话，不错就是这么简单：
        // 设置菜单创建器。
        rv.setSwipeMenuCreator(swipeMenuCreator);
        // 设置菜单Item点击监听。
        rv.setSwipeMenuItemClickListener(menuItemClickListener);


        rv.setAdapter(adapter);
        adapter.setItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(ContactActivity.this, ""+position, Toast.LENGTH_SHORT).show();
            }
        });

        setSideBar();

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

    }

    private void initView() {
        Search = (MyEditText) findViewById(R.id.Search);
        rv = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
        IndexList = (ListView) findViewById(R.id.IndexList);
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
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(getApplicationContext(), "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(getApplicationContext(), "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
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




            SwipeMenuItem closeItem = new SwipeMenuItem(getApplicationContext())
                    .setBackgroundDrawable(R.drawable.selector_purple)
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
        myAdapter = new MyIndexAdapter(this, d);
        IndexList.setAdapter(myAdapter);
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


}
