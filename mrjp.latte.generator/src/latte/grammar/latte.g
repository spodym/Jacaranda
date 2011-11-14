grammar latte;

options {
    language = Java;
}

@header {
    package latte.grammar;
}

@lexer::header {
    package latte.grammar;
}


/** program */

program
    : topdef+
    ;
topdef
    : type ident '(' arg? ')' block
    ;
arg
    : (type IDENT)(',' type IDENT)*
    ;


/** statements */

block
    : '{' stmt* '}'
    ;
stmt
    : empty
    | bstmt
    | decl
    | ass
    | incr
    | decr
    | ret
    | vret
    | cond
    | condelse
    | while
    | sexp
    ;
empty
    : ';'
    ;
bstmt
    : block
    ;
decl
    : type (item)(',' item)* ';'
    ;
item
    : noinit
    | init
    ;
noinit
    : ident
    ;
init
    : ident '=' expr
    ;
ass
    : ident '=' expr  ';'
    ;
incr
    : ident '++'  ';'
    ;
decr
    : ident '--'  ';'
    ;
ret
    : 'return' expr ';'
    ;
vret
    : 'return' ';'
    ;
cond
    : 'if' '(' expr ')' stmt
    ;
condelse
    : 'if' '(' expr ')' stmt 'else' stmt
    ;
while
    : 'while' '(' expr ')' stmt
    ;
sexp
    : expr ';'
    ;


/** types */

type
    : 'int'
    | 'string'
    | 'boolean'
    | 'void'
    ;

TYPE_INT : 'int' ;
TYPE_STRING : 'string' ;
TYPE_BOOLEAN : 'boolean' ;
TYPE_VOID : 'void' ;


/** expressions */
/**
evar.      Expr6 ::= Ident ;
elitInt.   Expr6 ::= Integer ;
elitTrue.  Expr6 ::= "true" ;
elitFalse. Expr6 ::= "false" ;
eapp.      Expr6 ::= Ident "(" [Expr] ")" ;
estring.   Expr6 ::= String ;
neg.       Expr5 ::= "-" Expr6 ;
not.       Expr5 ::= "!" Expr6 ;
emul.      Expr4 ::= Expr4 MulOp Expr5 ;
eadd.      Expr3 ::= Expr3 AddOp Expr4 ;
erel.      Expr2 ::= Expr2 RelOp Expr3 ;
eand.      Expr1 ::= Expr2 "&&" Expr1 ;
eor.       Expr ::= Expr1 "||" Expr ;
coercions  Expr 6 ;
separator  Expr "," ;*/


ident: IDENT ;

IDENT
    : ('a'..'z' | 'A'..'Z')('a'..'z' | 'A'..'Z' | '0'..'9')*
    ;

WHITESPACE
    : (' ' | '\t' | '\r' | '\n') { $channel = HIDDEN; }
    ;

/** comments */

COMMENT
    : '/*' .* '*/' { $channel=HIDDEN; }
    ;

LINE_COMMENT
    : ('//'|'#') ~('\n'|'\r')* '\r'? '\n' { $channel=HIDDEN; }
    ;
