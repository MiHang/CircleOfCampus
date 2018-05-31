package coc.team.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import coc.team.home.R;

public class CampusCircleListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<String> items;

    public CampusCircleListViewAdapter(Context context, ArrayList<String> items) {
        inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {

            view = inflater.inflate(R.layout.item_circle_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder)view.getTag();
        }

        holder.title.setText(items.get(i));

        return view;
    }

    class ViewHolder {

        TextView title;
        public ViewHolder(View itemView) {
            title = (TextView) itemView.findViewById(R.id.item_circle_title);
        }
    }
}
