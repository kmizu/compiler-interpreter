package com.github.kmizu.compiler_interpreter

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import java.io.File
import java.io.FileOutputStream

import kotlin.collections.*

object Compiler {
    private fun visitMethod(
            cw: ClassWriter, access: Int, name: String, desc: String, signature: String?, exceptions: Array<String>?, block: (MethodVisitor) -> Unit
    ) {
        val mv = cw.visitMethod(access, name, desc, signature, exceptions)
        try {
            block(mv)
        } finally {
            mv.visitEnd()
        }
    }

    fun compile(expression: Ast, className: String): ByteArray {
        val name = className
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
        cw.visit(
                V1_1, ACC_PUBLIC, name, null, "java/lang/Object", arrayOf<String>()
        )
        visitMethod(cw, ACC_PUBLIC, "<init>", "()V", null, null) {mv ->
            mv.visitVarInsn(ALOAD, 0)
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V")
            mv.visitInsn(RETURN)
            mv.visitMaxs(1, 1)
        }
        visitMethod(cw, ACC_PUBLIC or ACC_STATIC , "run", "()V", null, null) {mv ->
            fun compileExp (arg: Ast) {
                when(arg) {
                    is Ast.Add -> {
                        compileExp(arg.left)
                        compileExp(arg.right)
                        mv.visitInsn(IADD)
                    }
                    is Ast.Sub -> {
                        compileExp(arg.left)
                        compileExp(arg.right)
                        mv.visitInsn(ISUB)
                    }
                    is Ast.Mul -> {
                        compileExp(arg.left)
                        compileExp(arg.right)
                        mv.visitInsn(IMUL)
                    }
                    is Ast.Div -> {
                        compileExp(arg.left)
                        compileExp(arg.right)
                        mv.visitInsn(IDIV)
                    }
                    is Ast.Num -> {
                        mv.visitLdcInsn(arg.value)
                    }
                }
            }
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
            compileExp(expression)
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V")
            mv.visitInsn(RETURN)
            mv.visitMaxs(0, 0)
        }
        visitMethod(cw, ACC_PUBLIC or ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null) {mv ->
            mv.visitMethodInsn(INVOKESTATIC, "Main", "run", "()V")
            mv.visitInsn(RETURN)
            mv.visitMaxs(0, 0)
        }
        return cw.toByteArray()
    }

    fun compileToFile(expression: Ast, name: String) {
        val content = compile(expression, name)
        FileOutputStream(File("${name}.class")).use{ out ->
            out.write(content)
        }
    }

    fun compileAndRun(expression: Ast, name: String) {
        val content = compile(expression, name)
        val loader = object: ClassLoader() {
            override fun findClass(target: String): Class<*> {
                return if (target == name)
                    defineClass(content, 0, content.size)
                else
                    super.findClass(target)
            }
        }
        val target = loader.loadClass(name)
        val method = target.getMethod("run")
        method.invoke(null)
    }
}
