package com.openai.gpthelper.utils;

import okhttp3.Call;
import okhttp3.EventListener;

public class PicEventListenerFactory implements EventListener.Factory {
    @Override
    public EventListener create(Call call) {
        NetEventModel tag = call.request().tag(NetEventModel.class);
        return tag != null ? new NetEventListener(tag) : EventListener.NONE;
    }
}
