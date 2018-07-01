package team.circleofcampus.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.common.dao.Data_Dao;
import com.common.model.Message;
import com.common.model.Msg;
import com.common.utils.AudioUtils;
import com.common.utils.ByteUtils;
import com.common.utils.Symbol;
import com.example.library.Fragment.FaceFragment;
import com.example.library.Interface.PictureClickListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.io.IOException;
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
import team.circleofcampus.service.MyService;
import team.circleofcampus.util.SharedPreferencesUtil;
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

        Intent intent2 = new Intent(this, myService.getClass());

        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //返回一个MsgService对象
                myService = ((MyService.MsgBinder) service).getService();

                myClient = myService.getMyClient();

                myService.setMessageListener(new MessageListener() {
                    @Override
                    public void update(boolean isUpdate) {
                        data.clear();
                        List<Message> msg=dao.getMessage(receive, send);
                       for(Message m :msg){
                           data.add(m);
                       }
                        myAdapter.notifyDataSetChanged();
                        ChatRecord.smoothScrollToPosition(data.size());
                    }
                });

            }
        };
        bindService(intent2, conn, Context.BIND_AUTO_CREATE);
    }

    public void setAdapter() {
        faceFragment = new FaceFragment();
        faceFragment.bind(MsgText, null);
        faceFragment.setPictureClickListener(new PictureClickListener() {
            @Override
            public void PictureDisplay(int res) {
                if (myClient != null && myClient.getConnection().isOpen()) {//发送图片
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res);
                    Msg dataMsg = new Msg();
                    dataMsg.setSend(send);
                    dataMsg.setReceive(receive);
                    dataMsg.setImg(utils.BitmapToBytes(bitmap));
                    myClient.send(utils.toByteArray(dataMsg));

                    Message message = new Message();
                    message.setMsg(dataMsg);
                    message.setReceive(0);
                    data.add(message);
                    dao.setData(message);
                    myAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "未连接到服务器", Toast.LENGTH_SHORT).show();

                }

            }
        });
        fragments.add(faceFragment);

        fragmentAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        FaceViewPager.setAdapter(fragmentAdapter);


        myAdapter = new RecordAdapter(this, data);
        ChatRecord.setAdapter(myAdapter);

        myAdapter.setLongClickListener(new MsgLongClickListener() {
            @Override
            public void clickItem(int position) {
                Message msg = data.get(position);
                if (msg.getMsg().getAudio() != null) {
                    Toast.makeText(getApplicationContext(), "语音识别", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myAdapter.setListener(new RecordItemListener() {
            @Override
            public void ClickIcon(View v, int position) {

            }

            @Override
            public void ClickDialog(View v, int position) {
                Message msg = data.get(position);
//                if (msg.getMsg().getText() != null) {
//                    Toast.makeText(getApplicationContext(), "文本" + msg.getMsg().getText(), Toast.LENGTH_SHORT).show();
//                }

                if (msg.getReceive() == Symbol.Receive_Mode) {//接收

                    if (msg.getMsg().getAudio() != null) {
                        File file = new File(getFilesDir(), msg.getMsg().getAudioPath());
                        sm.PlayAudio(0, file);//播放
                        msg.setNew(0);
                        myAdapter.notifyDataSetChanged();
                        ChatRecord.smoothScrollToPosition(position);
                        Message message = dao.queryMessageById(position);
                        message.setNew(0);
                        dao.update(msg);

                    }
                } else {//发送
                    if (msg.getMsg().getAudio() != null) {
                        File file = new File(getFilesDir(), msg.getMsg().getAudioPath());
                        sm.PlayAudio(0, file);
                        data.get(position).setNew(0);
                        Message message = dao.queryMessageById(position);
                        message.setNew(0);
                        dao.update(msg);
                        myAdapter.notifyDataSetChanged();
                        ChatRecord.smoothScrollToPosition(position);

                    }
                }
            }

        });

    }

    public void init() {
        dao = new Data_Dao(this, "12.db");
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
                                Msg msg = new Msg();
                                msg.setSend(send);
                                msg.setReceive(receive);
                                msg.setText(MsgText.getText().toString());

                                myClient.send(utils.toByteArray(msg));

                                Message message = new Message();
                                message.setMsg(msg);
                                message.setReceive(0);

                                data.add(message);
                                dao.setData(message);

                                myAdapter.notifyDataSetChanged();

                                MsgText.setText("");
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


        data = dao.getMessage(receive, send);
        for (Message m : data) {
            Log.e("tag", m.toString());
        }


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
                    audioName = sdf.format(new Date());
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
                            Msg msg = new Msg();
                            msg.setSend(send);
                            msg.setReceive(receive);


                            msg.setAudio(audio);
                            msg.setAudioPath(audioName);
                            msg.setDuration(sm.getDuration(file) + "");

                            myClient.send(utils.toByteArray(msg));

                            //更新适配器
                            Message message = new Message();
                            message.setMsg(msg);
                            message.setReceive(0);
                            message.setNew(Symbol.NewMode);
                            data.add(message);
                            dao.setData(message);
                            myAdapter.notifyDataSetChanged();


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        sm.PlayAudio(duration, file);

                    }
                }


                return false;
            }

        });

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
