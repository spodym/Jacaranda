JFLAGS = -g -classpath mrjp.latte.generator/antlr-3.4-complete.jar:mrjp.latte.generator/src:mrjp.latte.generator/antlr-generated -d classes
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
	mrjp.latte.generator/src/jacaranda/frontend/Jacaranda.java

default: classes

classes: $(CLASSES:.java=.class)

jar: classes
	cd classes; jar cfm ../Jacaranda.jar ../manifest.txt .
	jar -i Jacaranda.jar mrjp.latte.generator/antlr-3.4-complete.jar

clean:
	rm -rf \
	classes/* \
	Jacaranda.jar
