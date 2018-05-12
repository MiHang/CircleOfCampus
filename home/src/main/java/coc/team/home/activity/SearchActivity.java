package coc.team.home.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import coc.team.home.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText search;
    private Button button;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        search = (EditText) findViewById(R.id.search);
        button = (Button) findViewById(R.id.button);
        result = (TextView) findViewById(R.id.result);

        button.setOnClickListener(this);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (search.getText().length()>0){
                    result.setText("");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final String s=getData(search.getText().toString());
                            result.post(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        JSONObject  js = new JSONObject(s);
                                        result.setText("搜索到"+js.getString("Result")+"条数据\n");
                                        JSONArray json = new JSONArray(js.getString("Info"));
                                        for(int i=0;i<json.length();i++){
                                            JSONObject jsonObject= new JSONObject(json.get(i).toString());
                                            result.setText(result.getText()+jsonObject.getString("UserName")+jsonObject.getString("Account")+"\n");
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},0);
                    Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+10086));
                    startActivity(intent);
                }


                break;
        }
    }


    public String getData(String account) {
        String url = "http://192.168.56.1:8080/coc/search.do";
        OkHttpClient okHttpClient = new OkHttpClient();
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
