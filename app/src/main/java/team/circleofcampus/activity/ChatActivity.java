package team.circleofcampus.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.common.model.Message;
import com.common.model.UserMsg;
import com.common.utils.AudioUtils;
import com.common.utils.ByteUtils;
import com.common.utils.Symbol;
import com.example.library.Fragment.FaceFragment;
import com.example.library.Interface.PictureClickListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team.circleofcampus.Interface.MessageListener;
import team.circleofcampus.Interface.MsgLongClickListener;
import team.circleofcampus.Interface.RecordItemListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.MyFragmentPagerAdapter;
import team.circleofcampus.adapter.RecordAdapter;
import team.circleofcampus.dao.Data_Dao;
import team.circleofcampus.dao.UserMsg_Dao;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.service.MyService;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.util.Uuidutil;
import team.circleofcampus.view.FontButton;
import team.circleofcampus.view.FontEditText;
import team.circleofcampus.view.FontTextView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    List<Message> data = new ArrayList<>();
    RecordAdapter myAdapter;
    String send;
    String receive;
    String nickName;
    SharedPreferencesUtil sharedPreferencesUtil;
    Data_Dao dao;
    WebSocketClient myClient;
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;
    boolean isDown = false;//用于上滑取消发送语音信息
    AudioUtils sm;//录音工具类
    Handler handler = new Handler();//录音时长线程
    int duration;//录音时长
    boolean RECORD_ON = false; // 是否正在录音
    String audioName;//录音文件名称
    ByteUtils utils = new ByteUtils();
    SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

    MyFragmentPagerAdapter fragmentAdapter;
    List<Fragment> fragments = new ArrayList<>();
    FaceFragment faceFragment;

    MyService myService = new MyService();
    private FontTextView header_left_text;
    private ImageView header_left_image;
    private FontTextView header_title;
    private FontTextView header_right_text;
    private ImageView header_right_image;
    private ListView ChatRecord;
    private ImageView Video;
    private FontButton Talk;
    private FontEditText MsgText;
    private ImageView Face;
    private ViewPager FaceViewPager;
    HttpHelper helper;
    String sex;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {}
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 返回一个MsgService对象
            myService = ((MyService.MsgBinder) service).getService();
            myClient = myService.getMyClient();

            myService.setMessageListener("C", new MessageListener() {
                @Override
                public void update( String account, boolean isUpdate) {

                    // 清空未读消息数
                    try {
                        UserMsg_Dao dao = new UserMsg_Dao(ChatActivity.this);
                        List<UserMsg> m = dao.queryMsgBySearch(account);
                        if (m != null && m.size() > 0){
                            UserMsg user = m.get(0);
                            user.setMsg(user.getMsg());
                            user.setAmount(0);
                            dao.update(user);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    data.clear();
                    List<Message> msg = dao.getMessage(send, receive);
                    if (msg != null) {
                        data.addAll(msg);
                    }

                    // 延迟刷新
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.notifyDataSetChanged();
                            ChatRecord.smoothScrollToPosition(data.size());
                        }
                    }, 500);
                }
            });
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
        intent.putExtra("selectedPageId", 1);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setNavigationBarTintEnabled(true);
        tintManager.setTintResource(R.drawable.bg);

        initView();

        Intent intent = getIntent();
        receive = intent.getStringExtra("receive");
        nickName = intent.getStringExtra("nickName");

        if (receive == null || receive.equals("")) {
            finish();
        }

        send = sharedPreferencesUtil.getAccount(this);

        init();
        setAdapter();
        header_title.setText(nickName);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    helper=new HttpHelper(getApplicationContext());
                    String result = helper.getUserInfoByAccount(send);
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("result").equals("success")) {
                        sex=jsonObject.getString("gender");
                    } else { // 查询失败
                        handler.sendEmptyMessage(0x0002);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Intent intent2 = new Intent(this, myService.getClass());
        bindService(intent2, conn, Context.BIND_AUTO_CREATE);
    }

    public void setAdapter() {
        faceFragment = new FaceFragment();
        faceFragment.bind(MsgText, null);
        faceFragment.setPictureClickListener(new PictureClickListener() {
            @Override
            public void PictureDisplay(int res) {
                if (myClient != null && myClient.getConnection().isOpen()) { // 发送图片
                    Log.e("tag", "发送图片....");
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res);
                    Message dataMsg = new Message();
                    dataMsg.setSend(send);
                    dataMsg.setSex(sex);
                    dataMsg.setReceive(receive);
                    dataMsg.setImg(utils.BitmapToBytes(bitmap));
                    myClient.send(utils.toByteArray(dataMsg));

                    try {
                        dao.save(dataMsg);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    data.clear();
                    List<Message> msg = dao.getMessage(receive, send);
                    for(Message m : msg){
                        data.add(m);
                    }

                    myAdapter.notifyDataSetChanged();
                    ChatRecord.smoothScrollToPosition(data.size());
                } else {
                    Toast.makeText(getApplicationContext(), "未连接到服务器", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fragments.add(faceFragment);

        fragmentAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        FaceViewPager.setAdapter(fragmentAdapter);

        data= dao.getMessage(send, receive);
        for (Message m : data) {
            Log.e("s", "用户"+m.getSend()+"向"+m.getReceive()+"发送"+m.getText()+m.getDate());
        }
        myAdapter = new RecordAdapter(this, data);
        ChatRecord.setAdapter(myAdapter);

        myAdapter.setLongClickListener(new MsgLongClickListener() {
            @Override
            public void clickItem(int position) {
                Message msg = data.get(position);
                if (msg.getAudio() != null) {
                    Toast.makeText(getApplicationContext(), "语音识别", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myAdapter.setListener(new RecordItemListener() {
            @Override
            public void ClickIcon(View v, int position) {
                Message msg = data.get(position);
                Toast.makeText(getApplicationContext(), "用户:" + msg.getSend()+"性别"+msg.getSex(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ClickDialog(View v, int position) {
                Message msg = data.get(position);
                if (msg.getAudio() != null) {//语音不为空
                    File file = new File(getFilesDir(), msg.getAudioPath());
                    sm.PlayAudio(Integer.parseInt(data.get(position).getDuration()), file);
                    msg.setMsg_New(Symbol.Msg_Old);//不显示红点
                    try {
                        dao.update(msg);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    data.get(position).setMsg_New(Symbol.Msg_Old);
                    myAdapter.notifyDataSetChanged();
                    ChatRecord.smoothScrollToPosition(position);
                }
            }
        });

    }

    public void init() {
        try {
            dao = new Data_Dao(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sm = new AudioUtils(this);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        MsgText.setSelection(MsgText.length());
        MsgText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        MsgText.setSingleLine(false);
        MsgText.setHorizontallyScrolling(false);
        MsgText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});//最大字数限制
        MsgText.setMaxHeight(displayMetrics.heightPixels / 8);

        MsgText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“发送键”键*/
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if (myClient != null) {
                        if (myClient.getConnection().isOpen()) {
                            if (MsgText.getText().toString().length() > 0) {
                                Message msg = new Message();
                                msg.setSend(send);
                                msg.setReceive(receive);
                                msg.setSex(sex);
                                msg.setText(MsgText.getText().toString());
                                msg.setMsg_Receive(Symbol.Msg_Send);
                                myClient.send(utils.toByteArray(msg));

                                myAdapter.notifyDataSetChanged();
                                MsgText.setText("");
                                data.add(msg);

                                try {
                                    dao.save(msg);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(ChatActivity.this, "请输入您想要发送的信息", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "未连接到服务器", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        Talk.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (!myClient.getConnection().isOpen()) {
                    Toast.makeText(getApplicationContext(), "未连接到服务器", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    Talk.setText("松开结束");
                    isDown = true;

                    //录制音频
//                    audioName = sdf.format(new Date());
                    audioName= Uuidutil.getUUID();
                    RECORD_ON = true;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    duration = 0;

                                    while (RECORD_ON) {
                                        try {
                                            Thread.sleep(1000);
                                            duration += 1000;

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }
                            }).start();
                        }
                    });

                    try {
                        sm.startAudio(audioName, "录音时间太短");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    Talk.setText("按下说话");
                    isDown = false;
                    RECORD_ON = false;

                    sm.stop();
                    if (sm.isOkAudio(duration, audioName)) {
                        File file = new File(getFilesDir(), audioName);

                        try {
                            byte[] audio = utils.DocToByte(file.getPath());//录音文件转字节流
                            Message msg = new Message();
                            msg.setSend(send);
                            msg.setReceive(receive);

                            msg.setAudio(audio);//语音字节
                            msg.setAudioPath(audioName);
                            msg.setDuration(sm.getDuration(file) + "");//语音时长
                            myClient.send(utils.toByteArray(msg));//发送语音

                            data.add(msg);
                            try {
                                dao.save(msg);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            myAdapter.notifyDataSetChanged();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                return false;
            }

        });

    }

    @Override
    protected void onDestroy() {
        Log.e("tag", "ChatActivity onDestroy......");

        // 解绑service
        myService.removeMessageListener("C");
        unbindService(conn);

        super.onDestroy();
    }

    /**
     * 事件分发机制
     * 点击软键盘区域以外自动关闭软键盘,
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //隐藏输入法
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            }
            x1 = event.getX();
            y1 = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            x2 = event.getX();
            y2 = event.getY();
            if (isDown) {
                if (y1 - y2 > 50) {
                    //   Toast.makeText(this, "取消发送", Toast.LENGTH_SHORT).show();
                    File file = new File(getFilesDir(), audioName);
                    sm.deleteAudio(file);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.MsgText:
                //隐藏表情面板
                if (FaceViewPager.getVisibility() == View.VISIBLE) {
                    Animation animBottomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
                    animBottomOut.setDuration(200);
                    FaceViewPager.setVisibility(View.GONE);
                    FaceViewPager.startAnimation(animBottomOut);
                }
                break;
            case R.id.Face:
                Animation animation;

                if (FaceViewPager.getVisibility() == View.VISIBLE) {//隐藏表情面板
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
                    animation.setDuration(100);
                    FaceViewPager.setVisibility(View.GONE);
                    FaceViewPager.startAnimation(animation);
                } else {//显示表情面板
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_in);
                    animation.setDuration(100);
                    FaceViewPager.setVisibility(View.VISIBLE);
                    FaceViewPager.startAnimation(animation);

                }

                break;
            case R.id.Video:
                //隐藏表情面板
                if (FaceViewPager.getVisibility() == View.VISIBLE) {
                    Animation animBottomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_out);
                    animBottomOut.setDuration(200);
                    FaceViewPager.setVisibility(View.GONE);
                    FaceViewPager.startAnimation(animBottomOut);
                }
                if (MsgText.getVisibility() == View.VISIBLE) {
                    Video.setImageResource(R.drawable.keyboard);
                    Talk.setVisibility(View.VISIBLE);
                    MsgText.setVisibility(View.GONE);
                } else {//打开键盘
                    Video.setImageResource(R.drawable.video);
                    MsgText.setVisibility(View.VISIBLE);
                    MsgText.setSelection(MsgText.getText().length());
                    Talk.setVisibility(View.GONE);
                    InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;


        }
    }

    private void initView() {
        header_left_text = (FontTextView) findViewById(R.id.header_left_text);
        header_left_image = (ImageView) findViewById(R.id.header_left_image);
        header_title = (FontTextView) findViewById(R.id.header_title);
        header_right_text = (FontTextView) findViewById(R.id.header_right_text);
        header_right_image = (ImageView) findViewById(R.id.header_right_image);
        ChatRecord = (ListView) findViewById(R.id.ChatRecord);
        Video = (ImageView) findViewById(R.id.Video);
        Talk = (FontButton) findViewById(R.id.Talk);
        MsgText = (FontEditText) findViewById(R.id.MsgText);
        Face = (ImageView) findViewById(R.id.Face);

        FaceViewPager = (ViewPager) findViewById(R.id.Face_ViewPager);
        Face.setOnClickListener(this);
        MsgText.setOnClickListener(this);
        Video.setOnClickListener(this);

        header_left_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

}
