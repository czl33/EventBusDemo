package com.newczl.eventbusdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.newczl.eventbusdemo.mes.Message1;
import com.newczl.eventbusdemo.mes.Message2;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@BindEventBus
public class MainActivity extends BaseActivity {

    @Override
    int getViewId() {
        return R.layout.activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage1(Message1 message1){
        textView.setText(message1.toString());
    }

    public void go1(View view) {
        startActivity(new Intent(this,OneActivity.class));
    }

    public void postSticky(View view) {
        showToast("发送粘性消息");
        EventBus.getDefault().postSticky(new Message2("我是粘性消息"));
    }

    public void go2(View view) {
        startActivity(new Intent(this,TwoActivity.class));
    }

}
