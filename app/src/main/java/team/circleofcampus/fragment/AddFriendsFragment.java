package team.circleofcampus.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.xiasuhuei321.loadingdialog.view.LoadingDialog;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import team.circleofcampus.Interface.ListListener;
import team.circleofcampus.Interface.OnItemClickListener;
import team.circleofcampus.R;
import team.circleofcampus.activity.AddRequestActivity;
import team.circleofcampus.activity.DecoderActivity;
import team.circleofcampus.activity.MainActivity;
import team.circleofcampus.adapter.FriendSearchAdapter;
import team.circleofcampus.http.HttpHelper;
import team.circleofcampus.model.Contact;
import team.circleofcampus.util.SharedPreferencesUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 惠普 on 2018-05-11.
 */
public class AddFriendsFragment extends Fragment {

    private EditText search;
    private SwipeMenuRecyclerView recycler_view;
    private TextView QR_btn;
    private TextView result;
    FriendSearchAdapter mMenuAdapter;
    List<Contact> data = new ArrayList<>();
    HttpHelper helper;
    SharedPreferencesUtil sharedPreferencesUtil;
    String Account;
    LoadingDialog dialog;
    ListListener listener;

    public void setListener(ListListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friends, null);
        initView(view);

        helper = new HttpHelper(getContext());
        recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));// 布局管理器。
        recycler_view.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recycler_view.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        sharedPreferencesUtil=new SharedPreferencesUtil();
        Account= sharedPreferencesUtil.getAccount(getActivity());

        mMenuAdapter = new FriendSearchAdapter(getContext(), data);
        mMenuAdapter.setItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(), AddRequestActivity.class);
                if (Account.equals( data.get(position).getAccount())){
                    intent.putExtra("isOther","no");
                }else{
                    intent.putExtra("isOther","yes");
                }
                intent.putExtra("user2", data.get(position).getAccount());
                startActivity(intent);
            }
        });
        recycler_view.setAdapter(mMenuAdapter);
        return view;
    }


    private void initView(View view) {
        search = (EditText) view.findViewById(R.id.search);
        recycler_view = (SwipeMenuRecyclerView) view.findViewById(R.id.recycler_view);
        QR_btn = (TextView) view.findViewById(R.id.QR_btn);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (search.getText().length() > 0) {
                    result.setText("");
                    data.clear();
                    QR_btn.setVisibility(View.GONE);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String s = helper.getUserInfoBySearch(search.getText().toString());
                            dialog = new LoadingDialog(getContext());
                            dialog.setLoadingText("请求中")
                                    .setSuccessText("添加成功")//显示加载成功时的文字
                                    .setFailedText("添加失败")
                                    .closeSuccessAnim()
                                    .setShowTime(1000)
                                    .setInterceptBack(false)
                                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                                    .show();
                            result.post(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        data.clear();
                                        JSONObject js = new JSONObject(s);
                                        result.setText("搜索到" + js.getString("Result") + "条数据\n");
                                        JSONArray json = new JSONArray(js.getString("Info"));
                                        for (int i = 0; i < json.length(); i++) {
                                            JSONObject jsonObject = new JSONObject(json.get(i).toString());
                                            Contact contact = new Contact();
                                            contact.setUserName(jsonObject.getString("UserName"));
                                            contact.setAccount(jsonObject.getString("Account"));
                                            contact.setSex(jsonObject.getString("Sex"));
                                            data.add(contact);

                                        }
                                        //延时2毫秒刷新
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                result.setVisibility(View.VISIBLE);
                                                mMenuAdapter.notifyDataSetChanged();
                                            }
                                        }, 200);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                            });
                        }
                    }).start();
                } else if (search.getText().length() == 0) {
                    data.clear();
                    mMenuAdapter.notifyDataSetChanged();
                    result.setVisibility(View.GONE);
                    QR_btn.setVisibility(View.VISIBLE);
                }
            }

        });

        result = (TextView) view.findViewById(R.id.result);
        QR_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DecoderActivity.class);
                startActivityForResult(intent, 1);

            }
        });
    }
    String message,jxResult;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (intent!=null) {
             message = intent.getStringExtra("message");
            final LoadingDialog  dialog = new LoadingDialog(getContext());
            dialog.setLoadingText("请求中")
                    .setSuccessText("添加成功")//显示加载成功时的文字
                    .setFailedText("请勿重复添加")
                    .closeSuccessAnim()
                    .setShowTime(1000)
                    .setInterceptBack(false)
                    .setLoadSpeed(LoadingDialog.Speed.SPEED_TWO)
                    .show();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    JSONObject js=null;
                    try {
                        js = new JSONObject(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (js.has("Account")){

                        try {
                            jxResult = helper.addFriend(Account, js.getString("Account"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        recycler_view.post(new Runnable() {
                            @Override
                            public void run() {

                                if (jxResult !=null) {

                                    JSONObject jsonObject = null;
                                    try {
                                        jsonObject = new JSONObject(jxResult);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String result = null;
                                    try {
                                        result = jsonObject.getString("result");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    final String finalResult = result;
                                    recycler_view.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (finalResult.equals("success")) {
                                                dialog.loadSuccess();
                                                if (listener != null) {
                                                    listener.update(true);
                                                }

                                            } else {
                                                dialog.loadFailed();

                                            }
                                        }
                                    });
                                }
                            }
                        });


                    }else{
                        Toast.makeText(getActivity(), "未扫描到账号信息", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(getActivity(), "扫描失败", Toast.LENGTH_SHORT).show();
        }


    }




}
