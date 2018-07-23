package eoscleosserver;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EosLogger {

    public static void WriteToLog(String s) {
        if (s == null) {
            return;
        }
        s = GetTimestamp() + " - " + s;
        if (EosCleosServer.bLogToStdout) {
            System.out.println(s);
        }
        try {
            synchronized (EosCleosServer.LogLockObj) {
                s += "\n";
                FileWriter fw = new FileWriter(EosCleosServer.sLogFile, true);
                fw.write(s);
                fw.close();
            }
        } catch (Exception e) {
            System.out.println("AwmTx WriteToAwmTxLog Error: " + e.getLocalizedMessage());
        }
    }

    public static String GetTimestamp() {
        String ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat isoFormatter = new SimpleDateFormat(ISO_FORMAT);
        return isoFormatter.format(new Date());
    }

}
