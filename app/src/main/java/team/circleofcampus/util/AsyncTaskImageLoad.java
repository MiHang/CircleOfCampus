package team.circleofcampus.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 校园和社团公告list item 封面图片 异步加载
 * @author Jaye Li
 */
public class AsyncTaskImageLoad extends AsyncTask<String, Integer, Drawable> {

    private ImageView imageView = null;

    public AsyncTaskImageLoad(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * 运行在子线程中
     * @param params
     * @return
     */
    @Override
    protected Drawable doInBackground(String... params) {
        try {
            String ImagesPath = params[0];
            if (ImagesPath != null && !ImagesPath.equals("")) {
                JSONArray jsonArray = new JSONArray(ImagesPath);
                if (jsonArray.length() > 0) {
                    JSONObject json = new JSONObject(jsonArray.getString(0));
                    String path = StorageUtil.getStorageDirectory();
                    String filePath = path + json.getString("url");
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    if(bitmap != null) {
                        bitmap = ImageUtil.sizeCompress(bitmap, 208, 114); // 尺寸压缩
                        bitmap = ImageUtil.compressImage(bitmap, 80); // 质量压缩
                        Drawable drawable = new BitmapDrawable(bitmap);
                        return drawable;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 处理结果
     * @param drawable
     */
    @Override
    protected void onPostExecute(Drawable drawable) {
        if(imageView != null && drawable != null) {
            imageView.setImageDrawable(drawable);
        }
        super.onPostExecute(drawable);
    }

}
