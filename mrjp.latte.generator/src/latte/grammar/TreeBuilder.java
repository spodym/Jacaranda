package latte.grammar;

import latte.grammar.latteParser.eadd_return;
import latte.grammar.latteParser.program_return;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.BaseTree;
import org.antlr.runtime.tree.CommonTree;

import java.util.Iterator;
import java.util.List;

public class TreeBuilder {
	
	public CommonTree buildTree(String program_data) throws RecognitionException {
		CharStream charStream = new ANTLRStringStream(program_data);
		latteLexer lexer = new latteLexer(charStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		latteParser parser = new latteParser(tokenStream);

		program_return program = parser.program();
		return program.tree;
	}
	
	public String checkTypes(CommonTree root) throws TypesMismatchException {
		int token_type = root.token.getType();
		@SuppressWarnings("unchecked")
		List<CommonTree> children = root.getChildren();
		
		switch (token_type) {
		case latteParser.OP_PLUS:
			for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
				CommonTree child = i.next();
				String type = checkTypes(child);
				System.out.println(type);
			}
			break;

		default:
			if (children != null) {
				for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
					CommonTree child = i.next();
					checkTypes(child);
				}
			}
			break;
		}
		
		return "default";
	}
}