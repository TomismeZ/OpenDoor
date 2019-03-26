package com.example.administrator.opendoor.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.opendoor.R;
import com.example.administrator.opendoor.activity.CheckGraphicActivity;
import com.example.administrator.opendoor.activity.CheckPassActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 贺志虎 on 2016/2/29 0029.
 */
public class TimeView extends android.support.v7.widget.AppCompatTextView {

    private long hours;
    public static long minutes;
    public  static long seconds;
    private long diff;
    private long days;
    private long time = 0;
    private SharedPreferences sp; //记录首页的

    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimeView);
        diff = a.getInteger(R.styleable.TimeView_time, 50) * 1000;
        Log.d("TAG", "再打印  onCreate :" + diff);
        sp=context.getSharedPreferences("time_info",Context.MODE_PRIVATE);

//        onCreate();

    }


    /**
     * 根据 attrs 设置时间开始
     */
    private void onCreate() {
        start();
    }

    //开始计时
    private void start() {

        handler.removeMessages(1);

        Log.d("TAG", "再打印  onCreate");
        Log.d("TAG", "再打印  setTime/..................................//////////////////////////");
        getTime();
        Message message = handler.obtainMessage(1);
        handler.sendMessageDelayed(message, 1000);
    }

    public void log() {
        Log.d("TAG", "再打印  log/////////////////////////////////////////////////////////////////");
    }


    /**
     * 设置事件
     *
     * @param time
     */
    public void setTime(long time) {
        this.time = time * 1000;
    }

    final Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {         // handle message
            Log.d("TAG", "再打印  handleMessage");

            switch (msg.what) {
                case 1:
                    setVisibility(View.VISIBLE);
                    diff = diff - 1000;
                    getShowTime();
                    if (diff > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 1000);
                    } else {
                        setVisibility(View.GONE);

                        int timeFalg = sp.getInt("timeFalg", 0);
                        SharedPreferences.Editor editor = sp.edit();
                        if(timeFalg == 0){
                            CheckPassActivity.graphicEooroNum=0;
                            CheckPassActivity.lockPatternView.enableInput();
                            CheckPassActivity.relativeLayout3.setVisibility(View.VISIBLE);
                            editor.putInt("graphicEooroNum",CheckPassActivity.graphicEooroNum);
                            editor.putLong("time",0);
                        }else{
                            CheckGraphicActivity.graphicEooroNum=0;
                            CheckGraphicActivity.lockPatternView.enableInput();
                            editor.putInt("cg_graphicErrorNum",CheckPassActivity.graphicEooroNum);
                            editor.putLong("cg_time",0);
                        }
                        editor.commit();
                    }

                    break;
                default:
                    break;
            }
            Log.d("TAG", "再打印");
            super.handleMessage(msg);
        }
    };

    /**
     * 得到时间差
     */
    private void getTime() {
        Log.d("TAG", "再打印 :getTime");

        try {

            days = diff / (1000 * 60 * 60 * 24);
            hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            seconds = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);
//            setText(days + ":" + hours + ":" + minutes + ":" + seconds);
//            setText("请在"+seconds+"秒后重试");

        } catch (Exception e) {
        }
    }

    /**
     * 获得要显示的时间
     */
    private void getShowTime() {
        Log.d("TAG", "再打印 :getShowTime");

        days = diff / (1000 * 60 * 60 * 24);
        hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        seconds = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / (1000);
//        setText(days + ":" + hours + ":" + minutes + ":" + seconds);

            setText("请在"+minutes+"分"+seconds+"秒后重试");

    }

    /**
     * 以之前设置的时间重新开始
     */
    public void reStart() {
        this.diff = this.time;
        start();
    }

    /**
     * 设置时间重新开始
     *
     * @param time 重新开始的事件
     */
    public void reStart(long time) {
        if (time > 0) {
            this.diff = time * 1000;
            Log.d("TAG", "+=========================" + diff);
        }
        start();
    }

}
