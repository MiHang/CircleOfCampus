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
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                }
                break;
            default:
                break;
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
