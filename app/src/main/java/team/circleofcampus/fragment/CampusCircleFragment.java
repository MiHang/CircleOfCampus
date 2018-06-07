package team.circleofcampus.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import team.circleofcampus.R;
import team.circleofcampus.adapter.MoreCircleListAdapter;
import team.circleofcampus.model.Circle;

public class CampusCircleFragment extends Fragment {

    private View view;

    @BindView(R.id.more_campus_circle_list)
    protected ListView listView;
    private MoreCircleListAdapter moreCircleListAdapter;
    private ArrayList<Circle> circles = new ArrayList<Circle>();

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
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_more_circle, null);
        ButterKnife.bind(this, view);

        // 时间戳
        Circle circle1 = new Circle();
        circle1.setId(-1);
        circle1.setPublishTime("09:00");
        circles.add(circle1);

        for (int i = 1; i < 4; i ++) {
            Circle circle = new Circle();
            circle.setId(i);
            circle.setTitle("title title " + i);
            circles.add(circle);
        }

        Circle circle2 = new Circle();
        circle2.setId(-1);
        circle2.setPublishTime("昨天19:26");
        circles.add(circle2);

        for (int i = 4; i < 6; i ++) {
            Circle circle = new Circle();
            circle.setId(i);
            circle.setTitle("title title " + i);
            circles.add(circle);
        }

        moreCircleListAdapter = new MoreCircleListAdapter(getContext(), circles);
        listView.setAdapter(moreCircleListAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return view;
    }

}
