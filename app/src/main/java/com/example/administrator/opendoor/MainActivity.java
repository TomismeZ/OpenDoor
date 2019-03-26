package com.example.administrator.opendoor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.administrator.opendoor.activity.SetPasswordActivity;
import com.example.administrator.opendoor.base.BaseActivity;
import com.example.administrator.opendoor.util.HttpCallbackListener;
import com.example.administrator.opendoor.util.HttpUtil;
import com.example.administrator.opendoor.util.SDHelper;
import com.myncic.mynciclib.helper.DES3;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends BaseActivity {
    public static final String PREFERENCE_PACKAGE = "com.myncic.ican";
    public static final String PREFERENCE_NAME = "userInfo";
    public static int MODE = Context.MODE_WORLD_WRITEABLE;
    private Context othercontext;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //让导航按钮显示出来
        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.mipmap.back_n);
        }
        initView(); //初始化
    }

    /**
     * 初始化组件
     */
    private void initView() {
        ImageView imageView=findViewById(R.id.iv_open_door);
        //点击开门
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readSDFile();
            }
        });
        readSDFile(); //进入主界面后直接开门
    }



    /**
     * 访问外部SharedPreference资源
     */
    private void externalSP(){
        //获得第一个应用的包名,从而获得对应的Context,需要对异常进行捕获
        try {
            othercontext= createPackageContext("com.myncic.ican",Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        preferences=othercontext.getSharedPreferences("userInfo", Context.MODE_WORLD_WRITEABLE|Context.MODE_WORLD_READABLE);

        String ipAdd=preferences.getString("ipAdd","oa.myncic.com");



        //获取用户登录信息
        Long selfId=preferences.getLong("selfId", -1);

        int port=preferences.getInt("port",1234);
        Log.d("OpenDoorActivity","ipAdd: "+ipAdd);
        Log.d("OpenDoorActivity","port: "+port);
        Log.d("OpenDoorActivity","selfId: "+selfId);

        if(selfId != -1){
            String securitycode=preferences.getString("security", "");
            String accountName=preferences.getString("accountName", "");
            Log.d("OpenDoorActivity","securitycode: "+securitycode);

            Log.d("OpenDoorActivity","accountName: "+accountName);
            HttpUtil.sendHttpRequest("http://oa.myncic.com/open_door.php?uid="+selfId+"&scode="+securitycode, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    Log.d("OpenDoorActivity","响应成功："+response);
                }

                @Override
                public void onError(Exception e) {
                    Log.d("OpenDoorActivity","响应失败："+e.getMessage());
                }
            });
//                    Check check=new Check(selfId,securitycode);
//                    check.execute();
            Toast.makeText(MainActivity.this,"selfId:"+selfId+",securitycode:"+securitycode,Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(MainActivity.this,"获取用户信息失败",Toast.LENGTH_SHORT).show();
        }

    }

    class Check extends AsyncTask<Integer,Integer,String> {
        Long selfId;
        String securitycode;

        public Check(Long selfId, String securitycode) {
            this.selfId = selfId;
            this.securitycode = securitycode;
        }

        @Override
        protected String doInBackground(Integer... integers) {
            try {
                URL url=new URL("http://oa.myncic.com/open_door.php?uid="+selfId+"&scode="+securitycode);
                HttpURLConnection conn= (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                //数据并没有发送到服务器

                //得到服务器返回的流信息
                InputStream is=conn.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, SetPasswordActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
