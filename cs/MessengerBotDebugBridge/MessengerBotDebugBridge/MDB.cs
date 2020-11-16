using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MessengerBotDebugBridge {
    public class MDB {
        private ADB adb;
        public MDB(ADB adb) {
            this.adb = adb;
        }
        private string ConvertBotName(string botName) {
            return botName.Replace(" ", "\\\\ ");
        }
        public string Compile(string botName) {
            string namearg = ConvertBotName(botName);
            return adb.Execute("shell am broadcast -a com.xfl.msgbot.broadcast.compile -p com.xfl.msgbot --es name " + namearg);
        }
        public string SetBotPower(string botName, bool power) {
            string namearg = ConvertBotName(botName);
            string powerstr = power.ToString().ToLower();
            return adb.Execute("shell am broadcast -a com.xfl.msgbot.broadcast.set_bot_power -p com.xfl.msgbot --es name " + namearg + " --ez power " + powerstr);
        }
        public string SetActivation(bool activation) {
            string activationstr = activation.ToString().ToLower();
            return adb.Execute("shell am broadcast -a com.xfl.msgbot.broadcast.set_activation -p com.xfl.msgbot --ez activation " + activationstr);
        }
    }
}
