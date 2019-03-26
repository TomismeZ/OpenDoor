package com.example.administrator.opendoor.base;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.opendoor.activity.CheckPassActivity;
import com.example.administrator.opendoor.util.HttpCallbackListener;
import com.example.administrator.opendoor.util.HttpUtil;
import com.example.administrator.opendoor.util.SDHelper;
import com.myncic.mynciclib.helper.DES3;

import org.json.JSONObject;

/**
 * Created by Administrator on 2018/1/4 0004.
 */

public class BaseActivity extends AppCompatActivity {
    private ForceFinishAllActivity receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.zdk.broadcastbestpractice.FORCE_FINISHALL");
        receiver = new ForceFinishAllActivity();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * 读取SD卡文件
     */
    public void readSDFile() {
        //读取文件
        String userInfoJson = SDHelper.readFile(this, "userInfo.json");
        //利用3DES对用户id与安全码进行解码
        if (!userInfoJson.equals("")) {
            try {
                DES3 des3 = new DES3();
                JSONObject jsonObject = new JSONObject(des3.decode(userInfoJson));
                String userinfo = jsonObject.getString("userInfo");
                JSONObject jsonObject1 = new JSONObject(userinfo);
                Long selfId = jsonObject1.getLong("selfId");
                String securitycode = jsonObject1.getString("security");
                Log.d("OpenDoorActivity", "selfId: " + selfId);
                Log.d("OpenDoorActivity", "securitycode: " + securitycode);

                HttpUtil.sendHttpRequest("http://oa.myncic.com/open_door.php?uid=" + selfId + "&scode=" + securitycode, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        Log.d("OpenDoorActivity", "响应成功：" + response);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("OpenDoorActivity", "响应失败：" + e.getMessage());
                    }
                });
//                Toast.makeText(this, "selfId:" + selfId + ",securitycode:" + securitycode, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "开门成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 定义一个广播接收器
     */
    class ForceFinishAllActivity extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityCollector.finishAll(); //销毁所有活动
            Intent intent1 = new Intent(context, CheckPassActivity.class);
            context.startActivity(intent1); //重新启动项目
        }
    }


    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }


}
