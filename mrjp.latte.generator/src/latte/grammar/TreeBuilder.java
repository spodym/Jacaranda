package latte.grammar;

import latte.grammar.latteParser.program_return;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

public class TreeBuilder {
	
	public lattetree buildTree(String program_data) throws RecognitionException {
		CharStream charStream = new ANTLRStringStream(program_data);
		latteLexer lexer = new latteLexer(charStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		latteParser parser = new latteParser(tokenStream);

		program_return program = parser.program();
		System.out.println(program.tree.toStringTree());
		CommonTreeNodeStream nodeStream = new CommonTreeNodeStream(program.tree);
		return new lattetree(nodeStream);
	}
}