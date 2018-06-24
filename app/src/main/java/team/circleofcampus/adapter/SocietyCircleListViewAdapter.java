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
import team.circleofcampus.util.AsyncTaskImageLoad;
import team.circleofcampus.util.ImageUtil;
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
        LoadImage(holder.cover, items.get(i).getImagesUrl());
        holder.title.setText(items.get(i).getTitle());
        holder.activityTime.setText("时 间：" + items.get(i).getActivityTime());
        holder.venue.setText("地 点：" + items.get(i).getVenue());

        return view;
    }

    /**
     * 异步加载图片资源
     * @param imageView - 图片控件
     * @param imagesPath - 图片路径
     */
    private void LoadImage(ImageView imageView, String imagesPath) {
        AsyncTaskImageLoad async = new AsyncTaskImageLoad(imageView);
        async.execute(imagesPath);
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
