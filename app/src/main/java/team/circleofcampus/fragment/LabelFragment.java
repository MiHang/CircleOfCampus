package team.circleofcampus.fragment;


/**
 * Created by 惠普 on 2018-03-10.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import team.circleofcampus.Interface.LabelFragmentListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.MyGridViewAdapter;
import team.circleofcampus.model.LabelIcon;
import team.circleofcampus.view.CustomViewPager;


public class LabelFragment extends Fragment {


    ImageView[] pointView;

    CustomViewPager ViewPager;
    LinearLayout ViewGroup;

    private int totalPage;//总的页数
    private int PageSize = 5;//每页显示的最大数量
    private List<LabelIcon> data;//总的数据源
    List<GridView> gridViews;
    LabelFragmentListener Listener;

    public LabelFragmentListener getListener() {
        return Listener;
    }

    public void setListener(LabelFragmentListener listener) {
        Listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             android.view.ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.operation_panel, container, false);
        ViewGroup = (LinearLayout) view.findViewById(R.id.ViewGroup);
        ViewPager = (CustomViewPager) view.findViewById(R.id.MyViewPager);


        setDataAndAdapter();




        //小点的相关设置
        setLabelPoint();

        return view;

    }

    /**
     * 设置适配器
     */
    public void setDataAndAdapter() {

        data = new ArrayList<>();
        int[] Icon = new int[]{
                R.drawable.ablum,
                R.drawable.take_photo,
                R.drawable.card,
                R.drawable.location,
        };
        String[] IconName = new String[]{
                "相册",
                "相机",
                "个人名片",
                "位置",
        };
        for (int i = 0; i < IconName.length; i++) {
            LabelIcon labelIcons = new LabelIcon();
            labelIcons.setIcon(Icon[i]);
            labelIcons.setIconName(IconName[i]);
            data.add(labelIcons);
        }

        gridViews = new ArrayList<>();


        totalPage = (int) Math.ceil(data.size() * 1.0 / PageSize);//总数据÷每页最大显示数量
        for (int i = 0; i < totalPage; i++) {
            GridView gv = new GridView(getContext());
            gv.setHorizontalSpacing(10);
            gv.setVerticalSpacing(10);
            gv.setNumColumns(4);
            gv.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除点击时的背景色

            gv.setAdapter(new MyGridViewAdapter(getContext(),
                    data, i, PageSize));
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (data.get(position).getIconName().equals("相机")) {
//                        Intent intent = new Intent(getContext(), TakePhotoActivity.class);
//                        startActivityForResult(intent, 1);
                    }
                    Toast.makeText(getContext(), "您点击了" + data.get(position).getIconName(), Toast.LENGTH_LONG).show();
                }
            });
            gridViews.add(gv);
        }

        ViewPager.setAdapter(new MyPagerAdapter(getContext(), gridViews));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (intent != null) {
            if (Listener!=null){
                Listener.OnReceive(intent);
            }
        }
    }

    /**
     * 小点的设置与监听
     */
    private void setLabelPoint() {
        //添加"·"的图片
        pointView = new ImageView[totalPage];
        for (int i = 0; i < totalPage; i++) {
            ImageView view = new ImageView(getContext());
            pointView[i] = view;
            pointView[i].setOnClickListener(new MyClickListener(i));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            ViewGroup.addView(view, layoutParams);
        }
        setPointState(0);
        //监听ViewPager的变化，从而更新·(小点)
        ViewPager.addOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPointState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    /**
     * 设置选中的"·"的状态，是否选中
     *
     * @param selectItems
     */
    private void setPointState(int selectItems) {
        for (int i = 0; i < totalPage; i++) {
            if (i == selectItems) {
                pointView[i].setBackgroundResource(R.drawable.point_checked);
            } else {
                pointView[i].setBackgroundResource(R.drawable.point_normal);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }


    /**
     * 自定义类实现PagerAdapter，填充显示数据
     */
    class MyPagerAdapter extends PagerAdapter {
        List<GridView> gridViews;
        Context context;

        public MyPagerAdapter(Context context, List<GridView> gridViews) {
            this.context = context;
            this.gridViews = gridViews;
        }

        // 显示多少个页面
        @Override
        public int getCount() {
            return gridViews.size();
        }

        // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        // 初始化显示的条目对象
        @Override
        public Object instantiateItem(android.view.ViewGroup container, final int position) {
            // return super.instantiateItem(container, position);
            container.addView(gridViews.get(position)); // 添加到ViewPager容器

            return gridViews.get(position);
        }


        // 销毁条目对象
        @Override
        public void destroyItem(android.view.ViewGroup container, int position, Object object) {
            // super.destroyItem(container, position, object);
            container.removeView(gridViews.get(position));
        }
    }


    class MyClickListener implements View.OnClickListener {
        int Position;

        public MyClickListener(int Position) {
            this.Position = Position;
        }

        @Override
        public void onClick(View view) {
            ViewPager.setCurrentItem(Position);
        }
    }

}