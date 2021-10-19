package jlox;

import java.util.List;

abstract class Stmt {
	interface Visitor<T> {
		T visitBlockStmt(Block stmt);
		T visitClassStmt(Class stmt);
		T visitExpressionStmt(Expression stmt);
		T visitFunctionStmt(Function stmt);
		T visitIfStmt(If stmt);
		T visitPrintStmt(Print stmt);
		T visitReturnStmt(Return stmt);
		T visitVarStmt(Var stmt);
		T visitWhileStmt(While stmt);
	}

	static class Block extends Stmt {
		public final List<Stmt> statements;

		Block(List<Stmt> statements) {
			this.statements = statements;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitBlockStmt(this);
		}
	}

	static class Class extends Stmt {
		public final Token name;
		public final Expr.Variable superclass;
		public final List<Stmt.Function> methods;

		Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
			this.name = name;
			this.superclass = superclass;
			this.methods = methods;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitClassStmt(this);
		}
	}

	static class Expression extends Stmt {
		public final Expr expression;

		Expression(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitExpressionStmt(this);
		}
	}

	static class Function extends Stmt {
		public final Token name;
		public final List<Token> params;
		public final List<Stmt> body;

		Function(Token name, List<Token> params, List<Stmt> body) {
			this.name = name;
			this.params = params;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitFunctionStmt(this);
		}
	}

	static class If extends Stmt {
		public final Expr condition;
		public final Stmt thenBranch;
		public final Stmt elseBranch;

		If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitIfStmt(this);
		}
	}

	static class Print extends Stmt {
		public final Expr expression;

		Print(Expr expression) {
			this.expression = expression;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitPrintStmt(this);
		}
	}

	static class Return extends Stmt {
		public final Token keyword;
		public final Expr value;

		Return(Token keyword, Expr value) {
			this.keyword = keyword;
			this.value = value;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitReturnStmt(this);
		}
	}

	static class Var extends Stmt {
		public final Token name;
		public final Expr initializer;

		Var(Token name, Expr initializer) {
			this.name = name;
			this.initializer = initializer;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitVarStmt(this);
		}
	}

	static class While extends Stmt {
		public final Expr condition;
		public final Stmt body;

		While(Expr condition, Stmt body) {
			this.condition = condition;
			this.body = body;
		}

		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitWhileStmt(this);
		}
	}

	abstract <T> T accept(Visitor<T> visitor);
}
