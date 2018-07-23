package eoscleosserver;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;

public class EosCleosServer {

    public static final Object LogLockObj = new Object();
    static boolean bLogToStdout = "true".equalsIgnoreCase(AwmUtils.GetAwmCnfValue("EosCleosLogToStdOut"));
    static int nDefaultPort = Integer.parseInt(AwmUtils.GetAwmCnfValue("EosCleosport"));
    static String sLogFile = AwmUtils.GetAwmCnfValue("EosCleosLogFile");
    static String sCleos = AwmUtils.GetAwmCnfValue("CleosLocationAndParams");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // TLS1.2 only
        System.setProperty("https.protocols", "TLSv1.2");

        // Start the Verifone Listener Threads
        EosLogger.WriteToLog("*** Starting EosCleosServer");

        // Create an HTTP server to listen for commands
        try {

            HttpServer server = HttpServer.create(new InetSocketAddress(nDefaultPort), 0);
            server.createContext("/", new CleosRelay());
            server.setExecutor(null); // creates a default executor
            server.start();
            EosLogger.WriteToLog("*** The HTTP server is running on port " + nDefaultPort);
        } catch (Exception e) {
            EosLogger.WriteToLog("Error: " + e.getLocalizedMessage());
        }
    }
}
