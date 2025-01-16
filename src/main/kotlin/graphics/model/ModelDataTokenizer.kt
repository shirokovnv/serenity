package graphics.model

enum class TokenType {
    VERTEX,
    TEXTURE_COORDINATE,
    NORMAL,
    FACE,
    USEMTL,
    SMOOTHING_GROUP,
    COMMENT,
    OBJECT,
    UNKNOWN
}

data class Token(val type: TokenType, val line: String)

class ModelDataTokenizer {
    fun tokenize(line: String): Token {
        val tokenType = when {
            line.startsWith("v ") -> TokenType.VERTEX
            line.startsWith("vt ") -> TokenType.TEXTURE_COORDINATE
            line.startsWith("vn ") -> TokenType.NORMAL
            line.startsWith("f ") -> TokenType.FACE
            line.startsWith("usemtl ") -> TokenType.USEMTL
            line.startsWith("s ") -> TokenType.SMOOTHING_GROUP
            line.startsWith("#") -> TokenType.COMMENT
            line.startsWith("o ") -> TokenType.OBJECT
            else -> TokenType.UNKNOWN
        }

        return Token(tokenType, line)
    }
}