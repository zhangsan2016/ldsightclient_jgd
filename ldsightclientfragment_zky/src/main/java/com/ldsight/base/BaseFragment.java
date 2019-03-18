package com.ldsight.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by ldgd on 2019/3/7.
 * 功能：
 * 说明：
 */
public abstract class BaseFragment  extends Fragment {

    // 显示Toast
    private static final int SHOW_TOAST = 20;
    // 显示加载框
    private static final int SHOW_PROGRESS = 21;
    // 关闭加载框
    private static final int STOP_PROGRESS = 22;
    protected final Context context;
    // 加载框
    private ProgressDialog mProgress;

    public BaseFragment(Context context) {
        this.context = context;

    }

    private Handler baseHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_TOAST:
                    String text = (String) msg.obj;
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                    break;

                case SHOW_PROGRESS:
                    mProgress = ProgressDialog.show(context, "", "Loading...", true, false);
                    break;

                case STOP_PROGRESS:
                    if (mProgress != null) {
                        mProgress.dismiss();
                    }
                    break;

            }
        }
    };

    protected void showToast(String msg) {
        Message message = baseHandler.obtainMessage();
        message.what = SHOW_TOAST;
        message.obj = msg;
        baseHandler.sendMessage(message);
    }



    protected void showProgress() {
        baseHandler.sendEmptyMessage(SHOW_PROGRESS);
    }

    protected void stopProgress() {
        baseHandler.sendEmptyMessage(STOP_PROGRESS);
    }

}
