package team.circleofcampus.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.common.utils.ByteUtils;
import com.lcodecore.tkrefreshlayout.utils.LogUtil;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

import team.circleofcampus.Interface.FragmentSwitchListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.ChatActivity;
import team.circleofcampus.activity.LoginActivity;
import team.circleofcampus.dao.UserDao;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.http.HttpRequest;
import team.circleofcampus.http.ImageRequest;
import team.circleofcampus.pojo.User;
import team.circleofcampus.service.SingleThreadService;
import team.circleofcampus.util.ImageUtil;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.util.StorageUtil;
import team.circleofcampus.view.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 惠普 on 2018-05-11.
 */
public class MineFragment extends Fragment {

    private View view;
    private CircleImageView Icon;
    private Button Log_out;
    HttpHelper helper;
    private TextView account;
    private TextView sex;
    private TextView college;
    private TextView department;
    private EditText userName;
    private TextView QR;

    String Account;
    LoadingDialog dialog;
    private boolean isStartCropAvatar = false;
    private boolean isCropAvatar = false;

    FragmentSwitchListener switchListener;
    public void setSwitchListener(FragmentSwitchListener switchListener) {
        this.switchListener = switchListener;
    }

    // 单例线程池
    private ExecutorService singleThreadExecutor;
    private ExecutorService downloadImageSingleThreadExecutor;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001 : { // 数据加载完成
                    if (dialog != null) dialog.loadSuccess();
                    loadLocalData();
                } break;
                case 0x0002 : { // 数据加载失败
                    if (dialog != null) dialog.loadFailed();
                } break;
                case 0x0003 : { // 头像修改成功
                    if (dialog != null) dialog.loadSuccess();
                    showImage((String)msg.obj);
                    deleteTempAvatar();
                } break;
                case 0x0004 : { // 头像修改失败
                    if (dialog != null) dialog.loadFailed();
                    deleteTempAvatar();
                } break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_mine, null);
        initView(view);
        Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用相册
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        // 加载本地数据
        loadLocalData();

        helper = new HttpHelper(getContext());
        Account = SharedPreferencesUtil.getAccount(getContext());
        boolean isNetworkAvailable = SharedPreferencesUtil.isNetworkAvailable(getContext());
        if (Account != null && isNetworkAvailable) {
            loadData(); // 联网加载数据
        }

        Log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 清空保存的登陆相关信息
                SharedPreferencesUtil.setUID(getContext(), 0);
                SharedPreferencesUtil.setAccount(getContext(), "");
                SharedPreferencesUtil.setLoginTime(getContext(), 0);

                // 跳转到登陆页面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("tag", "MineFragment onResume.....");
        if (isStartCropAvatar) {
            isStartCropAvatar = false;
            isCropAvatar = true;
            return;
        }
        if (isCropAvatar) {
            isCropAvatar = false;
            dialog = new LoadingDialog(getContext());
            dialog.setLoadingText("头像修改中")
                    .setSuccessText("头像修改成功")
                    .setFailedText("头像修改失败")
                    .closeSuccessAnim()
                    .setShowTime(1000)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();
            new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        String path = StorageUtil.getUploadImgTempPath();
                        File file = new File(path + "headIcon");
                        if (file.exists()) {
                            uploadAvatar(path + "headIcon");
                        } else {
                            Log.e("tag", "头像剪裁失败。。。");
                            handler.sendEmptyMessage(0x0004);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    Uri uri = data.getData();

                    // 获取上传图片的临时路径
                    String path = StorageUtil.getUploadImgTempPath();
                    File file = new File(path);
                    if (!file.exists() || !file.isDirectory()) {
                        file.mkdirs();
                        Log.e("tag", "创建路径：" + path);
                    }

                    // 初始化UCROP设置，原图uri，剪裁后的图片保存路径
                    UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(path + "headIcon")));
                    uCrop.withAspectRatio(1, 1); // 剪裁比例 1 ：1
                    //uCrop.withMaxResultSize(500, 500); // 最大长宽 200 * 200

                    // 图片保存为jpg格式
                    UCrop.Options options = new UCrop.Options();
                    options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                    options.setCompressionQuality(80); // 设置剪裁图片的质量
                    uCrop.withOptions(options);

                    // 开始剪裁
                    uCrop.start(getActivity());
                    isStartCropAvatar = true;
                };break;
                case UCrop.REQUEST_CROP : {
                    Log.e("tag", "头像剪裁成功...");
                }; break;
            }
        }
    }

    /**
     * 修改头像
     */
    private void uploadAvatar(final String filePath) {
        Log.e("tag", "alter avatar...");
        try {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("uId", SharedPreferencesUtil.getUID(getContext()));
                        File file=new File(filePath);
                        String result = helper.upload(json.toString(), file);
                        if (result != null) {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getString("result").equals("success")) {
                                downloadImageSingleThreadExecutor = SingleThreadService.newSingleThreadExecutor();
                                downloadImageSingleThreadExecutor.execute(ImageRequest.downloadImage(jsonObject.getString("url")));
                                Message message = new Message();
                                message.obj = filePath;
                                message.what = 0x0003;
                                handler.sendMessage(message);
                            }
                        } else {
                            handler.sendEmptyMessage(0x0004);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除临时头像
     */
    private void deleteTempAvatar() {
        String path = StorageUtil.getUploadImgTempPath();
        File file = new File(path + "headIcon");
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 设置头像
     * @param imaePath
     */
    private void showImage(String imaePath){
        Bitmap bm = BitmapFactory.decodeFile(imaePath);
        Icon.setImageBitmap(bm);
    }

    private void initView(View view) {

        Icon = (CircleImageView) view.findViewById(R.id.Icon);
        Log_out = (Button) view.findViewById(R.id.Log_out);
        account = (TextView) view.findViewById(R.id.account);
        sex = (TextView) view.findViewById(R.id.sex);
        college = (TextView) view.findViewById(R.id.college);
        department = (TextView) view.findViewById(R.id.department);
        userName = (EditText) view.findViewById(R.id.userName);
        userName.setEnabled(false);
        QR = (TextView) view.findViewById(R.id.QR);
        QR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Account!=null){
                    if (switchListener!=null){
                        switchListener.displayThisFragment(true);
                    }
                }else{
                    Toast.makeText(getContext(),"您暂未登录",Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view!=null){
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.close();
        }
    }

    /**
     * 加载本地数据
     */
    private void loadLocalData() {
        try {
            UserDao userDao = new UserDao(getContext());
            User user = userDao.queryData(SharedPreferencesUtil.getUID(getContext()));
            if (user != null) {
                // 加载用户头像
                String filePath = StorageUtil.getStorageDirectory() + user.getHeadIcon();
                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                if(bitmap != null) {
                    Icon.setImageBitmap(bitmap);
                } else {
                    Icon.setImageResource(user.getGender().equals("male")?R.drawable.man:R.drawable.woman);
                }
                sex.setText(user.getGender().equals("male")?"男":"女");
                account.setText(user.getEmail());
                userName.setText(user.getUserName());
                department.setText(user.getFacultyName());
                college.setText(user.getCampusName());
            } else {
                Log.e("tag", "user is null");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载数据
     */
    public void loadData() {
        Log.e("tag", "MineFragment load data...");
        dialog = new LoadingDialog(getContext());
        dialog.setLoadingText("加载中")
                .setSuccessText("加载成功")
                .setFailedText("加载失败")
                .closeSuccessAnim()
                .setShowTime(1000)
                .setInterceptBack(false)
                .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                .show();
        // 图片下载单例线程池
        downloadImageSingleThreadExecutor = SingleThreadService.newSingleThreadExecutor();
        if (singleThreadExecutor == null) { // 数据加载单例线程池
            singleThreadExecutor = SingleThreadService.getSingleThreadPool();
        }
        singleThreadExecutor.execute(loadingUserInfo()); // 加载用户数据
    }

    /**
     * 加载用户数据
     * @return
     */
    private Runnable loadingUserInfo() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    String result = helper.getUserInfoByAccount(Account);
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getString("result").equals("success")) {

                        // 将用户数据保存到本地数据库
                        UserDao userDao = new UserDao(getContext());
                        if(userDao.deleteForAllData() == 1) { // 清空本地数据库数据
                            Log.e("tag", "清空本地数据库用户表数据");
                        }

                        final User user = new User();
                        user.setuId(jsonObject.getInt("uId"));
                        user.setUserName(jsonObject.getString("userName"));
                        user.setHeadIcon("res/img/" + user.getUserName());
                        user.setEmail(jsonObject.getString("email"));
                        user.setGender(jsonObject.getString("gender"));
                        user.setCampusName(jsonObject.getString("campusName"));
                        user.setFacultyName(jsonObject.getString("facultyName"));

                        // 将数据写入本地数据库
                        userDao.insertData(user);

                        // 下载用户头像
                        downloadImageSingleThreadExecutor.execute(ImageRequest.downloadImage(user.getHeadIcon()));
                        downloadImageSingleThreadExecutor.shutdown();
                        while (true) {
                            if(downloadImageSingleThreadExecutor.isTerminated()){
                                Log.e("tag", "用户信息加载完毕");
                                handler.sendEmptyMessage(0x0001);
                                break;
                            }
                            Thread.sleep(1000);
                        }

                    } else { // 查询失败
                        handler.sendEmptyMessage(0x0002);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }

}
