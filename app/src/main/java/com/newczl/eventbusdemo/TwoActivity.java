package com.newczl.eventbusdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.newczl.eventbusdemo.mes.Message2;
import com.newczl.eventbusdemo.mes.Message3;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@BindEventBus
public class TwoActivity extends BaseActivity {
    TextView textView2;
    TextView textView3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
    }

    public void post(View view) {
        EventBus.getDefault().post(new Message3("我是优先级消息啊"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getStickyMessage(Message2 myStickyMessage){
        textView.setText(myStickyMessage.toString());
        EventBus.getDefault().removeAllStickyEvents();
    }

    @Subscribe(threadMode = ThreadMode.POSTING,priority = 2)
    public void getPriorityMessageHigh(Message3 myMessage){
        textView2.setText(myMessage.toString());
        //只有接收在POSTING下才可以使用取消事件传递
        //priority只会影响同线程下的传递顺序
        //优先级不会影响具有不同ThreadModes的订阅者的传递顺序！
        EventBus.getDefault().cancelEventDelivery(myMessage);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 1)
    public void getPriorityMessage(Message3 myMessage){
        textView3.setText(myMessage.toString());
    }

    @Override
    int getViewId() {
        return R.layout.activity_two;
    }
}
