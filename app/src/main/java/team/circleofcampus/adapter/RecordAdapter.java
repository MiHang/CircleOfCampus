package team.circleofcampus.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
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


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import team.circleofcampus.Interface.MsgLongClickListener;
import team.circleofcampus.Interface.RecordItemListener;
import team.circleofcampus.R;
import team.circleofcampus.dao.UserDao;
import team.circleofcampus.http.HttpRequest;
import team.circleofcampus.pojo.User;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.util.StorageUtil;
import team.circleofcampus.view.DialogTextView;
import team.circleofcampus.view.FontTextView;
import team.circleofcampus.view.IconImageView;
import team.circleofcampus.view.RoundRectImageView;

/**
 * 聊天列表适配器
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
    MsgLongClickListener longClickListener;
    UserDao userDao;
    Bitmap localUserAvatar;

    public void setLongClickListener(MsgLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

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
        try {
            userDao = new UserDao(context);
            User user = userDao.queryData(SharedPreferencesUtil.getUID(context));
            if (user != null) {
                // 加载用户头像
                String filePath = StorageUtil.getStorageDirectory() + user.getHeadIcon();
                localUserAvatar = BitmapFactory.decodeFile(filePath);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setListener(RecordItemListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return data != null ? data.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getReceive() == Symbol.Receive_Mode) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

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

        // 加载头像
        int res = R.drawable.woman;
        if (msg.getMsg().getSex()==null||msg.getMsg().getSex().equals("male")) {
            res = R.drawable.man;
        }
        if (msg.getReceive() == Symbol.Receive_Mode) { // 好友
            icon = vh.Receive_Icon;
            String username = msg.getMsg().getUserName();
            Glide.with(context)
                    .load("http://"+ HttpRequest.IP+":8080/res/img/" + username)
                    .asBitmap()
                    .error(res)
                    .placeholder(res)
                    .into(icon);
        } else {
            icon = vh.Send_Icon;
            if (localUserAvatar != null) {
                icon.setImageBitmap(localUserAvatar);
            } else {
                icon.setImageResource(res);
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 判断是否显示时间
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

                if (msg.getReceive() == Symbol.Receive_Mode) {// 接收
                    vh.Send_dialog.setVisibility(View.GONE);
                    vh.Receive_dialog.setVisibility(View.VISIBLE);
                    vh.Receive_dialog.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (longClickListener!=null){
                                longClickListener.clickItem(i);
                            }
                            return false;
                        }
                    });

                    if (msg.getMsg().getText() != null) { // 接收文本消息
                        vh.Receive_Duration.setVisibility(View.GONE);
                        vh.Receive_Msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        if (!convertNormalStringToSpannableString(msg.getMsg().getText(), vh.Receive_Msg)) {
                            vh.Receive_Msg.setText(msg.getMsg().getText());
                            setBackgroundAndKeepPadding(vh.Receive_Msg, R.drawable.receive_bg_1);
                        }

                    }else  if (msg.getMsg().getAudioPath()!= null) { // 接收语音消息
                        vh.Receive_Msg.setText("");
                        vh.Receive_Msg.setVisibility(View.GONE);
                        vh.Receive_Msg.setVisibility(View.VISIBLE);
                        if (msg.getNew() == Symbol.NewMode) {
                            vh.Receive_Duration.setVisibility(View.VISIBLE);
                        } else {
                            vh.Receive_Duration.setVisibility(View.GONE);
                        }

                        vh.Receive_Msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.receive_audio, 0, 0, 0);
                        vh.Receive_Duration.setText(msg.getMsg().getDuration());

                    }  else if (msg.getMsg().getImg() != null) {
                        vh.Receive_Img.setImageBitmap(bp);
                        vh.Receive_Layout.setVisibility(View.GONE);
                        vh.Receive_Img.setVisibility(View.VISIBLE);

                    }
                    vh.Receive_Msg.setOnClickListener(new ClickListener(i, ""));
                    vh.Receive_Icon.setOnClickListener(new ClickListener(i, "Icon"));
                } else { // 发送

                    vh.Receive_dialog.setVisibility(View.GONE);
                    vh.Send_dialog.setVisibility(View.VISIBLE);
                    vh.Send_dialog.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (longClickListener!=null){
                                longClickListener.clickItem(i);
                            }
                            return false;
                        }
                    });
                    if (msg.getMsg().getText() != null) {
                        vh.Send_Duration.setVisibility(View.GONE);
                        vh.Send_Msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        if (!convertNormalStringToSpannableString(msg.getMsg().getText(), vh.Send_Msg)) {
                            vh.Send_Msg.setText(msg.getMsg().getText());
                            setBackgroundAndKeepPadding(vh.Send_Msg, R.drawable.send_bg_1);
                        }

                    }else if (msg.getMsg().getAudioPath() != null) {//发送语音消息
                        vh.Send_Msg.setText("");
                        vh.Send_Img.setVisibility(View.GONE);
                        vh.Send_Layout.setVisibility(View.VISIBLE);

                        if (msg.getNew() == Symbol.NewMode) {
                            vh.Send_Duration.setVisibility(View.VISIBLE);
                        } else {
                            vh.Send_Duration.setVisibility(View.GONE);
                        }
                        vh.Send_Msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.send_audio, 0);
                        vh.Send_Duration.setText(msg.getMsg().getDuration());

                    }else if (msg.getMsg().getImg() != null) {
                        vh.Send_Layout.setVisibility(View.GONE);
                        vh.Send_Img.setVisibility(View.VISIBLE);
                        vh.Send_Img.setImageBitmap(bp);
                    }
                    vh.Send_Msg.setOnClickListener(new ClickListener(i, ""));
                    vh.Send_Icon.setOnClickListener(new ClickListener(i, "Icon"));
                }
            }
        },200);

        return view;
    }

    protected void setBackgroundAndKeepPadding(View view, int backgroundResId) {
        Drawable backgroundDrawable = view.getContext().getResources().getDrawable(backgroundResId);
        Rect drawablePadding = new Rect();
        backgroundDrawable.getPadding(drawablePadding);
        Rect viewPadding;
        if (view.getTag() == null) {
            viewPadding = new Rect(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            view.setTag(viewPadding);
        } else {
            viewPadding = (Rect) view.getTag();
        }
        int top = viewPadding.top + drawablePadding.top;
        int left = viewPadding.left + drawablePadding.left;
        int right = viewPadding.right + drawablePadding.right;
        int bottom = viewPadding.bottom + drawablePadding.bottom;
        view.setBackgroundDrawable(backgroundDrawable);
        view.setPadding(left, top, right, bottom);
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
        public FontTextView Time;
        public IconImageView Receive_Icon;
        public DialogTextView Receive_Msg;
        public FontTextView Receive_Duration;
        public LinearLayout Receive_Layout;
        public RoundRectImageView Receive_Img;

        public LinearLayout Receive_dialog;
        public FontTextView Send_Duration;
        public DialogTextView Send_Msg;
        public LinearLayout Send_Layout;
        public RoundRectImageView Send_Img;
        public IconImageView Send_Icon;
        public LinearLayout Send_dialog;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.Time = (FontTextView) rootView.findViewById(R.id.Time);
            this.Receive_Icon = (IconImageView) rootView.findViewById(R.id.Receive_Icon);
            this.Receive_Msg = (DialogTextView) rootView.findViewById(R.id.Receive_Msg);
            this.Receive_Duration = (FontTextView) rootView.findViewById(R.id.Receive_Duration);
            this.Receive_Layout = (LinearLayout) rootView.findViewById(R.id.Receive_Layout);
            this.Receive_Img = (RoundRectImageView) rootView.findViewById(R.id.Receive_Img);
            this.Receive_dialog = (LinearLayout) rootView.findViewById(R.id.Receive_dialog);
            this.Send_Duration = (FontTextView) rootView.findViewById(R.id.Send_Duration);
            this.Send_Msg = (DialogTextView) rootView.findViewById(R.id.Send_Msg);
            this.Send_Layout = (LinearLayout) rootView.findViewById(R.id.Send_Layout);
            this.Send_Img = (RoundRectImageView) rootView.findViewById(R.id.Send_Img);
            this.Send_Icon = (IconImageView) rootView.findViewById(R.id.Send_Icon);
            this.Send_dialog = (LinearLayout) rootView.findViewById(R.id.Send_dialog);
        }

    }
}
