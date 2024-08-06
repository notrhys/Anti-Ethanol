package me.rhys.agent.util;

import lombok.experimental.UtilityClass;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;

@UtilityClass
public class ASMUtil {
    public final byte[] CLASS_BYTES = {
                    -54, -2, -70, -66, 0, 0, 0, 49, 0, 5, 1, 0, 34, 115, 117, 110,
                    47, 105, 110, 115, 116, 114, 117, 109, 101, 110, 116, 47, 73,
                    110, 115, 116, 114, 117, 109, 101, 110, 116, 97, 116, 105, 111,
                    110, 73, 109, 112, 108, 7, 0, 1, 1, 0, 16, 106, 97, 118, 97, 47,
                    108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 7, 0, 3, 0, 1,
                    0, 2, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0
            };

    public void cleanMethod(MethodNode methodNode) {
        methodNode.instructions.clear();
        methodNode.tryCatchBlocks.clear();

        if (methodNode.localVariables != null) {
            methodNode.localVariables.clear();
        }

        InsnNode returnNode;
        switch (Type.getReturnType(methodNode.desc).getSort()) {
            case Type.VOID:
                returnNode = new InsnNode(Opcodes.RETURN);
                break;
            case Type.BOOLEAN:
            case Type.CHAR:
            case Type.BYTE:
            case Type.SHORT:
            case Type.INT:
                methodNode.instructions.add(new InsnNode(Opcodes.ICONST_0));
                returnNode = new InsnNode(Opcodes.IRETURN);
                break;
            case Type.FLOAT:
                methodNode.instructions.add(new InsnNode(Opcodes.FCONST_0));
                returnNode = new InsnNode(Opcodes.FRETURN);
                break;
            case Type.LONG:
                methodNode.instructions.add(new InsnNode(Opcodes.LCONST_0));
                returnNode = new InsnNode(Opcodes.LRETURN);
                break;
            case Type.DOUBLE:
                methodNode.instructions.add(new InsnNode(Opcodes.DCONST_0));
                returnNode = new InsnNode(Opcodes.DRETURN);
                break;
            case Type.ARRAY:
            case Type.OBJECT:
                methodNode.instructions.add(new InsnNode(Opcodes.ACONST_NULL));
                returnNode = new InsnNode(Opcodes.ARETURN);
                break;
            case Type.METHOD:
            default:
                throw new IllegalStateException("Unexpected value: " + Type.getReturnType(methodNode.desc).getSort());
        }

        methodNode.instructions.add(returnNode);
        methodNode.maxStack = 1;
        methodNode.maxLocals = 1;
    }

    public static ClassNode loadClass(String className, ClassLoader loader) {
        try {
            String internalClassName = className.replace('.', '/') + ".class";
            InputStream classStream = loader.getResourceAsStream(internalClassName);

            if (classStream != null) {
                ClassReader classReader = new ClassReader(classStream);
                ClassNode classNode = new ClassNode();

                classReader.accept(classNode, 0);
                return classNode;
            }
        } catch (IOException ignored) {
            //
        }

        return null;
    }
}
