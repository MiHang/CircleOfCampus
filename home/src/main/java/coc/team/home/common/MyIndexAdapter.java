package coc.team.home.common;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import coc.team.home.R;

/**
 * Created by 惠普 on 2018-05-08.
 */

public class MyIndexAdapter extends BaseAdapter {
    Context context;
    List<Letter> data;

    public MyIndexAdapter(Context context,
                          List<Letter> data) {
        this.context = context;
        this.data = data;

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.letter_item, null);
            vh=new ViewHolder(view);
            view.setTag(vh);
        }else{
            vh= (ViewHolder) view.getTag();
        }
        vh.letter.setText(data.get(i).getLetter());
        if (data.get(i).isHover){//经过时颜色
            vh.letter.setTextColor(context.getResources().getColor(data.get(i).getHover_TextColor()));
            vh.letter.setBackgroundResource(data.get(i).getHover_bg());
        }else{//默认颜色
            vh.letter.setTextColor(context.getResources().getColor(data.get(i).getTextColor()));
            vh.letter.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }


    class ViewHolder {
        public View rootView;
        public TextView letter;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.letter = (TextView) rootView.findViewById(R.id.letter);
        }

    }

}