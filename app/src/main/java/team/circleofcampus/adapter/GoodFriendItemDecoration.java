package team.circleofcampus.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import team.circleofcampus.R;


/**
 * Created by 惠普 on 2018-01-29.
 */

public class GoodFriendItemDecoration extends RecyclerView.ItemDecoration {
    private int dividerHeight;//分割线高度
    private int dividerPadding=15;//分割线左右距离
    int TitleHeight=50;//字母索引栏高度
    private Paint TextPaint;//绘制文本
    private Paint BgPaint;//绘制背景
    private Paint  dividerPaint;//绘制分割线
    private float TextHeight;//文本高度
    private float TextBottom;
    private Context Contxt;
    public Map<Integer, String> getTitles() {
        return Titles;
    }

    public void setTitles(Map<Integer, String> titles) {
        Titles = titles;
    }

    private Map<Integer,String> Titles=new HashMap<>();//存放所有标题的位置


    public GoodFriendItemDecoration(Context context) {

        this.Contxt=context;
        TextPaint=new Paint();
        TextPaint.setAntiAlias(true);
        TextPaint.setDither(true);
        TextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16,Contxt.getResources().getDisplayMetrics()));
        TextPaint.setColor(Contxt.getResources().getColor(R.color.titleColor));
        Paint.FontMetrics fm=TextPaint.getFontMetrics();
        TextHeight=fm.bottom-fm.top;//计算文字高度
        TextBottom=fm.bottom;

        BgPaint=new Paint();
        BgPaint.setAntiAlias(true);
        BgPaint.setDither(true);
        BgPaint.setColor(Contxt.getResources().getColor(R.color.titleBgColor));

        dividerPaint=new Paint();
        dividerPaint.setDither(true);
        dividerPaint.setAntiAlias(true);
        dividerHeight= (int) Contxt.getResources().getDimension(R.dimen.divide_line_height);
        dividerPaint.setColor(Contxt.getResources().getColor(R.color.dividerColor));
    }


    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state){

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int top=0;
        int bottom=0;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child=parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if(!getTitles().containsKey(params.getViewLayoutPosition())){
                //绘制分割线

                top=child.getTop()-params.topMargin-dividerHeight;
                bottom=top+dividerHeight;
                dividerPaint.setStyle(Paint.Style.FILL);
                c.drawRect(left+dividerPadding, top,
                        right-dividerPadding, bottom,dividerPaint);

            }else{ //绘制标题
                //绘制标题背景
                top=child.getTop()-params.topMargin-TitleHeight;
                bottom=top+TitleHeight;
                c.drawRect(left,top,right,bottom,BgPaint);
                //绘制标题文字
                float x= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,Contxt.getResources().getDisplayMetrics());
                float y=bottom - (TitleHeight - TextHeight) / 2 - TextBottom;//计算文字baseLine
                c.drawText(getTitles().get(params.getViewLayoutPosition()),x,y,TextPaint);
            }
        }

    }


    private String getPosition(int position) {
        while (position >= 0) {
            if (getTitles().containsKey(position)) {
                return getTitles().get(position);
            }
            position--;
        }
        return null;
    }

    /**
     * 绘制在内容的上面，覆盖内容
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        int FirstLine=((LinearLayoutManager)parent.getLayoutManager()).findFirstVisibleItemPosition();
        if(FirstLine==RecyclerView.NO_POSITION){
            return;
        }
        String title=getPosition(FirstLine);
        if(TextUtils.isEmpty(title)){
            return;
        }
        boolean flag=false;
        if(getPosition(FirstLine+1)!=null
                &&!title.equals(getPosition(FirstLine+1))){
            //说明是当前组最后一个元素，但不一定碰撞了
            View child=parent.findViewHolderForAdapterPosition(FirstLine).itemView;
            if(child.getTop()+child.getMeasuredHeight()<TitleHeight){

                //进一步检测碰撞
                c.save();//保存画布当前的状态
                flag=true;
                c.translate(0,child.getTop()+child.getMeasuredHeight()-TitleHeight);//负的代表向上
            }
        }
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int top=parent.getPaddingTop();
        int bottom=top+TitleHeight;
        c.drawRect(left,top,right,bottom,BgPaint);
        float x=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,Contxt.getResources().getDisplayMetrics());
        float y=bottom - (TitleHeight - TextHeight) / 2 - TextBottom;//计算文字baseLine
        c.drawText(title,x,y,TextPaint);
        if(flag){
            c.restore(); //还原画布为初始状态
        }
    }


    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        super.getItemOffsets(outRect, view, parent, state);
        int pos=parent.getChildViewHolder(view).getAdapterPosition();
        //是否标题栏的位置
        if (getTitles().containsKey(pos)){//是,留出头部偏移
            outRect.set(0,TitleHeight,0,0);
        }else{
            outRect.set(0,dividerHeight,0,0);
        }
    }


}