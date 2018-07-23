package eoscleosserver;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class AwmUtils {

    // Read a value from the AWM config file
    // /etc/mshift/awm/awm.cnf
    public static String GetAwmCnfValue(String sName) {
        String sCnfFile = "/etc/mshift/awm/awm.cnf";
        Properties prop = new Properties();
        InputStream input = null;
        String sValue = null;

        try {
            input = new FileInputStream("/etc/mshift/awm/awm.cnf");

            // load a properties file
            prop.load(input);
            sValue = prop.getProperty(sName);

        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("Error reading file " + sCnfFile);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e) {
                    System.out.println("Error closing file " + sCnfFile);
                }
            }
        }
        return sValue;
    }

    public static boolean IsEmpty(String s) {
        if (s == null || s.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
