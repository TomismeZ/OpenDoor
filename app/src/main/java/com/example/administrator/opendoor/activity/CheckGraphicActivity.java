package com.example.administrator.opendoor.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.opendoor.R;
import com.example.administrator.opendoor.base.BaseActivity;
import com.example.administrator.opendoor.view.LockPatternUtils;
import com.example.administrator.opendoor.view.LockPatternView;
import com.example.administrator.opendoor.view.TimeView;

import java.util.Calendar;
import java.util.List;

public class CheckGraphicActivity extends BaseActivity {
    public static LockPatternView lockPatternView = null;
    private LockPatternUtils lockPatternUtils = null;
    public static int graphicEooroNum = 0; //图形绘制错误次数

    public static TextView tvPaswword;
    private TimeView timeView;  //计时器
    private SharedPreferences timesp;
    RelativeLayout relativeLayout;
    private int timeFalg=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_graphic);
        timesp = this.getSharedPreferences("time_info", Context.MODE_PRIVATE);
        init(); //初始化
        lockPatternView =  findViewById(R.id.lock_check);
        lockPatternUtils = new LockPatternUtils(this);

        tvPaswword=findViewById(R.id.tv_password);
        timeView = findViewById(R.id.cg_time);
        relativeLayout=findViewById(R.id.cg_relativeLayout);

        //从文件中读取数据

        long currentTime = timesp.getLong("currentTime", 0);
        //当前当前时间的分钟和毫秒值
        Calendar calendar = Calendar.getInstance();
        long cpValue = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);

        long diff = timesp.getLong("time", 0);
        Log.d("OpenDoorActivity", "当前时间：" + cpValue);
        Log.d("OpenDoorActivity", "上次时间：" + currentTime);
        Log.d("OpenDoorActivity", "计时时间：" + diff);
        if (diff != 0) {
            cpValue = cpValue - currentTime;
            if (cpValue < 300) {
                if (diff > cpValue) {
                    lockPatternView.disableInput();
                    lockPatternView.clearPattern();
                    //这里写一个计时器功能
                    relativeLayout.setVisibility(View.VISIBLE);
                    diff = diff - cpValue;
                    if (diff <= 300) {
                        timeView.reStart(diff);
                    } else {
                        timeView.reStart(300);
                    }
                }
            }
        }


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
                SharedPreferences preferences=getSharedPreferences("pass", Context.MODE_PRIVATE);
                String pass=preferences.getString("ok_lock_pwd", "");
                if(pattern.size() >3){
                if (pass.trim().equals(lockPatternUtils.patternToString(pattern))) {
                    Intent intent=new Intent(CheckGraphicActivity.this,GraphicPassSetActivity.class);
                    startActivity(intent);
                    CheckGraphicActivity.this.finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else {
//                    Toast.makeText(CheckGraphicActivity.this, "密码错误，请重试！", Toast.LENGTH_LONG).show();
                    tvPaswword.setText("密码错误，请重新输入");
//                    tvPaswword.setTextColor(tvPaswword.getResources().getColor(R.color.red));
                    SharedPreferences.Editor editor = preferences.edit();
                    graphicEooroNum++;
                    editor.putInt("cg_graphicErrorNum", graphicEooroNum);
                    editor.commit();
                }
                }else{
                   Toast.makeText(CheckGraphicActivity.this, "选择的个数至少为4个",Toast.LENGTH_SHORT).show();
                }


                //次数超过五次时，就会提示时间
                if (graphicEooroNum == 5) {
                    Log.d("OpenDoorActivity", "graphicEooroNum: " + graphicEooroNum);
                    lockPatternView.disableInput();
                    lockPatternView.clearPattern();
                    tvPaswword.setText("错误的尝试次数过多！");
                    //这里写一个计时器功能
                    relativeLayout.setVisibility(View.VISIBLE);
                    timeView.reStart(5 * 60);

                    SharedPreferences.Editor editor = timesp.edit();

                    editor.putInt("timeFalg",timeFalg);
                    editor.commit();

                   /* handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lockPatternView.enableInput();
                            tvPaswword.setText("请绘制你的原始密码");
                            tvPaswword.setTextColor(getResources().getColor(R.color.white));
                        }
                    },1000*30);*/
                }
            }
        });
    }

    private Handler handler=new Handler();

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

        LinearLayout linearLayout=findViewById(R.id.check_pass_layout);
        linearLayout.getBackground().setAlpha(80);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //当activity销毁时，把等待时间保存起来
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
        SharedPreferences.Editor editor = timesp.edit();
        editor.putLong("time", timeView.minutes * 60 + timeView.seconds);
        editor.putLong("currentTime", currentTime);
        editor.commit();
    }
}
