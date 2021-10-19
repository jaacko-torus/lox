package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.List;

public class Lox {
	public static boolean hadError = false;
	public static boolean hadRuntimeError = false;

	private static final Interpreter interpreter = new Interpreter();

	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: jlox [script]");
			System.exit(64);
		} else if (args.length == 1) {
			Lox.runFile(args[0]);
		} else {
			Lox.runPrompt();
		}
	}

	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));

		Lox.run(new String(bytes, Charset.defaultCharset()));

		// https://www.freebsd.org/cgi/man.cgi?query=sysexits
		// indicate an error in the exit code
		if (hadError) {
			System.exit(65);
		}

		if (hadRuntimeError) {
			System.exit(70);
		}
	}

	private static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		while (true) {
			System.out.print("> ");
			String line = reader.readLine();

			if (line == null) {
				break;
			}

			Lox.run(line);
			Lox.hadError = false;
			Lox.hadRuntimeError = false;
		}
	}

	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		List<Token> tokens = scanner.scanTokens();

		Parser parser = new Parser(tokens);
		List<Stmt> statements = parser.parse();

		if (hadError) return;

		Resolver resolver = new Resolver(Lox.interpreter);
		resolver.resolve(statements);

		if (Lox.hadError) return;

		Lox.interpreter.interpret(statements);
		// System.out.println(new ASTPrinter().print(expression));
	}

	static void error(int line, String message) {
		Lox.report(line, "", message);
	}

	private static void report(int line, String where, String message) {
		System.err.println("[line " + line + "] Error" + where + ": " + message);
		Lox.hadError = true;
	}

	static void error(Token token, String message) {
		if (token.type == TokenType.EOF) {
			Lox.report(token.line, " at end", message);
		} else {
			Lox.report(token.line, " at'" + token.lexeme + "'", message);
		}
	}

	static void runtimeError(RuntimeError error) {
		System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
		Lox.hadRuntimeError = true;
	}
}
