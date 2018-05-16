package coc.team.home.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import coc.team.home.GlideCircleTransform;
import coc.team.home.Interface.OnItemClickListener;
import coc.team.home.R;
import coc.team.home.model.UserMsg;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 消息列表适配器
 */

public class MyMessageAdapter extends SwipeMenuAdapter<MyMessageAdapter.ViewHolder> implements View.OnClickListener {

    Context context;
    List<UserMsg> data;
    OnItemClickListener mOnItemClickListener;
    SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
    String path="http://192.168.1.157:8080";

    public MyMessageAdapter(Context context, List<UserMsg> data) {
        this.context = context;
        this.data = data;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(context).inflate(R.layout.msg_item, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context)
                .load(path+"/res/img/456.png")
                .transform(new GlideCircleTransform(context))
                .override(100,100)
                .transform(new GlideCircleTransform(context))
                .crossFade()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {


                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.icon);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
               final String s=getUserInfo("87654321@qq.com");
                Log.e("tag",s);

                holder.name.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            holder.name.setText(s);
                            JSONObject jsonObject=new JSONObject(s);
                            Log.e("tag","性别"+jsonObject.getString("Sex"));
                            holder.name.setText(jsonObject.getString("UserName" ));
                            Toast.makeText(context, "性别"+jsonObject.getString("Sex"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });


        holder.msg.setText(data.get(position).getMsg());
        holder.Amount.setText(data.get(position).getAmount()+"条消息");
        holder.time.setText(sdf.format(data.get(position).getDate()));
        holder.itemView.setOnClickListener(this);
        holder.itemView.setTag(position);

    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick((Integer) view.getTag());
        }
    }


     class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;
        public TextView msg;
        public TextView Amount;
        public TextView time;

        public ViewHolder(View rootView) {
            super(rootView);
            this.icon = (ImageView) rootView.findViewById(R.id.icon);
            this.name = (TextView) rootView.findViewById(R.id.name);
            this.msg = (TextView) rootView.findViewById(R.id.msg);
            this.Amount = (TextView) rootView.findViewById(R.id.amount);
            this.time = (TextView) rootView.findViewById(R.id.time);
        }

    }

    public String getUserInfo(String Account){
        OkHttpClient okHttpClient=new OkHttpClient();
        MediaType mediaType=MediaType.parse("application/json;charset=utf8");
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("Account" ,Account);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(mediaType,jsonObject.toString());
        Request request=new Request.Builder().url(path+"/coc/UserNameAndSexQuery.do").post(requestBody).build();
        try {
            Response response=okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}

