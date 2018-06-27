package team.circleofcampus.adapter;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import team.circleofcampus.R;
import team.circleofcampus.http.HttpRequest;
import team.circleofcampus.model.Circle;

/**
 * 更多校园官方公告和社团公告列表适配器
 */
public class MoreCircleListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<Circle> circles;
    private Context context;

    public MoreCircleListAdapter(Context context, ArrayList<Circle> circles) {
        this.context = context;
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
            loadImage(holder.cover, circles.get(position).getImagesUrl());
            holder.title.setText(circles.get(position).getTitle());
            holder.activityTime.setText("时 间：" + circles.get(position).getActivityTime());
            holder.venue.setText("地 点：" + circles.get(position).getVenue());
        }

        return convertView;
    }

    /**
     * 异步加载图片
     * @param imageView
     * @param url
     */
    private void loadImage(final ImageView imageView, final String url) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (url != null && !url.equals("")) {
                        JSONArray jsonArray = new JSONArray(url);
                        if (jsonArray.length() > 0) {
                            JSONObject json = new JSONObject(jsonArray.getString(0));
                            final String imgUrl = HttpRequest.URL + json.getString("url");
                            imageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(context)
                                            .load(imgUrl)
                                            .placeholder(R.drawable.img_loading_failed)
                                            .error(R.drawable.img_loading_failed)
                                            .into(imageView);
                                }
                            });
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        });
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
