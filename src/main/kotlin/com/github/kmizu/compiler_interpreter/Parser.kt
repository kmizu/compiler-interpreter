package com.github.kmizu.compiler_interpreter

import com.github.kmizu.kotbinator.*
import com.github.kmizu.kotbinator.Parser as KParser

object Parser {
    val E: KParser<Ast> by lazy {
        rule {
            (A seq ((s("+") seq A) / (s("-") seq A)).repeat()).map {result ->
                val (init, rights) = result
                rights.fold(init){left, rights ->
                    val (op, right) = rights
                    when(op) {
                        "+" -> Ast.Add(left, right)
                        "-" -> Ast.Sub(left, right)
                        else -> TODO()
                    }
                }
            }
        }
    }
    val A: KParser<Ast> by lazy {
        (P seq ((s("*") seq P) / (s("/") seq P)).repeat()).map {result ->
            val (init, rights) = result
            rights.fold(init){left, rights ->
                val (op, right) = rights
                when(op) {
                    "*" -> Ast.Mul(left, right)
                    "/" -> Ast.Div(left, right)
                    else -> TODO()
                }
            }
        }
    }
    val P: KParser<Ast> by lazy {
        rule {
            ((s("(") seqr E seql s(")")) / numeric )
        }
    }
    val numeric: KParser<Ast> by lazy {
        r('0', '9').repeat1().map { v -> Ast.Num(v.fold(""){ x, y -> x + y }.toInt()) as Ast}
    }
    fun parse(input: String): Ast {
        val result = E.parse(input)
        when(result) {
            is ParseResult.ParseSuccess ->
                return result.value
            is ParseResult.ParseFailure ->
                throw ParseError(result.rest)
        }
    }
}