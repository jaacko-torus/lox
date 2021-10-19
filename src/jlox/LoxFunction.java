package jlox;

import java.util.List;

public class LoxFunction implements LoxCallable {
	private final Stmt.Function declaration;
	private final Environment closure;

	public LoxFunction(Stmt.Function declaration, Environment closure) {
		this.declaration = declaration;
		this.closure = closure;
	}

	public LoxFunction bind(LoxInstance instance) {
		Environment environment = new Environment(this.closure);
		environment.define("this", instance);
		return new LoxFunction(this.declaration, environment);
	}

	@Override
	public int arity() {
		return this.declaration.params.size();
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Environment environment = new Environment(this.closure);

		for (int i = 0; i < this.declaration.params.size(); i += 1) {
			environment.define(this.declaration.params.get(i).lexeme, arguments.get(i));
		}

		try {
			interpreter.executeBlock(this.declaration.body, environment);
		} catch (Return returnValue) {
			return returnValue.value;
		}

		return null;
	}

	@Override
	public String toString() {
		return "<fun " + this.declaration.name.lexeme + ">";
	}
}
