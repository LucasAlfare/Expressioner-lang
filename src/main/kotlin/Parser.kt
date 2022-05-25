import Tokenizer.Companion.Token
import Tokenizer.Companion.TokenType

class Parser {
  companion object {

    val assignments = mutableMapOf<String, List<Token>>()

    fun fetchUnaries(tokens: List<Token>): MutableList<Token> {
      val nextTokens = mutableListOf<Token>()
      var lastUnary: Token? = null

      tokens.forEach {
        if (it.value.isNotEmpty()) { // just for security
          if (it.type == TokenType.Unary) {
            lastUnary = it
          } else {
            if (lastUnary != null && it.type == TokenType.Number) {
              val factor = if (lastUnary!!.value == "-") -1 else 1
              nextTokens += Token(type = TokenType.Number, value = (factor * it.value.toDouble()).toString())
              lastUnary = null
            } else if (it.type == TokenType.Number) {
              nextTokens += Token(type = it.type, value = it.value.toDouble().toString())
            } else {
              nextTokens += it
            }
          }
        }
      }

      return nextTokens
    }

    fun parseStatement(tokens: List<Token>) {
      var lastToken = Token()
      var variableNameToken = Token()
      val expressionTokensBuffer = mutableListOf<Token>()

      tokens.forEachIndexed { i, it ->
        if (lastToken.type == TokenType.Assigner) {
          variableNameToken = it
        }

        if (variableNameToken.type != TokenType.Empty) {
          if (it.value != "=" && it.value != variableNameToken.value) {
            expressionTokensBuffer += it
          }
        }

        lastToken = it
      }

      assignments[variableNameToken.value] = expressionTokensBuffer
    }
  }
}
