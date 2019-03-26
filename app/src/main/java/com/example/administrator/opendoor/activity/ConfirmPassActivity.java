package com.example.administrator.opendoor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.opendoor.MainActivity;
import com.example.administrator.opendoor.R;
import com.example.administrator.opendoor.base.ActivityCollector;
import com.example.administrator.opendoor.base.BaseActivity;
import com.example.administrator.opendoor.view.LockPatternUtils;
import com.example.administrator.opendoor.view.LockPatternView;

import java.util.List;

public class ConfirmPassActivity extends BaseActivity {
/*    private Button last;
    private Button ok;*/
    private static boolean isSet = false;
    private LockPatternView lockPatternView;
    private LockPatternUtils lockPatternUtils;
    private SharedPreferences preferences;
    private String pass="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_pass);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        //标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.getBackground().setAlpha(80);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //让导航按钮显示出来
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
        }

        RelativeLayout relativeLayout=findViewById(R.id.relativeLayout1);
        relativeLayout.getBackground().setAlpha(80);

        RelativeLayout relativeLayout1=findViewById(R.id.check_pass_rlayout);
        relativeLayout1.getBackground().setAlpha(80);

        lockPatternView = findViewById(R.id.lock_confirm);
      /*  last=findViewById(R.id.last);
        last.setOnClickListener(new LastListener());
        ok=findViewById(R.id.ok);
        ok.setOnClickListener(new OkListener());*/
        lockPatternUtils = new LockPatternUtils(this);
        preferences=getSharedPreferences("pass", Context.MODE_PRIVATE);
        pass=preferences.getString("lock_pwd", "");
        lockPatternView.setOnPatternListener(new LockPatternView.OnPatternListener() {
            @Override
            public void onPatternStart() {

            }

            @Override
            public void onPatternCleared() {

            }

            @Override
            public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {

            }

            @Override
            public void onPatternDetected(List<LockPatternView.Cell> pattern) {
//                if(pattern.size() > 3) {
                    if (pass.trim().equals(lockPatternUtils.patternToString(pattern))) {
//                    isSet=true;
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("passway", "graphicpass");
                        editor.putBoolean("isSet", true);
                        editor.putBoolean("state", false);
                        editor.putString("ok_lock_pwd", lockPatternUtils.patternToString(pattern));
                        editor.commit();
                        ConfirmPassActivity.this.finish();
                       /* Intent intent = new Intent("com.zdk.broadcastbestpractice.FORCE_FINISHALL");
                        sendBroadcast(intent);*/

                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        isSet = false;
//                        readSDFile();
                        Toast.makeText(ConfirmPassActivity.this, "密码设置成功", Toast.LENGTH_LONG).show();
                      /*  boolean state = preferences.getBoolean("state", true);
                        if(state){
                            Intent intent=new Intent("com.zdk.broadcastbestpractice.FORCE_FINISHALL");
                            sendBroadcast(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        }*/

                    } else {
                        Toast.makeText(ConfirmPassActivity.this, "密码不一致，请重新设置！", Toast.LENGTH_LONG).show();
                        isSet = false;
                        ConfirmPassActivity.this.finish();
                        boolean state = preferences.getBoolean("state", true);
                        Intent intent=null;
                        if(state){
                            intent=new Intent(ConfirmPassActivity.this,CheckPassActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        }else{
                            intent=new Intent(ConfirmPassActivity.this,GraphicPassSetActivity.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                        }
                    }
//               }
//               else{
//                    Toast.makeText(ConfirmPassActivity.this,"绘制图形的个数必须是4个以上",Toast.LENGTH_SHORT).show();
//                }

                lockPatternView.clearPattern();
            }
        });
    }

    /**
     * 上一步
     */
    class LastListener implements View.OnClickListener {
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent=new Intent();
            intent.setClass(ConfirmPassActivity.this, GraphicPassSetActivity.class);
            startActivity(intent);
            setPass();
            ConfirmPassActivity.this.finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    }

    /**
     * 确定
     */
    class OkListener implements View.OnClickListener {
        public void onClick(View v) {
            if (isSet) {
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("passway", "graphicpass");
                editor.putBoolean("isSet", true);
                editor.putBoolean("state", false);
                editor.commit();

                /*Intent intent=new Intent(ConfirmPassActivity.this,CheckPassActivity.class);
                startActivity(intent);*/
                ConfirmPassActivity.this.finish();
                Intent intent=new Intent("com.zdk.broadcastbestpractice.FORCE_FINISHALL");
                sendBroadcast(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                isSet=false;
                readSDFile();
                Toast.makeText(ConfirmPassActivity.this, "密码设置成功", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(ConfirmPassActivity.this, "密码不一致，设置失败！", Toast.LENGTH_LONG).show();

            }
        }
    }

    /**
     * 设置密码
     */
    public void setPass(){
        SharedPreferences.Editor editor = preferences.edit();
        if (GraphicPassSetActivity.pass!=null&&!GraphicPassSetActivity.pass.equals("")) {
            editor.putString("lock_pwd", GraphicPassSetActivity.pass);
            editor.commit();
        }else {
            editor.putString("lock_pwd", null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
              /*  boolean state = preferences.getBoolean("state", true);
                if(!state){
                    Intent intent=new Intent();
                    intent.setClass(ConfirmPassActivity.this, GraphicPassSetActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent("com.zdk.broadcastbestpractice.FORCE_FINISHALL");
                    sendBroadcast(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }*/
               /* Intent intent=new Intent();
                intent.setClass(ConfirmPassActivity.this, GraphicPassSetActivity.class);
                startActivity(intent);*/
                setPass();
                ConfirmPassActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
        }
        return true;
    }
}
