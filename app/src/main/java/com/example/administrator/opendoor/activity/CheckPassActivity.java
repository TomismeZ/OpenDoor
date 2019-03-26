package com.example.administrator.opendoor.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telecom.InCallService;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.opendoor.MainActivity;
import com.example.administrator.opendoor.R;
import com.example.administrator.opendoor.base.BaseActivity;
import com.example.administrator.opendoor.util.AccessApplicationUtils;
import com.example.administrator.opendoor.util.KeyboardUtil;
import com.example.administrator.opendoor.util.SDHelper;
import com.example.administrator.opendoor.view.FingerPrinterView;
import com.example.administrator.opendoor.view.LockPatternUtils;
import com.example.administrator.opendoor.view.LockPatternView;
import com.example.administrator.opendoor.view.TimeView;

import java.util.Calendar;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import zwh.com.lib.FPerException;
import zwh.com.lib.RxFingerPrinter;

public class CheckPassActivity extends BaseActivity {
    private SharedPreferences sp = null;
    //图形验证
    public static LockPatternView lockPatternView = null;
    private LockPatternUtils lockPatternUtils = null;
    //数字验证
    private boolean isSet = false;
    private String pass = null;
    private EditText checkPass = null;
    private EditText setPass = null;
    private EditText confirmPass = null;
    private KeyboardUtil keyboardUtil;
    //指纹识别
    private FingerPrinterView fingerPrinterView;
    private int fingerErrorNum = 0; // 指纹错误次数
    public static int graphicEooroNum = 0; //图形绘制错误次数
    RxFingerPrinter rxfingerPrinter;
    private Handler handler = new Handler();

    private boolean isFirst = true; //是否是第一次启动

    private TimeView timeView;  //计时器
    private SharedPreferences timesp;
    RelativeLayout relativeLayout;
    public static RelativeLayout relativeLayout3;
    private int no_fingerFalg = 1;

    private PackageInfo packageInfo; //包的信息(应用包)
    //要调用app的包名 + 类名
    private static String ICANPACKAGE = "com.myncic.ican";
    private static String ICANALASSNAME = "com.myncic.ican.Activity_Login";
    private static String MPOSCLASSNAME2 = "com.myncic.ican.Activity_Main";

    //要调用本应用app的服务
    private static String SERVICENAME = "com.example.administrator.opendoor.service.MainService";

    //如下是本应用的包名
    private static String PACKAGE = "com.example.administrator.opendoor";

