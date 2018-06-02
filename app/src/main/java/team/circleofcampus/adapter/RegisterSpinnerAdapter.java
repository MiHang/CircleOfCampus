package team.circleofcampus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import team.circleofcampus.R;
import team.circleofcampus.model.RegisterSpinnerItem;

/**
 * 注册页面选择学校和院系Spinner item 适配器
 */
public class RegisterSpinnerAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<RegisterSpinnerItem> items;

    public RegisterSpinnerAdapter (Context context, ArrayList<RegisterSpinnerItem> items) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getItemId();
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

        holder.itemName.setText(items.get(position).getItemName());

        return convertView;
    }

    class ViewHolder {
        TextView itemName;
        public ViewHolder (View itemView) {
            itemName = (TextView) itemView.findViewById(R.id.item_register_spinner_text);
        }
    }
}
