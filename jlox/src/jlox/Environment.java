package jlox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Environment {
	public final Environment enclosing;
	private final Map<String, Object> values = new HashMap<>();

	public Environment() {
		this.enclosing = null;
	}

	public Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}

	public Object get(Token name) {
		if (this.values.containsKey(name.lexeme)) {
			return this.values.get(name.lexeme);
		} else if (this.enclosing != null) {
			return this.enclosing.get(name);
		} else {
			throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
		}
	}

	public void define(String name, Object value) {
		this.values.put(name, value);
	}

	public Environment ancestor(int distance) {
		Environment environment = this;

		for (int i = 0; i < distance; i += 1) {
			assert environment != null;
			environment = environment.enclosing;
		}

		return environment;
	}

	public Object getAt(int distance, String name) {
		return this.ancestor(distance).values.get(name);
	}

	public void assignAt(int distance, Token name, Object value) {
		this.ancestor(distance).values.put(name.lexeme, value);
	}

	public void assign(Token name, Object value) {
		if (this.values.containsKey(name.lexeme)) {
			this.values.put(name.lexeme, value);
		} else if (this.enclosing != null) {
			this.enclosing.assign(name, value);
		} else {
			throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
		}
	}
}