    private AlertDialog alert;
    private AlertDialog alert1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alert = new AlertDialog.Builder(CheckPassActivity.this)
                .create();
        alert1 = new AlertDialog.Builder(CheckPassActivity.this)
                .create();
        timesp = this.getSharedPreferences("time_info", Context.MODE_PRIVATE);
        startMainActivity();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("OpenDoorActivity", "onResume");
        detectionApplication();

//        startMainActivity();
//        timeSurplus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("OpenDoorActivity", "onPause");
        alert.dismiss();
        alert1.dismiss();
    }

    /**
     * 检测应用是否安装
     */
    private void detectionApplication() {
        //http://blog.csdn.net/TTKatrina/article/details/50755024
        //String pkg代表包名，String download代表下载url

        //首先判断调用的apk是否安装
        //String pkg代表包名，String download代表下载url
//        final PackageManager pm = CheckPassActivity.this.getPackageManager();
//        Intent intent1 = pm.getLaunchIntentForPackage(ICANPACKAGE);
        boolean isAlive = AccessApplicationUtils.isInstall(CheckPassActivity.this, ICANPACKAGE);
        if (isAlive) {

/*//            Intent intent = new Intent(Intent.ACTION_MAIN);//设置action
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);//设置category
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置singleTask启动模式
            ComponentName cn = new ComponentName(ICANPACKAGE, ICANALASSNAME);//封装了包名 + 类名
            //设置数据
            //intent.putExtra("package",PACKAGE);
//            intent.putExtra("className","CheckPassActivity");
//            intent.putExtra("isOnLineSign", true);
            intent.setComponent(cn);
            startActivityForResult(intent,RESULT_OK);*/
//            startActivity(intent1);
            Boolean existFile = SDHelper.isExistFile(CheckPassActivity.this, "saveLogin.txt");
//            boolean loginState = AccessApplicationUtils.externalSharedPreferences(CheckPassActivity.this);
            if (!existFile) {

                alert.setMessage("您的吾能还没有登录哦！请前往登录");
                //添加取消按钮
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        CheckPassActivity.this.finish();
                    }
                });

                //添加确定按钮
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "前往登录", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                        AccessApplicationUtils.openApp(CheckPassActivity.this,ICANPACKAGE);
                    }
                });
                alert.setCancelable(false);
                alert.show();

            }

            Log.d("OpenDoorActivity", "应用已经安装！");
        } else {
            Log.d("OpenDoorActivity", "应用没有安装！");
            //把ICAN程序保存的文件删除
            //删除文件
            SDHelper.deleteFile(CheckPassActivity.this,"userInfo.json");

            SDHelper.deleteFile(CheckPassActivity.this,"saveLogin.txt");

            alert1.setMessage("您没有安装吾能，请前往下载");
            //添加取消按钮
            alert1.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    // TODO Auto-generated method stub
                    dialog.dismiss();
                    CheckPassActivity.this.finish();
                }
            });

            //添加确定按钮
            alert1.setButton(DialogInterface.BUTTON_POSITIVE, "前往下载", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.dismiss();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://app.myncic.com/1001"));
                    startActivity(browserIntent);
                }
            });
            alert1.setCancelable(false);
            alert1.show();

          /*  AlertDialog.Builder builder = new AlertDialog.Builder(CheckPassActivity.this)
                    .setMessage("您没有安装吾能，请前往下载")
                    .setPositiveButton("前往下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://app.myncic.com/1001"));
                            startActivity(browserIntent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            CheckPassActivity.this.finish();
                        }
                    }).setCancelable(false);
            builder.create().show();*/

        }

       /* try {
       //获取已经安装apk列表
 List<PackageInfo> packList = context.getPackageManager().getInstalledPackages(0);
//获得包名PackageInfo.packageName
            packageInfo=CheckPassActivity.this.getPackageManager().getPackageInfo("com.myncic.ican",0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(packageInfo == null){
            Log.d("OpenDoorActivity","应用没有安装！");

        }else{
            Log.d("OpenDoorActivity","应用已经安装！");
        }*/
    }

    /**
     * 根据条件来跳转到哪一个界面
     */
    private void startMainActivity() {
        sp = getSharedPreferences("pass", Context.MODE_PRIVATE);
        String passWay = sp.getString("passway", null);
        isFirst = sp.getBoolean("state", true);
        graphicEooroNum = sp.getInt("graphicEooroNum", 0);
        if (graphicEooroNum > 5) {
            graphicEooroNum = 0;
        }
        if (isFirst) {
            //第一次启动
            firstStart();

           /* setContentView(R.layout.activity_welcome);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CheckPassActivity.this.finish();
                    Intent intent = null;
                    intent = new Intent(CheckPassActivity.this, GraphicPassSetActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }, 800);*/

        } else {
            if (passWay != null) {
                if (passWay.equals("graphicpass")) {
                    checkGraphicPass();
                }
            }
        }

    }

    /**
     * 检查指纹识别
     */
    private void checkFingerPrinter() {


        Log.d("OpenDoorActivity", rxfingerPrinter.fingerSupport + "");
//        setContentView(R.layout.finger_printer_check);
        fingerPrinterView = findViewById(R.id.fpv);
        fingerPrinterView.setOnStateChangedListener(new FingerPrinterView.OnStateChangedListener() {
            @Override
            public void onChange(int state) {
                if (state == FingerPrinterView.STATE_CORRECT_PWD) {
                    fingerErrorNum = 0;
                    Toast.makeText(CheckPassActivity.this, "指纹识别成功", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(CheckPassActivity.this, MainActivity.class);
//                    startActivity(intent);
                    readSDFile();
                    finish();
                }
                if (state == FingerPrinterView.STATE_WRONG_PWD) {
                    Toast.makeText(CheckPassActivity.this, "指纹识别失败，还剩" + (5 - fingerErrorNum) + "次机会",
                            Toast.LENGTH_SHORT).show();
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                }
            }
        });

        rxfingerPrinter = new RxFingerPrinter(this);
        fingerErrorNum = 0;
        rxfingerPrinter.unSubscribe(this);
        Subscription subscription = rxfingerPrinter.begin().subscribe(new Subscriber<Boolean>() {
            @Override
            public void onStart() {
                super.onStart();
                if (fingerPrinterView.getState() == FingerPrinterView.STATE_SCANING) {
                    return;
                } else if (fingerPrinterView.getState() == FingerPrinterView.STATE_CORRECT_PWD
                        || fingerPrinterView.getState() == FingerPrinterView.STATE_WRONG_PWD) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_NO_SCANING);
                } else {
                    fingerPrinterView.setState(FingerPrinterView.STATE_SCANING);
                }
            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof FPerException) {
//                    Toast.makeText(CheckPassActivity.this, ((FPerException) e).getDisplayMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("OpenDoorActivity", ((FPerException) e).getDisplayMessage());
                    no_fingerFalg = -1;
                }
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    fingerPrinterView.setState(FingerPrinterView.STATE_CORRECT_PWD);
                } else {
                    fingerErrorNum++;
                    fingerPrinterView.setState(FingerPrinterView.STATE_WRONG_PWD);
                }
            }
        });
        rxfingerPrinter.addSubscription(this, subscription);
        //设置文件的显示与隐藏
        RelativeLayout rlFinger = findViewById(R.id.rl_finger);
        if (no_fingerFalg != -1) {
            rlFinger.setVisibility(View.VISIBLE);
        } else {
            rlFinger.setVisibility(View.GONE);
        }
    }

    private void timeSurplus(){
        //从文件中读取时间
        SharedPreferences sharedPreferences = getSharedPreferences("time_info", MODE_PRIVATE);

        long currentTime = sharedPreferences.getLong("currentTime", 0);

        //当前当前时间的分钟和毫秒值
        Calendar calendar = Calendar.getInstance();

        long cpValue = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);

        long diff = sharedPreferences.getLong("time", 0);
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
                    relativeLayout3.setVisibility(View.GONE);
                    diff = diff - cpValue;
                    if (diff <= 300) {
                        timeView.reStart(diff);
                    } else {
                        timeView.reStart(300);
                    }
                }
            }
        }

    }
    /**
     * 图形密码的验证
     */
    private void checkGraphicPass() {
        setContentView(R.layout.activity_check_pass);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);

