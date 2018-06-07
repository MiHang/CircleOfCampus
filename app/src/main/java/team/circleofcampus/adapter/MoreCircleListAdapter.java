package team.circleofcampus.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import team.circleofcampus.R;
import team.circleofcampus.model.Circle;

/**
 * 更多校园官方公告和社团公告列表适配器
 */
public class MoreCircleListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Circle> circles;

    public MoreCircleListAdapter(Context context, ArrayList<Circle> circles) {
        this.inflater = LayoutInflater.from(context);
        this.circles = circles;
    }

    @Override
    public int getCount() {
        return circles != null ? circles.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return circles != null ? circles.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return circles != null ? circles.get(position).getId() : position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            if (circles.get(position).getId() == -1) {
                convertView = inflater.inflate(R.layout.item_more_circle_time_stamp, null);
                holder = new ViewHolder(convertView, true);
            } else {
                convertView = inflater.inflate(R.layout.item_circle_list, null);
                holder = new ViewHolder(convertView, false);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (circles.get(position).getId() == -1) {
            holder.timestamp.setText(circles.get(position).getPublishTime());
        } else {
            holder.title.setText(circles.get(position).getTitle());
        }

        return convertView;
    }

    class ViewHolder {
        ImageView cover;
        TextView title;
        TextView activityTime;
        TextView venue;
        TextView timestamp; // 时间戳
        public ViewHolder (View itemView, boolean isTimestamp) {
            if (isTimestamp) {
                timestamp = (TextView) itemView.findViewById(R.id.item_more_time_stamp_text);
            } else {
                cover = (ImageView) itemView.findViewById(R.id.item_circle_image);
                title = (TextView) itemView.findViewById(R.id.item_circle_title);
                activityTime = (TextView) itemView.findViewById(R.id.item_circle_activity_time);
                venue = (TextView) itemView.findViewById(R.id.item_circle_venue);
            }
        }
    }
}
