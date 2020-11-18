package com.xfl.mdb;



import org.json.JSONObject;

import java.io.*;

public class DebugRoom {
    public interface OnMessageListener {
        void onEvent(MessageData message);
    }
    public interface OnBadRequestListener {
        void onEvent(String botName, String error);
    }
    private OnMessageListener onMessageListener;
    private OnBadRequestListener onBadRequestListener;
    private final Communicator communicator;

    protected DebugRoom(Communicator communicator) {
        this.communicator = communicator;
    }
    public OnMessageListener getOnMessageListener() {
        return onMessageListener;
    }

    public OnBadRequestListener getOnBadRequestListener() {
        return onBadRequestListener;
    }


    public void setOnMessageListener(OnMessageListener onMessageListener){
        this.onMessageListener = onMessageListener;
    }

    public void setOnBadRequestListener(OnBadRequestListener onErrorListener){
        this.onBadRequestListener = onErrorListener;
    }






    public void send(MessageData messageData) throws IOException {

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
        communicator.writeLine(str);
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
