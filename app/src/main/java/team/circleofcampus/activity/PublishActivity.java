package team.circleofcampus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import team.circleofcampus.R;
import team.circleofcampus.http.LoginRequest;
import team.circleofcampus.http.SocietyRequest;
import team.circleofcampus.util.SharedPreferencesUtil;
import team.circleofcampus.util.StorageUtil;

public class PublishActivity extends AppCompatActivity {

    private static final int GALLERY_REQUSET_CODE = 138;
    private static final int GALLERY_REQUSET_CODE_KITKAT = 139;
    private static final int CROP_REQUEST_CODE = 140;
    private static final int LOG_IN_REQUSET_CODE = 141;

    @BindView(R.id.header_title)
    protected TextView headerTitle;
    @BindView(R.id.header_right_text)
    protected TextView rightText;
    @BindView(R.id.publish_add_image_view)
    protected ImageView addImageView;
    @BindView(R.id.publish_display_image_view)
    protected ImageView displayImageView;
    @BindView(R.id.publish_title_edit)
    protected EditText title;
    @BindView(R.id.publish_activity_time_edit)
    protected EditText activityTime;
    @BindView(R.id.publish_activity_venue_edit)
    protected EditText activityVenue;
    @BindView(R.id.publish_content_edit)
    protected EditText content;

