package latte.grammar;

import latte.grammar.latteParser.program_return;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;

public class Test {
	public static void main(String[] args) throws RecognitionException {
		CharStream charStream = new ANTLRStringStream("int main() { return 1-1-1; }");
		latteLexer lexer = new latteLexer(charStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		latteParser parser = new latteParser(tokenStream);
		program_return evaluator = parser.program();
		System.out.println(evaluator.tree.toStringTree());
	}
}
