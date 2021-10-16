package tool;

import java.io.IOException;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GenerateAST {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: generate_ast <output directory>");
			System.exit(64);
		}

		String outputDir = args[0];

		defineAST(outputDir, "Expr", Arrays.asList(
				"Assign   : Token name, Expr value",
				"Binary   : Expr left, Token operator, Expr right",
				"Call     : Expr callee, Token paren, List<Expr> arguments",
				"Grouping : Expr expression",
				"Literal  : Object value",
				"Logical  : Expr left, Token operator, Expr right",
				"Unary    : Token operator, Expr right",
				"Variable : Token name"
		));

		defineAST(outputDir, "Stmt", Arrays.asList(
				"Block      : List<Stmt> statements",
				"Class      : Token name, List<Stmt.Function> methods",
				"Expression : Expr expression",
				"Function   : Token name, List<Token> params, List<Stmt> body",
				"If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
				"Print      : Expr expression",
				"Return     : Token keyword, Expr value",
				"Var        : Token name, Expr initializer",
				"While      : Expr condition, Stmt body"
		));
	}

	private static void defineAST(String outputDir, String baseName, List<String> types) throws IOException {
		String path = outputDir + "/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, StandardCharsets.UTF_8);

		writer.println("package jlox;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println();
		writer.println("abstract class " + baseName + " {");

		defineVisitor(writer, baseName, types);

		writer.println();

		for (String type : types) {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim();
			defineType(writer, baseName, className, fields);
			writer.println();
		}

		writer.println("\tabstract <T> T accept(Visitor<T> visitor);");

		writer.println("}");
		writer.close();
	}

	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		writer.println("\tinterface Visitor<T> {");
		for (String type : types) {
			String typeName = type.split(":")[0].trim();
			writer.println("\t\tT visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
		}
		writer.println("\t}");
	}


	private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
		String[] fields = fieldList.split(", ");

		writer.println("\tstatic class " + className + " extends " + baseName + " {");

		// fields
		for (String field : fields) {
			writer.println("\t\tpublic final " + field + ";");
		}
		writer.println();

		// constructor
		writer.println("\t\t" + className + "(" + fieldList + ") {");
		for (String field : fields) {
			String name = field.split(" ")[1];
			writer.println("\t\t\tthis." + name + " = " + name + ";");
		}
		writer.println("\t\t}");

		writer.println();

		// visitor pattern
		writer.println("\t\t@Override");
		writer.println("\t\tpublic <T> T accept(Visitor<T> visitor) {");
		writer.println("\t\t\treturn visitor.visit" + className + baseName + "(this);");
		writer.println("\t\t}");

		writer.println("\t}");
	}
}
