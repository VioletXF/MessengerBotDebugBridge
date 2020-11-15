# MessengerBotDebugBridge (MDB)
sample code is also included in `Main.java`

## MDB
`MDB` helps to compile, turn on/off bots, deactivate/activate MessengerBot remotely.
```java
ADB adb = new ADB("YOUR_ADB_PATH\\adb.exe");
MDB mdb = new MDB(adb);
mdb.setActivation(true); // activates MessengerBot's Notification Listener
mdb.compile("MyBot"); // compiles a bot named MyBot
mdb.setBotPower("MyBot", true); // turns on MyBot
```

## DebugRoom
`DebugRoom` helps to sync with MessengerBot App's DebugRoom.
```java
ADB adb = new ADB("YOUR_ADB_PATH\\adb.exe");
DebugRoom debugRoom = new DebugRoom(adb);
Integer port = 9500; // default port of MessengerBot
debugRoom.connect(port, port); // localPort, remotePort
debugRoom.setOnMessage(new DebugRoom.OnMessage() {
    @Override
    public void run(DebugRoom.MessageData message) {
        if(message.getIsBot()){
            System.out.println("the bot says: "+message.getMessage());
        }
    }
});

debugRoom.setOnError(new DebugRoom.OnError() {
    @Override
    public void run(String error) {
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
```
