package com.example.david.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity {

    private EditText username=null;
    private EditText  password=null;
    private Button btn1;
    private Button btn2;
    public URL http_url;
    public String data;
    public Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        btn1 = (Button)findViewById(R.id.login);
        btn2 = (Button)findViewById(R.id.register);
        //消息处理器

        handler=new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    //登入成功
                    case 1:
                        Toast.makeText(MainActivity.this, msg.getData().getString("msg"),
                                Toast.LENGTH_SHORT).show();
                        break;
                    //登入失败
                    case 2:
                        Toast.makeText(MainActivity.this, msg.getData().getString("msg"),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //是否输入账号密码
                if(username.getText().toString().length()>0&&password.getText().toString().length()>0){
                    //子线程可以获取UI的值，不能更改
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
//请求地址--
                                String url="http://10.0.2.2:8080/think/index.php/Home/Android?"+ "username=" + URLEncoder.encode(username.getText().toString(), "UTF-8")
                                        + "&password=" + URLEncoder.encode(password.getText().toString(), "UTF-8");
                                http_url=new URL(url);
                                if(http_url!=null)
                                {
                                    //打开一个HttpURLConnection连接
                                    HttpURLConnection conn = (HttpURLConnection) http_url.openConnection();
                                    conn.setConnectTimeout(5* 1000);//设置连接超时
                                    conn.setRequestMethod("GET");//以get方式发起请求
                                    //允许输入流
                                    conn.setDoInput(true);
                                    //接收服务器响应
                                    if (conn.getResponseCode() == 200) {
                                        InputStream is = conn.getInputStream();//得到网络返回的输入流
                                        BufferedReader buf=new BufferedReader(new InputStreamReader(is));//转化为字符缓冲流
                                        data=buf.readLine();
                                        buf.close();is.close();
                                        //判断登入结果
                                        analyse(data);
                                    }
                                }
                            } catch( Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "请完整输入账号密码",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void analyse (String data)
    {
        System.out.println(data);
        try {
            JSONObject json_data=new JSONObject(data);
            Boolean state=json_data.getBoolean("success");
            String msg=json_data.getString("msg");
            //登入成功
            if(state)
            {
                //发送消息
                Message message= new Message();
                message.what=1;
                Bundle temp = new Bundle();
                temp.putString("msg",msg);
                message.setData(temp);
                handler.sendMessage(message);

            }
            //登入失败
            else
            {
                Message message= new Message();
                message.what=2;
                Bundle temp = new Bundle();
                temp.putString("msg",msg);
                message.setData(temp);
                handler.sendMessage(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void login(View view) {
        if (username.getText().toString().equals("admin") &&
                password.getText().toString().equals("admin")) {
            makeText(this, "", Toast.LENGTH_SHORT).show();
            makeText(getApplicationContext(), "Redirecting...",Toast.LENGTH_SHORT).show();
        } else {
            makeText(getApplicationContext(), "Wrong Credentials",
                    Toast.LENGTH_SHORT).show();

        }
    }
}
