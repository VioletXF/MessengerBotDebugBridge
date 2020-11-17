package com.xfl.mdb;

import org.json.JSONObject;

import java.io.IOException;

public class BotManager {
    private OnCompileStartListener onCompileStartListener;
    private OnCompileSuccessListener onCompileSuccessListener;
    private OnCompileErrorListener onCompileErrorListener;
    private final Communicator communicator;

    public BotManager(Communicator communicator){
        this.communicator = communicator;
    }
    public OnCompileStartListener getOnCompileStartListener() {
        return onCompileStartListener;
    }

    public OnCompileSuccessListener getOnCompileSuccessListener() {
        return onCompileSuccessListener;
    }

    public void setOnCompileStartListener(OnCompileStartListener onCompileStartListener) {
        this.onCompileStartListener = onCompileStartListener;
    }

    public void setOnCompileSuccessListener(OnCompileSuccessListener onCompileSuccessListener) {
        this.onCompileSuccessListener = onCompileSuccessListener;
    }

    public OnCompileErrorListener getOnCompileErrorListener() {
        return onCompileErrorListener;
    }

    public void setOnCompileErrorListener(OnCompileErrorListener onCompileErrorListener) {
        this.onCompileErrorListener = onCompileErrorListener;
    }


    public interface OnCompileStartListener {
        void onEvent(String botName);
    }
    public interface OnCompileSuccessListener {
        void onEvent(String botName);
    }
    public interface OnCompileErrorListener{
        void onEvent(String botName, String error);
    }

    public void compile(String botName) throws IOException {
        JSONObject json = new JSONObject();
        json.put("name","compile");
        JSONObject data = new JSONObject();
        data.put("botName", botName);
        json.put("data", data);
        String str = json.toString();
        communicator.writeLine(str);
    }
}
