package coc.team.home.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import coc.team.home.Interface.OnItemClickListener;
import coc.team.home.R;
import coc.team.home.adapter.GoodFriendAdapter;
import coc.team.home.model.Contact;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText search;
    private Button button;
    private TextView result;
    private SwipeMenuRecyclerView recycler_view;
    GoodFriendAdapter mMenuAdapter;
    List<Contact> data=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));// 布局管理器。
        recycler_view.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recycler_view.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
//        mSwipeMenuRecyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。



        mMenuAdapter = new GoodFriendAdapter(getApplicationContext(),data);
        mMenuAdapter.setItemListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getApplicationContext(), ""+position, Toast.LENGTH_SHORT).show();
            }
        });
        recycler_view.setAdapter(mMenuAdapter);
    }

    private void initView() {
        search = (EditText) findViewById(R.id.search);
        button = (Button) findViewById(R.id.button);
        result = (TextView) findViewById(R.id.result);

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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String s = getData(search.getText().toString());
                            result.post(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        if(!s.equals("")){
                                            JSONObject js = new JSONObject(s);
                                            result.setText("搜索到" + js.getString("Result") + "条数据\n");
                                            JSONArray json = new JSONArray(js.getString("Info"));
                                            for (int i = 0; i < json.length(); i++) {
                                                JSONObject jsonObject = new JSONObject(json.get(i).toString());
                                                Contact contact=new Contact();
                                                contact.setUserName(jsonObject.getString("UserName"));
                                                contact.setAccount(jsonObject.getString("Account"));
                                                contact.setSex(jsonObject.getString("Sex"));
                                                data.add(contact);

//                                            result.setText(result.getText() + jsonObject.getString("UserName") + jsonObject.getString("Account") + "\n");

                                            }
                                            //延时2毫秒刷新
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mMenuAdapter.notifyDataSetChanged();
                                                }
                                            }, 200);
                                        }else{
                                            result.setText("连接服务器失败\n");
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                            });
                        }
                    }).start();
                }
            }

        });
        recycler_view = (SwipeMenuRecyclerView) findViewById(R.id.recycler_view);
    }


    public String getData(String account) {
        String url = "http://192.168.1.101:8080/coc/search.do";
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(5,TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(5,TimeUnit.SECONDS)//设置连接超时时间
                .build();

        JSONObject js = new JSONObject();

        try {
            js.put("Search", account);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.parse("application/json;charset=utf8");
        RequestBody requestBody = RequestBody.create(mediaType, js.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
}
