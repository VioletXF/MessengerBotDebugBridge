using System;
using System.Net.Sockets;
using System.Net;
using System.IO;
using System.Threading;
using Newtonsoft.Json.Linq;
namespace Mdb {
    public class DebugRoom {
        public event EventHandler Message;
        public event EventHandler Error;
        public class MessageEventArgs: EventArgs
        {
            public MessageData messageData;
            public MessageEventArgs(MessageData messageData)
            {
                this.messageData = messageData;
            }
        }
        public class ErrorEventArgs: EventArgs
        {
            public string error;
            public ErrorEventArgs(string error)
            {
                this.error = error;
            }
        }
        private SocketConnection connection;
        private StreamReader reader;
        private StreamWriter writer;
        private ADB adb;
        private bool isConnected = false;
        public DebugRoom(ADB adb) {
            this.adb = adb;
            this.connection = new SocketConnection();
        }

        public void Connect(int localPort, int remotePort) {
            adb.Execute("forward tcp:" + localPort + " tcp:" + remotePort);
            connection.Connect(localPort);
            reader = connection.GetStreamReader();
            writer = connection.GetStreamWriter();
            isConnected = true;
            Thread readThread = new Thread(() => {
                while (true) {
                    string line = null;
                    try {
                        line = reader.ReadLine();
                    } catch (IOException e) {
                        if (!connection.IsConnected()) break;
                        Console.Out.WriteLine(e);
                    }
                    if (line == null) {
                        break;
                    }
                    JObject json = JObject.Parse(line);
                    string name = json.Value<string>("name");
                    if (name == "debugRoom") {
                        MessageData msg = new MessageData();
                        JObject data = json.Value<JObject>("data");
                        msg.SetBotName(data.Value<string>("botName"));
                        msg.SetRoomName(data.Value<string>("roomName"));
                        msg.SetAuthorName(data.Value<string>("authorName"));
                        msg.SetMessage(data.Value<string>("message"));
                        msg.SetIsBot(data.Value<bool>("isBot"));
                        Message?.Invoke(this, new MessageEventArgs(msg));
                    } else if(name == "badRequest:debugRoom") {
                        JObject edata = json.Value<JObject>("data");
                        string error = edata.Value<string>("error");
                        Error?.Invoke(this, new ErrorEventArgs(error));
                    }

                }
            });
            readThread.IsBackground = true;
            readThread.Start();
        }
        public void Disconnect() {
            if (!isConnected) {
                throw new IOException("not connected yet");
            }
            connection.Disconnect();
            reader.Close();
            writer.Close();
            isConnected = false;
        }

        public void Send(MessageData messageData) {
            if (!isConnected) {
                throw new IOException("not connected yet");
            }
            JObject json = new JObject();
            json.Add("name", "debugRoom");
            JObject data = new JObject();
            data.Add("isGroupChat", messageData.GetIsGroupChat());
            data.Add("botName", messageData.GetBotName());
            data.Add("packageName", messageData.GetPackageName());
            data.Add("roomName", messageData.GetRoomName());
            data.Add("authorName", messageData.GetAuthorName());
            data.Add("message", messageData.GetMessage());
            json.Add("data", data);
            string str = json.ToString(Newtonsoft.Json.Formatting.None);
            writer.WriteLine(str);
            writer.Flush();
        }


        private class SocketConnection {
            private Socket socket;
            private NetworkStream networkStream;
            private StreamReader streamReader;
            private StreamWriter streamWriter;
            public void Connect(int port) {
                socket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.IP);
                socket.Connect(new IPEndPoint(IPAddress.Loopback, port));
                networkStream = new NetworkStream(socket);
                streamReader = new StreamReader(networkStream);
                streamWriter = new StreamWriter(networkStream);
            }
            public void Disconnect() {
                socket.Close();
            }

            public bool IsConnected() {
                return socket.Connected;
            }

            public StreamReader GetStreamReader() {
                return streamReader;
            }

            public StreamWriter GetStreamWriter() {
                return streamWriter;
            }
            
        }
        public class MessageData {
            private string botName;
            private string roomName;
            private string authorName;
            private string packageName;
            private string message;
            private bool isGroupChat;
            private bool isBot = false;


            public string GetRoomName() {
                return roomName;
            }

            public MessageData SetRoomName(string roomName) {
                this.roomName = roomName;
                return this;
            }

            public string GetAuthorName() {
                return authorName;
            }

            public MessageData SetAuthorName(string authorName) {
                this.authorName = authorName;
                return this;
            }

            public string GetPackageName() {
                return packageName;
            }

            public MessageData SetPackageName(string packageName) {
                this.packageName = packageName;
                return this;
            }

            public bool GetIsGroupChat() {
                return isGroupChat;
            }

            public MessageData SetIsGroupChat(bool groupChat) {
                isGroupChat = groupChat;
                return this;
            }

            public string GetBotName() {
                return botName;
            }

            public MessageData SetBotName(string botName) {
                this.botName = botName;
                return this;
            }

            public bool GetIsBot() {
                return isBot;
            }

            public void SetIsBot(bool bot) {
                isBot = bot;
            }

            public string GetMessage() {
                return message;
            }

            public void SetMessage(string message) {
                this.message = message;
            }
            public string Tostring() {
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
    
}
