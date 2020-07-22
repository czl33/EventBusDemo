package com.newczl.eventbusdemo.mes;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message3 {
    private long time;
    private String message;

    public Message3(long time, String message) {
        this.time = time;
        this.message = message;
    }
    public Message3( String message) {
        this.time = System.currentTimeMillis();
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        SimpleDateFormat f = new SimpleDateFormat( "HH:mm:ss");
        return String.format(Locale.CHINA,"消息：%s,时间：%s",message,f.format(new Date(time)));
    }
}
