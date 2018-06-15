package team.circleofcampus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team.circleofcampus.R;
import team.circleofcampus.model.SpinnerItem;

/**
 * 注册页面选择学校和院系Spinner item 适配器
 */
public class MySpinnerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<SpinnerItem> items;

    public MySpinnerAdapter(Context context, ArrayList<SpinnerItem> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        if (items.size() > 0) {
            return items.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (items.size() > 0) {
            return items.get(position).getItemId();
        }
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_register_spinner, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (items.size() > 0) holder.itemName.setText(items.get(position).getItemName());

        return convertView;
    }

    class ViewHolder {
        TextView itemName;
        public ViewHolder (View itemView) {
            itemName = (TextView) itemView.findViewById(R.id.item_register_spinner_text);
        }
    }
}
