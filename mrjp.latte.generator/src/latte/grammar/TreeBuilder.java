package latte.grammar;

import latte.grammar.latteParser.program_return;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class TreeBuilder {

	private HashMap<String, CommonTree> storage_func = new HashMap<String, CommonTree>();
	private Stack<HashMap<String, CommonTree>> storage_vars = new Stack<HashMap<String,CommonTree>>();
	
	public CommonTree buildTree(String program_data) throws RecognitionException {
		CharStream charStream = new ANTLRStringStream(program_data);
		latteLexer lexer = new latteLexer(charStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		latteParser parser = new latteParser(tokenStream);

		program_return program = parser.program();
		return program.tree;
	}

	public void checkType(CommonTree root) throws TypesMismatchException {
		loadFunctions(root);
		checkTypes(root);
	}

	private void loadFunctions(CommonTree root) {
		if (root.token == null) {
			@SuppressWarnings("unchecked")
			List<CommonTree> children = root.getChildren();

			if (children != null) {
				for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
					CommonTree child = i.next();
					addFunction(child);
				}
			}			
		} else {
			addFunction(root);
		}
	}

	private void addFunction(CommonTree topdef) {
		@SuppressWarnings("unchecked")
		List<CommonTree> func = topdef.getChildren();
		String ident = func.get(1).token.getText();
		System.out.println(ident);
		
		storage_func.put(ident, topdef);
	}

	public int checkTypes(CommonTree root) throws TypesMismatchException {
			int token_type = root.token.getType();
		@SuppressWarnings("unchecked")
		List<CommonTree> children = root.getChildren();

//		System.out.println(token_type);
//		System.out.println(root.token.getText());
		
		switch (token_type) {
		
		// int int || str str
		case latteParser.OP_PLUS: {
			int type_left = checkTypes(children.get(0));
			int type_right = checkTypes(children.get(1));
			
			if (type_left != type_right || 
					(type_left != latteParser.TYPE_INT &&
					type_left != latteParser.TYPE_STRING)) {
				throw new TypesMismatchException("Add mismatch");
			}
			
			return type_left;
		}
			
		// int int
		case latteParser.OP_MINUS:
		case latteParser.OP_TIMES:
		case latteParser.OP_DIV:
		case latteParser.OP_MOD:
		case latteParser.OP_LTH:
		case latteParser.OP_LE:
		case latteParser.OP_GTH:
		case latteParser.OP_GE: {
			int type_left = checkTypes(children.get(0));
			int type_right = checkTypes(children.get(1));

			System.out.println(type_left);
			System.out.println(type_right);

			if (type_left != type_right ||
					type_left != latteParser.TYPE_INT) {
				throw new TypesMismatchException("Mismatch");
			}

			return type_left;
		}
		
		// int int || bool bool
		case latteParser.OP_EQU:
		case latteParser.OP_NE: {
			int type_left = checkTypes(children.get(0));
			int type_right = checkTypes(children.get(1));

			System.out.println(type_left);
			System.out.println(type_right);

			if (type_left != type_right ||
					(type_left != latteParser.TYPE_INT &&
					type_left != latteParser.TYPE_BOOLEAN)) {
				throw new TypesMismatchException("Mismatch");
			}

			return type_left;
		}
		
		case latteParser.NEGATION: {
			int type_left = checkTypes(children.get(0));

			if (type_left != latteParser.TYPE_INT) {
				throw new TypesMismatchException("Mismatch");
			}
			
			return latteParser.TYPE_INT;
		}
		
		case latteParser.NOT: {
			int type_left = checkTypes(children.get(0));

			if (type_left != latteParser.TYPE_BOOLEAN) {
				throw new TypesMismatchException("Mismatch");
			}
			
			return latteParser.TYPE_BOOLEAN;
		}

		case latteParser.INTEGER:
			return latteParser.TYPE_INT;
		case latteParser.FALSE:
			return latteParser.TYPE_BOOLEAN;
		case latteParser.TRUE:
			return latteParser.TYPE_BOOLEAN;
		case latteParser.STRING:
			return latteParser.TYPE_STRING;
		case latteParser.IDENT:
			return -1;
		case latteParser.EAPP:
			return -1;
			
		default: {
			if (children != null) {
				for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
					CommonTree child = i.next();
					checkTypes(child);
				}
			}
			break;
		}
		}
		
		return token_type;
	}
}