//        detectionApplication(); //检测应用是否安装

        lockPatternView = findViewById(R.id.lock_check);
        lockPatternUtils = new LockPatternUtils(this);

        ImageView modifyPassword = findViewById(R.id.modify_pass_image);

        relativeLayout3 = findViewById(R.id.relativeLayout3);
        timeView = findViewById(R.id.time_demo);
        //定义一个标志，时间标记
        final int timeFalg = 0;

        relativeLayout = findViewById(R.id.relativeLayout2);


        timeSurplus();
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
                SharedPreferences preferences = getSharedPreferences("pass", Context.MODE_PRIVATE);
                String pass = preferences.getString("ok_lock_pwd", "");
                //绘制的个数至少为四个才能解锁
                if (pattern.size() > 3) {
                    if (pass.trim().equals(lockPatternUtils.patternToString(pattern))) {
//                    Intent intent = new Intent(CheckPassActivity.this, MainActivity.class);
//                    startActivity(intent);
                        if(!isGrantExternalRW(CheckPassActivity.this)){
                            return;
                        }

                        readSDFile(); //公司开门
                        CheckPassActivity.this.finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        SharedPreferences.Editor editor = timesp.edit();
                        editor.putInt("graphicEooroNum", 0);

                        editor.commit();
                    } else {
                        SharedPreferences.Editor editor = timesp.edit();
                        graphicEooroNum++;
                        if (graphicEooroNum <= 5) {
                            editor.putInt("graphicEooroNum", graphicEooroNum);
                            editor.commit();
                        }
                        Toast.makeText(CheckPassActivity.this, "绘制密码错误，还剩" + (5 - graphicEooroNum) + "次机会", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("OpenDoorActivity", "选择的个数至少为4个，当前个数为: " + pattern.size());
                }
                lockPatternView.clearPattern();

                //次数超过五次时，就会提示时间
                if (graphicEooroNum == 5) {
                    Log.d("OpenDoorActivity", "graphicEooroNum: " + graphicEooroNum);
                    lockPatternView.disableInput();
                    lockPatternView.clearPattern();
                    //这里写一个计时器功能
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout3.setVisibility(View.GONE);
                    timeView.reStart(5 * 60);

                    SharedPreferences.Editor editor = timesp.edit();

                    editor.putInt("timeFalg", timeFalg);
                    editor.commit();
                   /* handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //这里写一个计时器
                            graphicEooroNum=0;
                            lockPatternView.enableInput();
                        }
                    },1000*30);*/
                }
            }
        });

        modifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckPassActivity.this, CheckGraphicActivity.class);
                startActivity(intent);
            }
        });

        checkFingerPrinter(); //指纹解锁
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.settings:
//                choosePassword(); //选择密码
//                break;
//
//        }
//        return true;
//    }

    /**
     * 选择密码的登录方式
     */
    private void choosePassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckPassActivity.this)
                .setTitle("选择登录方式")
                .setIcon(R.drawable.choose_back)
