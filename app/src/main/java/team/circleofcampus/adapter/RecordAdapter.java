package team.circleofcampus.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import com.common.model.Message;
import com.common.utils.ByteUtils;
import com.common.utils.ImgUtils;
import com.common.utils.Symbol;
import com.example.library.Utils.EmojiData;
import com.example.library.disPlayGif.AnimatedGifDrawable;
import com.example.library.disPlayGif.AnimatedImageSpan;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team.circleofcampus.Interface.RecordItemListener;
import team.circleofcampus.R;
import team.circleofcampus.view.DialogTextView;
import team.circleofcampus.view.IconImageView;
import team.circleofcampus.view.RoundRectImageView;

/**
 * Created by 惠普 on 2018-05-23.
 */

public class RecordAdapter extends BaseAdapter {
    Context context;
    List<Message> data;
    List<Map<String, Integer>> emojis = new ArrayList<>();
    ImgUtils utils;
    RecordItemListener listener;
    ByteUtils byteUtils = new ByteUtils();
    IconImageView icon;
    Bitmap bp = null;

    public RecordAdapter(Context context, List<Message> data) {
        this.context = context;
        this.data = data;
        utils = new ImgUtils(context);
        Map<String, Integer> map = new EmojiData().initDatas();//获取表情包资源
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Map<String, Integer> m = new HashMap<>();
            m.put(entry.getKey(), entry.getValue());
            emojis.add(m);

        }
    }

    public void setListener(RecordItemListener listener) {
        this.listener = listener;
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


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder vh;

        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_msg, null);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) view.getTag();
        }
        final Message msg = data.get(i);



        if (msg.getMsg().getImg() != null) {//图片不为空
            bp = byteUtils.BytesToBitmap(msg.getMsg().getImg());
        }
        icon=vh.Send_Icon;
        String account=msg.getMsg().getSend();
        int res=R.drawable.woman;
        if (msg.getReceive() == Symbol.Receive_Mode) {//接收
            icon=vh.Receive_Icon;
            account=msg.getMsg().getReceive();
        }
        if (msg.getMsg().getSex()==null||msg.getMsg().getSex().equals("male")) {
            res=R.drawable.man;

        }
        if (icon.getDrawable()==null) {

            //加载头像
            Glide.with(context)
                    .load("http://192.168.1.157:8080/res/img/" + account)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(res)
                    .placeholder(R.drawable.man)
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                           return false;
                        }
                    })
                    .into(icon);


        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断是否显示时间
                if (i != 0) {
                    if (msg.getMsg().getDate() == null || data.get(i - 1).getMsg().getDate() == null) {
                        vh.Time.setText(msg.getMsg().getDate());
                        vh.Time.setVisibility(View.VISIBLE);
                    } else {
                        if (DisPlayTime(msg.getMsg().getDate(), data.get(i - 1).getMsg().getDate())) {
                            vh.Time.setText(msg.getMsg().getDate());
                            vh.Time.setVisibility(View.VISIBLE);
                        } else {
                            vh.Time.setVisibility(View.GONE);
                        }
                    }

                } else {
                    vh.Time.setVisibility(View.VISIBLE);
                    vh.Time.setText(msg.getMsg().getDate());
                }
                if (msg.getReceive() == Symbol.Receive_Mode) {//接收
                    vh.Receive_dialog.setVisibility(View.VISIBLE);
                    vh.Send_dialog.setVisibility(View.GONE);



                    if (msg.getMsg().getText() != null) {//接收文本消息
                        vh.Receive_Duration.setVisibility(View.GONE);
                        vh.Receive_Msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        if (!convertNormalStringToSpannableString(msg.getMsg().getText(), vh.Receive_Msg)) {
                            vh.Receive_Msg.setText(msg.getMsg().getText());
                        }

                    } else if (msg.getMsg().getAudio() != null) {//接收语音消息
                        vh.Receive_Msg.setText("");
                        if (msg.getNew() == Symbol.NewMode) {
                            vh.Receive_Duration.setVisibility(View.VISIBLE);
                        } else {
                            vh.Receive_Duration.setVisibility(View.GONE);
                        }

                        vh.Receive_Msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.receive_audio, 0, 0, 0);
                        vh.Receive_Duration.setText(msg.getMsg().getDuration());

                    } else if (msg.getMsg().getImg() != null) {
                        vh.Receive_Img.setImageBitmap(bp);
                        vh.Receive_Layout.setVisibility(View.GONE);
                        vh.Receive_Img.setVisibility(View.VISIBLE);

                    }
                    vh.Receive_Msg.setOnClickListener(new ClickListener(i, ""));
                    vh.Receive_Icon.setOnClickListener(new ClickListener(i, "Icon"));
                } else {//发送


                    vh.Send_dialog.setVisibility(View.VISIBLE);
                    vh.Receive_dialog.setVisibility(View.GONE);

                    if (msg.getMsg().getText() != null) {
                        vh.Send_Duration.setVisibility(View.GONE);
                        vh.Send_Msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        if (!convertNormalStringToSpannableString(msg.getMsg().getText(), vh.Send_Msg)) {
                            vh.Send_Msg.setText(msg.getMsg().getText());
                        }

                    }
                    if (msg.getMsg().getAudio() != null) {//发送语音消息
                        vh.Send_Msg.setText("");
                        if (msg.getNew() == Symbol.NewMode) {
                            vh.Send_Duration.setVisibility(View.VISIBLE);
                        } else {
                            vh.Send_Duration.setVisibility(View.GONE);
                        }

                        vh.Send_Msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.send_audio, 0);
                        vh.Send_Duration.setText(msg.getMsg().getDuration());

                    }
                    if (msg.getMsg().getImg() != null) {
                        vh.Send_Img.setImageBitmap(bp);
                        vh.Send_Layout.setVisibility(View.GONE);
                        vh.Send_Img.setVisibility(View.VISIBLE);

                    }
                    vh.Send_Msg.setOnClickListener(new ClickListener(i, ""));
                    vh.Send_Icon.setOnClickListener(new ClickListener(i, "Icon"));
                }
            }
        },200);



        return view;
    }

    public boolean DisPlayTime(String t1, String t2) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date d1 = df.parse(t1);
            Date d2 = df.parse(t2);
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别


            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            days = Math.abs(days);
            hours = Math.abs(hours);
            minutes = Math.abs(minutes);

            if (minutes > 5) {
                return true;
            } else if (hours >= 1) {
                return true;
            } else if (days >= 1) {
                return true;
            }


            System.out.println("" + days + "天" + hours + "小时" + minutes + "分");
        } catch (Exception e) {

        }
        return false;
    }

    class ClickListener implements View.OnClickListener {
        int position;
        String Type;

        public ClickListener(int position, String Type) {
            this.position = position;
            this.Type = Type;
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                if (Type.equals("Icon")) {
                    listener.ClickIcon(view, position);
                } else {
                    listener.ClickDialog(view, position);

                }
            }


        }
    }

    /**
     * check the cacke first
     *
     * @param message
     * @param tv
     * @return
     */
    private boolean convertNormalStringToSpannableString(String message, final TextView tv) {

        SpannableString value = SpannableString.valueOf(message);
        String pattern = "\\[[\u4e00-\u9fa5\\w]+\\]";//分割表情与文字
        boolean isFlag = false;

        Matcher m = Pattern.compile(pattern).matcher(value);

        while (m.find()) {
            isFlag = true;
            for (Map<String, Integer> map : emojis) {
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    if (entry.getKey().equals(m.group(0))) {
                        if (utils.isGif(entry.getValue())) {//判断是否gif

                            int s = m.start();
                            int e = m.end();
                            if (e - s < 8) {
                                int face = entry.getValue();
                                if (-1 != face) {//wrapping with weakReference
                                    Log.d("face", face + "");
                                    AnimatedImageSpan localImageSpanRef = new AnimatedImageSpan(new AnimatedGifDrawable(context.getResources().openRawResource(face), new AnimatedGifDrawable.UpdateListener() {
                                        @Override
                                        public void update() {//update the textview
                                            tv.postInvalidate();
                                        }
                                    },context));
                                    value.setSpan(localImageSpanRef, s, e, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                }
                            }
                            tv.setText(value);
                        }
                        break;
                    }
                }
            }
        }
        return isFlag;

    }


    class ViewHolder {
        public View rootView;
        public TextView Time;
        public IconImageView Receive_Icon;
        public TextView Receive_Msg;
        public TextView Receive_Duration;
        public LinearLayout Receive_Layout;
        public RoundRectImageView Receive_Img;

        public LinearLayout Receive_dialog;
        public TextView Send_Duration;
        public TextView Send_Msg;
        public LinearLayout Send_Layout;
        public RoundRectImageView Send_Img;
        public IconImageView Send_Icon;
        public LinearLayout Send_dialog;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.Time = (TextView) rootView.findViewById(R.id.Time);
            this.Receive_Icon = (IconImageView) rootView.findViewById(R.id.Receive_Icon);
            this.Receive_Msg = (TextView) rootView.findViewById(R.id.Receive_Msg);
            this.Receive_Duration = (TextView) rootView.findViewById(R.id.Receive_Duration);
            this.Receive_Layout = (LinearLayout) rootView.findViewById(R.id.Receive_Layout);
            this.Receive_Img = (RoundRectImageView) rootView.findViewById(R.id.Receive_Img);
            this.Receive_dialog = (LinearLayout) rootView.findViewById(R.id.Receive_dialog);
            this.Send_Duration = (TextView) rootView.findViewById(R.id.Send_Duration);
            this.Send_Msg = (TextView) rootView.findViewById(R.id.Send_Msg);
            this.Send_Layout = (LinearLayout) rootView.findViewById(R.id.Send_Layout);
            this.Send_Img = (RoundRectImageView) rootView.findViewById(R.id.Send_Img);
            this.Send_Icon = (IconImageView) rootView.findViewById(R.id.Send_Icon);
            this.Send_dialog = (LinearLayout) rootView.findViewById(R.id.Send_dialog);
        }

    }
}
