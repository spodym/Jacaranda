package latte.grammar;

import org.antlr.runtime.tree.CommonTree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
		this.fwriter = new FileWriter(className+".j");
		this.output = new BufferedWriter(fwriter);
	}

	private void JVMwrite(String out, int indentionLevel) throws IOException {
		System.out.println(out);
		String indention = "";
		for (int i = 0; i < indentionLevel; i++) {
			indention = indention.concat("    ");
		}
		output.write(indention + out + "\r\n");
	}

	private void JVMwrite(String out) throws IOException {
		JVMwrite(out, 0);
	}

	private void JVMwriteEnd() throws IOException {
		output.close();
	}

	public void JVMgenerate() throws IOException {
		JVMwrite(".class public " + className);
		JVMwrite(".super java/lang/Object");
		JVMwrite(".method public <init>()V");
		JVMwrite("aload_0", 1);
		JVMwrite("invokespecial java/lang/Object/<init>()V", 1);
		JVMwrite("return", 1);
		JVMwrite(".end method");

		JVMwrite(".method public static main([Ljava/lang/String;)V");
		JVMwrite("invokestatic "+className+".main()I", 1);
		JVMwrite("pop", 1);
		JVMwrite("return", 1);
		JVMwrite(".end method");
		
		JVMtraverse(troot);
		
		JVMwriteEnd();
	}

	private int JVMtraverse(CommonTree tree) throws IOException {
 		int token_type = -1;
		if (tree.token != null) {
			token_type = tree.token.getType();
		}
		@SuppressWarnings("unchecked")
		List<CommonTree> children = tree.getChildren();

		switch (token_type) {

		case latteParser.TOP_DEF: {
			String name = children.get(1).getText();
			CommonTree args = children.get(2);
			if (name.compareTo("main") == 0) {
				JVMwrite(".method public static main()I");	
			} else {
				if (args.getType() == latteParser.ARGS) {
					String arguments = "";
					JVMwrite(".method public static "+name+"("+arguments+")V");
				} else {
					JVMwrite(".method public static "+name+"()V");
				}
			}
		    JVMwrite(".limit stack 5", 1);
		    JVMwrite(".limit locals 100", 1);
		    
		    // Traversing function body.
			if (args.getType() == latteParser.ARGS) {
				JVMtraverse(children.get(3));
			} else {
				JVMtraverse(children.get(2));
			}
		    
			JVMwrite(".end method");
			break;
		}
		case latteParser.BLOCK: {
			if (children != null) {
				for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
					CommonTree child = i.next();
					JVMtraverse(child);
				}
			}
			break;
		}
		case latteParser.DECL: {
		    JVMwrite("iconst_0", 1);
			JVMwrite("istore_0", 1);
			break;
		}
		case latteParser.EAPP: {
			String functionName = children.get(0).getText();
			if (functionName.compareTo("printInt") == 0) {
				JVMwrite("getstatic java/lang/System/out Ljava/io/PrintStream;", 1);
				JVMtraverse(children.get(1));
				JVMwrite("invokevirtual java/io/PrintStream/println(I)V", 1);	
			} else {
			}
			break;
		}
		case latteParser.ASS:
		case latteParser.DECR:
		case latteParser.INCR: {		
		    JVMwrite("iinc 0 1", 1);
			break;
		}
		case latteParser.RET: {
		    JVMwrite("iload_0", 1);
		    JVMwrite("ireturn", 1);
			break;
		}
		case latteParser.RETV: {
		    JVMwrite("return", 1);
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
		case latteParser.VAR_IDENT: {
			JVMwrite("iload_0", 1);
			break;
		}
		case latteParser.INTEGER: {
			JVMwrite("ldc " + tree.getText(), 1);
			break;
		}
		case latteParser.FALSE:
		case latteParser.TRUE:
		case latteParser.STRING:
		default:
		}

		return token_type;
	}
		
}
