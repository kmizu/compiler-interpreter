package com.github.kmizu.compiler_interpreter

object Interpreter {
    fun interpret(input: Ast): Int {
        when(input) {
            is Ast.Num ->
                return input.value
            is Ast.Add ->
                return interpret(input.left) + interpret(input.right)
            is Ast.Sub ->
                return interpret(input.left) - interpret(input.right)
            is Ast.Mul ->
                return interpret(input.left) * interpret(input.right)
            is Ast.Div ->
                return interpret(input.left) / interpret(input.right)
        }
    }
}