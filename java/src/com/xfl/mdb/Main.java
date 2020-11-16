package com.xfl.mdb;

import java.io.IOException;

public class Main {
    static public void main(String[] args){
        String adbPath = "C:\\SDK\\Android\\platform-tools\\adb.exe"; // your adb path
        try {
            ADB adb = new ADB(adbPath);
            DebugRoom debugRoom = new DebugRoom(adb);
            Integer port = 9500; // default port of MessengerBot
            debugRoom.connect(port, port); // localPort, remotePort
            debugRoom.setOnMessageListener(new DebugRoom.OnMessageListener() {
                @Override
                public void onEvent(DebugRoom.MessageData message) {
                    if(message.getIsBot()){
                        System.out.println("the bot says: "+message.getMessage());
                    }
                }
            });

            debugRoom.setOnErrorListener(new DebugRoom.OnErrorListener() {
                @Override
                public void onEvent(String error) {
                    System.out.println("error: " + error);
                }
            });

            DebugRoom.MessageData messageData = new DebugRoom.MessageData(); // build message to send
            messageData.setBotName("MyBot") // target bot name (important)
                    .setAuthorName("David")
                    .setIsGroupChat(true)
                    .setPackageName("com.xfl.msgbot")
                    .setRoomName("MyChatRoom")
                    .setMessage("Hello World!");
            debugRoom.send(messageData);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
