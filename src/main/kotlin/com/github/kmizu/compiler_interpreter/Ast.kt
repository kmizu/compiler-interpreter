package com.github.kmizu.compiler_interpreter

sealed abstract class Ast {
    data class Num(val value: Int) : Ast()
    data class Add(val left: Ast, val right: Ast): Ast()
    data class Sub(val left: Ast, val right: Ast): Ast()
    data class Mul(val left: Ast, val right: Ast): Ast()
    data class Div(val left: Ast, val right: Ast): Ast()
}