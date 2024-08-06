package me.rhys.agent;

import lombok.Getter;
import me.rhys.agent.security.CustomSecurityManager;
import me.rhys.agent.transformer.ClassTransformer;
import me.rhys.agent.util.RuntimeUtil;
import org.tinylog.Logger;

import java.lang.instrument.Instrumentation;

@Getter
public enum Agent {
    INSTANCE;

    private Instrumentation instrumentation;

    public void initialize(Instrumentation instrumentation) {
        int javaVersion = RuntimeUtil.getJVMVersion();
        Logger.info("Initializing Agent... (running on Java " + javaVersion + ")");

        this.instrumentation = instrumentation;
        instrumentation.addTransformer(new ClassTransformer());

        // newer java doesn't allow the use of the security manager
        // this agent will still be able to flag and clean out malware
        // just one less step
        if (javaVersion < 21) {
            Logger.info("Set custom security manager");
            System.setSecurityManager(new CustomSecurityManager());
        } else {
            Logger.warn("Unable to use a custom security manager for monitoring connections, " +
                    "because it was removed in Java 21+");
        }

        // after looking more into this malware on SpigotMC it doesn't attempt to run if a specific
        // system property is set, quite funny, this malware is poorly made clearly LMFAO.

        Logger.info("Set fake system property");
        System.setProperty("ethanol.running", "1");
    }

    public boolean checkConnection(String ip, int port) {

        if (port == 40041 || port == 40000) {
            Logger.warn("Suspicious connection ~ " + ip + ":" + port);
        }

        // would be funny if the person behind this changed the ip
        return ip.equalsIgnoreCase("84.252.120.172");
    }
}
