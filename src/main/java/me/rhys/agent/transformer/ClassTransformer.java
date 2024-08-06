package me.rhys.agent.transformer;

import me.rhys.agent.Agent;
import me.rhys.agent.util.ASMUtil;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.tinylog.Logger;

import java.lang.instrument.ClassFileTransformer;
import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;

public class ClassTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classFileBuffer) {

        // check existing classes for malware bootstrap
        // we have to pass in the classes loaded class-loader otherwise ASM will not be able to find the class
        ClassNode classNode = ASMUtil.loadClass(className, loader);

        if (classNode != null) {
            int methodCalls = 0;
            boolean detected = false;

            block:
            {
                for (MethodNode methodNode : classNode.methods) {
                    boolean isEntryPoint = methodNode.name.equals("lambda$onEnable$$0");

                    // simple check
                    for (AbstractInsnNode instruction : methodNode.instructions) {
                        if (instruction instanceof MethodInsnNode) {
                            MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;

                            // this is very basis check for checking the stack based on the infection entry point
                            if (isEntryPoint) {
                                if (methodInsnNode.desc.equals("()Ljava/lang/ClassLoader;")) {
                                    methodCalls++;
                                }

                                if (methodInsnNode.desc
                                        .equals("Ljava/net/URLClassLoader;Ljava/lang/String;)Ljava/lang/Class;")
                                        && methodInsnNode.name.equals("invoke")) {
                                    methodCalls++;
                                }

                                if (methodInsnNode.desc.equals("([B)V") && methodInsnNode.name.equals("<init>")) {
                                    methodCalls++;
                                }

                                if (methodInsnNode.desc
                                        .equals("Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
                                        && methodInsnNode.name.equals("invoke")) {
                                    methodCalls++;
                                }

                                if (methodCalls >= 5) {
                                    detected = true;
                                    ASMUtil.cleanMethod(methodNode);
                                    break block;
                                }
                            }
                        }
                    }
                }
            }

            if (detected) {
                Logger.warn("Found infected class " + className);

                try {
                    ClassWriter classWriter = new ClassWriter(
                            ClassWriter.COMPUTE_MAXS
                    );

                    classNode.accept(classWriter);

                    Logger.info("Cleaned " + className);
                    return classWriter.toByteArray();
                } catch (Exception e) {
                    return ASMUtil.CLASS_BYTES;
                }
            }
        }

        // clean out the malware's NMS mappings
        if (className.startsWith("rocks/ethanol")) {
            Logger.info("Nullifying the class " + className);
            return ASMUtil.CLASS_BYTES;
        }

        return classFileBuffer;
    }
}
