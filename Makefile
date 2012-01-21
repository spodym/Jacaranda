JFLAGS = -g -classpath mrjp.latte.generator/jasmin.jar:mrjp.latte.generator/antlr-3.4-complete.jar:mrjp.latte.generator/src:mrjp.latte.generator/antlr-generated -d classes
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	mrjp.latte.generator/antlr-generated/latte/grammar/latteLexer.java \
	mrjp.latte.generator/antlr-generated/latte/grammar/latteParser.java \
	mrjp.latte.generator/antlr-generated/latte/grammar/lattetree.java \
	mrjp.latte.generator/src/latte/grammar/LatteException.java \
	mrjp.latte.generator/src/latte/grammar/TreeBuilder.java \
	mrjp.latte.generator/src/latte/grammar/JVMCompiler.java \
	mrjp.latte.generator/src/latte/grammar/X86Compiler.java \
	mrjp.latte.generator/src/jacaranda/frontend/Jacaranda.java

default: jar

dirs:
	mkdir -p classes

classes: dirs grammar $(CLASSES:.java=.class)

grammar:
	cd mrjp.latte.generator/src; java -jar ../antlr-3.4-complete.jar -o ../antlr-generated latte/grammar/latte.g
	cd mrjp.latte.generator/src; java -jar ../antlr-3.4-complete.jar -o ../antlr-generated latte/grammar/lattetree.g

jar: classes
	cd classes; jar xf ../mrjp.latte.generator/antlr-3.4-complete.jar
	cd classes; jar xf ../mrjp.latte.generator/jasmin.jar
	cd classes; jar cfm ../Jacaranda.jar ../manifest.txt .

clean:
	rm -rf \
	classes/* \
	Jacaranda.jar
