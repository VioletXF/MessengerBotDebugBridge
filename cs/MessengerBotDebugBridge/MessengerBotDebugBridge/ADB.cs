using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MessengerBotDebugBridge {
    public class ADB {
        private string adbPath;
        public ADB(string adbPath) {
            this.adbPath = adbPath;
        }
        public string Execute(string args) {
            try {
                System.Diagnostics.ProcessStartInfo procStartInfo =
                    new System.Diagnostics.ProcessStartInfo("adb", args);

                procStartInfo.RedirectStandardOutput = true;
                procStartInfo.RedirectStandardError = true;
                procStartInfo.UseShellExecute = false;
                procStartInfo.CreateNoWindow = true;
                procStartInfo.StandardErrorEncoding = Encoding.UTF8;
                procStartInfo.StandardOutputEncoding = Encoding.UTF8;

                System.Diagnostics.Process proc = new System.Diagnostics.Process();
                proc.StartInfo = procStartInfo;
                proc.Start();

                string result = proc.StandardOutput.ReadToEnd();
                string error = proc.StandardError.ReadToEnd();
                if (error.Length != 0) return error;
                return result;
            } catch (Exception e) {
                // Console.WriteLine(e);
            }

            return "";
        }
    }
}
