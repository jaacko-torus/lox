package jlox;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance {
	private LoxClass loxClass;
	private final Map<String, Object> fields = new HashMap<>();

	public LoxInstance(LoxClass loxClass) {
		this.loxClass = loxClass;
	}

	public Object get(Token name) {
		if (this.fields.containsKey(name.lexeme)) {
			return this.fields.get(name.lexeme);
		}

		throw new RuntimeError(name, "Undefined property \"" + name.lexeme + "\".");
	}

	public Object set(Token name, Object value) {
		this.fields.put(name.lexeme, value);
	}
	
	@Override
	public String toString() {
		return this.loxClass + " instance";
	}
}
