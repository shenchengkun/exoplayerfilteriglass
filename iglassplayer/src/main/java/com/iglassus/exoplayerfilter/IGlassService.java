package com.iglassus.exoplayerfilter;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


public class IGlassService extends Service {
        private DifferentDisplay mPresentation;
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.i("fd","发的发发的发发的方法的发放额凤飞飞飞");
                //Toast.makeText(getApplicationContext(), "哈哈哈哈哈第二块屏幕", Toast.LENGTH_LONG).show();
            DisplayManager mDisplayManager= (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays=mDisplayManager.getDisplays();
            mPresentation = new DifferentDisplay(getApplicationContext(),displays[1]);// displays[1]是副屏
            mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mPresentation.show();
        }



    /*
    TextView mView;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(), "onCreate", Toast.LENGTH_LONG).show();
        mView = new TextView(this);
        mView.setText("haha");
        mView.bringToFront();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
//              WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                      | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.setTitle("Load Average");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getBaseContext(), "onDestroy", Toast.LENGTH_LONG).show();
        if (mView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresentation != null) {
            mPresentation = null;
        }
    }
}