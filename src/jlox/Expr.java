package jlox;

import java.util.List;

abstract class Expr {
	interface Visitor<T> {
		T visitAssignExpr(Assign expr);
		T visitBinaryExpr(Binary expr);
		T visitCallExpr(Call expr);
		T visitGetExpr(Get expr);
		T visitGroupingExpr(Grouping expr);
		T visitLiteralExpr(Literal expr);
		T visitLogicalExpr(Logical expr);
		T visitSetExpr(Set expr);
		T visitUnaryExpr(Unary expr);
		T visitVariableExpr(Variable expr);
	}

	static class Assign extends Expr {
		public final Token name;
		public final Expr value;

		Assign(Token name, Expr value) {
			this.name = name;
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitAssignExpr(this);
		}
	}

	static class Binary extends Expr {
		public final Expr left;
		public final Token operator;
		public final Expr right;

		Binary(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitBinaryExpr(this);
		}
	}

	static class Call extends Expr {
		public final Expr callee;
		public final Token paren;
		public final List<Expr> arguments;

		Call(Expr callee, Token paren, List<Expr> arguments) {
			this.callee = callee;
			this.paren = paren;
			this.arguments = arguments;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitCallExpr(this);
		}
	}

	static class Get extends Expr {
		public final Expr object;
		public final Token name;

		Get(Expr object, Token name) {
			this.object = object;
			this.name = name;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitGetExpr(this);
		}
	}

	static class Grouping extends Expr {
		public final Expr expression;

		Grouping(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitGroupingExpr(this);
		}
	}

	static class Literal extends Expr {
		public final Object value;

		Literal(Object value) {
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLiteralExpr(this);
		}
	}

	static class Logical extends Expr {
		public final Expr left;
		public final Token operator;
		public final Expr right;

		Logical(Expr left, Token operator, Expr right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitLogicalExpr(this);
		}
	}

	static class Set extends Expr {
		public final Expr object;
		public final Token name;
		public final Expr value;

		Set(Expr object, Token name, Expr value) {
			this.object = object;
			this.name = name;
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSetExpr(this);
		}
	}

	static class Unary extends Expr {
		public final Token operator;
		public final Expr right;

		Unary(Token operator, Expr right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitUnaryExpr(this);
		}
	}

	static class Variable extends Expr {
		public final Token name;

		Variable(Token name) {
			this.name = name;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitVariableExpr(this);
		}
	}

	abstract <T> T accept(Visitor<T> visitor);
}
