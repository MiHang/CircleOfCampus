package coc.team.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 自定义MyListView，用于解决ListView高度自适应和ScrollView冲突的问题<br/>
 * 注: ListView的每个Item必须是LinearLayout，
 * 因其他Layout没有重写onMeasure()，所以会在onMeasure()时抛出异常。
 */

public class MyListView extends ListView {

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }


    /**
     * 计算listView的每个Item<br/>
     * 此方法要在设置LIstView的Adapter后调用
     */
    public void setListViewHeightBasedOnChildren() {

        // 获取ListView对应的Adapter
        ListAdapter listAdapter = this.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目

            View listItem = listAdapter.getView(i, null, this);

            listItem.measure(0, 0); // 计算子项View 的宽高

            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = this.getLayoutParams();

        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        params.height = totalHeight
                + (this.getDividerHeight() * (listAdapter.getCount() - 1));

        this.setLayoutParams(params);

    }


}
