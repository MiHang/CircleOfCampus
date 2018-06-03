package team.circleofcampus.adapter;

/**
 * Created by 惠普 on 2018-03-11.
 */


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.List;

import team.circleofcampus.R;
import team.circleofcampus.model.LabelIcon;

/**
 * GridView加载数据的适配器
 *
 * @author Administrator
 */
public class MyGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<LabelIcon> data;//数据源
    private int Index; // 页数下标，标示第几页，从0开始
    private int PageSize;// 每页显示的最大的数量


    public MyGridViewAdapter(Context context, List<LabelIcon> data,
                             int Index, int PageSize) {
        this.context = context;
        this.data = data;
        this.Index = Index;
        this.PageSize = PageSize;
    }

    /**
     * 先判断数据及的大小是否显示满本页lists.size() > (mIndex + 1)*mPagerSize
     * 如果满足，则此页就显示最大数量lists的个数
     * 如果不够显示每页的最大数量，那么剩下几个就显示几个
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size() > (Index + 1) * PageSize ?
                PageSize : (data.size() - Index * PageSize);
    }

    @Override
    public LabelIcon getItem(int arg0) {
        // TODO Auto-generated method stub
        return data.get(arg0 + Index * PageSize);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0 + Index * PageSize;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.label_icon, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //重新确定position因为拿到的总是数据源，数据源是分页加载到每页的GridView上的
        final int pos = position + Index * PageSize;//假设mPageSiez
        //假设mPagerSize=8，假如点击的是第二页（即mIndex=1）上的第二个位置item(position=1),那么这个item的实际位置就是pos=9
        holder.Icon_Name.setText(data.get(pos).getIconName() + "");
        holder.Icon.setImageResource(data.get(pos).getIcon());

        return view;
    }


    class ViewHolder {
        public View rootView;
        public ImageView Icon;
        public TextView Icon_Name;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.Icon = (ImageView) rootView.findViewById(R.id.Icon);
            this.Icon_Name = (TextView) rootView.findViewById(R.id.Icon_Name);
        }

    }
}