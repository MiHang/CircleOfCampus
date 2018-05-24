package coc.team.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import coc.team.home.GlideCircleTransform;
import coc.team.home.R;
import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by 惠普 on 2018-05-11.
 */

public class DFragment extends Fragment implements View.OnClickListener {


    private ImageView Icon;
    private Button Log_out;
    int icon = R.drawable.icon;
    private ImageView icon_bg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d, null);
        initView(view);
        DisplayMetrics dm=getResources().getDisplayMetrics();

        Glide.with(this)
                .load(icon)
                .override(dm.widthPixels,400)
                .bitmapTransform(new BlurTransformation(getActivity(), 14, 3))
                .into(icon_bg);
        Glide.with(getActivity())
                .load(icon)
                .crossFade(1000)
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不加入磁盘缓存
                .skipMemoryCache(true)//不加入内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(Icon);
        return view;
    }

    private void initView(View view) {

        Icon = (ImageView) view.findViewById(R.id.Icon);
        Icon.setOnClickListener(this);
        Log_out = (Button) view.findViewById(R.id.Log_out);
        Log_out.setOnClickListener(this);
        icon_bg = (ImageView) view.findViewById(R.id.icon_bg);
        icon_bg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Log_out:

                break;
        }
    }
}
