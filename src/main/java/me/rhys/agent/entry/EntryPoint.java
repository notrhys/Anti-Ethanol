package me.rhys.agent.entry;

import me.rhys.agent.Agent;

import java.lang.instrument.Instrumentation;

public class EntryPoint {

    public static void premain(String args, Instrumentation inst) {
        Agent.INSTANCE.initialize(inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        Agent.INSTANCE.initialize(inst);
    }
}
