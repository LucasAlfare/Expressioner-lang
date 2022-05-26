import java.util.*
import kotlin.collections.set

/*

  2
  *
  (
  5
  +
  3
  ) / 4


  nums: 2 5 3
  ops : * ( +

 */

private fun eval(operands: Stack<Int>, operators: Stack<String>) {
  val b = operands.pop()
  val a = operands.pop()
  val currentResult = when (operators.pop()) {
    "+" -> {
      a + b
    }
    "-" -> {
      a - b
    }
    "*" -> {
      a * b
    }
    else -> {
      a / b
    }
  }

  operands.push(currentResult)
}

fun main() {
  //2 * (5 + 3) / 4
  //val expr = arrayOf(2, "*", "(", 5, "+", 3, ")", "/", 4) //4

  // (2 * (3 * (4 * 5))) / 4 + 3
  val expr = arrayOf("(", 2, "*", "(", 3, "*", "(", 4, "*", 5, ")", ")", ")", "/", 4, "+", 3)

  // 2 * (5 * (3 + 6)) / 15 - 2
  //val expr = arrayOf(2, "*", "(", 5, "*", "(", 3, "+", 6, ")", ")", "/", 15, "-", 2)

  val precedences = mutableMapOf<String, Int>()
  precedences["+"] = 1
  precedences["-"] = 1
  precedences["*"] = 2
  precedences["/"] = 2
  precedences["^"] = 3

  val operands = Stack<Int>()
  val operators = Stack<String>()
  var nOpPar = 0

  expr.forEach {
    if (it is Int) {
      operands.push(it)
    } else if (it == "+" || it == "-" || it == "*" || it == "/") {
      val curr = it as String
      if (operators.isEmpty() || nOpPar >= 0) {
        operators.push(curr)
      } else {
        val last = operators.peek()
        if (last != "(" && last != ")") {
          if (precedences[curr]!! >= precedences[last]!!) {
            operators.push(curr)
          } else {
            while (operators.isNotEmpty() || (precedences[curr]!! < precedences[last]!!)) {
              eval(operands, operators)
            }
          }
        }
      }
    } else if (it == "(") {
      operators.push(it as String)
      nOpPar++
    } else if (it == ")") {
      while (operators.peek() != "(") {
        eval(operands, operators)
      }
      operators.pop()
      nOpPar--
    }
  }

  while (operators.isNotEmpty()) {
    eval(operands, operators)
  }
}