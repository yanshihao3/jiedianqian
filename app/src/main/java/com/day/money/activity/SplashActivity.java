package com.day.money.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.meituan.android.walle.WalleChannelReader;
import com.umeng.analytics.MobclickAgent;
import com.day.money.R;
import com.day.money.net.Api;
import com.day.money.net.ApiService;
import com.day.money.net.OnRequestDataListener;
import com.day.money.utils.SPUtil;
import com.day.money.utils.SharedPreferencesUtil;
import com.day.money.utils.StatusBarUtil;
import com.day.money.utils.ToastUtils;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author apple
 */
public class SplashActivity extends AppCompatActivity {
    private SwitchHandler mHandler = new SwitchHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.theme_color), 40);

        boolean open = SPUtil.getBoolean(SplashActivity.this, "open", false);
        if (!open) {
            setUrl();
        } else {
            mHandler.sendEmptyMessageDelayed(3, 1000);
        }
    }

    private void setUrl() {

        Map<String, String> map = new HashMap<>();
        String name = getResources().getString(R.string.appName);
        String channel = WalleChannelReader.getChannel(this.getApplicationContext());

        ApiService.GET_SERVICE(Api.STATUS.getStatus, map, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {

                try {
                    JSONObject data = json.getJSONObject("data");
                    int status = data.getIntValue("status");
                    if (status == 0) {
                        mHandler.sendEmptyMessageDelayed(2
                                , 1000);
                    } else {
                        SPUtil.putBoolean(SplashActivity.this, "open", true);
                        setWelcome();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showToast("网络连接失败");
            }
        });

    }


    private static class SwitchHandler extends Handler {
        private WeakReference<SplashActivity> mWeakReference;

        SwitchHandler(SplashActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 2:
                        activity.startActivity(new Intent(activity, MainActivity.class));

                        activity.finish();
                        break;
                    case 3:
                        activity.startActivity(new Intent(activity, MainActivity.class));
                        activity.finish();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setWelcome() {
        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(SplashActivity.this, SharedPreferencesUtil.FIRST_OPEN, true);
        if (isFirstOpen) {
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            startActivity(intent);
            finish();
            return;
        } else {
            mHandler.sendEmptyMessageDelayed(3, 1000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
