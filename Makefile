JFLAGS = -g -classpath mrjp.latte.generator/antlr-3.4-complete.jar:mrjp.latte.generator/src:mrjp.latte.generator/antlr-generated
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
	cd mrjp.latte.generator/src; jar cfm ../../Jacaranda.jar ../../manifest.txt .
	#cd mrjp.latte.generator/antlr-generated; jar uf ../../Jacaranda.jar .

clean:
	$(RM) \
	mrjp.latte.generator/antlr-generated/latte/grammar/*.class \
	mrjp.latte.generator/src/latte/grammar/*.class \
	mrjp.latte.generator/src/jacaranda/frontend/*.class \
	Jacaranda.jar
