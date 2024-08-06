package me.rhys.agent.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RuntimeUtil {
    public int getJVMVersion() {
        String version = System.getProperty("java.version");

        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }

        return Integer.parseInt(version);
    }
}
