package jlox;

public enum TokenType {
	// single character tokens
	LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, RIGHT_CURLY,
	COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

	// one ot two character tokens
	BANG, BANG_EQUAL,
	EQUAL, EQUAL_EQUAL,
	GREATER, GREATER_EQUAL,
	LESS, LESS_EQUAL,

	// literals
	IDENTIFIER, STRING, NUMBER,

	// keywords
	AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
	PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

	EOF
}
