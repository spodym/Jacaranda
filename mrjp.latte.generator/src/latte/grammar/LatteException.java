package latte.grammar;

import org.antlr.runtime.tree.CommonTree;

public class LatteException extends Exception {
	private static final long serialVersionUID = -5587373151372831328L;
	private static CommonTree exceptionNode = null;

	public LatteException(String message) {
		super(message);
	}

	public LatteException(String message, CommonTree node) {
		super(message);
		exceptionNode = node;
	}
	
	public void customLatteErrorPrint() {
		String msg;
		if (exceptionNode != null) {
			msg = "Latte error in line " + exceptionNode.token.getLine() + " ";
			msg += "starts at " + exceptionNode.token.getCharPositionInLine() + ":\n";
			msg += "\t"+this.getMessage();
		} else {
			msg = "Latte error:\n\t"+this.getMessage();
		}
		System.err.println(msg);
	}
}