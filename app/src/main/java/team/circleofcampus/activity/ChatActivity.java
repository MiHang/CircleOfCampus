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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.java_websocket.client.WebSocketClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team.circleofcampus.Interface.MessageListener;
import team.circleofcampus.Interface.RecordItemListener;
import team.circleofcampus.R;
import team.circleofcampus.adapter.MyFragmentPagerAdapter;
import team.circleofcampus.adapter.RecordAdapter;
import team.circleofcampus.background.MyService;
import team.circleofcampus.fragment.LabelFragment;
import team.circleofcampus.view.FontTextView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {


    List<Message> data = new ArrayList<>();
    RecordAdapter myAdapter;
    AlertDialog alert;
    static String Send = "jaye@163.com";
    static String Receive = "5085";
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
    LabelFragment b = new LabelFragment();
    FaceFragment a;

    MyService myService = new MyService();
    private FontTextView header_left_text;
    private ImageView header_left_image;
    private FontTextView header_title;
    private FontTextView header_right_text;
    private ImageView header_right_image;
    private ListView ChatRecord;
    private ImageView Video;
    private Button Talk;
    private EditText MsgText;
    private ImageView Face;
    private ImageView More;
    private ViewPager FaceViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();

        init();
        setAdapter();
        header_title.setText("聊天");

        Intent intent = new Intent(this, myService.getClass());

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
                    public void sendMessage(byte[] bytes) {
                        ReceiveMessage(bytes);
                    }
                });

            }
        };

        bindService(intent, conn, Context.BIND_AUTO_CREATE);


    }

    public void setAdapter() {
        a = new FaceFragment();
        a.bind(MsgText, null);
        a.setPictureClickListener(new PictureClickListener() {
            @Override
            public void PictureDisplay(int res) {
                if (myClient != null && myClient.getConnection().isOpen()) {//发送图片
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res);
                    Msg dataMsg = new Msg();
                    dataMsg.setSend(Send);
                    dataMsg.setReceive(Receive);
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
        fragments.add(a);

//        fragments.add(b);
        fragmentAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        FaceViewPager.setAdapter(fragmentAdapter);


        myAdapter = new RecordAdapter(this, data);
        ChatRecord.setAdapter(myAdapter);
        myAdapter.setListener(new RecordItemListener() {
            @Override
            public void ClickIcon(View v, int position) {
                Toast.makeText(ChatActivity.this, "toux", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void ClickDialog(View v, int position) {
                Message msg = data.get(position);
                if (msg.getMsg().getText() != null) {
                    Toast.makeText(getApplicationContext(), "文本" + msg.getMsg().getText(), Toast.LENGTH_SHORT).show();
                }

                if (msg.getReceive() == Symbol.Receive_Mode) {//接收

                    if (msg.getMsg().getAudio() != null) {
                        File file = new File(getFilesDir(), msg.getMsg().getAudioPath());
                        sm.PlayAudio(0, file);//播放
                        msg.setNew(0);
                        myAdapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), "地址" + msg.getMsg().getAudioPath(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (msg.getMsg().getAudio() != null) {
                        Toast.makeText(getApplicationContext(), "发送" + msg.getMsg().getAudioPath(), Toast.LENGTH_SHORT).show();
                        File file = new File(getFilesDir(), msg.getMsg().getAudioPath());
                        sm.PlayAudio(0, file);
                        data.get(position).setNew(0);
                        myAdapter.notifyDataSetChanged();
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
                                msg.setSend(Send);
                                msg.setReceive(Receive);
                                msg.setText(MsgText.getText().toString());

                                myClient.send(utils.toByteArray(msg));

                                Message message = new Message();
                                message.setMsg(msg);
                                message.setReceive(0);

                                data.add(message);
                                dao.setData(message);
                                Log.e("TAG", dao.getAllMessage().toString());
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


        data = dao.getMessage(Receive, Send);
        for (Message m : data) {
            Log.e("tag", m.toString());
        }


        Talk.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (myClient.getConnection().isOpen()) {
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(ChatActivity.this);
//                    View v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.dialog, null);
//                    dialog.setView(v);
//                    alert = dialog.create();
//                    WindowManager.LayoutParams layoutParams = alert.getWindow().getAttributes();
//                    layoutParams.alpha = 0.5f;
//                    alert.getWindow().setAttributes(layoutParams);
//                    alert.show();
                        MsgText.setText("松开结束");
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
                            sm.startAudio(audioName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "未连接到服务器", Toast.LENGTH_SHORT).show();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    alert.cancel();//关闭弹窗
                    MsgText.setText("按下说话");
                    isDown = false;
                    RECORD_ON = false;

                    sm.stop();
                    if (sm.isOkAudio(duration, audioName)) {
                        File file = new File(getFilesDir(), audioName);
                        Log.d("", file.getPath());

                        try {
                            byte[] audio = utils.DocToByte(file.getPath());//录音文件转字节流
                            Msg msg = new Msg();
                            msg.setSend(Send);
                            msg.setReceive(Receive);

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
     * 表情面板管理
     */
    public void FragmentManage(boolean isVisible) {
        if (isVisible) {
            FaceViewPager.setVisibility(View.VISIBLE);
        } else {
            FaceViewPager.setVisibility(View.GONE);
        }

    }


    /**
     * 接收服务器信息，更新Ui
     *
     * @param bytes
     */
    public void ReceiveMessage(final byte[] bytes) {
        ChatRecord.post(new Runnable() {
            @Override
            public void run() {
                Msg msg = utils.toT(bytes);
                Log.e("tag", "文本" + msg.getText() + "语音" + msg.getAudio() + "图片" + msg.getImg());
                Message message = new Message();

                if (msg.getReceive().equals(Send)) {
                    msg.setSend(Receive);
                }

                message.setMsg(msg);
                if (msg.getAudio() != null) {   //处理语音信息
                    //保存录音文件
                    File files = new File(getFilesDir(), msg.getAudioPath());
                    sm.ByteToDoc(msg.getAudio(), files);

                }
                dao.setData(message);//储存聊天信息
                data.add(message);
                myAdapter.notifyDataSetChanged();
            }
        });


    }


    // RotateAnimation
    private RotateAnimation setRotateAnimation(int fromDegrees, int toDegrees, int Duration) {
        RotateAnimation animation = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(Duration);
        animation.setFillAfter(true);
        return animation;
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
                FragmentManage(false);
                break;
            case R.id.Face:

                FragmentManage(true);
                FaceViewPager.setCurrentItem(0, false);

                break;
            case R.id.More:
                Toast.makeText(this, "暂未开放", Toast.LENGTH_SHORT).show();
//                FaceViewPager.setCurrentItem(1, false);
//                FragmentManage(true);
                break;
            case R.id.Video:
                //隐藏表情面板
                FragmentManage(false);

                if (Video.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.keyboard).getConstantState())) {//打开键盘
                    Video.setImageResource(R.drawable.video);
                    MsgText.setVisibility(View.VISIBLE);
                    MsgText.setSelection(MsgText.getText().length());
                    Talk.setVisibility(View.GONE);
                    InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

                } else {
                    Video.setImageResource(R.drawable.keyboard);
                    Talk.setVisibility(View.VISIBLE);
                    MsgText.setVisibility(View.GONE);
                }


                break;
            case R.id.Talk:
                Toast.makeText(this, "弹力", Toast.LENGTH_SHORT).show();
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
        Talk = (Button) findViewById(R.id.Talk);
        MsgText = (EditText) findViewById(R.id.MsgText);
        Face = (ImageView) findViewById(R.id.Face);
        More = (ImageView) findViewById(R.id.More);
        FaceViewPager = (ViewPager) findViewById(R.id.Face_ViewPager);

        Talk.setOnClickListener(this);
        header_left_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });
    }


}
