package jacaranda.frontend;

import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import latte.grammar.TreeBuilder;
import latte.grammar.latteLexer;
import latte.grammar.latteParser;
import latte.grammar.lattetree;
import latte.grammar.latteParser.program_return;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeNodeStream;

public class Jacaranda {

	public static void main(String[] args) throws RecognitionException {
		
		String file_path = args[0];
		System.out.println(file_path);
		
		String file_data;
		try {
			file_data = readFile(file_path);
			System.out.println(file_data);
			
			TreeBuilder builder = new TreeBuilder();
			lattetree tree = builder.buildTree(file_data);
			
			System.out.println(file_path);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
