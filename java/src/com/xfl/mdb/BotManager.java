package com.xfl.mdb;

import org.json.JSONObject;

import java.io.IOException;

public class BotManager {


    public interface OnCompileStartListener {
        void onEvent(String botName);
    }
    public interface OnCompileFinishListener{
        void onEvent(String botName, boolean success, String error);
    }
    public interface OnBadRequestListener{
        void onEvent(String botName, String error);
    }
    public interface OnRuntimeErrorListener{
        void onEvent(String botName, String error);
    }
    private OnCompileStartListener onCompileStartListener;
    private OnCompileFinishListener onCompileFinishListener;
    private OnBadRequestListener onBadRequestListener;
    private OnRuntimeErrorListener onRuntimeErrorListener;
    private final Communicator communicator;

    protected BotManager(Communicator communicator){
        this.communicator = communicator;
    }
    public OnCompileStartListener getOnCompileStartListener() {
        return onCompileStartListener;
    }

    public OnCompileFinishListener getOnCompileFinishListener() {
        return onCompileFinishListener;
    }

    public OnBadRequestListener getOnBadRequestListener() {
        return onBadRequestListener;
    }

    public OnRuntimeErrorListener getOnRuntimeErrorListener() {
        return onRuntimeErrorListener;
    }

    public void setOnCompileStartListener(OnCompileStartListener onCompileStartListener) {
        this.onCompileStartListener = onCompileStartListener;
    }

    public void setOnCompileFinishListener(OnCompileFinishListener onCompileSuccessListener) {
        this.onCompileFinishListener = onCompileSuccessListener;
    }

    public void setOnBadRequestListener(OnBadRequestListener onBadRequestListener) {
        this.onBadRequestListener = onBadRequestListener;
    }

    public void setOnRuntimeErrorListener(OnRuntimeErrorListener onRuntimeErrorListener) {
        this.onRuntimeErrorListener = onRuntimeErrorListener;
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
