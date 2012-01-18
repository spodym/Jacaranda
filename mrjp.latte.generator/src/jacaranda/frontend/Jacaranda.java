package jacaranda.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import latte.grammar.JVMCompiler;
import latte.grammar.TreeBuilder;
import latte.grammar.LatteException;
import latte.grammar.X86Compiler;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

public class Jacaranda {

	public static void main(String[] args) {
		
		String file_path = args[0];
		
		String file_data;
		try {
			file_data = readFile(file_path);
			TreeBuilder builder = new TreeBuilder();
			CommonTree tree = builder.buildTree(file_data);
			builder.checkType(tree);
			X86Compiler x86 = new X86Compiler(getFileName(file_path), tree);
			x86.X86generate();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RecognitionException e) {
			e.printStackTrace();
		} catch (LatteException e) {
			e.customLatteErrorPrint();
		}
	}
	
	private static String getFileName(String file_path) {
		File file = new File(file_path);
		String name = file.getName();
		if (name.lastIndexOf(".") != -1) {
			name = name.substring(0, name.lastIndexOf("."));
		}
		return name;
	}

	private static String readFile(String path) throws IOException {
		FileInputStream stream = new FileInputStream(new File(path));
		try {
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
			/* Instead of using default, pass in a decoder. */
			return Charset.defaultCharset().decode(bb).toString();
		}
		finally {
			stream.close();
		}
	}
	
}
