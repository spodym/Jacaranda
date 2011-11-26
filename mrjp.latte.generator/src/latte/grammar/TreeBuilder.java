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
	private Stack<HashMap<String, Integer>> storage_vars = new Stack<HashMap<String,Integer>>();
	
	public CommonTree buildTree(String program_data) throws RecognitionException {
		CharStream charStream = new ANTLRStringStream(program_data);
		latteLexer lexer = new latteLexer(charStream);
		TokenStream tokenStream = new CommonTokenStream(lexer);
		latteParser parser = new latteParser(tokenStream);

		program_return program = parser.program();
		return program.tree;
	}

	public void checkType(CommonTree root) throws TypesMismatchException {
		lookupVar("dd");
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
		
		storage_func.put(ident, topdef);
	}
	
	private int lookupVar(String ident) {
		for(int i = storage_vars.size()-1; i >= 0; i--) {
			HashMap<String,Integer> locVar = storage_vars.get(i);
			if (locVar.containsKey(ident)) {
				return locVar.get(ident);
			}
		}
		return -1;
	}

	public int checkTypes(CommonTree root) throws TypesMismatchException {
		int token_type = -1;
		if (root.token != null) {
			token_type = root.token.getType();
		}
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
		case latteParser.VAR_IDENT:
			int result = lookupVar(children.get(0).token.getText());
			if (result != -1) {
				return lookupVar(children.get(0).token.getText());
			} else {
				throw new TypesMismatchException("unknown variable");
			}
			
		case latteParser.EAPP:
			CommonTree func = storage_func.get(children.get(0).token.getText());
			// TODO: args type cheking...
			return checkTypes((CommonTree)func.getChildren().get(0));
			
//		case latteParser.ASS:
//			return checkTypes((CommonTree)func.getChildren().get(0));
			
		case latteParser.BLOCK: {
			if (children != null) {
				// new block vars
				storage_vars.push(new HashMap<String, Integer>());

				// iterating with new variables block
				for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
					CommonTree child = i.next();
					checkTypes(child);
				}
				
				// old block vars
				storage_vars.pop();
			}
			break;
		}

		case latteParser.DECL: {
			int type = children.get(0).token.getType();
			for(int i = 1; i < children.size(); i++) {
				CommonTree child = children.get(i);
				@SuppressWarnings("unchecked")
				List<CommonTree> declaration = child.getChildren();
				String ident = declaration.get(0).token.getText();

				if (lookupVar(ident) != -1) {
					throw new TypesMismatchException("already exists");
				}
				
				if (child.token.getType() == latteParser.INIT) {
					int currType = checkTypes(declaration.get(1));
					if (type != currType) {
						throw new TypesMismatchException("Mismatch");
					}
				}

				storage_vars.peek().put(ident, type);
			}
			break;
		}

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