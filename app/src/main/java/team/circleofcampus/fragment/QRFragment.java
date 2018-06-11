package team.circleofcampus.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.common.utils.QRCodeUtils;

import team.circleofcampus.R;
import team.circleofcampus.util.SharedPreferencesUtil;

/**
 * 我的发布页面，未审核fragment
 */
public class QRFragment extends Fragment {

    private View view;
    String account="校园圈";
    private ImageView qr_img;

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != view) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getActivity().getLayoutInflater().inflate(R.layout.qr_dialog, null);
        initView(view);
      
        Bitmap bitmap=new QRCodeUtils(getContext()).createQRCodeWithLogo6(account, 1200, R.drawable.icon);
        qr_img.setImageBitmap(bitmap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return view;
    }

    private void initView(View view) {
        qr_img = (ImageView) view.findViewById(R.id.qr_img);
    }
}
