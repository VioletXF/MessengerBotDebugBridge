package com.xfl.mdb;

import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Communicator {
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    private final DebugRoom debugRoom;
    private final BotManager botManager;
    private boolean isConnected;
    private ADB adb;


    public Communicator(ADB adb){
        this();
        this.adb = adb;

    }
    public Communicator(){
        debugRoom = new DebugRoom(this);
        botManager = new BotManager(this);
    }
    public void connect(Integer localPort, Integer remotePort) throws Exception {
        if(adb!=null)adb.execute("forward tcp:"+localPort+" tcp:"+remotePort);
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
                        DebugRoom.MessageData msg = new DebugRoom.MessageData();
                        JSONObject data = json.getJSONObject("data");
                        msg.setBotName(data.getString("botName"));
                        msg.setRoomName(data.getString("roomName"));
                        msg.setAuthorName(data.getString("authorName"));
                        msg.setMessage(data.getString("message"));
                        msg.setIsBot(data.getBoolean("isBot"));
                        if(debugRoom.getOnMessageListener()!=null)
                            debugRoom.getOnMessageListener().onEvent(msg);
                    }
                    case "badRequest:debugRoom" -> {
                        JSONObject edata = json.getJSONObject("data");
                        String error = edata.getString("error");
                        if(debugRoom.getOnErrorListener()!=null)
                            debugRoom.getOnErrorListener().onEvent(error);
                    }
                    case "compileStatus" -> {
                        JSONObject data = json.getJSONObject("data");
                        String botName = data.getString("botName");
                        String result = data.getString("status");
                        if(result.equals("start")){
                            if(botManager.getOnCompileStartListener()!=null){
                                botManager.getOnCompileStartListener().onEvent(botName);
                            }
                        } else if(result.equals("success")){

                            if (botManager.getOnCompileSuccessListener() != null) {
                                botManager.getOnCompileSuccessListener().onEvent(botName);
                            }
                        } else {
                            String error = data.getString("error");
                            if (botManager.getOnCompileErrorListener() != null) {
                                botManager.getOnCompileErrorListener().onEvent(botName, error);
                            }
                        }
                    }
                }
            }
            if(debugRoom.getOnReadEndListener()!=null)
                debugRoom.getOnReadEndListener().onEvent();
        });
        readThread.start();
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
        return debugRoom;
    }
    public BotManager getBotManager(){
        return botManager;
    }
}
