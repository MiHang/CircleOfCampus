package team.circleofcampus.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import team.circleofcampus.R;
import team.circleofcampus.pojo.SocietyCircle;
import team.circleofcampus.util.StorageUtil;

/**
 * 首页社团圈公告展示列表
 */
public class SocietyCircleListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<SocietyCircle> items;

    public SocietyCircleListViewAdapter(Context context, List<SocietyCircle> items) {
        inflater = LayoutInflater.from(context);
        this.items = items;
    }

    @Override
    public int getCount() {
        return (items != null ? items.size() : 0);
    }

    @Override
    public Object getItem(int i) {
        return (items != null ? items.get(i) : null);
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

        // 加载公告封面
        if (items.get(i).getImagesUrl() != null && !items.get(i).getImagesUrl().equals("")) {
            try {
                JSONArray jsonArray = new JSONArray(items.get(i).getImagesUrl());
                if (jsonArray.length() > 0) {
                    JSONObject json = new JSONObject(jsonArray.getString(0));
                    String path = StorageUtil.getStorageDirectory();
                    String filePath = path + json.getString("url");
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    Drawable drawable = new BitmapDrawable(bitmap);
                    holder.cover.setImageDrawable(drawable);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        holder.title.setText(items.get(i).getTitle());
        holder.activityTime.setText(items.get(i).getActivityTime());
        holder.venue.setText(items.get(i).getVenue());

        return view;
    }

    class ViewHolder {
        ImageView cover;
        TextView title;
        TextView activityTime;
        TextView venue;
        public ViewHolder (View itemView) {
            cover = (ImageView) itemView.findViewById(R.id.item_circle_image);
            title = (TextView) itemView.findViewById(R.id.item_circle_title);
            activityTime = (TextView) itemView.findViewById(R.id.item_circle_activity_time);
            venue = (TextView) itemView.findViewById(R.id.item_circle_venue);
        }
    }
}
