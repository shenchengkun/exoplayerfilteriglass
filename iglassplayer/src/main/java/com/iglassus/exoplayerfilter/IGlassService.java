package com.iglassus.exoplayerfilter;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;


public class IGlassService extends Service {

    // 获取设备上的屏幕
    DisplayManager mDisplayManager;// 屏幕管理器
    Display[] displays;// 屏幕数组
    DifferentDisplay mPresentation;   //（继承Presentation）

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("fd","发的发发的发发的方法的发放额凤飞飞飞");
        mDisplayManager = (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        displays = mDisplayManager.getDisplays();
        if (null == mPresentation&&displays.length>1) {
            //Toast.makeText(getApplicationContext(), "哈哈哈哈哈", Toast.LENGTH_LONG).show();
            mPresentation = new DifferentDisplay(getApplicationContext(),displays[1]);// displays[1]是副屏
            mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mPresentation.show();
        }
    }
}