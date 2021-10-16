package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
	public final Environment globals = new Environment();
	private Environment environment = globals;
	private final Map<Expr, Integer> locals = new HashMap<>();

	public Interpreter() {
		this.globals.define("clock", new LoxCallable() {
			@Override
			public int arity() {
				return 0;
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				return (double)System.currentTimeMillis() / 1000.0;
			}

			@Override
			public String toString() {
				return "<native fun>";
			}
		});
	}

	public void interpret(List<Stmt> statements) {
		try {
			for (Stmt statement : statements) {
				this.execute(statement);
			}
		} catch (RuntimeError e) {
			Lox.runtimeError(e);
		}
	}

	private void execute(Stmt stmt) {
		stmt.accept(this);
	}

	public void resolve(Expr expr, int depth) {
		this.locals.put(expr, depth);
	}

	public void executeBlock(List<Stmt> statements, Environment environment) {
		Environment previous = this.environment;

		try {
			this.environment = environment;

			for (Stmt statement : statements) {
				this.execute(statement);
			}
		} finally {
			this.environment = previous;
		}
	}

	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object left = this.evaluate(expr.left);
		Object right = this.evaluate(expr.right);

		switch (expr.operator.type) {
			case MINUS:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left - (double)right;
			case SLASH:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left / (double)right;
			case STAR:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left * (double)right;
			case PLUS:
				if (left instanceof Double && right instanceof Double) {
					return (double)left + (double)right;
				} else if (left instanceof String && right instanceof String) {
					return left + (String)right;
				} else {
					throw new RuntimeError(expr.operator, "Operands must be two numbers, or two strings");
				}
			case GREATER:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left > (double)right;
			case GREATER_EQUAL:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left >= (double)right;
			case LESS:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left < (double)right;
			case LESS_EQUAL:
				this.checkNumberOperands(expr.operator, left, right);
				return (double)left <= (double)right;
			case EQUAL:
				return this.isEqual(left, right);
			case BANG_EQUAL:
				return !this.isEqual(left, right);
		}

		return null;
	}

	@Override
	public Object visitCallExpr(Expr.Call expr) {
		Object callee = this.evaluate(expr.callee);

		List<Object> arguments = new ArrayList<>();
		for (Expr argument : expr.arguments) {
			arguments.add(evaluate(argument));
		}

		if (!(callee instanceof LoxCallable)) {
			throw new RuntimeError(expr.paren, "Can only call functions and classes.");
		}

		LoxCallable function = (LoxCallable)callee;

		if (arguments.size() != function.arity()) {
			throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
		}

		return function.call(this, arguments);
	}

	@Override
	public Object visitGetExpr(Expr.Get expr) {
		Object object = this.evaluate(expr.object);

		if (object instanceof LoxInstance) {
			return ((LoxInstance)object).get(expr.name);
		}

		throw new RuntimeError(expr.name, "Only instances have properties.");
	}

	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return this.evaluate(expr.expression);
	}

	@Override
	public Object visitLiteralExpr(Expr.Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitLogicalExpr(Expr.Logical expr) {
		Object left = this.evaluate(expr.left);

		if (expr.operator.type == TokenType.OR) {
			if (this.isTruthy(left)) {
				return left;
			}
		} else {
			if (!this.isTruthy(left)) {
				return left;
			}
		}

		return this.evaluate(expr.right);
	}

	@Override
	public Object visitSetExpr(Expr.Set expr) {
		Object object = this.evaluate(expr.object);

		if (!(object instanceof LoxInstance)) {
			throw new RuntimeError(expr.name, "Only instances have fields");
		}

		Object value = this.evaluate(expr.value);
		((LoxInstance)object).set(expr.name, value);
		return value;
	}

	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = this.evaluate(expr.right);

		switch (expr.operator.type) {
			case BANG:
				return !this.isTruthy(right);
			case MINUS:
				this.checkNumberOperand(expr.operator, right);
				return -(double)right;
		}

		return null;
	}

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		this.executeBlock(stmt.statements, new Environment(this.environment));
		return null;
	}

	@Override
	public Void visitClassStmt(Stmt.Class stmt) {
		this.environment.define(stmt.name.lexeme, null);
		LoxClass loxClass = new LoxClass(stmt.name.lexeme);
		this.environment.assign(stmt.name, loxClass);
		return null;
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		this.evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitFunctionStmt(Stmt.Function stmt) {
		LoxFunction function = new LoxFunction(stmt, this.environment);
		this.environment.define(stmt.name.lexeme, function);
		return null;
	}

	@Override
	public Void visitIfStmt(Stmt.If stmt) {
		if (this.isTruthy(this.evaluate(stmt.condition))) {
			this.execute(stmt.thenBranch);
		} else if (stmt.elseBranch != null) {
			this.execute(stmt.elseBranch);
		}
		return null;
	}

	@Override
	public Void visitPrintStmt(Stmt.Print stmt) {
		Object value = this.evaluate(stmt.expression);
		System.out.println(this.stringify(value));
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		Object value = null;
		if (stmt.value != null) {
			value = this.evaluate(stmt.value);
		}

		throw new Return(value);
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		Object value = null;
		if (stmt.initializer != null) {
			value = this.evaluate(stmt.initializer);
		}

		this.environment.define(stmt.name.lexeme, value);
		return null;
	}

	@Override
	public Void visitWhileStmt(Stmt.While stmt) {
		while (this.isTruthy(this.evaluate(stmt.condition))) {
			this.execute(stmt.body);
		}

		return null;
	}

	@Override
	public Object visitVariableExpr(Expr.Variable expr) {
		return this.lookupVariable(expr.name, expr);
	}

	private Object lookupVariable(Token name, Expr expr) {
		Integer distance = this.locals.get(expr);

		if (distance != null) {
			return this.environment.getAt(distance, name.lexeme);
		} else {
			return this.globals.get(name);
		}
	}

	@Override
	public Object visitAssignExpr(Expr.Assign expr) {
		Object value = this.evaluate(expr.value);

		Integer distance = this.locals.get(expr);
		if (distance != null) {
			this.environment.assignAt(distance, expr.name, value);
		} else {
			this.globals.assign(expr.name, value);
		}

		return 0;
	}

	private void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) {
			return;
		} else {
			throw new RuntimeError(operator, "Operand must be a number.");
		}
	}

	private void checkNumberOperands(Token operator, Object left, Object right) {
		if (left instanceof Double && right instanceof Double) {
			return;
		} else {
			throw new RuntimeError(operator, "Operands must be a number.");
		}
	}

	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}

	private boolean isTruthy(Object object) {
		if (object == null) {
			return false;
		} else if (object instanceof Boolean) {
			return (boolean)object;
		} else {
			return true;
		}
	}

	private boolean isEqual(Object a, Object b) {
		if (a == null && b == null) {
			return true;
		} else if (a == null) {
			return false;
		} else {
			return a.equals(b);
		}
	}

	private String stringify(Object object) {
		if (object == null) {
			return "nil";
		}

		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}

		return object.toString();
	}
}
