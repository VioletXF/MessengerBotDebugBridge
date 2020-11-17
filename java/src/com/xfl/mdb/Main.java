package com.xfl.mdb;

public class Main {
    static public void main(String[] args){
        String adbPath = "C:\\SDK\\Android\\platform-tools\\adb.exe"; // your adb path
        try {
            ADB adb = new ADB(adbPath);
            Communicator communicator = new Communicator(adb);
            DebugRoom debugRoom = communicator.getDebugRoom();
            Integer port = 9500; // default port of MessengerBot
            communicator.connect(port, port); // localPort, remotePort
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
            messageData.setBotName("asdf") // target bot name (important)
                    .setAuthorName("David")
                    .setIsGroupChat(true)
                    .setPackageName("com.xfl.msgbot")
                    .setRoomName("MyChatRoom")
                    .setMessage("Hello World!");
            debugRoom.send(messageData);
            debugRoom.setOnReadEndListener(new DebugRoom.OnReadEndListener() {
                @Override
                public void onEvent() {
                    System.out.println("read end");
                }
            });

            BotManager botManager = communicator.getBotManager();
            botManager.setOnCompileStartListener(new BotManager.OnCompileStartListener() {
                @Override
                public void onEvent(String botName) {
                    System.out.println("compile start: "+botName);
                }
            });
            botManager.setOnCompileSuccessListener(new BotManager.OnCompileSuccessListener() {
                @Override
                public void onEvent(String botName) {
                    System.out.println("compile success: "+botName);
                }
            });
            botManager.setOnCompileErrorListener(new BotManager.OnCompileErrorListener() {
                @Override
                public void onEvent(String botName, String error) {
                    System.out.println("compile error: "+botName+"  "+error);
                }
            });
            botManager.compile("asdf");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
