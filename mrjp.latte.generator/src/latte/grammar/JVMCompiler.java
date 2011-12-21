package latte.grammar;

import org.antlr.runtime.tree.CommonTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class JVMCompiler {

	private HashMap<String, CommonTree> storage_func = new HashMap<String, CommonTree>();
	private Stack<HashMap<String, Integer>> storage_vars = new Stack<HashMap<String,Integer>>();
	private String className;
	private CommonTree troot;
	FileWriter fwriter;
	BufferedWriter output;
	File fout;
	
	public JVMCompiler(
			String name,
			CommonTree tree) throws IOException {
		this.className = name;
		this.troot = tree;
		
		fout = new File(name+".j");
		if(!fout.exists()){
			fout.createNewFile();
		}
		this.fwriter = new FileWriter(name+".j");
		this.output = new BufferedWriter(fwriter);
	}
	
	private void JVMwrite(String out) throws IOException {
		System.out.println(out);
		output.write(out + "\r\n");
	}

	public void JVMgenerate() throws IOException {
		JVMwrite(".class public " + className);
		JVMwrite(".super java/lang/Object");
		JVMwrite(".method public <init>()V");
		JVMwrite("	aload_0");
		JVMwrite("	invokespecial java/lang/Object/<init>()V");
		JVMwrite("	return");
		JVMwrite(".end method");
		
		JVMtraverse(troot);
		
//		.method public static main([Ljava/lang/String;)V 
//		    .limit stack 5 
//		    .limit locals 100
//		    iconst_0
//		    istore_0
//		    iinc	0 1
//		    iload_0
//		    getstatic java/lang/System/out Ljava/io/PrintStream;
//		    swap
//		    invokevirtual java/io/PrintStream/println(I)V
//		    return
//		.end method
	}

	private int JVMtraverse(CommonTree tree) {
		int token_type = -1;
		if (tree.token != null) {
			token_type = tree.token.getType();
		}
		@SuppressWarnings("unchecked")
		List<CommonTree> children = tree.getChildren();

		switch (token_type) {

		case latteParser.TOP_DEF: {
			break;
		}
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
		case latteParser.DECL:
		default:
		}

		return token_type;
	}
		
}
