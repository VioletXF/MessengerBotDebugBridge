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

## DebugRoom, BotManager
`DebugRoom` helps to sync with MessengerBot App's DebugRoom.
`BotManager` helps to compile bots and to listen for compile results.
```java
String adbPath = "C:\\Users\\user\\AppData\\Local\\Android\\Sdk\\platform-tools\\adb.exe"; // your adb path
try {
    ADB adb = new ADB(adbPath);
    Communicator communicator = new Communicator(adb); // Create Communicator to communicate with MessengerBot
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

    debugRoom.setOnBadRequestListener(new DebugRoom.OnBadRequestListener() {
        @Override
        public void onEvent(String botName, String error) {
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

    communicator.setOnReadEndListener(new Communicator.OnReadEndListener() {
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
    botManager.setOnCompileFinishListener(new BotManager.OnCompileFinishListener() {
        @Override
        public void onEvent(String botName, boolean success, String error) {
            if(success) {
                System.out.println("compile success: " + botName);
                try {
                    debugRoom.send(messageData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("sent");
            } else {
                System.out.println("compile error: "+botName+"  "+error);
            }
        }
    });
    botManager.setOnBadRequestListener(new BotManager.OnBadRequestListener() {
        @Override
        public void onEvent(String botName, String error) {
            System.out.println(error+": "+botName);
        }
    });
    botManager.setOnRuntimeErrorListener(new BotManager.OnRuntimeErrorListener() {
        @Override
        public void onEvent(String botName, String error) {
            System.out.println("RuntimeError("+botName+"): "+error);
        }
    });
    botManager.compile("MyBot");

} catch (Exception e) {
    e.printStackTrace();
}
```
