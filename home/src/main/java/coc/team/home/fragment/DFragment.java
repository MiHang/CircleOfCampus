package coc.team.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import coc.team.home.R;

/**
 * Created by 惠普 on 2018-05-11.
 */

public class DFragment extends Fragment {

    private TextView title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d, null);
        initView(view);
        title.setText("我的");
        return view;
    }

    private void initView(View view) {
        title = (TextView) view.findViewById(R.id.title);
    }
}
