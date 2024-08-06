package me.rhys.agent.security;

import me.rhys.agent.Agent;
import me.rhys.agent.util.SneakyThrow;
import org.tinylog.Logger;

import java.net.SocketTimeoutException;
import java.security.Permission;

public class CustomSecurityManager extends SecurityManager {

    @Override
    public void checkPermission(Permission perm) {
        if (perm.getName().equalsIgnoreCase("setSecurityManager")
                || perm.getName().equalsIgnoreCase("SecurityManager")) {
            Logger.error("Something tried to override the security manager (" + perm.getName() + ")");
            System.exit(-1);
        }
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        checkPermission(perm);
    }

    @Override
    public void checkConnect(String host, int port, Object context) {
        checkConnect(host, port);
    }

    @Override
    public void checkConnect(String host, int port) {
        if (Agent.INSTANCE.checkConnection(host, port)) {
            Logger.warn("Blocked connection to " + host + ":" + port);
            SneakyThrow.sneakyThrow(new SocketTimeoutException("Connection timed out"));
        }
    }
}
