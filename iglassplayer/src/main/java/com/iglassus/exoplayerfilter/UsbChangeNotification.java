package com.iglassus.exoplayerfilter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;


/**
 * Created by AdminUser on 3/22/2018.
 */

public class UsbChangeNotification extends BroadcastReceiver {
    private static String HDMIINTENT = "android.intent.action.HDMI_PLUGGED";

    @Override
    public void onReceive(final Context ctxt, Intent receivedIt) {
        String action = receivedIt.getAction();

        if (action.equals(HDMIINTENT)) {
            boolean state = receivedIt.getBooleanExtra("state", false);

            if (state == true) {
                Log.d("HDMIListner", "BroadcastReceiver.onReceive() : Connected HDMI-TV");
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        ctxt.startActivity(new Intent(ctxt,IGLassMainActivity.class));
                    }
                }, 100);
                //Toast.makeText(ctxt, "HDMI >>", Toast.LENGTH_LONG).show();
            } else {
                Log.d("HDMIListner", "HDMI >>: Disconnected HDMI-TV");
                //Toast.makeText(ctxt, "HDMI DisConnected>>", Toast.LENGTH_LONG).show();
                if(IGLassMainActivity.app!=null) IGLassMainActivity.app.finishAndRemoveTask();
            }
            return;
        }

        if(action.equals("android.hardware.usb.action.USB_STATE")){
            if (receivedIt.getExtras().getBoolean("connected")){
                // usb 插入
                //Toast.makeText(ctxt, "插入", Toast.LENGTH_LONG).show();
            }else{
                //   usb 拔出
                //Toast.makeText(ctxt, "拔出", Toast.LENGTH_LONG).show();
            }
            if(IGLassMainActivity.app!=null) {
                IGLassMainActivity.app.finishAndRemoveTask();
            }
            else {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        //Toast.makeText(ctxt, "插入", Toast.LENGTH_LONG).show();
                        DisplayManager mDisplayManager = (DisplayManager) ctxt.getSystemService(Context.DISPLAY_SERVICE);
                        Display[] displays = mDisplayManager.getDisplays();
                        if (displays.length > 1) {
                            ctxt.startActivity(new Intent(ctxt,IGLassMainActivity.class));
                        }
                    }
                }, 2500);
            }
        }
    }

}