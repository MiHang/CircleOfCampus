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
    int res;
    LoadingDialog dialog;

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
            dialog = new LoadingDialog(getContext());
            dialog.setLoadingText("加载中")
                    .setSuccessText("加载成功")
                    .setFailedText("加载失败")
                    .closeSuccessAnim()
                    .setShowTime(1000)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                final String absolutePath = getAbsolutePath(getContext(), uri);

//                Toast.makeText(getContext(), "图片路径"+absolutePath, Toast.LENGTH_SHORT).show();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject json = new JSONObject();
                        try {
                            json.put("uId", SharedPreferencesUtil.getUID(getContext()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        File file=new File(absolutePath);
                        String result = helper.upload(json.toString(),file);
                        Log.e("tag", "result = " + result);

                    }
                });
                showImage(absolutePath);
            } catch (Exception e) {
                e.printStackTrace();

            }

        }
    }
    public String getAbsolutePath(final Context context,
                                  final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA},
                    null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(
                            MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }     return data;
    }
    //加载图片
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
                        user.setHeadIcon("res/img/" + Account);
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
