package com.newczl.eventbusdemo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.newczl.eventbusdemo.mes.Message1;
import com.newczl.eventbusdemo.mes.Message2;
import com.newczl.eventbusdemo.mes.Message3;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

@BindEventBus
public class OneActivity extends BaseActivity {

    public void post(View view) {
        EventBus.getDefault().post(new Message1("默认消息已发送"));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getStickyMessage(Message2 myStickyMessage){
        textView.setText(myStickyMessage.toString());
    }

    @Override
    int getViewId() {
        return R.layout.activity_one;
    }
}
