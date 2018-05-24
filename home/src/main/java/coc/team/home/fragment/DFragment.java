package coc.team.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import coc.team.home.GlideCircleTransform;
import coc.team.home.R;

/**
 * Created by 惠普 on 2018-05-11.
 */

public class DFragment extends Fragment implements View.OnClickListener {

    private TextView title;
    private ImageView Icon;
    private Button Log_out;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d, null);
        initView(view);
        title.setText("我的");
        Glide.with(getActivity())
                .load(R.drawable.icon)
                .diskCacheStrategy(DiskCacheStrategy.NONE)//不加入磁盘缓存
                .skipMemoryCache(true)//不加入内存缓存
                .transform(new GlideCircleTransform(getContext()))
                .into(Icon);
        return view;
    }

    private void initView(View view) {
        title = (TextView) view.findViewById(R.id.title);
        Icon = (ImageView) view.findViewById(R.id.Icon);
        Icon.setOnClickListener(this);
        Log_out = (Button) view.findViewById(R.id.Log_out);
        Log_out.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Log_out:

                break;
        }
    }
}
