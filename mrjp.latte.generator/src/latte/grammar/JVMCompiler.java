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

	private HashMap<String, String> storage_func = new HashMap<String, String>();
	private Stack<HashMap<String, Integer>> storage_vars = new Stack<HashMap<String,Integer>>();
	private int labelCounter;
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
		JVMLoadFunctions();

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

	private void JVMLoadFunctions() {
		if (troot.token == null) {
			@SuppressWarnings("unchecked")
			List<CommonTree> children = troot.getChildren();

			if (children != null) {
				for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
					CommonTree child = i.next();
					JVMAddFunction(child);
				}
			}			
		} else {
			JVMAddFunction(troot);
		}
	}


	private void JVMAddFunction(CommonTree topdef) {
		@SuppressWarnings("unchecked")
		List<CommonTree> func = topdef.getChildren();
		
		String ident = func.get(1).token.getText();
		String out = JVMEncodeType(func.get(0).token.getType());
		String args = "";
		if (func.get(2).getType() == latteParser.ARGS) {
			@SuppressWarnings("unchecked")
			List<CommonTree> argsList = func.get(2).getChildren();
			for (Iterator<CommonTree> iterator = argsList.iterator(); iterator.hasNext();) {
				CommonTree commonTree = (CommonTree) iterator.next();
				int type = commonTree.getChild(0).getType();
				args = args.concat(JVMEncodeType(type));
			}
		}

		String jvmname = ident + "(" + args + ")" + out;
		storage_func.put(ident, jvmname);
	}

	private String JVMEncodeType(int type) {
		String out = "";
		switch (type) {
		case latteParser.TYPE_INT:
			out = "I";
			break;
		case latteParser.TYPE_STRING:
			out = "I"; // TODO
			break;
		case latteParser.TYPE_BOOLEAN:
			out = "I";
			break;
		case latteParser.TYPE_VOID:
			out = "V";
			break;
		default:
		}
		return out;
	}

	private String JVMNextLabel() {
		labelCounter++;
		return "Label"+labelCounter;
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
			JVMwrite(".method public static " + storage_func.get(name));
		    JVMwrite(".limit stack 5", 1);
		    JVMwrite(".limit locals 100", 1);
		    
		    // Traversing function body.
			if (args.getType() == latteParser.ARGS) {
				storage_vars.push(new HashMap<String, Integer>());
				@SuppressWarnings("unchecked")
				List<CommonTree> argsList = args.getChildren();
				int freeId = JVMFreeVarId(storage_vars);
				for (int i = 0; i < argsList.size(); i++) {
					String ident = argsList.get(i).getChild(1).getText();
					int freeIdShift = freeId + i;
					storage_vars.peek().put(ident, freeIdShift);
				}
				JVMtraverse(children.get(3));
				storage_vars.pop();
			} else {
				JVMtraverse(children.get(2));
			}
		    
			JVMwrite(".end method");
			break;
		}
		case latteParser.BLOCK: {
			storage_vars.push(new HashMap<String, Integer>());
			if (children != null) {
				for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
					CommonTree child = i.next();
					JVMtraverse(child);
				}
			}
			storage_vars.pop();
			break;
		}
		case latteParser.DECL: {
			int freeId = JVMFreeVarId(storage_vars);
			int varType = children.get(0).token.getType();
			for(int i = 1; i < children.size(); i++) {
				CommonTree child = children.get(i);
				@SuppressWarnings("unchecked")
				List<CommonTree> declaration = child.getChildren();
				String ident = declaration.get(0).token.getText();

				int freeIdShift = freeId + i - 1;
				storage_vars.peek().put(ident, freeIdShift);

				switch (varType) {
				case latteParser.TYPE_INT:
				case latteParser.TYPE_BOOLEAN: {
					if (declaration.size() == 2) {
					    JVMtraverse(declaration.get(1));
					} else {
					    JVMwrite("ldc 0", 1);	
					}
					JVMwrite("istore " + freeIdShift, 1);
					break;
				}
				default:
					break;
				}
			}
			break;
		}
		case latteParser.EAPP: {
			String functionName = children.get(0).getText();
			if (functionName.compareTo("printInt") == 0) {
				JVMwrite("getstatic java/lang/System/out Ljava/io/PrintStream;", 1);
				JVMtraverse(children.get(1));
				JVMwrite("invokevirtual java/io/PrintStream/println(I)V", 1);	
			} else {
				for (int i = 1; i < children.size(); i++) {
					JVMtraverse(children.get(i));
				}
				JVMwrite("invokestatic "+className+"."+storage_func.get(functionName), 1);
			}
			break;
		}
		case latteParser.COND: {
			if (children.size() == 3) {
				String elseLabel = JVMNextLabel();
				String endifLabel = JVMNextLabel();
				JVMtraverse(children.get(0));
				JVMwrite("ifeq " + elseLabel, 1);
				JVMtraverse(children.get(1));
				JVMwrite("goto " + endifLabel, 1);
				JVMwrite(elseLabel+":");
				JVMtraverse(children.get(2));
				JVMwrite(endifLabel+":");
			} else {
				String endifLabel = JVMNextLabel();
				JVMtraverse(children.get(0));
				JVMwrite("ifeq " + endifLabel, 1);
				JVMtraverse(children.get(1));
				JVMwrite(endifLabel+":");
			}
			break;
		}
		case latteParser.SWHILE: {
			String whilebodyLabel = JVMNextLabel();
			String endwhileLabel = JVMNextLabel();
			JVMwrite(whilebodyLabel+":");
			JVMtraverse(children.get(0));
			JVMwrite("ifeq " + endwhileLabel, 1);
			JVMtraverse(children.get(1));
			JVMwrite("goto " + whilebodyLabel, 1);
			JVMwrite(endwhileLabel+":");
			break;
		}
		case latteParser.ASS: {
			JVMtraverse(children.get(1));
			String idName = children.get(0).getText();
			int idNo = JVMVarToId(idName);
			JVMwrite("istore " + idNo, 1);
			break;
		}
		case latteParser.DECR: {
			String idName = children.get(0).getText();
			int idNo = JVMVarToId(idName);
		    JVMwrite("iinc " + idNo + " -1", 1);
			break;
		}
		case latteParser.INCR: {
			String idName = children.get(0).getText();
			int idNo = JVMVarToId(idName);
		    JVMwrite("iinc " + idNo + " 1", 1);
			break;
		}
		case latteParser.RET: {
			JVMtraverse(children.get(0));
		    JVMwrite("ireturn", 1);
			break;
		}
		case latteParser.RETV: {
		    JVMwrite("return", 1);
			break;
		}
		case latteParser.OP_PLUS: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
		    JVMwrite("iadd", 1);
		    break;
		}
		case latteParser.OP_MINUS: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
		    JVMwrite("isub", 1);
		    break;
		}
		case latteParser.OP_TIMES: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
		    JVMwrite("imul", 1);
		    break;
		}
		case latteParser.OP_DIV: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
		    JVMwrite("idiv", 1);
		    break;
		}
		case latteParser.OP_MOD: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
		    JVMwrite("irem", 1);
			break;
		}
		case latteParser.OP_LTH: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
			String elseLabel = JVMNextLabel();
			String endifLabel = JVMNextLabel();
			JVMwrite("if_icmplt " + elseLabel, 1);
			JVMwrite("iconst_0");
			JVMwrite("goto " + endifLabel, 1);
			JVMwrite(elseLabel+":");
			JVMwrite("iconst_1");
			JVMwrite(endifLabel+":");
			break;
		}
		case latteParser.OP_LE: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
			String elseLabel = JVMNextLabel();
			String endifLabel = JVMNextLabel();
			JVMwrite("if_icmple " + elseLabel, 1);
			JVMwrite("iconst_0");
			JVMwrite("goto " + endifLabel, 1);
			JVMwrite(elseLabel+":");
			JVMwrite("iconst_1");
			JVMwrite(endifLabel+":");
			break;
		}
		case latteParser.OP_GTH: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
			String elseLabel = JVMNextLabel();
			String endifLabel = JVMNextLabel();
			JVMwrite("if_icmpgt " + elseLabel, 1);
			JVMwrite("iconst_0");
			JVMwrite("goto " + endifLabel, 1);
			JVMwrite(elseLabel+":");
			JVMwrite("iconst_1");
			JVMwrite(endifLabel+":");
			break;
		}
		case latteParser.OP_GE: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
			String elseLabel = JVMNextLabel();
			String endifLabel = JVMNextLabel();
			JVMwrite("if_icmpge " + elseLabel, 1);
			JVMwrite("iconst_0");
			JVMwrite("goto " + endifLabel, 1);
			JVMwrite(elseLabel+":");
			JVMwrite("iconst_1");
			JVMwrite(endifLabel+":");
			break;
		}
		case latteParser.OP_EQU: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
			String elseLabel = JVMNextLabel();
			String endifLabel = JVMNextLabel();
			JVMwrite("if_icmpne " + elseLabel, 1);
			JVMwrite("iconst_1");
			JVMwrite("goto " + endifLabel, 1);
			JVMwrite(elseLabel+":");
			JVMwrite("iconst_0");
			JVMwrite(endifLabel+":");
			break;
		}
		case latteParser.OP_NE: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
			String elseLabel = JVMNextLabel();
			String endifLabel = JVMNextLabel();
			JVMwrite("if_icmpeq " + elseLabel, 1);
			JVMwrite("iconst_1");
			JVMwrite("goto " + endifLabel, 1);
			JVMwrite(elseLabel+":");
			JVMwrite("iconst_0");
			JVMwrite(endifLabel+":");
			break;
		}
		case latteParser.OP_AND: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
		    JVMwrite("iand", 1);
			break;
		}
		case latteParser.OP_OR: {
			JVMtraverse(children.get(0));
			JVMtraverse(children.get(1));
		    JVMwrite("ior", 1);
			break;
		}
		case latteParser.NEGATION: {
			JVMtraverse(children.get(0));
		    JVMwrite("ineg", 1);
		    break;
		}
		case latteParser.NOT: {
			String elseLabel = JVMNextLabel();
			String endifLabel = JVMNextLabel();
			JVMtraverse(children.get(0));
			JVMwrite("ifne " + elseLabel, 1);
			JVMwrite("iconst_1");
			JVMwrite("goto " + endifLabel, 1);
			JVMwrite(elseLabel+":");
			JVMwrite("iconst_0");
			JVMwrite(endifLabel+":");
			break;
		}
		case latteParser.VAR_IDENT: {
			String idName = children.get(0).getText();
			int idNo = JVMVarToId(idName);
			JVMwrite("iload " + idNo, 1);
			break;
		}
		case latteParser.INTEGER: {
			JVMwrite("ldc " + tree.getText(), 1);
			break;
		}
		case latteParser.FALSE: {
			JVMwrite("ldc 0", 1);
			break;
		}
		case latteParser.TRUE: {
			JVMwrite("ldc 1", 1);
			break;
		}
		case latteParser.STRING: {
			break;
		}
		default: {
			if (children != null) {
				for (Iterator<CommonTree> i = children.iterator(); i.hasNext();) {
					CommonTree child = i.next();
					JVMtraverse(child);
				}
			}
			break;
		}
		
		}

		return token_type;
	}

	private int JVMVarToId(String idName) {
		for(int i = storage_vars.size()-1; i >= 0; i--) {
			HashMap<String,Integer> locVar = storage_vars.get(i);
			if (locVar.containsKey(idName)) {
				return locVar.get(idName);
			}
		}
		return -1;
	}

	private int JVMFreeVarId(Stack<HashMap<String, Integer>> vars) {
		int freeId = 0;
		for (Iterator<HashMap<String, Integer>> iterator = vars.iterator(); iterator.hasNext();) {
			HashMap<String, Integer> hashMap = (HashMap<String, Integer>) iterator.next();
			freeId += hashMap.size();
		}
		return freeId;
	}
		
}