//                .setIcon(getResources().getDrawable(R.drawable.ican_home_normal))
                .setItems(new String[]{"数字登录", "图形登录"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                checkDigitalPass(); //数字密码
                                break;
                            case 1:
                                checkGraphicPass(); //图形密码
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxfingerPrinter != null) {
            rxfingerPrinter.unSubscribe(this);
        }

        //当activity销毁时，把等待时间保存起来
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
        SharedPreferences.Editor editor = timesp.edit();
        editor.putLong("time", timeView.minutes * 60 + timeView.seconds);
        editor.putLong("currentTime", currentTime);
        editor.commit();

    }

    /**
     * 第一次启动时加载这个页面
     */
    private void firstStart() {
        setContentView(R.layout.activity_graphic_pass_set);
        //标题栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.getBackground().setAlpha(80);
        setSupportActionBar(toolbar);
    /*    ActionBar actionBar = getSupportActionBar();
        //让导航按钮显示出来
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.btn_back);
        }*/

        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout1);
        relativeLayout.getBackground().setAlpha(80);

        RelativeLayout relativeLayout1 = findViewById(R.id.check_pass_rlayout);
        relativeLayout1.getBackground().setAlpha(80);

        lockPatternView = findViewById(R.id.lock);
        lockPatternUtils = new LockPatternUtils(this);
       /* cancel=findViewById(R.id.cancel);
        cancel.setOnClickListener(new CheckPassActivity.CancelListener());
        next=findViewById(R.id.next);
        next.setOnClickListener(new CheckPassActivity.NextListener());*/
        pass = sp.getString("lock_pwd", "");

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
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("lock_pwd", lockPatternUtils.patternToString(pattern));
                    editor.commit();
                    finish();
                    //直接就进行下一步
                    Intent intent = new Intent();
                    intent.setClass(CheckPassActivity.this, ConfirmPassActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                    isSet=true;
                } else {
                    Toast.makeText(CheckPassActivity.this, "选中图形的个数必须4个以上", Toast.LENGTH_SHORT).show();
                }
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
            Intent intent = new Intent("com.zdk.broadcastbestpractice.FORCE_FINISHALL");
            sendBroadcast(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * 下一步时的监听
     */
    class NextListener implements View.OnClickListener {
        public void onClick(View v) {
            if (isSet) {
                Intent intent = new Intent();
                intent.setClass(CheckPassActivity.this, ConfirmPassActivity.class);
                startActivity(intent);
                CheckPassActivity.this.finish();
//                overridePendingTransition(R.anim.push_below_in,R.anim.push_below_out);
                isSet = false;
            } else {
                Toast.makeText(CheckPassActivity.this, "请设置密码", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 设置密码
     */
    public void setPass() {
        SharedPreferences.Editor editor = sp.edit();
        if (pass != null && !pass.equals("")) {
            editor.putString("lock_pwd", pass);
            editor.commit();
        } else {
            editor.putString("lock_pwd", null);
        }
    }

    /**
     * 确认数字密码
     */
    private void checkDigitalPass() {
        setContentView(R.layout.confirm_pass);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("");
//        toolbar.getBackground().setAlpha(5);
//        setSupportActionBar(toolbar);
        checkPass = findViewById(R.id.check_pass);

        isSet = sp.getBoolean("isSet", false);
        pass = sp.getString("password", null);

        if (isSet) {
            checkPass.addTextChangedListener(new SearchDigital());
            checkFingerPrinter();
            keyboardUtil = new KeyboardUtil(this, this, checkPass);
            checkPass.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int numberType = checkPass.getInputType();
                    checkPass.setInputType(InputType.TYPE_NULL);
                    keyboardUtil.showKeyboard();
                    checkPass.setInputType(numberType);
                    return true;
                }
            });
        } else {
            LayoutInflater factory = LayoutInflater.from(CheckPassActivity.this);
            final View textEntry = factory.inflate(R.layout.digital_pass, null);
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CheckPassActivity.this)
                    .setTitle("设置密码")
                    .setIcon(getResources().getDrawable(R.drawable.ican_home_normal))
                    .setView(textEntry)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setPass = textEntry.findViewById(R.id.set_pass);
                            confirmPass = textEntry.findViewById(R.id.confirm_pass);
                            if (!confirmPass.getText().toString().trim().equals("") &&
                                    confirmPass.getText().toString().trim().equals(setPass.getText().toString().trim())) {

                                sp = getSharedPreferences("pass", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
//                                editor.putString("passway", "digitalpass");
                                editor.putBoolean("isSet", !isSet);
                                editor.putString("password", setPass.getText().toString().trim());
                                editor.commit();
                                dialog.dismiss();
                                Toast.makeText(CheckPassActivity.this, "密码设置成功", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(CheckPassActivity.this, "密码不一致，设置失败！", Toast.LENGTH_LONG).show();
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

    /**
     * 文字改变监听
     */
    class SearchDigital implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (checkPass.getText().toString()
                    .trim().equals(pass)) {
                Intent intent = new Intent(CheckPassActivity.this, MainActivity.class);
                startActivity(intent);
            } else {

             /*   Toast.makeText(
                        CheckPassActivity.this,
                        "密码错误，请重试！",
                        Toast.LENGTH_LONG)
                        .show();*/
//                checkPass.setText("");

            }
        }
    }

    /**
     * 运行时权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    readSDFile();
                    CheckPassActivity.this.finish();
                }else{
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
                default:
        }
    }
}
