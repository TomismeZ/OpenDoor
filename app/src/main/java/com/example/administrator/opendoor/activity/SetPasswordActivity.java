package com.example.administrator.opendoor.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.opendoor.R;
import com.example.administrator.opendoor.base.BaseActivity;

public class SetPasswordActivity extends BaseActivity {
    private boolean isSet=false;
    private TextView digitalPass=null;
    private TextView graphicPass;
    private TextView  fingerprints;
    private EditText setPass=null;
    private EditText confirmPass=null;
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        initView();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        //标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //让导航按钮显示出来
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //数字密码
        digitalPass=findViewById(R.id.digitalpass);
        digitalPass.setOnClickListener(new DigitalPassListener());

        //图形密码
        graphicPass=findViewById(R.id.graphicpass);
        graphicPass.setOnClickListener(new GraphicPasslistener());

        //指纹密码
        fingerprints=findViewById(R.id.fingerprints);
        fingerprints.setOnClickListener(new Fingerprintslistener());


    }

    /**
     * 跳转到指纹界面
     */
    class Fingerprintslistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

        }
    }

    /**
     * 跳转到图形界面
     */
    class GraphicPasslistener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent intent=new Intent();
            intent.setClass(SetPasswordActivity.this,GraphicPassSetActivity.class);
            startActivity(intent);
        }
    }
    /**
     * 数字密码
     */
    class DigitalPassListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            LayoutInflater factory= LayoutInflater.from(SetPasswordActivity.this);
            final View textEntry=factory.inflate(R.layout.digital_pass,null);
            AlertDialog.Builder builder=new AlertDialog.Builder(SetPasswordActivity.this)
                    .setTitle("设置密码")
                    .setIcon(getResources().getDrawable(android.R.drawable.ic_lock_lock))
                    .setView(textEntry)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setPass=textEntry.findViewById(R.id.set_pass);
                            confirmPass=textEntry.findViewById(R.id.confirm_pass);
                            if (!confirmPass.getText().toString().trim().equals("")&&
                                    confirmPass.getText().toString().trim().equals(setPass.getText().toString().trim())) {

                                preferences=getSharedPreferences("pass", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=preferences.edit();
//                                editor.putString("passway", "digitalpass");
                                editor.putBoolean("isSet", !isSet);
                                editor.putString("password", setPass.getText().toString().trim());
                                editor.commit();
                                dialog.dismiss();
                                Toast.makeText(SetPasswordActivity.this,"密码设置成功", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                Toast.makeText(SetPasswordActivity.this,"密码不一致，设置失败！", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();

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
