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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.opendoor.R;
import com.example.administrator.opendoor.base.ActivityCollector;
import com.example.administrator.opendoor.base.BaseActivity;
import com.example.administrator.opendoor.view.LockPatternUtils;
import com.example.administrator.opendoor.view.LockPatternView;

import java.util.List;

public class GraphicPassSetActivity extends BaseActivity {
    private static boolean isSet = false; //是否设置
    private Button cancel;
    private Button next;
    private LockPatternView lockPatternView;
    private LockPatternUtils lockPatternUtils;
    private SharedPreferences preferences;
    public static String pass="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphic_pass_set);
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

        lockPatternView = findViewById(R.id.lock);
        lockPatternUtils = new LockPatternUtils(this);
       /* cancel=findViewById(R.id.cancel);
        cancel.setOnClickListener(new CancelListener());
        next=findViewById(R.id.next);
        next.setOnClickListener(new NextListener());*/
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

                if (pattern.size() > 3) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("lock_pwd", lockPatternUtils.patternToString(pattern));
                    editor.commit();
                    finish();
                    //直接就进行下一步
                    Intent intent = new Intent();
                    intent.setClass(GraphicPassSetActivity.this, ConfirmPassActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

//                    isSet=true;
                } else {
                    Toast.makeText(GraphicPassSetActivity.this, "选中图形的个数必须4个以上", Toast.LENGTH_SHORT).show();
                }

                lockPatternView.clearPattern();
               /* SharedPreferences.Editor editor=preferences.edit();
                editor.putString("lock_pwd", lockPatternUtils.patternToString(pattern));
                editor.commit();
                isSet=true;*/
            }


        });
    }

    /**
     * 取消时的监听
     */
    class CancelListener implements View.OnClickListener {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            setPass();
//            finish();
            Intent intent=new Intent("com.zdk.broadcastbestpractice.FORCE_FINISHALL");
            sendBroadcast(intent);
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
    }

    /**
     * 下一步时的监听
     */
    class NextListener implements View.OnClickListener {
        public void onClick(View v) {
            if (isSet) {
                Intent intent=new Intent();
                intent.setClass(GraphicPassSetActivity.this, ConfirmPassActivity.class);
                startActivity(intent);
                GraphicPassSetActivity.this.finish();
//                overridePendingTransition(R.anim.push_below_in,R.anim.push_below_out);
                isSet=false;
            }
            else {
                Toast.makeText(GraphicPassSetActivity.this, "请设置密码", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void setPass(){
        SharedPreferences.Editor editor = preferences.edit();
        if (pass!=null&&!pass.equals("")) {
            editor.putString("lock_pwd", pass);
            editor.commit();
        }else {
            editor.putString("lock_pwd", null);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
