package com.example.administrator.opendoor.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.opendoor.activity.CheckPassActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/8.
 */

public class AccessApplicationUtils {

    public static Context othercontext;
    /**
     * 根据包名判断某个应用程序是否安装方法
     */
    public static boolean isInstall(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断某个进程是否正在运行的方法
     */
    public static boolean isProcessWork(Context context, String runningPackage) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listOfProcesses = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : listOfProcesses) {
            if (process.processName.contains(runningPackage)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个服务是否正在运行的方法
     */
    public static boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    /**
     * 检测应用是否安装
     */
    private void detectionApplication(final Context context, String pageName) {
       /* final PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(pageName);
        if (null == intent) {//没有获取到intent
            if (!TextUtils.isEmpty(downoad)) {//跳转到下载第三方应用界面
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downoad)));
            }
        } else {
            context.startActivity(intent);
        }*/
        //参考网站
        //http://blog.csdn.net/TTKatrina/article/details/50755024
        //首先判断调用的apk是否安装
        //String pkg代表包名，String download代表下载url
        final PackageManager pm = context.getPackageManager();
        Intent intent1 = pm.getLaunchIntentForPackage(pageName);
        boolean isAlive = AccessApplicationUtils.isInstall(context, pageName);
        if (isAlive) {
           /* Intent intent = new Intent();
//            Intent intent = new Intent(Intent.ACTION_MAIN);//设置action
//            intent.addCategory(Intent.CATEGORY_LAUNCHER);//设置category
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置singleTask启动模式
            ComponentName cn = new ComponentName(ICANPACKAGE, "Activity_Login");//封装了包名 + 类名
            //设置数据
            *//*intent.putExtra("package",PACKAGE);
            intent.putExtra("className","CheckPassActivity");
            intent.putExtra("isOnLineSign", true);*//*
            intent.setComponent(cn);
            startActivity(intent);*/

            context.startActivity(intent1);
            Log.d("OpenDoorActivity", "应用已经安装！");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setMessage("您没有安装唔能程序，点击前往安装")
                    .setPositiveButton("同意前往", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://app.myncic.com/1001"));
                            context.startActivity(browserIntent);
                        }
                    })
                    .setNegativeButton("拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
//                            context.finish();
                        }
                    });
            builder.create().show();

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

   /* public void exit(View view){
        Toast.makeText(MainActivity.this, "退出调用的的应用程序", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("com.bocs.mpos.home");//设置action
        intent.putExtra("isExit", true);  //设置数据
        intent.setClassName(MOSPACKAGE, MPOSCLASSNAME2);//内部调用了ComponentName组件
        startActivity(intent);
    }*/

    /**
     * 打开APP
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String packageName1 = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName1, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }

    //下面介绍怎么判断手机已安装某程序的方法：
    private boolean isAvilible(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();//获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);//获取所有已安装程序的包信息
        List<String> pName = new ArrayList<String>();//用于存储所有已安装程序的包名
        //从pinfo中将包名字逐一取出，压入pName list中
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);//判断pName中是否有目标程序的包名，有TRUE，没有FALSE
    }
//下面是调用该方法进行判断后的逻辑：
//已安装，打开程序，需传入参数包名："com.skype.android.verizon"
/*if(

    isAvilible(this,"com.skype.android.verizon"))

    {
        Intent i = new Intent();
        ComponentName cn = new ComponentName("com.skype.android.verizon",
                "com.skype.android.verizon.SkypeActivity");
        i.setComponent(cn);
        startActivityForResult(i, RESULT_OK);
    }
//未安装，跳转至market下载该程序
else

    {
        Uri uri = Uri.parse("market://details?id=com.skype.android.verizon");//id为包名
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(it);
    }*/

  public static boolean externalSharedPreferences(Context context){
      //获得第一个应用的包名,从而获得对应的Context,需要对异常进行捕获
      SharedPreferences preferences;
      try {
          othercontext = context.createPackageContext("com.myncic.ican",Context.CONTEXT_IGNORE_SECURITY);
      } catch (PackageManager.NameNotFoundException e) {
          e.printStackTrace();
      }
      preferences=othercontext.getSharedPreferences("login_state", Context.MODE_WORLD_WRITEABLE|Context.MODE_WORLD_READABLE);

      boolean state = preferences.getBoolean("state", false);
      if (state){
          Log.d("OpenDoorActivity","已经登录！");
          return true;
      }else{
          Log.d("OpenDoorActivity","未登录！");
          return false;
      }

  }
}
