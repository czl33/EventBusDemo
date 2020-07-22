package com.newczl.eventbusdemo;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;


/**
 * @desc Activity基类
 * @author czl
 * @date 2020-7-20
*/
abstract class BaseActivity extends AppCompatActivity {

    protected TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewId());
        textView = findViewById(R.id.textView);
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.getClass().isAnnotationPresent(BindEventBus.class)) {
            EventBus.getDefault().unregister(this);
        }
    }

    abstract int getViewId();

    protected void showToast(String toast){
        Toast.makeText(this,toast,Toast.LENGTH_LONG).show();
    }

}
