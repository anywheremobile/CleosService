package eoscleosserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import static eoscleosserver.EosCleosServer.sCleos;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;

// Handles requests from AwmTx
// Looking for <url>?cmd=<cmd url encoded>
public class CleosRelay implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String sRet = "";
        HashMap<String, String> params = new HashMap<>();

        parseGetParams(params, httpExchange);
        parsePostParams(params, httpExchange);

        String sCmd = params.get("cmd");
        if (sCmd == null) {
            sCmd = "";
        }

        String[] aCleos = sCleos.split(" ");
        String [] cmdParams = sCmd.split("\\|");
        String [] aCmd = new String [aCleos.length+ cmdParams.length];
        System.arraycopy(aCleos, 0, aCmd, 0, aCleos.length);
        System.arraycopy(cmdParams, 0, aCmd, aCleos.length, cmdParams.length);
        
        sRet = RunCommand(aCmd);

        writeResponse(httpExchange, sRet);
    }

    private static void parseGetParams(HashMap<String, String> params, HttpExchange exchange) {
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(params, query);
    }

    private static void parsePostParams(HashMap<String, String> params, HttpExchange exchange) {
        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {

            try {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                String query = br.readLine();

                parseQuery(params, query);
            } catch (Exception e) {
                if (EosCleosServer.bLogToStdout) {
                    System.out.println("parsePostParams Error: " + e.getLocalizedMessage());
                }
                EosLogger.WriteToLog("parsePostParams Error: " + e.getLocalizedMessage());
            }
        }

    }

    private static void parseQuery(HashMap<String, String> parameters, String query) {

        if (query != null) {
            String pairs[] = query.split("[&]");

            for (String pair : pairs) {
                String param[] = pair.split("[=]");

                String key = null;
                String value = null;

                try {
                    if (param.length > 0) {
                        System.out.println("");
                        key = URLDecoder.decode(param[0],
                                System.getProperty("file.encoding"));
                    }

                    if (param.length > 1) {
                        value = URLDecoder.decode(param[1],
                                System.getProperty("file.encoding"));
                    }
                } catch (Exception e) {
                    EosLogger.WriteToLog("Error: " + e.getLocalizedMessage());
                    return;
                }

                parameters.put(key, value);
            }
        }
    }

    public static void writeResponse(HttpExchange httpExchange, String response) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static String RunCommand(String[] sCmd) {

        String s = null;
        String sRet = "";

        try {
            if (EosCleosServer.bLogToStdout) {
                System.out.println(Arrays.toString(sCmd));
            }
            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            Process p = Runtime.getRuntime().exec(sCmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            // read the output from the command
            //System.out.println("Here is the standard output of the command:\n");
            while ((s = stdInput.readLine()) != null) {
                if (EosCleosServer.bLogToStdout) {
                    System.out.println(s);
                }

                sRet += s + "\n";
            }

            // read any errors from the attempted command
            //System.out.println("Here is the standard error of the command (if any):\n");
            while ((s = stdError.readLine()) != null) {
                if (EosCleosServer.bLogToStdout) {
                    System.out.println(s);
                }
                sRet += s + "\n";
            }

        } catch (IOException e) {
            if (EosCleosServer.bLogToStdout) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            System.out.println("EosCleosServer Error: ");

            EosLogger.WriteToLog(e.getMessage());
            EosLogger.WriteToLog(Arrays.toString(e.getStackTrace()));
        }
        return sRet;
    }

}
