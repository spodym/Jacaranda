package latte.grammar;

import org.antlr.runtime.tree.CommonTree;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class JVMCompiler {

	private HashMap<String, CommonTree> storage_func = new HashMap<String, CommonTree>();
	private Stack<HashMap<String, Integer>> storage_vars = new Stack<HashMap<String,Integer>>();

	public int generateJVM(CommonTree root) {
		int token_type = -1;
		if (root.token != null) {
			token_type = root.token.getType();
		}
		@SuppressWarnings("unchecked")
		List<CommonTree> children = root.getChildren();

		switch (token_type) {
		
		case latteParser.OP_PLUS:
		case latteParser.OP_MINUS:
		case latteParser.OP_TIMES:
		case latteParser.OP_DIV:
		case latteParser.OP_MOD:
		case latteParser.OP_LTH:
		case latteParser.OP_LE:
		case latteParser.OP_GTH:
		case latteParser.OP_GE:
		case latteParser.OP_EQU:
		case latteParser.OP_NE:
		case latteParser.OP_AND:
		case latteParser.OP_OR:
		case latteParser.NEGATION:
		case latteParser.NOT:
		case latteParser.INTEGER:
		case latteParser.FALSE:
		case latteParser.TRUE:
		case latteParser.STRING:
		case latteParser.VAR_IDENT:
		case latteParser.EAPP:
		case latteParser.ASS:
		case latteParser.DECR:
		case latteParser.INCR:
		case latteParser.BLOCK:
		case latteParser.TOP_DEF:
		case latteParser.DECL:
		default:
		}

		return token_type;
	}
		
}