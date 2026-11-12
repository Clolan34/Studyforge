package com.example.calculator

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.*

enum class AngleMode {
    DEG, RAD
}

data class CalculationRecord(
    val id: String = "calc_${System.currentTimeMillis()}_${(1000..9999).random()}",
    val expression: String,
    val result: String,
    val angleMode: AngleMode,
    val timestamp: Long = System.currentTimeMillis()
)

object ScientificCalculatorEngine {

    /**
     * Evaluates a mathematical expression string.
     * Supports:
     * - Operators: +, -, *, /, ^, %, mod, nPr, nCr
     * - Functions: sin, cos, tan, asin, acos, atan, sinh, cosh, tanh, sqrt, cbrt, log, ln, abs, exp
     * - Constants: pi, e, ANS
     * - Factorial: ! (postfix)
     * - Parentheses: (, )
     * - Angle modes: DEG, RAD
     */
    fun evaluate(
        rawExpression: String,
        angleMode: AngleMode = AngleMode.DEG,
        lastAnswer: Double = 0.0
    ): Result<Double> {
        val sanitized = sanitizeExpression(rawExpression, lastAnswer)
        if (sanitized.isBlank()) {
            return Result.failure(IllegalArgumentException("Expression is empty"))
        }

        return try {
            val parser = ExpressionParser(sanitized, angleMode)
            val result = parser.parse()
            if (result.isNaN()) {
                Result.failure(ArithmeticException("Math Error: Undefined result"))
            } else if (result.isInfinite()) {
                Result.failure(ArithmeticException("Error: Division by zero or overflow"))
            } else {
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error: Undefined"
        if (value.isInfinite()) return if (value > 0) "Error: +Infinity" else "Error: -Infinity"

        // Check if value is very close to an integer
        val rounded = value.roundToLong()
        if (abs(value - rounded) < 1e-11) {
            return rounded.toString()
        }

        // Check for common floating-point approximations like 0.5000000000000001
        val symbols = DecimalFormatSymbols(Locale.US)
        val df = if (abs(value) >= 1e10 || (abs(value) < 1e-4 && value != 0.0)) {
            DecimalFormat("0.######E0", symbols)
        } else {
            DecimalFormat("0.##########", symbols)
        }
        val formatted = df.format(value)
        return if (formatted == "-0") "0" else formatted
    }

    private fun sanitizeExpression(expr: String, lastAnswer: Double): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "pi")
            .replace("ANS", lastAnswer.toString())
            .replace("ans", lastAnswer.toString())
            .replace(" ", "")
    }

    // Mathematical utility functions
    fun factorial(n: Double): Double {
        if (n < 0 || n != floor(n)) {
            throw ArithmeticException("Math Error: Factorial defined only for non-negative integers")
        }
        if (n > 170) {
            throw ArithmeticException("Math Error: Factorial overflow")
        }
        var res = 1.0
        val count = n.toLong()
        for (i in 2..count) {
            res *= i
        }
        return res
    }

    fun permutation(n: Double, r: Double): Double {
        if (n < 0 || r < 0 || n != floor(n) || r != floor(r) || r > n) {
            throw ArithmeticException("Math Error: Invalid nPr arguments (requires 0 <= r <= n integers)")
        }
        return factorial(n) / factorial(n - r)
    }

    fun combination(n: Double, r: Double): Double {
        if (n < 0 || r < 0 || n != floor(n) || r != floor(r) || r > n) {
            throw ArithmeticException("Math Error: Invalid nCr arguments (requires 0 <= r <= n integers)")
        }
        return factorial(n) / (factorial(r) * factorial(n - r))
    }
}

/**
 * Recursive descent parser implementing correct mathematical precedence:
 * Precedence:
 * 1. Add / Subtract (+, -)
 * 2. Multiply / Divide / Modulo (*, /, %, mod, nPr, nCr)
 * 3. Exponentiation / Power (^)
 * 4. Unary Prefix (+, -) & Postfix Factorial (!)
 * 5. Primary (Numbers, constants, parentheses, functions)
 */
