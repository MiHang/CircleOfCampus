package team.circleofcampus.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import team.circleofcampus.view.SendLayout;

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
        if (data.get(position).getMsg_Receive() == Symbol.Msg_Send) {
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        ViewHolder vh;
        Message msg = data.get(i);

        // 加载布局
        int itemType = getItemViewType(i);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(itemType==0?R.layout.dialog_send:R.layout.dialog_receive, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        // 加载头像
        int res = R.drawable.woman;
        if (msg.getSex() == null || msg.getSex().equals("male")) {
            res = R.drawable.man;
        }
        String username = msg.getUserName();

        Glide.with(context)
                .load("http://" + HttpRequest.IP + ":8080/res/img/" + username)
                .asBitmap()
                .error(res)
                .into(vh.Icon);

        // 判断是否显示时间
        if (i != 0) {
            if (msg.getDate() == null || data.get(i - 1).getDate() == null) {
                vh.Time.setText(msg.getDate());
                vh.Time.setVisibility(View.VISIBLE);
            } else {
                if (DisPlayTime(msg.getDate(), data.get(i - 1).getDate())) {
                    vh.Time.setText(msg.getDate());
                    vh.Time.setVisibility(View.VISIBLE);
                } else {
                    vh.Time.setVisibility(View.GONE);
                }
            }
        } else {
            vh.Time.setVisibility(View.VISIBLE);
            vh.Time.setText(msg.getDate());
        }

        //消息处理
        if (msg.getImg() != null) {//图片不为空
            bp = byteUtils.BytesToBitmap(msg.getImg());
        }
        if (msg.getText() != null) { // 接收文本消息
            vh.Duration.setVisibility(View.GONE);
            vh.Msg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (!convertNormalStringToSpannableString(msg.getText(), vh.Msg)) {
                vh.Msg.setText(msg.getText());
                setBackgroundAndKeepPadding(vh.Msg, itemType==0?R.drawable.send_bg_1:R.drawable.receive_bg_1);
            }

        } else if (msg.getAudioPath() != null) {//接收语音信息

            vh.Msg.setText("");
            if (msg.getMsg_New() == Symbol.Msg_New) {//新消息 显示小点
                vh.Duration.setVisibility(View.VISIBLE);
            } else {
                vh.Duration.setVisibility(View.GONE);
            }
            if (itemType==0){//发送
                vh.Msg.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.send_audio,0);
            }else  if (itemType==1){//接收
                vh.Msg.setCompoundDrawablesWithIntrinsicBounds(R.drawable.receive_audio,0,0,0);
            }
            if (Integer.valueOf(msg.getDuration())==0){//语音时长
                vh.Msg.setText("1");
            }else{
                vh.Msg.setText(msg.getDuration());
            }
        } else if (msg.getImg() != null) {//接收图片信息
            vh.Img.setImageBitmap(bp);
            vh.Duration.setVisibility(View.GONE);
            vh.Msg.setVisibility(View.GONE);
            vh.Img.setVisibility(View.VISIBLE);//显示图片
        }
        vh.Msg.setOnLongClickListener(new View.OnLongClickListener() {//长按监听
            @Override
            public boolean onLongClick(View view) {
                if (longClickListener != null) {
                    longClickListener.clickItem(i);
                }
                return false;
            }
        });
        vh.Msg.setOnClickListener(new ClickListener(i, ""));
        vh.Icon.setOnClickListener(new ClickListener(i, "Icon"));



        return convertView;
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
                                    }, context));
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

    public class ViewHolder {

        public View rootView;
        public FontTextView Time;
        public IconImageView Icon;
        public DialogTextView Msg;
        public FontTextView Duration;
        public RoundRectImageView Img;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.Time = (FontTextView) rootView.findViewById(R.id.Time);
            this.Icon = (IconImageView) rootView.findViewById(R.id.Icon);
            this.Msg = (DialogTextView) rootView.findViewById(R.id.Msg);
            this.Duration = (FontTextView) rootView.findViewById(R.id.Duration);
            this.Img = (RoundRectImageView) rootView.findViewById(R.id.Img);
        }

    }
}
