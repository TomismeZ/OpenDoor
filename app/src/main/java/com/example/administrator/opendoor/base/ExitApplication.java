package com.example.administrator.opendoor.base;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/8.
 */

public class ExitApplication extends Application {
    private static List<Activity> activityList = new ArrayList<Activity>();

    public static void remove(Activity activity) {
        activityList.remove(activity);
    }

    public static void add(Activity activity) {
        activityList.add(activity);
    }

    //销毁掉所有的activity
    public static void exitApplication() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        android.os.Process.killProcess(android.os.Process.myPid());//直接杀掉当前应用进程
    }
}
