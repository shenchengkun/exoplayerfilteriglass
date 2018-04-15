package com.iglassus.exoplayerfilter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;


public class IGlassService extends Service {
        private DisplayPresentation mPresentation;
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.i("服务service","开始服务");
            DisplayManager mDisplayManager= (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
            Display[] displays=mDisplayManager.getDisplays();
            mPresentation = new DisplayPresentation(getApplicationContext(),displays[1]);// displays[1]是副屏

            if(Build.VERSION.SDK_INT >= 26) mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            else mPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

            mPresentation.show();
        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresentation != null) {
            mPresentation.cancel();
            mPresentation.dismiss();
            mPresentation.onDetachedFromWindow();
            mPresentation = null;
        }
    }
}