private class ExpressionParser(
    private val text: String,
    private val angleMode: AngleMode
) {
    private var pos = 0

    fun parse(): Double {
        val result = parseExpression()
        if (pos < text.length) {
            throw IllegalArgumentException("Syntax Error: Unexpected symbol '${text[pos]}' at position $pos")
        }
        return result
    }

    // 1. Addition and Subtraction
    private fun parseExpression(): Double {
        var x = parseTerm()
        while (pos < text.length) {
            when {
                match('+') -> x += parseTerm()
                match('-') -> x -= parseTerm()
                else -> break
            }
        }
        return x
    }

    // 2. Multiplication, Division, Modulo, nPr, nCr
    private fun parseTerm(): Double {
        var x = parseFactor()
        while (pos < text.length) {
            when {
                match('*') -> x *= parseFactor()
                match('/') -> {
                    val divisor = parseFactor()
                    if (divisor == 0.0) throw ArithmeticException("Error: Division by zero")
                    x /= divisor
                }
                match('%') -> {
                    // Check if it's modulo or percentage
                    if (pos < text.length && (text[pos].isDigit() || text[pos] == '(' || text[pos] == '-')) {
                        val modVal = parseFactor()
                        if (modVal == 0.0) throw ArithmeticException("Error: Modulo by zero")
                        x %= modVal
                    } else {
                        // percentage operator
                        x /= 100.0
                    }
                }
                matchWord("mod") -> {
                    val modVal = parseFactor()
                    if (modVal == 0.0) throw ArithmeticException("Error: Modulo by zero")
                    x %= modVal
                }
                matchWord("nPr") || matchWord("P") -> {
                    val r = parseFactor()
                    x = ScientificCalculatorEngine.permutation(x, r)
                }
                matchWord("nCr") || matchWord("C") -> {
                    val r = parseFactor()
                    x = ScientificCalculatorEngine.combination(x, r)
                }
                else -> break
            }
        }
        return x
    }

    // 3. Exponentiation / Power (^) - Right associative
    private fun parseFactor(): Double {
        var x = parseUnary()
        if (match('^')) {
            val exponent = parseFactor() // right-associative
            x = x.pow(exponent)
        }
        return x
    }

    // 4. Unary Prefix & Postfix Operators
    private fun parseUnary(): Double {
        if (match('+')) return parseUnary()
        if (match('-')) return -parseUnary()

        var x = parsePrimary()

        // Postfix Factorial (!)
        while (match('!')) {
            x = ScientificCalculatorEngine.factorial(x)
        }

        // Postfix % if not followed by an expression
        while (match('%')) {
            x /= 100.0
        }

        return x
    }

    // 5. Primaries: Numbers, Functions, Parentheses, Constants
    private fun parsePrimary(): Double {
        if (match('(')) {
            val x = parseExpression()
            if (!match(')')) throw IllegalArgumentException("Syntax Error: Missing closing parenthesis ')'")
            return x
        }

        // Function Parsing
        val func = parseIdentifier()
        if (func.isNotEmpty()) {
            return evaluateFunction(func)
        }

        // Number parsing
        val startPos = pos
        if (pos < text.length && (text[pos].isDigit() || text[pos] == '.')) {
            while (pos < text.length && (text[pos].isDigit() || text[pos] == '.')) {
                pos++
            }
            // Scientific notation (e.g. 1.5e3 or 2E-4)
            if (pos < text.length && (text[pos] == 'e' || text[pos] == 'E')) {
                val nextPos = pos + 1
                if (nextPos < text.length && (text[nextPos].isDigit() || text[nextPos] == '+' || text[nextPos] == '-')) {
                    pos++ // consume 'e'
                    if (pos < text.length && (text[pos] == '+' || text[pos] == '-')) pos++
                    while (pos < text.length && text[pos].isDigit()) pos++
                }
            }

            val numStr = text.substring(startPos, pos)
            return numStr.toDoubleOrNull() ?: throw IllegalArgumentException("Syntax Error: Invalid number '$numStr'")
        }

        throw IllegalArgumentException("Syntax Error: Unexpected character '${if (pos < text.length) text[pos] else "EOF"}'")
    }

    private fun parseIdentifier(): String {
        val startPos = pos
        while (pos < text.length && (text[pos].isLetter() || text[pos] == '_')) {
            pos++
        }
        return text.substring(startPos, pos)
    }

    private fun evaluateFunction(func: String): Double {
        // Constants first
        when (func.lowercase()) {
            "pi" -> return Math.PI
            "e" -> {
                // If followed by '(', it's exp(), else it's constant e = 2.71828...
                if (pos < text.length && text[pos] == '(') {
                    // treat as exp
                } else {
                    return Math.E
                }
            }
        }

        // Function call requires argument (with or without parenthesis)
        val arg = if (pos < text.length && text[pos] == '(') {
            match('(')
            val v = parseExpression()
            if (!match(')')) throw IllegalArgumentException("Syntax Error: Missing closing parenthesis for function $func")
            v
        } else {
            // Implicit argument e.g. sin 30 or sqrt 4
            parseFactor()
        }

        return when (func.lowercase()) {
            "sin" -> {
                val radians = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                // Clean close to 0 e.g. sin(180 deg) = 0
                val res = sin(radians)
                if (abs(res) < 1e-15) 0.0 else res
            }
            "cos" -> {
                val radians = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                val res = cos(radians)
                if (abs(res) < 1e-15) 0.0 else res
            }
            "tan" -> {
                val radians = if (angleMode == AngleMode.DEG) Math.toRadians(arg) else arg
                // Check if cos(radians) == 0 (tan undefined at 90°, 270°, etc.)
                if (abs(cos(radians)) < 1e-15) {
                    throw ArithmeticException("Math Error: Tangent undefined at 90° + k*180°")
                }
                tan(radians)
            }
            "asin", "arcsin", "sin^-1", "asin_d" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Math Error: Domain [-1, 1] exceeded for asin")
                val rad = asin(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
            }
            "acos", "arccos", "cos^-1" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("Math Error: Domain [-1, 1] exceeded for acos")
                val rad = acos(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
            }
            "atan", "arctan", "tan^-1" -> {
                val rad = atan(arg)
                if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
            }
            "sinh" -> sinh(arg)
            "cosh" -> cosh(arg)
            "tanh" -> tanh(arg)
            "sqrt" -> {
                if (arg < 0) throw ArithmeticException("Math Error: Cannot take square root of negative number")
                sqrt(arg)
            }
            "cbrt" -> cbrt(arg)
            "log", "log10" -> {
                if (arg <= 0) throw ArithmeticException("Math Error: Logarithm domain requires x > 0")
                log10(arg)
            }
            "ln" -> {
                if (arg <= 0) throw ArithmeticException("Math Error: Natural log domain requires x > 0")
                ln(arg)
            }
            "exp" -> exp(arg)
            "abs" -> abs(arg)
            else -> throw IllegalArgumentException("Syntax Error: Unknown mathematical function '$func'")
        }
    }

    private fun match(expected: Char): Boolean {
        if (pos < text.length && text[pos] == expected) {
            pos++
            return true
        }
        return false
    }

    private fun matchWord(expectedWord: String): Boolean {
        if (text.startsWith(expectedWord, pos)) {
            pos += expectedWord.length
            return true
        }
        return false
    }
}