    // 加载框
    private LoadingDialog loadingDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x0001 : { // 网络连接出错
                    if (loadingDialog != null) {
                        loadingDialog.close();
                    }
                    Toast.makeText(PublishActivity.this, "无法与服务器通信，请检查您的网络连接", Toast.LENGTH_SHORT).show();
                } break;
                case 0x0002 : { // 发布成功
                    if (loadingDialog != null) {
                        loadingDialog.loadSuccess();
                    }
                    SharedPreferencesUtil.setPublishedNewCircle(PublishActivity.this, true);
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                // 删除临时图片文件
                                File file = new File(StorageUtil.getUploadImgTempPath() + "uploadTempImage");
                                if (file.exists()) file.delete();
                                sleep(1000);
                                handler.sendEmptyMessage(0x0004);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } break;
                case 0x0003 : { // 发布失败
                    if (loadingDialog != null) {
                        loadingDialog.loadFailed();
                    }
                } break;
                case 0x0004 : { // 返回
                    onBackPressed();
                } break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (loadingDialog != null) {
            loadingDialog.close();
        }
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        headerTitle.setText("社团公告发布");
        rightText.setText("发布");
    }

    /**
     * 标题栏右侧文本点击事件
     */
    @OnClick(R.id.header_right_text)
    public void onClickRightText() {
        if (title.getText().toString().trim().equals("")) {
            Toast.makeText(PublishActivity.this, "公告标题不能为空", Toast.LENGTH_SHORT).show();
        } else if (title.getText().toString().trim().length() > 45) {
            Toast.makeText(PublishActivity.this, "公告标题超出45字，当前字数为"
                    + title.getText().toString().trim().length() + "字", Toast.LENGTH_SHORT).show();
        } else if (activityTime.getText().toString().trim().equals("")) {
            Toast.makeText(PublishActivity.this, "活动时间不能为空", Toast.LENGTH_SHORT).show();
        } else if (activityTime.getText().toString().trim().length() > 23) {
            Toast.makeText(PublishActivity.this, "活动时间内容过长", Toast.LENGTH_SHORT).show();
        } else if (activityVenue.getText().toString().trim().equals("")) {
            Toast.makeText(PublishActivity.this, "活动地点不能为空", Toast.LENGTH_SHORT).show();
        } else if (activityVenue.getText().toString().trim().length() > 45) {
            Toast.makeText(PublishActivity.this, "活动地点内容过长(45字以内)", Toast.LENGTH_SHORT).show();
        } else if (content.getText().toString().trim().equals("")) {
            Toast.makeText(PublishActivity.this, "公告内容不能为空", Toast.LENGTH_SHORT).show();
        } else if (content.getText().toString().trim().length() > 240) {
            Toast.makeText(PublishActivity.this, "公告内容过长(240字以内), 当前字数为"
                    +content.getText().toString().trim().length() + "字", Toast.LENGTH_SHORT).show();
        } else if (!new File(StorageUtil.getUploadImgTempPath() + "uploadTempImage").exists()) {
            Toast.makeText(PublishActivity.this, "请添加一张图片", Toast.LENGTH_SHORT).show();
        } else {

            // 加载对话框
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setLoadingText("正在发布")
                    .setSuccessText("发布成功")//显示加载成功时的文字
                    .setFailedText("发布失败")
                    .closeSuccessAnim()
                    .closeFailedAnim()
                    .setShowTime(1000)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();

            new Thread(){
                @Override
                public void run() {
                    int uId = SharedPreferencesUtil.getUID(PublishActivity.this);
                    File file = new File(StorageUtil.getUploadImgTempPath() + "uploadTempImage");
                    String result = SocietyRequest.addSocietyCircle(uId, title.getText().toString().trim(),
                            content.getText().toString().trim(), activityVenue.getText().toString().trim(),
                            activityTime.getText().toString().trim(), file);
                    if (null != result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            result = json.getString("result");

                            if (result.equals("success")) {
                                handler.sendEmptyMessage(0x0002);
                            } else if (result.equals("error")) {
                                handler.sendEmptyMessage(0x0003);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.sendEmptyMessage(0x0001);
                    }
                }
            }.start();
        }
    }

    /**
     * 标题栏返回按钮图标点击事件
     * @param view
     */
    @OnClick(R.id.header_left_image)
    public void onClickBackIcon(View view) {
        onBackPressed();
    }

    /**
     * 点击添加图片按钮
     */
    @OnClick(R.id.publish_add_image_view)
    public void onClickAddImage() {

        // 打开相册
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            startActivityForResult(intent, GALLERY_REQUSET_CODE_KITKAT);
        } else {
            startActivityForResult(intent, GALLERY_REQUSET_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOG_IN_REQUSET_CODE:
                if(resultCode == RESULT_OK) {
                    // ......
                } else {
                    finish();
                }
                break;
            case GALLERY_REQUSET_CODE:
                handleGalleryResult(resultCode, data);
                break;
            case GALLERY_REQUSET_CODE_KITKAT:
                handleGalleryKitKatResult(resultCode, data);
                break;
            case CROP_REQUEST_CODE:
                // 此处crop正常返回resultCode也不为RESULT_OK
                if(data == null) return;
                Bundle bundle = data.getExtras();
                if(bundle != null) {
                    Bitmap bitmap = bundle.getParcelable("data");
                    displayImageView.setImageBitmap(bitmap);
                    saveBitmapFile(bitmap); // 将图片保存到手机内存卡
                }
                break;
            default:
                break;
        }
    }

    /**
     * 将剪裁过的图片保存为临时文件
     * @param bitmap
     */
    public void saveBitmapFile(Bitmap bitmap){

        // 保存校园圈临时数据的根路径
        String path = StorageUtil.getUploadImgTempPath();
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
            System.out.println("创建路径：" + path);
        }

        Log.e("tag", "path = " + path);
        File imageFile = new File(path + "uploadTempImage");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleGalleryResult(int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;
        String path = data.getData().getPath();
        Bitmap image = BitmapFactory.decodeFile(path);
        File faceFile;
        try {
            faceFile = saveBitmap(image);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Uri fileUri = Uri.fromFile(faceFile);
        routeToCrop(fileUri);
    }

    // Result uri is "content://" after Android 4.4
    private void handleGalleryKitKatResult(int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;
        Uri contentUri = data.getData();
        if (contentUri == null) return;
        File faceFile;
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(contentUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            faceFile = saveBitmap(image);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Uri fileUri;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Android 7.0 "file://" uri权限适配
            fileUri = FileProvider.getUriForFile(this,
                    "gavinli.translator", faceFile);
        } else {
            fileUri = Uri.fromFile(faceFile);
        }
        routeToCrop(fileUri);
    }

    /**
     * 剪裁图片
     * @param uri
     */
    private void routeToCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.putExtra("crop", false);
        intent.putExtra("aspectX", 1.82);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 416);
        intent.putExtra("outputY", 228);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private File saveBitmap(Bitmap bitmap) throws IOException {
        File file = new File(getExternalCacheDir(), "face-cache");
        if (!file.exists()) file.createNewFile();
        try {
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

}
