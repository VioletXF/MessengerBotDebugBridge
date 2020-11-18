package com.xfl.mdb;

import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Communicator {
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private DebugRoom debugRoom;
    private BotManager botManager;
    private boolean isConnected;
    private ADB adb;
    private OnReadEndListener onReadEndListener;
    public interface OnReadEndListener{
        void onEvent();
    }
    public OnReadEndListener getOnReadEndListener() {
        return onReadEndListener;
    }
    public void setOnReadEndListener(OnReadEndListener onReadEndListener){
        this.onReadEndListener = onReadEndListener;
    }


    public Communicator(ADB adb){
        this();
        this.adb = adb;
    }
    public Communicator(){}
    public void setADB(ADB adb){
        this.adb = adb;
    }
    public void connect(Integer localPort) throws IOException{
        socket = new Socket(InetAddress.getLoopbackAddress(),localPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        isConnected = true;
        Thread readThread = new Thread(() -> {
            while(true) {
                String line = null;
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    if (socket.isClosed()) break;
                    e.printStackTrace();
                }
                if (line == null) {
                    break;
                }
                JSONObject json = new JSONObject(line);
                switch (json.getString("name")) {
                    case "debugRoom" -> {
                        if(debugRoom!=null) {
                            if (debugRoom.getOnMessageListener() != null) {
                                DebugRoom.MessageData msg = new DebugRoom.MessageData();
                                JSONObject data = json.getJSONObject("data");
                                msg.setBotName(data.getString("botName"));
                                msg.setRoomName(data.getString("roomName"));
                                msg.setAuthorName(data.getString("authorName"));
                                msg.setMessage(data.getString("message"));
                                msg.setIsBot(data.getBoolean("isBot"));
                                debugRoom.getOnMessageListener().onEvent(msg);
                            }
                        }
                    }

                    case "compileStatus" -> {
                        if(botManager!=null) {
                            JSONObject data = json.getJSONObject("data");
                            String botName = data.getString("botName");
                            String result = data.getString("status");
                            if (result.equals("start")) {
                                if (botManager.getOnCompileStartListener() != null) {
                                    botManager.getOnCompileStartListener().onEvent(botName);
                                }
                            } else {
                                if (botManager.getOnCompileFinishListener() != null) {
                                    boolean success = result.equals("success");
                                    String error = null;
                                    if (!success) {
                                        error = data.getString("error");
                                    }
                                    botManager.getOnCompileFinishListener().onEvent(botName, success, error);
                                }
                            }
                        }
                    }
                    case "runtimeError" -> {
                        if(botManager!=null){
                            if(botManager.getOnRuntimeErrorListener()!=null) {
                                JSONObject data = json.getJSONObject("data");
                                String botName = data.getString("botName");
                                String error = data.getString("error");
                                botManager.getOnRuntimeErrorListener().onEvent(botName, error);
                            }
                        }
                    }
                    case "badRequest:debugRoom" -> {
                        if(debugRoom!=null) {
                            if (debugRoom.getOnBadRequestListener() != null) {
                                JSONObject edata = json.getJSONObject("data");
                                debugRoom.getOnBadRequestListener().onEvent(edata.getString("botName"), edata.getString("error"));
                            }
                        }
                    }
                    case "badRequest:compile"->{
                        if(botManager!=null){
                            JSONObject data = json.getJSONObject("data");
                            if(botManager.getOnBadRequestListener()!=null){
                                botManager.getOnBadRequestListener().onEvent(data.getString("botName"), data.getString("error"));
                            }
                        }
                    }
                }
            }
            if(onReadEndListener!=null)
                onReadEndListener.onEvent();
        });
        readThread.start();
    }

    public void connect(Integer localPort, Integer remotePort) throws Exception {
        if(adb==null)throw new NullPointerException("Forwarding failed: adb is null.");
        adb.execute("forward tcp:" + localPort + " tcp:" + remotePort);
        connect(localPort);
    }
    public void disconnect() throws IOException {
        socket.close();
    }
    public void writeLine(String line) throws IOException {
        if(!isConnected){
            throw new IOException("not connected yet");
        }
        writer.write(line);
        writer.newLine();
        writer.flush();
    }
    public DebugRoom getDebugRoom(){
        if(debugRoom==null)debugRoom = new DebugRoom(this);
        return debugRoom;
    }
    public BotManager getBotManager(){
        if(botManager==null)botManager = new BotManager(this);
        return botManager;
    }
}
