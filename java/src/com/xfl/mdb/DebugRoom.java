package com.xfl.mdb;



import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class DebugRoom {
    private final SocketConnection connection;
    private BufferedReader reader;
    private BufferedWriter writer;
    private OnMessageListener onMessageListener;
    private OnErrorListener onErrorListener;
    private final ADB adb;
    private Boolean isConnected = false;
    public interface OnMessageListener {
        void onEvent(MessageData message);
    }
    public interface OnErrorListener {
        void onEvent(String error);
    }
    public DebugRoom(ADB adb){
        this.adb = adb;
        connection = new SocketConnection();
    }
    public void connect(Integer localPort, Integer remotePort) throws Exception {
        adb.execute("forward tcp:"+localPort+" tcp:"+remotePort);
        connection.connect(localPort);
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        isConnected = true;
        Thread readThread = new Thread(() -> {
            while(true) {
                String line = null;
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    if (connection.isClosed()) break;
                    e.printStackTrace();
                }
                if (line == null) {
                    break;
                }
                JSONObject json = new JSONObject(line);
                switch (json.getString("name")) {
                    case "debugRoom" -> {
                        MessageData msg = new MessageData();
                        JSONObject data = json.getJSONObject("data");
                        msg.setBotName(data.getString("botName"));
                        msg.setRoomName(data.getString("roomName"));
                        msg.setAuthorName(data.getString("authorName"));
                        msg.setMessage(data.getString("message"));
                        msg.setIsBot(data.getBoolean("isBot"));
                        onMessageListener.onEvent(msg);
                    }
                    case "badRequest:debugRoom" -> {
                        JSONObject edata = json.getJSONObject("data");
                        String error = edata.getString("error");
                        onErrorListener.onEvent(error);
                    }
                }


            }
        });
        readThread.start();

    }
    public void disconnect() throws IOException {
        if(!isConnected) {
            throw new IOException("not connected yet");
        }
        connection.disconnect();
        reader.close();
        writer.close();
        isConnected = false;
    }

    public void send(MessageData messageData) throws IOException {
        if(!isConnected){
            throw new IOException("not connected yet");
        }
        JSONObject json = new JSONObject();
        json.put("name","debugRoom");
        JSONObject data = new JSONObject();
        data.put("isGroupChat", messageData.getIsGroupChat())
                .put("botName", messageData.getBotName())
                .put("packageName", messageData.getPackageName())
                .put("roomName", messageData.getRoomName())
                .put("authorName", messageData.getAuthorName())
                .put("message", messageData.getMessage());
        json.put("data", data);
        String str = json.toString();
        writer.write(str+"\n");
        writer.flush();
    }
    public void setOnErrorListener(OnErrorListener onErrorListener){
        this.onErrorListener = onErrorListener;
    }
    public void setOnMessageListener(OnMessageListener onMessageListener){
        this.onMessageListener = onMessageListener;
    }
    private static class SocketConnection{
        private Socket socket;
        public void connect(Integer port) throws IOException{
            socket = new Socket(InetAddress.getLoopbackAddress(),port);
        }
        public void disconnect() throws IOException {
            socket.close();
        }
        public Boolean isClosed(){
            return socket.isClosed();
        }
        public InputStream getInputStream() throws IOException {
            return socket.getInputStream();
        }
        public OutputStream getOutputStream() throws IOException{
            return socket.getOutputStream();
        }
    }


    public static class MessageData {
        private String botName;
        private String roomName;
        private String authorName;
        private String packageName;
        private String message;
        private Boolean isGroupChat;
        private Boolean isBot = false;


        public String getRoomName() {
            return roomName;
        }

        public MessageData setRoomName(String roomName) {
            this.roomName = roomName;
            return this;
        }

        public String getAuthorName() {
            return authorName;
        }

        public MessageData setAuthorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public String getPackageName() {
            return packageName;
        }

        public MessageData setPackageName(String packageName) {
            this.packageName = packageName;
            return this;
        }

        public Boolean getIsGroupChat() {
            return isGroupChat;
        }

        public MessageData setIsGroupChat(Boolean groupChat) {
            isGroupChat = groupChat;
            return this;
        }

        public String getBotName() {
            return botName;
        }

        public MessageData setBotName(String botName) {
            this.botName = botName;
            return this;
        }

        public Boolean getIsBot() {
            return isBot;
        }

        public void setIsBot(Boolean bot) {
            isBot = bot;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
        public String toString(){
            return "botName: " +
                    botName +
                    ", roomName: " +
                    roomName +
                    ", authorName: " +
                    authorName +
                    ", message: " +
                    message +
                    ", isBot: " +
                    isBot;
        }
    }
}
