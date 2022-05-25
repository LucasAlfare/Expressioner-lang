class Tokenizer {
  companion object {

    private const val ASSIGNER_CHARACTER = "@"
    private const val ENDING_CHAR = "?"

    enum class TokenType {
      Unary,
      Operator,
      Number,
      Word,
      Parenthesis,
      Assigner,

      Empty
    }

    private enum class TokenizingState {
      Number,
      Unary,
      Operator,
      Associative,
      Word,
      Assignment,
      Unknown
    }

    class Token(var type: TokenType = TokenType.Empty, var value: String = "[empty]") {
      override fun toString() = " $type{ $value } "
    }

    private fun isDigit(c: String) =
      (c == "1") ||
              (c == "2") ||
              (c == "3") ||
              (c == "4") ||
              (c == "5") ||
              (c == "6") ||
              (c == "7") ||
              (c == "8") ||
              (c == "9") ||
              (c == "0") ||
              (c == ".")

    private fun isOperator(c: String) =
      (c == "+") || (c == "-") || (c == "*") || (c == "/") || (c == "=")

    private fun isUnary(last: String, current: String, next: Char): Boolean {
      if (last.isNotEmpty() && current.isNotEmpty()) {
        return (!isDigit(last) && !isLetter(last)) &&
                (current == "+" || current == "-") &&
                isDigit(next.toString())
      }
      return false
    }

    private fun isLetter(c: String) = arrayOf(
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'j',
      'k',
      'l',
      'm',
      'n',
      'o',
      'p',
      'q',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'x',
      'y',
      'z'
    ).contains(c.lowercase()[0])

    private fun getCurrentTokenizingState(a: String = "", b: String = "", c: Char): TokenizingState {
      return when {
        isDigit(b) -> {
          TokenizingState.Number
        }
        isUnary(a, b, c) -> {
          TokenizingState.Unary
        }
        (b == ")" || b == "(") -> {
          TokenizingState.Associative
        }
        isOperator(b) -> {
          TokenizingState.Operator
        }
        isLetter(b) -> {
          TokenizingState.Word
        }
        b == ASSIGNER_CHARACTER -> {
          TokenizingState.Assignment
        }
        else -> TokenizingState.Unknown
      }
    }

    fun tokenize(input: String): MutableList<Token> {
      val cleanInput =
        "${
          input.replace(" ", "")
        }$ENDING_CHAR$ENDING_CHAR" //concatenated with [two ENDING_CHARACTERS]!
      val tokens = mutableListOf<Token>()
      var lastCharacter = ""
      var numberBuffer = ""
      var wordBuffer = ""
      var tmpType: TokenType? = null
      var tmpValue = ""
      var finishedNumberBuffering = false
      var finishedWordBuffering = false

      cleanInput.forEachIndexed { i, c ->
        if (i < cleanInput.length - 1) {
          val currentCharacter = c.toString()

          // determines the type and value of current token
          // based on [previous, current and next] characters
          when (getCurrentTokenizingState(
            lastCharacter,
            currentCharacter,
            cleanInput[i + 1]
          )) {
            TokenizingState.Number -> {
              finishedNumberBuffering = false
              finishedWordBuffering = true
              numberBuffer += currentCharacter
            }

            TokenizingState.Word -> {
              finishedWordBuffering = false
              finishedNumberBuffering = true
              wordBuffer += currentCharacter
            }

            TokenizingState.Operator -> {
              finishedNumberBuffering = true
              finishedWordBuffering = true
              tmpType = TokenType.Operator
              tmpValue = currentCharacter
            }

            TokenizingState.Unary -> {
              finishedNumberBuffering = true
              finishedWordBuffering = true
              tmpType = TokenType.Unary
              tmpValue = currentCharacter
            }

            TokenizingState.Associative -> {
              finishedNumberBuffering = true
              finishedWordBuffering = true
              tmpType = TokenType.Parenthesis
              tmpValue = currentCharacter
            }

            TokenizingState.Assignment -> {
              finishedNumberBuffering = true
              finishedWordBuffering = true
              tmpType = TokenType.Assigner
              tmpValue = currentCharacter
            }

            TokenizingState.Unknown -> {
              if (currentCharacter != ENDING_CHAR) {
                println(
                  """
                    |Reached an unknown character: $currentCharacter
                    |This character is not handled then it will not be tokenized.
                  """.trimMargin()
                )
              }
            }
          }

          // FIRST appends tokens based on the buffers (numbers and words), if valid
          if (finishedNumberBuffering || currentCharacter == ENDING_CHAR) {
            if (numberBuffer.isNotEmpty()) {
              tokens += Token(type = TokenType.Number, value = numberBuffer)
              numberBuffer = ""
            }
          }

          if (finishedWordBuffering || currentCharacter == ENDING_CHAR) {
            if (wordBuffer.isNotEmpty()) {
              tokens += Token(type = TokenType.Word, value = wordBuffer)
              wordBuffer = ""
            }
          }

          // THEN appends a token based on early detection
          if (tmpType != null && tmpValue.isNotEmpty()) {
            tokens += Token(type = tmpType!!, value = tmpValue)
            tmpType = null
            tmpValue = ""
          }

          lastCharacter = currentCharacter
        }
      }

      return tokens
    }
  }
}
