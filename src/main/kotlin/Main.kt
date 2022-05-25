import java.io.File

fun main() {
  // loads a generic file containing some valid syntax
  val input = File("src/main/resources/here_contains_valid_syntax.expr")
    .bufferedReader()
    .readText()
    .split("\n")

  input.forEach {
    val statementTokens = Parser.fecthUnaries(Tokenizer.tokenize(it))
    if (statementTokens.isNotEmpty()) {
      Parser.parseStatement(statementTokens)
    }
  }

  Parser.assignments.keys.forEach {
    println("$it=${Parser.assignments[it]}")
  }
}