package jlox;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ASTPrinter implements Expr.Visitor<String> {
	String print(Expr expr) {
		return expr.accept(this);
	}

	@Override
	public String visitAssignExpr(Expr.Assign expr) {
		return this.parenthesize("=", expr, expr.value);
	}

	@Override
	public String visitBinaryExpr(Expr.Binary expr) {
		return this.parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	@Override
	public String visitCallExpr(Expr.Call expr) {
		return this.parenthesize("fun", Stream
				.concat(Stream.of(expr.callee), expr.arguments.stream())
				.toArray(Expr[]::new)
		);
	}

	@Override
	public String visitGetExpr(Expr.Get expr) {
		return "<not implemented>";
	}

	@Override
	public String visitGroupingExpr(Expr.Grouping expr) {
		return this.parenthesize("group", expr.expression);
	}

	@Override
	public String visitLiteralExpr(Expr.Literal expr) {
		if (expr.value == null) {
			return "nil";
		} else {
			return expr.value.toString();
		}
	}

	@Override
	public String visitLogicalExpr(Expr.Logical expr) {
		return this.parenthesize(expr.operator.lexeme, expr.left, expr.right);
	}

	@Override
	public String visitSetExpr(Expr.Set expr) {
		return "<not implemented>";
	}

	@Override
	public String visitUnaryExpr(Expr.Unary expr) {
		return this.parenthesize(expr.operator.lexeme, expr.right);
	}

	@Override
	public String visitVariableExpr(Expr.Variable expr) {
		return expr.name.lexeme;
	}

	private String parenthesize(String name, Expr... exprs) {
		StringBuilder builder = new StringBuilder();

		builder.append("(").append(name);
		for (Expr expr : exprs) {
			builder.append(" ").append(expr.accept(this));
		}
		builder.append(")");

		return builder.toString();
	}

	public static void main(String[] args) {
		Expr expr = new Expr.Binary(
				new Expr.Unary(
						new Token(TokenType.MINUS, "-", null, 1),
						new Expr.Literal(123)
				),
				new Token(TokenType.STAR, "*", null, 1),
				new Expr.Grouping(new Expr.Literal(45.67))
		);

		System.out.println(new ASTPrinter().print(expr));
	}
}
