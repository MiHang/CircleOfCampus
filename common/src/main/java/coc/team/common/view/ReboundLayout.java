package coc.team.common.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.Scroller;

/**
 * 具有回弹效果的Layout<br/>
 * 支持LinearLayout，FrameLayout，RelativeLayout，ListView，ScrollView，RecycleView
 */
public class ReboundLayout extends ViewGroup {

    /**
     * 拉动阻尼
     */
    private static final float PULL_DAMP = 0.2f;
    /**
     * Scroller滑动的速度
     */
    private static final int SCROLL_SPEED = 80;
    /**
     * 用于滑动的Scroller对象
     */
    private Scroller mLayoutScroller;
    /**
     * 用户滑动时触发操作的最小滑动距离
     */
    private int minTouchSlop;
    /**
     * 手指按下时的Y坐标
     */
    private int downY;
    /**
     * 手指上一次按下时的Y坐标
     */
    private int lastDwonY;

    public ReboundLayout(Context context) {
        this(context, null);
    }

    public ReboundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReboundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化Scroller,第二个参数是一个匀速插值器
        mLayoutScroller = new Scroller(context, new LinearInterpolator(context, null));

        // 计算用户滑动时触发操作的最小滑动距离
        minTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 测量View尺寸
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 遍历进行子视图测量
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 布局
     * @param b
     * @param i
     * @param i1
     * @param i2
     * @param i3
     */
    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        int mLayoutContentHeight = 0; // ViewGroup的内容高度
        int lastChildIndex = getChildCount() - 1;

        // 遍历进行子视图的置位工作
        for (int index = 0; index < getChildCount(); index++) {

            // 内容视图按由上到下的顺序在垂直方向进行排列
            View child = getChildAt(index);
            child.layout(0, mLayoutContentHeight, child.getMeasuredWidth(), mLayoutContentHeight + child.getMeasuredHeight());
            if (index <= lastChildIndex) {
                if (child instanceof ScrollView) {
                    mLayoutContentHeight += getMeasuredHeight();
                    continue;
                }
                mLayoutContentHeight += child.getMeasuredHeight();
            }
        }
    }

    /**
     * 触摸事件拦截
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        boolean intercept = false; // 是否拦截touch事件
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN : {
                downY = y; // 记录手指按下的Y坐标
            };break;
            case MotionEvent.ACTION_MOVE: { // 手指移动

                if (y > lastDwonY) { // 手指向下滑动

                    // 获取最顶部的子视图
                    View child = getChildAt(0);
                    if (child instanceof AdapterView) {
                        intercept = isAdapterViewTop(child);
                    } else if (child instanceof ScrollView) {
                        intercept = isScrollViewTop(child);
                    } else if (child instanceof RecyclerView) {
                        intercept = isRecyclerViewTop(child);
                    }

                } else if (y < lastDwonY) { // 手指向上滑动

                    // 获取最底部的子视图
                    View child = getChildAt(getChildCount() - 1);
                    if (child instanceof AdapterView) {
                        intercept = isAdapterViewBottom(child);
                    } else if (child instanceof ScrollView) {
                        intercept = isScrollViewBottom(child);
                    } else if (child instanceof RecyclerView) {
                        intercept = isRecyclerViewBottom(child);
                    }

                } else {
                    intercept = false;
                }

            };break;
            case MotionEvent.ACTION_UP: { // 手指抬起
                intercept = false;
            };break;
        }

        lastDwonY = y;
        return intercept;
    }

    /**
     * 手指触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE : {
                int offset = downY - y;
                if (Math.abs(offset) >= minTouchSlop) {
                    scrollBy(0, (int)(offset * PULL_DAMP)); // 移动布局位置
                    downY = y;
                }
            };break;
            case MotionEvent.ACTION_UP : {
                mLayoutScroller.startScroll(0, getScrollY(), 0, -getScrollY(), SCROLL_SPEED);
            };break;
        }

        postInvalidate();
        return true;
    }

    /**
     * 移动Layout
     */
    public void computeScroll() {
        if (mLayoutScroller.computeScrollOffset()) {
            scrollTo(mLayoutScroller.getCurrX(), mLayoutScroller.getCurrY());
            postInvalidate(); // 刷新Layout
        }
    }

    /**
     * 判断ListView是否已经到达内容最顶部
     * @return
     */
    private boolean isAdapterViewTop(View view) {

        boolean intercept = true;
        AdapterView adapterChild = (AdapterView) view;

        // 如果没有达到最顶端
        if (adapterChild.getFirstVisiblePosition() != 0 || adapterChild.getChildAt(0).getTop() != 0) {
            intercept = false;
        }

        return intercept;
    }

    /**
     * 判断ListView是否已经到达内容最底部
     * @param view
     * @return
     */
    private boolean isAdapterViewBottom(View view) {

        boolean intercept = false;
        AdapterView adapterChild = (AdapterView) view;

        // 如果到达底部，则拦截事件
        if (adapterChild.getLastVisiblePosition() == adapterChild.getCount() - 1
                && (adapterChild.getChildAt(adapterChild.getChildCount() - 1).getBottom() == getMeasuredHeight())) {
            intercept = true;
        }

        return intercept;
    }

    /**
     * 判断ScrollView是否已经到达内容最顶部
     * @param view
     * @return
     */
    private boolean isScrollViewTop(View view) {

        boolean intercept = false;
        if (view.getScrollY() == 0) {
            intercept = true;
        }
        return intercept;
    }

    /**
     * 判断ScrollView是否已经到达内容最底部
     * @param view
     * @return
     */
    private boolean isScrollViewBottom(View view) {

        boolean intercept = false;
        ScrollView sv = (ScrollView) view;
        if (sv.getScrollY() + sv.getHeight() - sv.getPaddingTop() - sv.getPaddingBottom()
                == sv.getChildAt(0).getHeight()) {
            intercept = true;
        }
        return intercept;
    }

    /**
     * 判断RecyclerView是否已经到达内容最顶部
     * @param view
     * @return
     */
    private boolean isRecyclerViewTop(View view) {

        boolean intercept = false;

        RecyclerView recyclerChild = (RecyclerView) view;
        if (recyclerChild.computeVerticalScrollOffset()<=0) intercept = true;

        return intercept;
    }

    /**
     * 判断RecyclerView是否已经到达内容最底部
     * @param view
     * @return
     */
    private boolean isRecyclerViewBottom(View view) {

        boolean intercept = false;

        RecyclerView recyclerChild = (RecyclerView) view;
        if (recyclerChild.computeVerticalScrollExtent() + recyclerChild.computeVerticalScrollOffset()
                >= recyclerChild.computeVerticalScrollRange()) {
            intercept = true;
        }

        return intercept;
    }

}
