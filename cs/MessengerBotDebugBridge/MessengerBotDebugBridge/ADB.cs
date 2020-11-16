using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Mdb {
    public class ADB {
        private string adbPath;
        public ADB(string adbPath) {
            this.adbPath = adbPath;
        }
        public string Execute(string args)
        {

            System.Diagnostics.ProcessStartInfo procStartInfo =
                new System.Diagnostics.ProcessStartInfo(adbPath, args);
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
            if (error.Length != 0) throw new Exception(error);


            return "";
        }
    }
}
