# MessengerBotDebugBridge (MDB)
sample code is also included in `Main.java`

## Dependencies
org.json is needed to use this library.
```gradle
// gradle
implementation group: 'org.json', name: 'json', version: '20200518'
```

And, of course, you need an ADB binary file. [Download SDK Platform-Tools for Windows](https://developer.android.com/studio/releases/platform-tools)

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

debugRoom.setOnReadEndListener(new DebugRoom.OnReadEndListener() {
    @Override
    public void onEvent() {
        System.out.println("read end");
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
