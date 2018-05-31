package coc.team.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import coc.team.home.R;

/**
 * Created by 惠普 on 2018-05-11.
 */

public class AFragment extends Fragment {

    private List<Integer> images = new ArrayList<>();//声明数组


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, null);

        Banner banner = (Banner) view.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new Lunbotu());
        //设置图片集合
        images.add(R.drawable.lunbo01);
        images.add(R.drawable.lunbo02);
        images.add(R.drawable.lunbo04);
        banner.setImages(images);
        //banner设置方法全部调用完毕时最后调用
        banner.start();

        return view;
    }


}
