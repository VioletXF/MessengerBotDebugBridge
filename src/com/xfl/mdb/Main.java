package com.xfl.mdb;

import java.io.IOException;

public class Main {
    static public void main(String[] args){
        String adbPath = "C:\\SDK\\Android\\platform-tools\\adb.exe"; // your adb path
        ADB adb = new ADB(adbPath);
        DebugRoom dr = new DebugRoom(adb);
        try {
            MDB mdb = new MDB(adb);
            mdb.setActivation(true);
            mdb.compile("MyBot");
            mdb.setBotPower("MyBot", true);
            Integer port = 9500; // default port number of MessengerBot

            dr.connect(port, port);
            dr.setOnMessageListener(new DebugRoom.OnMessageListener() {
                @Override
                public void onEvent(DebugRoom.MessageData message) {
                    System.out.println("onMessage: " + message);
                }
            });
            dr.setOnErrorListener(new DebugRoom.OnErrorListener() {
                @Override
                public void onEvent(String error) {
                    System.out.println("onError: " + error);
                }
            });
            DebugRoom.MessageData messageData = new DebugRoom.MessageData();
            messageData
                    .setBotName("MyBot")
                    .setAuthorName("David")
                    .setIsGroupChat(true)
                    .setPackageName("com.xfl.msgbot")
                    .setRoomName("MyChatRoom")
                    .setMessage("Hello World!");
            dr.send(messageData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
